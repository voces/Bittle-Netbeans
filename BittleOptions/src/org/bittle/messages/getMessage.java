/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bittle.messages;

import com.eclipsesource.json.*;
import java.io.IOException;
import org.bittle.beansmod.*;

/**
 *
 * @author chmar
 */
public class getMessage implements Message {
    
    private JsonObject message;
    private String id;
    private String file;
    private String status;
    private JsonArray lines;
    
    public getMessage(JsonObject message){
        this.message = message;
        this.id = message.getString("id", null);
        this.file = message.getString("file", null);
        this.status = message.getString("status", null);
        this.lines = message.get("lines").asArray();
    }


    @Override
    public void handleMessage() {
        try {
            Share.getInstance().addFileFromServer(file, lines);
        } catch (IOException ex) {
        }
    }
    
}
