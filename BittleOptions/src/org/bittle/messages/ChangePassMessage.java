package org.bittle.messages;

import com.eclipsesource.json.*;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;

/**
 * Change Password Message 
 * @author chmar
 */
public class ChangePassMessage implements Message {
    
    private JsonObject message;     // JSON message from server
    private String id;              // Message id
    private String status;          // Status of response
    private String reason;          // Reason for status failure 
    
    // Parse the JSON
    public ChangePassMessage(JsonObject message){
        this.message = message;
        this.id = message.getString("id", null);
        this.status = message.getString("status", null);
        this.reason = message.getString("reason", null);
    }

    @Override
    public void handleMessage() {
        if(status.equals("failed")){
            NotifyDescriptor nd = new NotifyDescriptor.Message(reason, NotifyDescriptor.ERROR_MESSAGE);
            DialogDisplayer.getDefault().notify(nd);
        }
        else{
            NotifyDescriptor nd = new NotifyDescriptor.Message("Password successfully changed!", NotifyDescriptor.INFORMATION_MESSAGE);
            DialogDisplayer.getDefault().notify(nd);
        }
    }
}
