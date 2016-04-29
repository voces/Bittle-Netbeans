/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bittle.messages;

import com.eclipsesource.json.JsonObject;

/**
 *
 * @author chmar
 */
public class Invitation implements Message{
    
    private JsonObject invitation;
    private String id;
    private String blame;
    private int shareID;
    
    public Invitation(JsonObject invitation, String id){
        this.invitation = invitation;
        this.id = id;
        this.blame = invitation.getString("blame", null);
        this.shareID = invitation.getInt("shareId", -1);
        
    }

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
