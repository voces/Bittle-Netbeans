package org.bittle.beansmod;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static java.nio.file.StandardOpenOption.CREATE;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.cookies.OpenCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.windows.WindowManager;

/**
 * A Singleton HashSet that holds Strings
 * Will contain the names of all the files currently being synced
 * Also maintains the contents of the Bittle directory 
 * As well as GUI file tree
 */
public class SyncList extends HashSet<String> {
    
    private static final SyncList instance;
    private final BittleTreeTopComponent fileTree;
    private final Connection connection;
    public static String bittlePath;
    
    // Initialize the instance and get the GUI tree
    static{
        instance = new SyncList();

    }
    
    private SyncList() {
        fileTree = (BittleTreeTopComponent) WindowManager.getDefault().findTopComponent("BittleTree");
        connection = Connection.getInstance();
    }
    
    public static SyncList getInstance(){
        return instance;
    }
    
    /**
     * Expects a path of form "/folderA/folderB/file.txt"
     * Checks if "file.txt" is being synced
     * If it is not, adds the file to the list and begins syncing 
     * @param filePath path of the new file to be added
     * @return true if the file was added, false otherwise
     */
    public void addFile(String filePath) throws IOException{
        String fileName = getFileName(filePath);
        if(!this.contains(fileName)){
            this.add(fileName);
            addFileToFolder(filePath);
            fileTree.addObject(fileName);
            trackFile(fileName);
        }
        else{
            NotifyDescriptor nd = new NotifyDescriptor.Message(fileName + " already being synced!", NotifyDescriptor.ERROR_MESSAGE);
            DialogDisplayer.getDefault().notify(nd);
        }
    }
    
    /**
     * Expects a file name of the form "file.txt"
     * Removes the file from the sync list if it exists
     * Removes the file from the bittle folder if it exists
     * Note: This method does not remove the file from the tree
     * This should be handled after calling this function
     * @param fileName name of the file to be removed
     */
    public void removeFile(String fileName) throws IOException{
        FileObject fo = FileUtil.toFileObject(FileUtil.normalizeFile(new File(getBittleFilePath(fileName))));
        if(!fo.isLocked()){
            Files.deleteIfExists(Paths.get(getBittleFilePath(fileName)));
            this.remove(fileName);
        }
        else{
            NotifyDescriptor nd = new NotifyDescriptor.Message("Can not remove locked file " + fileName, NotifyDescriptor.ERROR_MESSAGE);
            DialogDisplayer.getDefault().notify(nd);
        }
    }
        
    /**
     * Scans the current bittle directory
     * Any files that are not in the sync list
     * Are added to the sync list and the file tree
     */
    public void scanFolder(){
        String[] filesInBittleFolder = new File(bittlePath).list();
        Iterable<String> fileList = Arrays.asList(filesInBittleFolder);
        for(String fileName : fileList){
            if(!this.contains(fileName)){
                this.add(fileName);
                fileTree.addObject(fileName);
                trackFile(fileName);
            }
        }
    }
    
    public void clearList() throws IOException{
        Object[] files = this.toArray();
        for(int i = 0; i < files.length; i++){
            this.removeFile((String)files[i]);
        }
    }
    
    /**
     * Checks if a file exists in the bittle directory.
     * If it does, it returns the path of that file in the directory
     * If it doesn't it returns null
     * @param fileName file name of the form file.txt
     * @return The path of that file in the Bittle directory, if it exists
     */
    public String getBittleFilePath(String fileName){
        if(this.contains(fileName)){
            return bittlePath + "\\" + fileName;
        }
        else
            return null;
    }
    
    /**
     * Expects a path of form "/folderA/folderB/file.txt"
     * Returns a string with "file.txt"
     * @param filePath path of desired file name
     * @return tring - name of file at given path
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
            connection.track(filePath, lines);
            
            // Wait for response from the server
            while(Connection.response == null)
                try {
                    TimeUnit.MILLISECONDS.sleep(50);
                } catch (InterruptedException ex) {
                }
            
            if(connection.checkResponse("track")){
                NotifyDescriptor nd = new NotifyDescriptor.Message("Sharing " + fileName, NotifyDescriptor.INFORMATION_MESSAGE);
                DialogDisplayer.getDefault().notify(nd);
            }
        } catch (IOException ex) {
        }   
    }
}
