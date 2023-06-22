package uta.cse3310;

import java.io.IOException;
import java.net.InetAddress;
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

public class App extends WebSocketServer
{

    int id = 1; //ID set to assign first person to join

    public App(int port) throws UnknownHostException {
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
        System.out.println(
            conn.getRemoteSocketAddress().getAddress().getHostAddress() + " entered the room!");

        System.out.println("Client " + id + " joined");

        Gson gson = new Gson();
        Client c = new Client(id);

        conn.send(gson.toJson(c)); //Send the client id in json
        System.out.println(gson.toJson(c).toString());

        id++;
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        broadcast(conn + " has left the room!");
        System.out.println(conn + " has left the room!");
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        System.out.println(conn + ": " + message);
    }

    @Override
    public void onMessage(WebSocket conn, ByteBuffer message) {
        System.out.println(conn + ": " + message);
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        ex.printStackTrace();
        if (conn != null) {
            System.out.println("Port Binding Error!");
        }
    }

    @Override
    public void onStart() {
        System.out.println("WebSocket server started!");
        setConnectionLostTimeout(0);
        setConnectionLostTimeout(100);
    }

    public static void main( String[] args ) throws IOException {
        //Setup the http server
        int port = 8080;
        httpServer h = new httpServer(port, "webchat/html/index.html");
        h.start();
        System.out.println("HTTPServer started on port: " + port);

        //Create and start websocket server
        port = 8081;
        App a = new App(port);
        a.start();
        System.out.println("WebSocket Server started on port: " + port);

        String systemName = InetAddress.getLocalHost().getHostAddress();
        System.out.println("\nVisit ws://" + systemName + ":8081");
    }
}
