package org.bittle.messages;

import org.bittle.utilities.*;
import com.eclipsesource.json.*;

/**
 * Line Message
 * @author chmar
 */
public class lineMessage implements Message {

    private JsonObject message;    // JSON message from the server
    private String filename;       // Name of the file being changed
    private String line;           // Change in the line of text 
    private String blame;          // Name of the user who changed the file
    private int lineIndex;         // Is this necessary here?
    private int start;             // Start column of the change
    private int deleteCount;       // Number of characters being deleted 
    
    // Parse the JSON
    public lineMessage(JsonObject message){
        this.message = message;
        this.filename = message.getString("filename", null);
        this.line = message.getString("line", null);
        this.blame = message.getString("blame", null);
        this.lineIndex = message.getInt("lineIndex", -1);
        this.start = message.getInt("start", -1);
        this.deleteCount = message.getInt("deleteCount", -1);
    }
   
    @Override
    public void handleMessage() {
        if(blame != null && !blame.equals(Share.getInstance().getMe())){
            if (deleteCount > 0)
                DocumentManipulator.getInstance().deleteText(filename, start, deleteCount);
            else
                DocumentManipulator.getInstance().insertText(line, filename, start, lineIndex);
        }
    }
    
}
