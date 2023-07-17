package uta.cse3310;

import java.net.UnknownHostException;
import java.util.Scanner;


public class App {
    public static void main( String[] args ) throws UnknownHostException {
        System.out.println("\nWelcome to Webchat!\n");
        Scanner sc = new Scanner(System.in);
        int port = 0;       
        
        //Setup the http server
        System.out.println("Enter HTTP Server port: ");
        port = sc.nextInt();
        HttpServerImplementation h = new HttpServerImplementation(port, "webchat/html/index.html");
        h.start();
        System.out.println("http Server started on port: " + port);


        //Create and start websocket server
        System.out.println("\nEnter WebSocket Server port: ");
        port = sc.nextInt();
        sc.close();
        WS a = new WS(port);
        a.start();
        System.out.println("WebSocket Server started on port: " + port);

        //Create and write to Log.txt
        System.out.println("Message history will be saved to: Log.txt");

        // a.selectAccounts(); //RM
    }
}
