/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bittle.messages;

import com.eclipsesource.json.*;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;

/**
 * Track Message
 * @author chmar
 */
public class TrackMessage implements Message{
    
    private JsonObject message;     // JSON message from server
    private String id;              // Message id
    private String status;          // Status of response
    private String reason;          // Reason for status failure 
    
    // Parse the JSON
    public TrackMessage(JsonObject message){
        this.message = message;
        this.id = message.getString("id", null);
        this.status = message.getString("status", null);
        this.reason = message.getString("reason", null);
    }

    @Override
    public void handleMessage() {
        // Notify if the track failed 
        if(status.equals("failed")){
            NotifyDescriptor nd = new NotifyDescriptor.Message(reason, NotifyDescriptor.ERROR_MESSAGE);
            DialogDisplayer.getDefault().notify(nd);
        }
    }
    
}
