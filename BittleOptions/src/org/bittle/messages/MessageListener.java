/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bittle.messages;

import java.util.EventListener;

/**
 *
 * @author chmar
 */
public interface MessageListener extends EventListener  {
    public void messageRecieved(Message m);
}
