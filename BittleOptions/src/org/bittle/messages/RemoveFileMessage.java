package org.bittle.messages;

import com.eclipsesource.json.*;
import java.io.IOException;
import org.bittle.utilities.Share;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;

/**
 * Remove File Message
 * @author chmar
 */
public class RemoveFileMessage implements Message {
    
    private JsonObject message;    // JSON message from the server
    private String filename;       // Name of the file to be removed
    private String blame;          // User who removed the file 
    
    public RemoveFileMessage(JsonObject message){
        this.message = message;
        this.filename = message.getString("filename", null);
        this.blame = message.getString("blame", null);
    }

    @Override
    public void handleMessage() {
        
        // Ignore files removed by self
        if(blame != null && !blame.equals(Share.getInstance().getMe())){
            try {
                Share.getInstance().removeFile(filename);
            } catch (IOException ex) {
            }
            
            NotifyDescriptor nd = new NotifyDescriptor.Message(
                    blame + " has removed " + filename + " from the share session", 
                    NotifyDescriptor.INFORMATION_MESSAGE
            );
            DialogDisplayer.getDefault().notify(nd);
        }
    }
    
}
