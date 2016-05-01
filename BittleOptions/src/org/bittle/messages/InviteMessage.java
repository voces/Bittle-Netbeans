/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bittle.messages;

import org.bittle.utilities.Connection;
import com.eclipsesource.json.*;
import org.bittle.beansmod.*;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;

/**
 * Invite Message
 * Invite messages will either be a response or an invitation
 * If the invite is a response, it's status field will be non null
 * Otherwise it will have blame and shareID
 * @author chmar
 */
public class InviteMessage implements Message{
    
    private JsonObject message;     // JSON message from server
    private String id;              // Message id
    private String status;          // Status of response
    private String reason;          // Reason for status failure 
    private String blame;           // Who invited you 
    private int shareID;            // ID of the share you got invited to 
    
    // Parse the JSON
    public InviteMessage(JsonObject message){
        this.message = message;
        this.id = message.getString("id", null);
        this.status = message.getString("status", null);
        this.reason = message.getString("reason", null);
        this.blame = message.getString("blame", null);
        this.shareID = message.getInt("shareId", -1);
    }

    @Override
    public void handleMessage() {
        if(status != null){
            if(reason != null){
                NotifyDescriptor nd = new NotifyDescriptor.Message(reason, NotifyDescriptor.ERROR_MESSAGE);
                DialogDisplayer.getDefault().notify(nd);
            }
        }
        else{
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
            if (DialogDisplayer.getDefault().notify(nd) == NotifyDescriptor.YES_OPTION)
                Connection.getInstance().accept(inviter, shareID);
            // Otherwise, send the server an decline 
            else {
                Connection.getInstance().decline(inviter, shareID);
            }
        }
    }
}
