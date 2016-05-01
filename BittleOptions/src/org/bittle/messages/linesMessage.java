package org.bittle.messages;

import com.eclipsesource.json.*;
import java.util.ArrayList;
import java.util.List;
import org.bittle.beansmod.*;

/**
 * Lines Message 
 * @author chmar
 */
public class linesMessage implements Message {
    
    private JsonObject message;
    private JsonArray lines;
    private String filename;
    private String blame;
    private int start;
    private int deleteCount;
    
    // Parse the JSON
    public linesMessage(JsonObject message){
        this.message = message;
        this.lines = message.get("lines").asArray();
        this.filename = message.getString("filename", null);
        this.blame = message.getString("blame", null);
        this.start = message.getInt("start", -1);
        this.deleteCount = message.getInt("deleteCount", -1);
    }
   
    @Override
    public void handleMessage() {

        // Make sure you didn't cause the change         
        if(!blame.equals(Share.getInstance().getMe())){
            
            // Create a list of strings 
            List<String> lineList = new ArrayList<>();
            
            // Convert JSON array to list of strings 
            for(JsonValue line : lines)
                lineList.add(line.asString());
            
            // Json Array converted into a list of strings
            // Not sure what to call for the document class...
        }
    }
    
}
