package org.bittle.beansmod;

import com.eclipsesource.json.*;
import java.net.URI;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.Exceptions;

@WebSocket
public class Connection {
    
    //singleton
    private static final Connection instance;
    public static JsonObject response = null;
    
    static {
        instance = new Connection();
    }
    
    private Connection() { }
    
    public static Connection getInstance() {
        return instance;
    }
    
    private static final Logger LOG = Log.getLogger(Connection.class);
    private static Session session;
    
    @OnWebSocketConnect
    public void onConnect(Session sess)
    {
        LOG.info("onConnect({})",sess);
    }

    @OnWebSocketClose
    public void onClose(int statusCode, String reason)
    {
        LOG.info("onClose({}, {})", statusCode, reason);
    }

    @OnWebSocketError
    public void onError(Throwable cause)
    {
        LOG.warn(cause);
    }

    @OnWebSocketMessage
    public void onMessage(String msg)
    {
        LOG.info("onMessage() - {}", msg);
        
        System.out.println("============onMessage called!==========");
        System.out.println(msg);
        System.out.println("=======================================");
        // Parse the message as a JSON object
        JsonObject jsonMessage = Json.parse(msg).asObject();
        
        // Get the 'id' field of the JSON object 
        String id = jsonMessage.getString("id", null);
        
        //System.out.println("============id is " + id + "=============");
        
        //big switch statement goes here based on the response's id
        // Do we really need a switch statement here?
        // We can just send the JSON response to whoever is expecting it 
        switch(id.toLowerCase()){
            case "register":
            case "login":
            case "logout":
            case "track":
            case "invite":
                response = jsonMessage;
                break;
            case "addfile":
            case "lines":
            case "line":
                // absorb response for now
                break;
            default:
                // is this even possible?
                break;
        }
        
        //on the message responding to an editor change, check awaitingServerResponseForEdit flag; true = don't update, false = update
        //not the best solution because this client won't get any editor updates until getting a response from the server?
        //might be able to get around this if the server's messages for line changes have something that tells us an update is from another client  
    }
    
    public void connect(String url) {
//        String url = "wss://notextures.io:8086";

        SslContextFactory sslContextFactory = new SslContextFactory();
        sslContextFactory.setTrustAll(true); // Good for testing, but make sure to change later

        WebSocketClient client = new WebSocketClient(sslContextFactory);
        try
        {
            client.start();
            Connection socket = new Connection();
            Future<Session> fut = client.connect(socket,URI.create(url));
            session = fut.get();
//            session.getRemote().sendString("{\"id\": \"clean\"}");
        }
        catch (Throwable t)
        {
            LOG.warn(t);
        }
    }
    
    public void close() {
        session.close();
    }
    
    /**
     * Parses a response from the server 
     * If the id matches the given id, check the status
     * If the status is failed, displays the reason for failure
     * @param id the kind of response being checked
     * @return true if the response matched the given id and didn't fail, 
     * false otherwise 
     */
    public boolean checkResponse(String id){        
        if(response.getString("id", null).equals(id)){
            if(response.getString("status", null).equals("failed")){
                NotifyDescriptor nd = new NotifyDescriptor.Message(response.getString("reason",  id + " failed"), NotifyDescriptor.ERROR_MESSAGE);
                DialogDisplayer.getDefault().notify(nd);
            }
            else
                return true;
        }
        
        return false;
    }
    
    private void sendMessage(String message) {
        response = null;
        try {
            session.getRemote().sendString(message);
        }
        catch (Throwable t)
        {
            LOG.warn(t);
        }
    }
    
    public void register(String name, String pass) {
        sendMessage("{\"id\":\"register\", \"name\":\"" + name + "\", \"pass\":\"" + pass + "\"}");
    }
    
    public void login(String name, String pass) {
        sendMessage("{\"id\":\"login\", \"name\":\"" + name + "\", \"pass\":\"" + pass + "\"}");
    }
    
    public void logout() {
        sendMessage("{\"id\":\"logout\"}");
    }
    
    public void changePass(String name, String pass, String newPass) {
        sendMessage("{\"id\":\"changePass\", \"name\":\"" + name + "\", \"pass\":\"" + pass + "\", \"newPass\":\"" + newPass + "\"}");
    }
    
    public void changeEmail(String name, String pass, String newEmail) {
        sendMessage("{\"id\":\"changeEmail\", \"name\":\"" + name + "\", \"pass\":\"" + pass + "\", \"newEmail\":\"" + newEmail + "\"}");
    }
    
    public void createRepo(String name) {
        sendMessage("{\"id\":\"createRepo\", \"name\":\"" + name + "\"}");
    }
    
    public void setPermission(String repo, String user, String role) {
        sendMessage("{\"id\":\"setPermission\", \"repo\":\"" + repo + "\", \"user\":\"" + user + "\", \"role\":\"" + role + "\"}");
    }
    
    public void deletePermission(String repo, String user) {
        sendMessage("{\"id\":\"deletePermission\", \"repo\":\"" + repo + "\", \"user\":\"" + user + "\"}");
    }
    
    public void createFile(String repo, String file) {
        sendMessage("{\"id\":\"createFile\", \"repo\":\"" + repo + "\", \"file\":\"" + file + "\"}");
    }
    
    public void moveFile(String repo, String file, String newPath) {
        sendMessage("{\"id\":\"moveFile\", \"repo\":\"" + repo + "\", \"file\":\"" + file + "\", \"newPath\":\"" + newPath + "\"}");
    }
    
    public void deleteFile(String repo, String file) {
        sendMessage("{\"id\":\"deleteFile\", \"repo\":\"" + repo + "\", \"file\":\"" + file + "\"}");
    }
    
    public void createDirectory(String repo, String directory) {
        sendMessage("{\"id\":\"createDirectory\", \"repo\":\"" + repo + "\", \"directory\":\"" + directory + "\"}");
    }
    
    public void moveDirectory(String repo, String directory, String newPath) {
        sendMessage("{\"id\":\"moveDirectory\", \"repo\":\"" + repo + "\", \"directory\":\"" + directory + "\", \"newPath\":\"" + newPath + "\"}");
    }
    
    public void deleteDirectory(String repo, String directory) {
        sendMessage("{\"id\":\"deleteDirectory\", \"repo\":\"" + repo + "\", \"directory\":\"" + directory + "\"}");
    }
    
    public void insert(String repo, String file, String lineId, String col, String data) {
        sendMessage("{\"id\":\"insert\", \"repo\":\"" + repo + "\", \"file\":\"" + file + "\", \"lineId\":\"" + lineId + "\", \"col\": \"" + col + "\", \"data\":\"" + data + "\"}");
    }
    
    public void getLine(String repo, String file, String lineId) {
        sendMessage("{\"id\":\"getLine\", \"repo\":\"" + repo + "\", \"file\":\"" + file + "\", \"lineId\":\"" + lineId + "\"}");
    }
    
    public void erase(String repo, String file, String lineId, String col, String count) {
        sendMessage("{\"id\":\"erase\", \"repo\":\"" + repo + "\", \"file\":\"" + file + "\", \"lineId\":\"" + lineId + "\", \"col\": \"" + col + "\", \"count\":\"" + count + "\"}");
    }
    
    public void split(String repo, String file, String lineId, String col, String newLineId) {
        sendMessage("{\"id\":\"insert\", \"repo\":\"" + repo + "\", \"file\":\"" + file + "\", \"lineId\":\"" + lineId + "\", \"col\": \"" + col + "\", \"newLineId\":\"" + newLineId + "\"}");
    }
    
    public void merge(String repo, String file, String lineId) {
        sendMessage("{\"id\":\"merge\", \"repo\":\"" + repo + "\", \"file\":\"" + file + "\", \"lineId\":\"" + lineId + "\"}");
    }
    
    public void getFile(String repo, String file) {
        sendMessage("{\"id\":\"getFile\", \"repo\":\"" + repo + "\", \"file\":\"" + file + "\"}");
    }
    
    public void clean() {
        sendMessage("{\"id\":\"clean\"}");
    }
    
    //update: 4/23/16
    public void lines(String filename, int start, int deleteCount, String lines) {
        sendMessage("{\"id\":\"lines\", \"filename\":\"" + filename + "\", \"start\":" + start + ", \"deleteCount\":" + deleteCount + ", \"lines\": " + lines + "}");
    }
    
    public void line(String filename, int lineIndex, int start, int deleteCount, String line) {
        sendMessage("{\"id\":\"line\", \"filename\":\"" + filename + "\", \"lineIndex\":" + lineIndex + ", \"start\":" + start + ", \"deleteCount\": " + deleteCount +  ", \"line\":" + line + "}");
    }
    
    //update: 4/27/16
    public void track(String filename, String[] lines){
        JsonObject file = Json.object().add("id", "track").add("filename", filename);
        JsonArray linesArray = Json.array(lines);
        file.add("lines", linesArray);
        
        sendMessage(file.toString());
    }
    
    public void invite(String username){
        JsonObject invitation = Json.object().add("id", "invite").add("name", username);
        sendMessage(invitation.toString());
    }
}