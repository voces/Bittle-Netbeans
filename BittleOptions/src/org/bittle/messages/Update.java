/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bittle.messages;

import com.eclipsesource.json.*;
import org.bittle.beansmod.Connection;
import org.bittle.beansmod.Share;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;

/**
 * Update message 
 * @author chmar
 */
public class Update implements Message{
    
    private JsonObject update;    // JSON message from server
    private String id;            // Message id
    private String blame;         // User that caused the update
    
    // Intialize the fields
    public Update(JsonObject update, String id){
        this.update = update;
        this.id = id;
        this.blame = update.getString("blame", null);
    }

    // Getters for all the fields 
    @Override
    public String getID() {
        return id;
    }
    
    public String getBlame(){
        return blame;
    }
    
    /**
     * Lazily return what the client was notified that changed in the update 
     * @param changeType the name of the field that was changed
     * @return The object the client expects was changed 
     */
    public JsonValue getChange(String changeType){
        return update.get(changeType);
    }
    

    @Override
    public void handle() {
                // If the update is an add file update
        if (id.equals("addFile")) {

            // Get the user that added the file 
            String changer = blame;

            // Ignore files added by the client 
            if (!changer.equals(Share.getInstance().getMe())) {
                // Get the name of the added file 
                String addedFile = getChange("filename").asString();
                NotifyDescriptor nd = new NotifyDescriptor.Message(
                        blame + " has added " + addedFile + " to the share session", NotifyDescriptor.INFORMATION_MESSAGE
                );
                DialogDisplayer.getDefault().notify(nd);
                if (!Share.getInstance().files.contains(addedFile)) {
                    Connection.getInstance().get(addedFile);
                }
            }
        }
    }
}
