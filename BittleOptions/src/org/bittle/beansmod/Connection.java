package org.bittle.beansmod;

import com.eclipsesource.json.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Future;
import org.bittle.messages.*;
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

@WebSocket
public class Connection {
 
    //singleton
    private static final Connection instance;
    //public static JsonObject response = null;
    
    private static final List<MessageListener> listeners = new ArrayList<>();
    
    static {
        instance = new Connection();
    }
    
    private Connection() { }
    
    public static Connection getInstance() {
        return instance;
    }
    
    private static final Logger LOG = Log.getLogger(Connection.class);
    private static Session session = null;
    
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
        
        //NotifyDescriptor nd = new NotifyDescriptor.Message(msg, NotifyDescriptor.INFORMATION_MESSAGE);
        //DialogDisplayer.getDefault().notify(nd);
        
        // Parse the message as a JSON object
        JsonObject jsonMessage = Json.parse(msg).asObject();
        
        // Get the 'id' field of the JSON object 
        String id = jsonMessage.getString("id", null);
        
        // The response that will be fired off to the listeners
        Message response;
        
        //System.out.println("============id is " + id + "=============");
        
        //big switch statement goes here based on the response's id
        // Do we really need a switch statement here?
        // We can just send the JSON response to whoever is expecting it 
        switch(id){
            case "register":
            case "login":
            case "logout":
            case "track":
            case "changePass":
                response = new Response(jsonMessage, id);
                break;
            case "invite":
                // Invite messages will either be a response or an invitation
                // If there is a status in the JSON, it is a response
                if(jsonMessage.getString("status", null) != null)
                    response = new Response(jsonMessage, id);
                // Otherwise send the invitation
                else
                    response = new Invitation(jsonMessage, id);
                break;
            case "addFile":
                // May need to request new file here
                response = new Update(jsonMessage, id);
            case "addClient":
                response = new shareSession(jsonMessage, id);
            case "lines":
            case "line":
            default:
                response = null;
                break;
        }
        fireMessage(response);
        
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
        if(session != null)
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
    //public boolean checkResponse(String id){        
    //}
    
    private void sendMessage(String message) {
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
    
    public void accept(String username, int shareID){
        JsonObject accept = Json.object().add("id", "accept").add("blame", username).add("shareId", shareID);
        sendMessage(accept.toString());
    }
    
    public void decline(String username, int shareID){
        JsonObject decline = Json.object().add("id", "decline").add("blame", username).add("shareId", shareID);
        sendMessage(decline.toString());
    }

    public void addMessageListener(MessageListener l) {
        listeners.add(l);
    }
    
    public void removeMessageListner(MessageListener l){
        listeners.remove(l);
    }

    private void fireMessage(Message m) {
        Iterator listenerIterator = listeners.iterator();
        while(listenerIterator.hasNext()){
            ((MessageListener)listenerIterator.next()).messageRecieved(m);
        }
    }

}