package uta.cse3310;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Collections;

import org.java_websocket.WebSocket;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class App extends WebSocketServer {
    
    public App(int port) {
        super(new InetSocketAddress(port));
    }

    public App(InetSocketAddress address) {
        super(address);
    }

    public App(int port, Draft_6455 draft) {
        super(new InetSocketAddress(port), Collections.<Draft>singletonList(draft));
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        System.out.println(conn.getRemoteSocketAddress().getAddress() + " connected");

    Gson gson = new Gson();
    String jsonString = "Hello"; //CHANGE
    broadcast(jsonString);
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        System.out.println(conn + " has closed");
    }   

    @Override
    public void onMessage(WebSocket conn, String message) {
        System.out.println(conn + ": " + message);

        // Date from the page from Player
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        Player player = gson.fromJson(message, Player.class);        
        System.out.println(player.getChips());

        //Obtain game object
        

        //Broadcast updates to players
    }

    @Override
    public void onMessage(WebSocket conn, ByteBuffer message) {
        System.out.println(conn + ": " + message);
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        ex.printStackTrace();
        if (conn != null) {
            System.out.println("Error: Port binding failed; may not be a specific websocket");
        }
    }

    @Override
    public void onStart() {
        System.out.println("Server started!");
        setConnectionLostTimeout(0);
    }
    public static void main(String[] args) {

        // Creating HTTP server
        int port = 8080;
        Server s = new Server(port, "webchat/html/index.html");
        s.start();
        System.out.println("HTTP Server stated on port: " + port);

        // Creating WebSocket Server
        port = 8081;
        App A = new App(port);
        A.start();
        System.out.println("WebSocket Server started on port: " + port);
    }
}
