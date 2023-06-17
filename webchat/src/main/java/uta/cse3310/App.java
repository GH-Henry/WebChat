package uta.cse3310;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Collections;

import org.java_websocket.WebSocket;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;


public class App extends WebSocketServer
{

    public int client_id=0;

    public App(int port) throws UnknownHostException {  super(new InetSocketAddress(port));     }
    public App(InetSocketAddress address) {     super(address);     }
    public App(int port, Draft_6455 draft) {    super(new InetSocketAddress(port), Collections.<Draft>singletonList(draft));   }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        conn.send("Welcome to the server! Your client id: " + client_id++); //This method sends a message to the new client
        broadcast("new connection: " + handshake
            .getResourceDescriptor()); //This method sends a message to all clients connected
        System.out.println(
            conn.getRemoteSocketAddress().getAddress().getHostAddress() + " entered the room!");

        //Requirement #2, print new client id to stdout
        System.out.println("Client " + client_id + " joined"); 
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        broadcast(conn + " has left the room!");
        System.out.println(conn + " has left the room!");
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        broadcast(message);
        System.out.println(conn + ": " + message);
    }

    @Override
    public void onMessage(WebSocket conn, ByteBuffer message) {
        broadcast(message.array());
        System.out.println(conn + ": " + message);
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        ex.printStackTrace();
        if (conn != null) {
        // some errors like port binding failed may not be assignable to a specific websocket
        }
    }

    @Override
    public void onStart() {
        System.out.println("Server started!");
        setConnectionLostTimeout(0);
        setConnectionLostTimeout(100);
    }


    public static void main( String[] args ) throws IOException
    {
        //Setup the http server
        int port = 8080;

        //HttpServer H = new HttpServer(port, "./html");
        HTTPServer h = new HTTPServer(port);
        h.start();
        System.out.println("\n\nhttp Server started on port: " + port + "\n\n");

        //Create and start websocket server

        port = 8081;
        App a = new App(port);
        a.start();
        System.out.println("\n\nwebsocket Server started on port: " + port + "\n\n");
    }

    

}
