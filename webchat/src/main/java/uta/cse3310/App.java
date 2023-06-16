package uta.cse3310;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Collections;

import org.java_websocket.WebSocket;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import uta.cse3310.HTTPServer;

/**
 * Hello world!
 *
 */
public class App extends WebSocketServer {
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
			conn.send("Welcome to the server!");	// This method sends a message to the client
			broadcast("new connection: " + handshake.getResourceDescriptor());	// This method sends a message to all clients connected
			System.out.println(conn.getRemoteSocketAddress().getAddress().getHostAddress() + " entered the room!");
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

    public static void main( String[] args ) throws InterruptedException, IOException {
			int httpPort = 8080;
			int port = 8081;

			// try {
			// 	port = Integer.parseInt(args[0]);
			// } catch (Exception ex) {

			// }

			HTTPServer h = new HTTPServer(httpPort, "./html");
			h.start();
			System.out.println("HTTPServer started on port: " + httpPort);

			App s = new App(port);
			s.start();
			System.out.println("ChatServer started on port: " + s.getPort());

			BufferedReader sysin = new BufferedReader(new InputStreamReader(System.in));
			while (true) {
				String in = sysin.readLine();
				s.broadcast(in);
				if (in.equals("exit")) {
					s.stop(1000);
					break;
				}
			}
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
}
