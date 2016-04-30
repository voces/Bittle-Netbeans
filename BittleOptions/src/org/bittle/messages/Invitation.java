/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bittle.messages;

import com.eclipsesource.json.JsonObject;
import org.bittle.beansmod.Connection;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;

/**
 * Invitation Message
 * @author chmar
 */
public class Invitation implements Message {
    
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
    
    public void handle() {
        // Get the user that invited the client 
        String inviter = blame;

        // Ask the client if they want to accept the invitation
        NotifyDescriptor nd = new NotifyDescriptor.Confirmation(
                inviter + " has invited you to a share session. \n Do you accept?",
                "Invitation Recieved!",
                NotifyDescriptor.YES_NO_OPTION,
                NotifyDescriptor.QUESTION_MESSAGE
        );

        // If they do, send the server an accept
        if (DialogDisplayer.getDefault().notify(nd) == NotifyDescriptor.YES_OPTION) {
            Connection.getInstance().accept(inviter, shareID);
        } // Otherwise, send the server an decline 
        else {
            Connection.getInstance().decline(inviter, shareID);
        }
    }
}
