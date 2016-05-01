package org.bittle.messages;

import com.eclipsesource.json.*;
import java.io.IOException;
import org.bittle.beansmod.Share;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;

/**
 * Remove File Message
 * @author chmar
 */
public class RemoveFileMessage implements Message {
    
    private JsonObject message;
    private String filename;
    private String blame;
    
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
