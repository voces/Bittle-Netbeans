/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bittle.beansmod;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import jdk.nashorn.internal.objects.NativeError;

/**
 * A Singleton HashSet that holds Strings
 * Will contain the names of all the files currently being synced
 */
public class SyncList extends HashSet<String> {
    
    private static final SyncList instance;
    public static String bittlePath;
    
    static{
        instance = new SyncList();
    }
    
    private SyncList(){}
    
    public static SyncList getInstance(){
        return instance;
    }
    
    public static boolean addNewFile(String filePath) throws IOException{
        String fileName = getFileName(filePath);
        if(!instance.contains(fileName)){
            instance.add(fileName);
            addFileToFolder(filePath);
            return true;
        }
        return false;
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
        Path to = Paths.get(SyncList.bittlePath + "\\" + getFileName(filePath));
        Files.copy(from, to);
    }
}
