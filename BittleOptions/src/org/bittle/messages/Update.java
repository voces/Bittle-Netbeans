/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bittle.messages;

import com.eclipsesource.json.*;

/**
 *
 * @author chmar
 */
public class Update implements Message{
    
    private JsonObject update;
    private String id;
    private String blame;
    private String change;
    
    public Update(JsonObject update, String id){
        this.update = update;
        this.id = id;
        this.blame = update.getString("blame", null);
    }

    @Override
    public String getID() {
        return id;
    }
    
    public String getBlame(){
        return blame;
    }
    
    public String getChange(String changeType){
        return update.getString(changeType, null);
    }
    
}
