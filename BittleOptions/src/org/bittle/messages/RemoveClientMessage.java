package org.bittle.messages;

import com.eclipsesource.json.*;
import org.bittle.utilities.Share;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;

/**
 * Remove Client Message
 * @author chmar
 */
public class RemoveClientMessage implements Message {

    private JsonObject message;    // The JSON message from the server 
    private String name;           // Name of the user that was removed
    
    public RemoveClientMessage(JsonObject message){
        this.message = message;
        this.name = message.getString("name", null);
    }
    
    @Override
    public void handleMessage() {
        if(!name.equals(Share.getInstance().getMe())){
            NotifyDescriptor nd = new NotifyDescriptor.Message(
                    name + " has been removed from the share session", 
                    NotifyDescriptor.INFORMATION_MESSAGE
            );
            DialogDisplayer.getDefault().notify(nd);
            Share.getInstance().removeUser(name);
        }
    }
}
