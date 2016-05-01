package org.bittle.messages;

import com.eclipsesource.json.*;
import org.bittle.beansmod.*;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;

/**
 * Register message 
 * @author chmar
 */
public class RegisterMessage implements Message {

    private JsonObject message;     // JSON message from server
    private String id;              // Message id
    private String status;          // Status of response
    private String reason;          // Reason for status failure 
    
    // Parse the JSON
    public RegisterMessage(JsonObject message){
        this.message = message;
        this.id = message.getString("id", null);
        this.status = message.getString("status", null);
        this.reason = message.getString("reason", null);
    }
     
    @Override
    public void handleMessage() {
        
        // Get the options panel controller
        BittleOptionsPanelController controller = BittleOptionsPanelController.getInstance();
        
        // Notify the options to stop waiting
        controller.stopWaiting();
        
        // Handle the messsage 
        if(status.equals("failed")){
            controller.setLoginState(false);
            NotifyDescriptor nd = new NotifyDescriptor.Message(reason, NotifyDescriptor.ERROR_MESSAGE);
            DialogDisplayer.getDefault().notify(nd);
        }
        else{
            controller.setLoginState(true);
            controller.logIn();
            NotifyDescriptor nd = new NotifyDescriptor.Message("Thanks for signing up!", NotifyDescriptor.INFORMATION_MESSAGE);
            DialogDisplayer.getDefault().notify(nd);
        }
    }
}
