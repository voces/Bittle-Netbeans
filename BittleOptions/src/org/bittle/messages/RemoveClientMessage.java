package org.bittle.messages;

import com.eclipsesource.json.*;
import org.bittle.utilities.Share;

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
        Share.getInstance().removeUser(name);
    }
    
}
