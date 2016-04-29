/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bittle.messages;

import com.eclipsesource.json.*;
import java.util.List;

/**
 *
 * @author chmar
 */
public class shareSession implements Message{
    
    private JsonObject response;
    private String id;
    private List<JsonValue> files;
    private List<JsonValue> names;
    
    public shareSession(JsonObject response, String id){
        this.response = response;
        this.id = id;
        this.files = response.get("files").asArray().values();
        this.names = response.get("names").asArray().values();
    }

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
    
}
