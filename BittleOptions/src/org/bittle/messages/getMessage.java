package org.bittle.messages;

import org.bittle.utilities.Share;
import com.eclipsesource.json.*;
import java.io.IOException;

/**
 * Get Message
 * @author chmar
 */
public class getMessage implements Message {
    
    private JsonObject message;
    private String id;
    private String filename;
    private String status;
    private JsonArray lines;
    
    public getMessage(JsonObject message){
        this.message = message;
        this.id = message.getString("id", null);
        this.filename = message.getString("filename", null);
        this.status = message.getString("status", null);
        this.lines = message.get("lines").asArray();
    }

    @Override
    public void handleMessage() {
        try {
            Share.getInstance().addFileFromServer(filename, lines);
        } catch (IOException ex) {
        }
    }
    
}
