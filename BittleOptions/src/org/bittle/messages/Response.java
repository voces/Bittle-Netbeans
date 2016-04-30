package org.bittle.messages;

import com.eclipsesource.json.*;

/**
 * Response Message
 * @author chmar
 */
public class Response implements Message{
    
    private JsonObject response;    // JSON message from server
    private String id;              // Message id
    private String status;          // Status of response
    private String reason;          // Reason for status failure 
    
    // Initialize all the fields
    // Note: Only reasons with a failed status field have a reason field
    // If a response is not a failed response, reason will be null 
    public Response(JsonObject response, String id){
        this.response = response;
        this.id = id;
        this.status = response.getString("status", null);
        this.reason = response.getString("reason", null);
    }

    // Getters for all the fields 
    @Override
    public String getID() {
        return id;
    }

    public String getStatus() {
        return status;
    }
    
    public String getReason(){
        return reason;
    }
}
