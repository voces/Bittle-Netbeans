package org.bittle.beansmod;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static java.nio.file.StandardOpenOption.CREATE;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.Preferences;
import org.bittle.messages.*;
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
        // Add a lister to it that is only interested in response messages
        // If the share is waiting for a response, stop waiting and handle response
        connection = Connection.getInstance();
        connection.addMessageListener((Message m) -> {
            if(m instanceof Response){
                if(waitingForResponse){
                    waitingForResponse = false;
                    handleResponse((Response) m);
                }
            }
        });
       
       
        // Gets the preferences stored in the options
        // If the rootpath preference changes, update the bittlePath
        preferences = NbPreferences.forModule(BittlePanel.class);
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
     * Adds user to share if they are not already in it
     * @param username User to be added to the share
     */
    public void addUser(String username) {
        if(!users.contains(username))
            users.add(username);
    }
    
    /**
     * Expects a path of form "/folderA/folderB/file.txt"
     * Adds file to share if it is not already in it.
     * Adds file to list, folder, GUI tree, and requests
     * server to track file 
     * @param filePath path of the new file to be added to the share 
     */
    public void addFile(String filePath) throws IOException{
        String fileName = getFileName(filePath);
        if(!files.contains(fileName)){
            files.add(fileName);
            addFileToFolder(filePath);
            fileTree.addObject(fileName);
            trackFile(fileName);
        }
    }
    
    public void addFileFromServer(String fileName){
        // TODO: Transform filename and array of lines into a file
        // and add it to the share 
    }
    
    /**
     * Expects a file name of the form "file.txt"
     * Removes from share if it is in it.
     * Removes file from list, folder and requests server to
     * stop tracking file. 
     * Note: This method does not remove the file from the tree
     * This should be handled after calling this function
     * @param fileName name of the file to be removed
     */
    public void removeFile(String fileName) throws IOException{
        if(files.contains(fileName)){
            // Get the file object from the bittle directory 
            FileObject fo = FileUtil.toFileObject(FileUtil.normalizeFile(new File(getBittleFilePath(fileName))));
            
            // Do not attempt to delete a locked file 
            if(!fo.isLocked()){
                Files.deleteIfExists(Paths.get(getBittleFilePath(fileName)));
            files.remove(fileName);
            }
            else{
                NotifyDescriptor nd = new NotifyDescriptor.Message("Can not remove locked file " + fileName, NotifyDescriptor.ERROR_MESSAGE);
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
        for(String fileName : fileList){
            if(!files.contains(fileName)){
                files.add(fileName);
                fileTree.addObject(fileName);
                trackFile(fileName);
            }
        }
    }
    
    /**
     * Removes all the files from the share 
     */
    public void clearList() throws IOException{
        Object[] fileArray = files.toArray();
        for(int i = 0; i < fileArray.length; i++){
            this.removeFile((String)fileArray[i]);
        }
    }
    
    /**
     * If a file exists in the share, returns it's file path 
     * @param fileName File name of the form file.txt
     * @return File path of the form /folder/Bittle/file.txt,
     * null if given file name does not exist in the share.
     */
    public String getBittleFilePath(String fileName){
        if(files.contains(fileName)){
            return bittlePath + "\\" + fileName;
        }
        else
            return null;
    }
    
    /**
     * @return Username of the current user 
     */
    public String getMe(){
        return preferences.get("username", null);
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
    private void addFileToFolder(String filePath) throws IOException {
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
     * Constructs a file from an array of Strings.
     * If the file already exists, the array of Strings will be written to it.
     * @param filename String - Name of the file to be created
     * @param lines String[] - Array of lines to be written to the file
     */
    private void constructFile(String filename, String[] lines) throws IOException{
       
        // Put the file in the current bittle directory
        Path filePath = Paths.get(bittlePath + "\\" + filename);
        
        // Convert the array of Strings into an iterable list
        Iterable<String> lineList = Arrays.asList(lines);
        
        // Write the lines to the file
        // The CREATE flag creates the file if it doesn't exist
        Files.write(filePath, lineList, CREATE);
    }
    
    /**
     * Sends a file to the server to be tracked
     * Expects a filename of the form "file.txt"
     * Grabs the file from the bittle folder
     * Converts the contents to an array of Strings
     * Sends the file name and array to the server
     * Handles the response from the server
     * @param fileName 
     */
    private void trackFile(String fileName){
        
        String filePath = getBittleFilePath(fileName);
        try {
            String[] lines = fileToStringArray(filePath);
            NotifyDescriptor nd = new NotifyDescriptor.Message("Attempting to track: " + fileName, NotifyDescriptor.INFORMATION_MESSAGE);
            DialogDisplayer.getDefault().notify(nd);
            
            waitingForResponse = true;
            connection.track(fileName, lines);
            waitForResponse();
        } catch (IOException ex) {
        }   
    }

    /**
     * Handles response events from the server 
     * If the response is a failed response, gives the reason and returns 
     * Otherwise notify the success response 
     * @param r Response message from the server 
     */
    private void handleResponse(Response r) {
        if(r.getStatus().equals("failed")){
            NotifyDescriptor nd = new NotifyDescriptor.Message(r.getReason(), NotifyDescriptor.ERROR_MESSAGE);
            DialogDisplayer.getDefault().notify(nd);
        }
        else{
            NotifyDescriptor nd = new NotifyDescriptor.Message("Success!", NotifyDescriptor.INFORMATION_MESSAGE);
            DialogDisplayer.getDefault().notify(nd);
        }
    }
    
    /**
     * Waits by putting thread to sleep for 50ms
     * Spins on response flag, which is changed in response handler
     * If the thread has been spinning for too long, notify time out
     */
    private void waitForResponse() {
        int timer = 0;
        
        while(waitingForResponse){
            if(timer > 100){
                NotifyDescriptor nd = new NotifyDescriptor.Message("Waiting for server timed out...", NotifyDescriptor.ERROR_MESSAGE);
                DialogDisplayer.getDefault().notify(nd);
                break;
            }
            
            try {
                TimeUnit.MILLISECONDS.sleep(50);
            } catch (InterruptedException ex) {
            }
            
            timer ++;
        }
    }

    void setPath(String rootpath) {
        bittlePath = rootpath;
    }
}
