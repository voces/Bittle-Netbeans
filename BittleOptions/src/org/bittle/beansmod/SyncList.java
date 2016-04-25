/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bittle.beansmod;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import org.openide.windows.WindowManager;

/**
 * A Singleton HashSet that holds Strings
 * Will contain the names of all the files currently being synced
 * Also maintains the contents of the Bittle directory 
 */
public class SyncList extends HashSet<String> {
    
    private static final SyncList instance;
    private static final BittleTreeTopComponent fileTree;
    public static String bittlePath;
    
    static{
        instance = new SyncList();
        fileTree = (BittleTreeTopComponent) WindowManager.getDefault().findTopComponent("BittleTree");
    }
    
    private SyncList(){}
    
    public static SyncList getInstance(){
        return instance;
    }
    
    /**
     * Expects a path of form "/folderA/folderB/file.txt"
     * Checks if "file.txt" is being synced
     * If it is not, adds the file to the sync list and the bittle folder
     * @param filePath path of the new file to be added
     * @return true if the file was added, false otherwise
     */
    public static void addNewFile(String filePath) throws IOException{
        String fileName = getFileName(filePath);
        if(!instance.contains(fileName)){
            instance.add(fileName);
            addFileToFolder(filePath);
            fileTree.addObject(fileName);
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
    public static void removeFile(String fileName) throws IOException{
        Files.deleteIfExists(Paths.get(getBittleFilePath(fileName)));
        instance.remove(fileName);
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
    private static String getFileName(String filePath) {
        return filePath.substring(filePath.lastIndexOf("\\")+1);
    }
    
    /**
     * Copies the file at the given path to the Bittle directory
     * @param filePath path of the file to be copied
     */
    private static void addFileToFolder(String filePath) throws IOException {
        Path from = Paths.get(filePath);
        Path to = Paths.get(getBittleFilePath(getFileName(filePath)));
        Files.copy(from, to);
    }
}
