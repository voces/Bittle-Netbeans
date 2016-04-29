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
import static org.bittle.beansmod.Connection.response;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.windows.WindowManager;

/**
 * A Singleton HashSet that holds Strings
 * Will contain the names of all the files currently being synced
 * Also maintains the contents of the Bittle directory 
 * As well as GUI file tree
 */
public class SyncList extends HashSet<String> {
    
    private static final SyncList instance;
    private static final BittleTreeTopComponent fileTree;
    private static final Connection connection;
    public static String bittlePath;
    
    // Initialize the instance and get the GUI tree
    static{
        instance = new SyncList();
        fileTree = (BittleTreeTopComponent) WindowManager.getDefault().findTopComponent("BittleTree");
        connection = Connection.getInstance();
    }
    
    private SyncList() {}
    
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
        if(!instance.contains(fileName)){
            instance.add(fileName);
            addFileToFolder(filePath);
            fileTree.addObject(fileName);
            trackFile(fileName);
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
        Files.deleteIfExists(Paths.get(getBittleFilePath(fileName)));
        instance.remove(fileName);
    }
    
    private static void trackFile(String fileName){
        
        String filePath = getBittleFilePath(fileName);
        try {
            String[] lines = fileToStringArray(filePath);
            connection.track(filePath, lines);
            // Wait for response from the server
            while(response == null)
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
    
    /**
     * Scans the current bittle directory
     * Any files that are not in the sync list
     * Are added to the sync list and the file tree
     */
    public static void scanFolder(){
        String[] filesInBittleFolder = new File(bittlePath).list();
        Iterable<String> fileList = Arrays.asList(filesInBittleFolder);
        for(String fileName : fileList){
            if(!instance.contains(fileName)){
                instance.add(fileName);
                fileTree.addObject(fileName);
                trackFile(fileName);
            }
        }
    }
    
    /**
     * Checks if a file exists in the bittle directory.
     * If it does, it returns the path of that file in the directory
     * If it doesn't it returns null
     * @param fileName file name of the form file.txt
     * @return The path of that file in the Bittle directory, if it exists
     */
    public static String getBittleFilePath(String fileName){
        if(instance.contains(fileName)){
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
    public static String[] fileToStringArray(String filePath) throws IOException{
        
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
    public void constructFile(String filename, String[] lines) throws IOException{
       
        // Put the file in the current bittle directory
        Path filePath = Paths.get(bittlePath + "\\" + filename);
        
        // Convert the array of Strings into an iterable list
        Iterable<String> lineList = Arrays.asList(lines);
        
        // Write the lines to the file
        // The CREATE flag creates the file if it doesn't exist
        Files.write(filePath, lineList, CREATE);
    }
}
