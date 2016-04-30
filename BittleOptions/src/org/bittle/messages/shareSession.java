/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bittle.messages;

import com.eclipsesource.json.*;
import java.util.List;

/**
 * shareSession Message
 * This is used to give an invited client a new share session 
 * @author chmar
 */
public class shareSession implements Message{
    
    private JsonObject session;       // JSON message from server 
    private String id;                // Message ID
    private List<JsonValue> files;    // Files in shares session    
    private List<JsonValue> names;    // Users in share session 
    
    // Initialize all the fields 
    public shareSession(JsonObject session, String id){
        this.session = session;
        this.id = id;
        this.files = session.get("files").asArray().values();
        this.names = session.get("names").asArray().values();
    }

    // Getters for all the fields 
    @Override
    public String getID() {
        return id;
    }

    public List<JsonValue> getFiles() {
        return files;
    }
    
    public List<JsonValue> getNames(){
        return names;
    }
    

    @Override
    public void handle() {
        
    }
}
