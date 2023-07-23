package uta.cse3310;

import java.net.UnknownHostException;


public class App {
    public static void main( String[] args ) throws UnknownHostException {
        System.out.println("\nWelcome to Webchat!\n");
        int HTTPport = Integer.parseInt(args[0]);
        int WSport = Integer.parseInt(args[1]);

        
        //Setup the http server
        System.out.println("Enter HTTP Server port: ");
        HttpServerImplementation h = new HttpServerImplementation(HTTPport, "webchat/html/index.html");
        h.start();
        System.out.println("http Server started on port: " + HTTPport);


        //Create and start websocket server
        System.out.println("\nEnter WebSocket Server port: ");
        WS a = new WS(WSport);
        a.start();
        System.out.println("WebSocket Server started on port: " + WSport);

        //Create and write to Log.txt
        System.out.println("Message history will be saved to: Log.txt");

        // a.selectAccounts(); //RM
    }
}
