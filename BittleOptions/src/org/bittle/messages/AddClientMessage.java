package org.bittle.messages;

import com.eclipsesource.json.*;
import java.io.IOException;
import org.bittle.utilities.Share;
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
    private JsonValue names;       // List of users in the new share
    private JsonValue files;       // List of files in the new share 
    private String id;             // Message ID
    private String blame;          // Client who added the new user
    private String name;           // Name of the new user 
    
    // Parse the JSON
    public AddClientMessage(JsonObject message){
        this.message = message;
        this.id = message.getString("id", null);
        this.blame = message.getString("blame", null);
        this.name = message.getString("name", null);
        this.names = message.get("names");
        this.files = message.get("files"); 
    }

    @Override
    public void handleMessage() {
        
        // If the message is a notification type 
        if(name != null){
            
            // Notify the user if they weren't the ones that added the client 
            if(!blame.equals(Share.getInstance().getMe())){
            NotifyDescriptor nd = new NotifyDescriptor.Message(
                    blame + " added " + name + "to the share session", 
                    NotifyDescriptor.INFORMATION_MESSAGE
            );
            DialogDisplayer.getDefault().notify(nd);
            }
        }
        // Otherwise give the new client the share 
        else{
            try {
                JsonArray namesArray = names.asArray();
                JsonArray filesArray = files.asArray();
                Share.getInstance().makeNewShare(namesArray, filesArray);
            } catch (IOException ex) {
            }
        }
    }
}
