package uta.cse3310;

import java.net.UnknownHostException;

public class App {
    public static void main(String[] args) throws UnknownHostException {
        if ((args[0].equals("8080")) && (args[1].equals("8081"))) {
            System.out.println("\nWelcome to Webchat!\n");

            int HTTPport = Integer.parseInt(args[0]); // HTTP port from first arg
            int WSport = Integer.parseInt(args[1]); // Websocket port from second arg

            // Setup the http server
            HttpServerImplementation h = new HttpServerImplementation(HTTPport, "webchat/html/index.html");
            h.start();
            System.out.println("http Server started on port: " + HTTPport);

            // Create and start websocket server
            WS a = new WS(WSport);
            a.start();
            System.out.println("WebSocket Server started on port: " + WSport);

            // Create and write to Log.txt
            System.out.println("Message history will be saved to: Log.txt");

            // a.selectAccounts(); //RM
        }

        else { // exiting program
            System.out.println("Please use ports: args[0] = 8080(HTTP) and args[1] = 8081(WebSocket)");
            System.out.println("\nExiting Program...\n");
            System.exit(-1);
        }
    }
}
