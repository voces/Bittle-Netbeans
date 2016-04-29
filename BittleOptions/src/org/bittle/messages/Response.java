package org.bittle.messages;

import com.eclipsesource.json.*;

/**
 *
 * @author chmar
 */
public class Response implements Message{
    
    private JsonObject response;
    private String id;
    private String status;
    private String reason;
    
    public Response(JsonObject response, String id){
        this.response = response;
        this.id = id;
        this.status = response.getString("status", null);
        this.reason = response.getString("reason", null);
    }

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
