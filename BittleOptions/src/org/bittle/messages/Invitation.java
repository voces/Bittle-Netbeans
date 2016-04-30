/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bittle.messages;

import com.eclipsesource.json.JsonObject;

/**
 * Invitation Message
 * @author chmar
 */
public class Invitation implements Message{
    
    private JsonObject invitation;    // JSON message from server
    private String id;                // Message id
    private String blame;             // User that sent the invitation 
    private int shareID;              // Share session id
    
    // Initialize all the fields 
    public Invitation(JsonObject invitation, String id){
        this.invitation = invitation;
        this.id = id;
        this.blame = invitation.getString("blame", null);
        this.shareID = invitation.getInt("shareId", -1);
        
    }

    // Getters for all the fields 
    @Override
    public String getID() {
        return id;
    }
    
    public String getBlame(){
        return blame;
    }
    
    public int getshareID(){
        return shareID;
    }
    
}
