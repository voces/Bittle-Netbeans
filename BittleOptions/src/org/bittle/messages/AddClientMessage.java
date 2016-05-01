/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bittle.messages;

import com.eclipsesource.json.*;
import java.io.IOException;
import org.bittle.beansmod.Share;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;

/**
 * Add Client Message
 * This can be received for two reasons
 * 1 - The new client being added receives lists of users and files
 * 2 - Clients already in the share get the name of the added client 
 * @author chmar
 */
public class AddClientMessage implements Message {
    
    private JsonObject message;    // JSON message from the server 
    private JsonArray names;       // List of users in the new share
    private JsonArray files;       // List of files in the new share 
    private String id;             // Message ID
    private String blame;          // Client who added the new user
    private String name;           // Name of the new user 
    
    // Parse the JSON
    public AddClientMessage(JsonObject message){
        this.message = message;
        this.id = message.getString("id", null);
        this.blame = message.getString("blame", null);
        this.name = message.getString("name", null);
        this.names = message.get("names").asArray();
        this.files = message.get("files").asArray();   
    }

    @Override
    public void handleMessage() {
        if(name != null){
            NotifyDescriptor nd = new NotifyDescriptor.Message(
                    blame + " added " + name + "to the share session", 
                    NotifyDescriptor.INFORMATION_MESSAGE
            );
            DialogDisplayer.getDefault().notify(nd);
        }
        else{
            try {
                Share.getInstance().makeNewShare(names, files);
            } catch (IOException ex) {
            }
        }
    }
}
