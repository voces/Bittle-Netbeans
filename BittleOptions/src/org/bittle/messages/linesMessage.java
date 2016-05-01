package org.bittle.messages;

import com.eclipsesource.json.*;
import java.util.ArrayList;
import java.util.List;
import org.bittle.beansmod.*;
import org.bittle.utilities.DocumentManipulator;

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
        
        //a new line stripped of the newline character returns an empty array
        if (message.get("lines") != null) {
            this.lines = message.get("lines").asArray();
        }
        
        this.filename = message.getString("filename", null);
        this.blame = message.getString("blame", null);
        this.start = message.getInt("start", -1);
        this.deleteCount = message.getInt("deleteCount", -1);
    }
   
    @Override
    public void handleMessage() {

        // Make sure you didn't cause the change         
        if(blame != null && !blame.equals(Share.getInstance().getMe())){
            
            // Create an array of strings
            String[] lines;
            if (this.lines != null) {
                lines = new String[this.lines.size()];

                // Convert JSON array to array of strings
                int lineIndex = 0;
                for(JsonValue line : this.lines) {
                    lines[lineIndex] = line.asString();
                    lineIndex++;
                }
            }
            else
                lines = new String[]{""};
            
            if (deleteCount > 0)
                DocumentManipulator.getInstance().deleteLines(lines, filename, start, deleteCount);
            else
                DocumentManipulator.getInstance().insertLines(lines, filename, start);
        }
    }
    
}
