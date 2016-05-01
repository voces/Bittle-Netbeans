package org.bittle.beansmod;

import com.eclipsesource.json.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static java.nio.file.StandardOpenOption.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.prefs.Preferences;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbPreferences;
import org.openide.windows.WindowManager;

/**
 * A Singleton that represents the user's current share session
 * Contains two hash sets: one for the users in the share, 
 * and one for the files being shared.
 * Files being shared has 1 to 1 correspondence with bittle directory
 * and GUI tree
 * @author chmar
 */
public class Share {
    
    private static final Share instance;
        
    private final BittleTreeTopComponent fileTree;
    private final Connection connection;
    private final Preferences preferences;
    
    private boolean waitingForResponse;
    private String bittlePath;
    private String owner;
    private String me;
    
    public HashSet<String> users;
    public HashSet<String> files; 
    
    // Initialize the instance
    static{
        instance = new Share();
    }
    
    // Share Consturctor 
    private Share() {
        
        // Find the GUI tree
        fileTree = (BittleTreeTopComponent) WindowManager.getDefault().findTopComponent("BittleTree");
        
        // Initialize what needs initializing 
        waitingForResponse = false;
        users = new HashSet<>();
        files = new HashSet<>();
        
        // Get the connection to the server
        connection = Connection.getInstance();
       
       
        // Gets the preferences stored in the options
        preferences = NbPreferences.forModule(BittlePanel.class);
        
        // If the rootpath preference changes, update the bittlePath
        /*
        preferences.addPreferenceChangeListener((PreferenceChangeEvent evt) -> {
            if(evt.getKey().equals("path"))
                bittlePath = evt.getNewValue();
        });*/
    }
    
    /**
     * @return The instance of the share
     */
    public static Share getInstance(){
        return instance;
    }
    
    /**
     * Adds user to the share
     * Sets the user as the current user
     * @param username Name of the current user 
     */
    public void addMe(String username) {
        if(!users.contains(username)){
            users.add(username);
            me = username;
        }
    }
    
    /**
     * @param rootpath The new bittle path 
     */
    public void setPath(String rootpath) {
        bittlePath = rootpath;
    }
        
    /**
     * Expects a path of form "/folderA/folderB/file.txt"
     * Adds file to share if it is not already in it.
     * Adds file to list, folder, GUI tree, and requests
     * server to track file 
     * @param filePath path of the new file to be added to the share 
     */
    public void addFile(String filePath) throws IOException{
        String filename = getFileName(filePath);
        if(!files.contains(filename)){
            files.add(filename);
            copyToBittle(filePath);
            fileTree.addObject(filename);
            trackFile(filename);
        }
    }
    
    /**
     * Called by get message handler
     * If the file being added by the server is not in the share
     * Constructs a file from the lines and adds it to the share
     * @param filename Name of file being added to share
     * @param jsonLines Contents of file being added to the share
     */
    public void addFileFromServer(String filename, JsonArray jsonLines) throws IOException{
       
        // Make sure the file is not already in the share
        if(!files.contains(filename)){
            
            // Create an list of strings 
            List<String> lines = new ArrayList<>();
        
            // Convert the json array to a list of strings 
            for(JsonValue line : jsonLines)
                lines.add(line.asString());
            
            // Construct the file and place it in the bittle folder 
            constructFile(filename, lines);
            
            // Add file to list
            files.add(filename);
            
            // Add file to gui tree 
            fileTree.addObject(filename);
        }
    }
    
    /**
     * Expects a file name of the form "file.txt"
     * Removes from share if it is in it.
     * Removes file from list, folder and requests server to
     * stop tracking file. 
     * Note: This method does not remove the file from the tree
     * This should be handled after calling this function
     * @param filename name of the file to be removed
     */
    public void removeFile(String filename) throws IOException{
        if(files.contains(filename)){
            // Get the file object from the bittle directory 
            FileObject fo = FileUtil.toFileObject(FileUtil.normalizeFile(new File(getBittleFilePath(filename))));
            
            // Do not attempt to delete a locked file 
            if(!fo.isLocked()){
                Files.deleteIfExists(Paths.get(getBittleFilePath(filename)));
                files.remove(filename);
                connection.untrack(filename);
                // Remove file from tree 
            }
            else{
                NotifyDescriptor nd = new NotifyDescriptor.Message("Can not remove locked file " + filename, NotifyDescriptor.ERROR_MESSAGE);
                DialogDisplayer.getDefault().notify(nd);
            }
        }
    }
    
    /**
     * Scans the current bittle directory
     * Any files that are not in the share are added 
     */
    public void scanFolder(){
        String[] filesInBittleFolder = new File(bittlePath).list();
        Iterable<String> fileList = Arrays.asList(filesInBittleFolder);
        for(String filename : fileList){
            if(!files.contains(filename)){
                files.add(filename);
                fileTree.addObject(filename);
                trackFile(filename);
            }
        }
    }
    
    /**
     * Removes all the files from the share 
     */
    public void purgeFiles() throws IOException{
        Object[] fileArray = files.toArray();
        for(int i = 0; i < fileArray.length; i++){
            this.removeFile((String)fileArray[i]);
        }
    }
    
    /**
     * This function should purge the current share and make a new one with the new info
     * Call get on all the files that are added, and add all the names to the user list
     * @param names
     * @param files 
     */
    public void makeNewShare(JsonArray names, JsonArray files) throws IOException {
        
        // Purge the old share
        purgeShare();
        
        // Get the new users from the JSON
        List<JsonValue> newUsers = names.values();
        
        // Add them to the share's user list 
        for(JsonValue user : newUsers)
            users.add(user.asString());
        
        // Get the new files from the JSON
        List<JsonValue> newFiles = files.values();
        
        // Get the files from the server
        // The server will respond with the filename and the array of lines
        // The message handler will then add that response to the share 
        for(JsonValue file : newFiles)
            connection.get(file.asString());
    }
    
    /**
     * If a file exists in the share, returns it's file path 
     * @param filename File name of the form file.txt
     * @return File path of the form /folder/Bittle/file.txt,
     * null if given file name does not exist in the share.
     */
    public String getBittleFilePath(String filename){
        if(files.contains(filename))
            return bittlePath + File.separator + filename;
        else
            return null;
    }
    
    /**
     * @return Username of the current user 
     */
    public String getMe(){
        return me;
    }
    
    /**
     * Adds user to share if they are not already in it
     * @param username User to be added to the share
     */
    private void addUser(String username) {
        if(!users.contains(username))
            users.add(username);
    }
    
    /**
     * @param filePath path of the form "/folderA/folderB/file.txt"
     * @return Name of file at given path of the form "file.txt"
     */
    private String getFileName(String filePath) {
        return Paths.get(filePath).getFileName().toString();
    }
    
    /**
     * Copies the file at the given path to the Bittle directory
     * @param filePath path of the file to be copied
     */
    private void copyToBittle(String filePath) throws IOException {
        Path from = Paths.get(filePath);
        Path to = Paths.get(getBittleFilePath(getFileName(filePath)));
        Files.copy(from, to);
    }
    
    /**
     * Converts a file at a given path into an array of strings
     * @param filePath String - Where the file to be converted resides
     * @return - An array of strings containing the lines in the file
     */
    private String[] fileToStringArray(String filePath) throws IOException{
        
        // Get the path of the file to be deconstructed
        Path file = Paths.get(filePath);
        
        // Read the lines of the file into a list
        List<String> lines = Files.readAllLines(file);
        
        // Convert that list into an array
        String[] linesArray = lines.toArray(new String[lines.size()]);
        
        return linesArray;
    }
    
    /**
     * Constructs a file from a list of Strings
     * Places the file in the bittle folder 
     * @param filename Name of the file to be created
     * @param lines List of lines to be written to the file
     */
    private void constructFile(String filename, List<String> lines) throws IOException{
       
        // Put the file in the current bittle directory
        Path filePath = Paths.get(bittlePath + File.separator + filename);
        
        // Write the lines to the file
        // The CREATE_NEW flag makes the write fail if the file already exists
        Files.write(filePath, lines, CREATE_NEW);
    }
    
    /**
     * Sends a file to the server to be tracked
     * Expects a filename of the form "file.txt"
     * Grabs the file from the bittle folder
     * Converts the contents to an array of Strings
     * Sends the file name and array to the server
     * Handles the response from the server
     * @param filename 
     */
    private void trackFile(String filename){
        
        String filePath = getBittleFilePath(filename);
        try {
            String[] lines = fileToStringArray(filePath);
            connection.track(filename, lines);
        } catch (IOException ex) {
        }   
    }
    
    /**
     * Clears all users from the share
     * Deletes all files from the share and bittle folder
     * Clears the file tree 
     */
    private void purgeShare() throws IOException {
        users.clear();
        fileTree.clearFiles();
        purgeFiles();
    }
}
