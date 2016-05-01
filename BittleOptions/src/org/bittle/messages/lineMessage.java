/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bittle.messages;

import com.eclipsesource.json.*;
import java.util.ArrayList;
import java.util.List;
import org.bittle.beansmod.*;

/**
 *
 * @author chmar
 */
public class lineMessage implements Message {

    private JsonObject message;
    private String filename;
    private String line;
    private String blame;
    private int lineIndex;
    private int start;
    private int deleteCount;
    
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
        
        if(!blame.equals(Share.getInstance().getMe())){
            
            // Do stuff here...idk what...
        }
    }
    
}
