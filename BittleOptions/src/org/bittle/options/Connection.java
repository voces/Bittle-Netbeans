package org.bittle.options;

import java.io.InputStream;
import java.io.OutputStream;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class Connection {
    
    private OutputStream outputStream;
    private InputStream inputStream;
    
    //might be worthwhile to put together a message/response structure
    
    public enum Message {
        REGISTER,
        LOGIN,
        CHANGEPASSWORD,
        CHANGEEMAIL,
        RESETPASSWORD,
        CREATEPROJECT,
        DELETEPROJECT,
        DELETEPROJECTCONFIRM,
        ADDPERMISSION,
        ADDPERMISSIONCONFIRM,
        REMOVEPERMISSION,
        CREATEBRANCH,
        DELETEBRANCH,
        EDITBRANCH,
        SYNCPROJECT,
        SYNCFILE,
        SYNCLINE
    }
    
    public void connect(String serverAddress, int serverPort) {
        try {
            SSLSocket socket = (SSLSocket)((SSLSocketFactory) SSLSocketFactory.getDefault()).createSocket(serverAddress, serverPort);
            outputStream = socket.getOutputStream();
            inputStream = socket.getInputStream(); //might block on initialization? if so just initialize at first use
        }
        catch(Exception e) {
            System.out.println(e);
        }
    }
    
    
    public void sendMessage(Message messageType, Object information) {
        switch(messageType) {
            case REGISTER:
                
                break;
            case LOGIN:
                
                break;
            case CHANGEPASSWORD:
                
                break;
            case CHANGEEMAIL:
                
                break;
            case RESETPASSWORD:
                
                break;
            case CREATEPROJECT:
                
                break;
            case DELETEPROJECT:
                
                break;
            case DELETEPROJECTCONFIRM:
                
                break;
            case ADDPERMISSION:
                
                break;
            case ADDPERMISSIONCONFIRM:
                
                break;
            case REMOVEPERMISSION:
                
                break;
            case CREATEBRANCH:
                
                break;
            case DELETEBRANCH:
                
                break;
            case EDITBRANCH:
                
                break;
            case SYNCPROJECT:
                
                break;
            case SYNCFILE:
                
                break;
            case SYNCLINE:
                
                break;
        }
        
    }
    
    public void receiveMessage() {
        //only if we can distinguish what a message is in response to,
        //otherwise read within the switch statement of sendMessage
        try {
            inputStream.read();
        }
        catch(Exception e) {
            System.out.println(e);
        }
    }
    
}
