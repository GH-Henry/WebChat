package uta.cse3310;

public class App {
    public static void main(String[] args) {
        int HTTPport;
        int WSport;

        try {
            HTTPport = Integer.parseInt(args[0]); // HTTP port from first arg
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("No port specified, defaulting to HTTP server on port 8080");
            HTTPport = 8080;
        } catch (NumberFormatException e) {
            System.out.println("Couln't parse port from input, defaulting to HTTP server on port 8080");
            HTTPport = 8080;
        }

        WSport = HTTPport + 1;

        try {
            // Setup the http server
            HttpServerImplementation h = new HttpServerImplementation(HTTPport, "webchat/html");
            h.start();
            System.out.println("Http Server started on port: " + HTTPport);

            // Create and start websocket server
            WS a = new WS(WSport);
            a.start();
            System.out.println("WebSocket Server started on port: " + WSport);

            // Create and write to Log.txt
            System.out.println("Message history will be saved to: Log.txt");
        }
        catch (Exception e) {
            System.out.println("Failed to start. Due to " + e.getCause());
            return;
        }
    }
}
