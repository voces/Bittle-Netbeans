package org.bittle.messages;

import com.eclipsesource.json.*;
import org.bittle.beansmod.*;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;

/**
 * Login Message 
 * @author chmar
 */
public class LoginMessage implements Message {
    
    private JsonObject message;     // JSON message from server
    private String id;              // Message id
    private String status;          // Status of response
    private String reason;          // Reason for status failure 
    
    // Parse the JSON
    public LoginMessage(JsonObject message){
        this.message = message;
        this.id = message.getString("id", null);
        this.status = message.getString("status", null);
        this.reason = message.getString("reason", null);
    }

    @Override
    public void handleMessage() {
        
        // Get the options controller
        BittleOptionsPanelController controller = BittleOptionsPanelController.getInstance();
        
        // Notify it to stop waiting for response 
        controller.stopWaiting();
        
        // Handle the message 
        if(status.equals("failed")){
            controller.setLoginState(false);
            NotifyDescriptor nd = new NotifyDescriptor.Message(reason, NotifyDescriptor.ERROR_MESSAGE);
            DialogDisplayer.getDefault().notify(nd);
        }
        else{
            controller.setLoginState(true);
            NotifyDescriptor nd = new NotifyDescriptor.Message("Welcome Back!", NotifyDescriptor.INFORMATION_MESSAGE);
            DialogDisplayer.getDefault().notify(nd);
        }
    }
}
