package org.bittle.messages;

import org.bittle.utilities.*;
import com.eclipsesource.json.*;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;

/**
 * Add File Message 
 * @author chmar
 */
public class AddFileMessage implements Message {
    
    private JsonObject message;     // JSON message from server
    private String id;              // Message id
    private String filename;        // Name of the added file
    private String blame;           // User who added the file 
    
    // Parse the JSON
    public AddFileMessage(JsonObject message){
        this.message = message;
        this.id = message.getString("id", null);
        this.filename = message.getString("filename", null);
        this.blame = message.getString("blame", null);
    }
    
    @Override
    public void handleMessage() {
        
        // Ignore files added by self 
        if(blame != null && !blame.equals(Share.getInstance().getMe())){
            
            // If you don't already have the file, get it from the server 
            if(!Share.getInstance().files.contains(filename))
                Connection.getInstance().get(filename);
            
            // Notify the user the file was added 
            NotifyDescriptor nd = new NotifyDescriptor.Message(
                    blame + " has added " + filename + " to the share session", 
                    NotifyDescriptor.INFORMATION_MESSAGE
            );
            DialogDisplayer.getDefault().notify(nd);
        }
    }
}
