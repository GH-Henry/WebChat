package uta.cse3310;

import java.io.File;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;

import org.java_websocket.WebSocket;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class WS extends WebSocketServer {
    private static Connection db = null;
    Log log = new Log();

    public WS(int port) throws UnknownHostException {
        super(new InetSocketAddress(port));
    }

    public WS(InetSocketAddress address) {
        super(address);
    }

    public WS(int port, Draft_6455 draft) {
        super(new InetSocketAddress(port), Collections.<Draft>singletonList(draft));
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        String ip = conn.getRemoteSocketAddress().getAddress().getHostAddress();
        System.out.println("Host address: " + ip + " entered the room!");
        log.writeToLog("Host address: " + ip + " entered the room!");
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        Account account = conn.getAttachment();

        String str = "User: " + account + " has left the room!";
        System.out.println(str);
        log.writeToLog(str);
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        System.out.println("\nReceived message: " + message + "\n"); // RM
        log.writeToLog("Received message: " + message);

        Gson gson = new Gson();
        JsonObject json = gson.fromJson(message, JsonObject.class);
        String type = json.get("type").getAsString();

        System.out.println("Received msg of type: " + type); // RM
        log.writeToLog("Received msg of type: " + type);

        if (type.equals("typing_status")) {
            typing_status_request(conn);
        } else if (type.equals("msg")) {
            msg_request(
                    conn,
                    json.get("content").getAsString());
        } else if (type.equals("login_request")) {

            // RM
            System.out.println("Creating login request with user: ("
                    + json.get("username").getAsString()
                    + ") and pwd: ("
                    + json.get("password").getAsString() + ")");

            log.writeToLog("Creating login request with user: ("
                    + json.get("username").getAsString()
                    + ") and pwd: ("
                    + json.get("password").getAsString() + ")");

            login_request(
                    conn,
                    json.get("username").getAsString(),
                    json.get("password").getAsString());

        } else if (type.equals("signup_request")) {
            signup_request(
                    conn,
                    json.get("username").getAsString(),
                    json.get("password").getAsString());
        }
    }

    @Override
    public void onMessage(WebSocket conn, ByteBuffer message) {
        System.out.println(conn + ": " + message);
        log.writeToLog(conn + ": " + message);
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        System.out.println("\nWebsocket connection (" + conn + ") encountered exception: " + ex + "\n");
        log.writeToLog("Websocket connection (" + conn + ") encountered exception: " + ex);
        ex.printStackTrace();
        if (conn != null) {
            // some errors like port binding failed may not be assignable to a specific
            // websocket
            System.out.println("Port Binding Failed!");
            log.writeToLog("Port Binding Failed!");
        }
    }

    @Override
    public void onStart() {
        // reseting Database on server reset and recreating Log.txt
        try {
            resetDB();
            log.createLog();
        } catch (SQLException e) {
            System.out.println("SQL Database not able to be reset!");
            e.printStackTrace();
        }

        System.out.println("WebSocket server started!");
        setConnectionLostTimeout(0);
        setConnectionLostTimeout(100);
    }

    private static void connectDB() throws SQLException {
        String url = "jdbc:sqlite:webchat/db/test.db";
        db = DriverManager.getConnection(url);
    }

    static void resetDB() throws SQLException {
        db = null;
        File f = new File("webchat/db/test.db");
        if (f.exists()) {
            System.out.println("Deleted db at: " + f.getAbsolutePath());
            f.delete();
        }
        connectDB();
        createTable();
    }

    public static void createTable() throws SQLException {
        String sqlString = "Create TABLE Account (\n"
                + "     username TEXT NOT NULL CHECK (length(username) > 2 AND length(username) < 31),\n"
                + "     password TEXT NOT NULL CHECK (length(password) > 2 AND length(password) < 31),\n"
                + "     PRIMARY KEY(username)\n"
                + ");";

        Statement statement = db.createStatement();
        statement.executeUpdate(sqlString);
        statement.close();
        // System.out.println("Created sqlite table: \n" + sqlString); //RM
    }

    // Prints all accounts in db
    private void selectAccounts() throws SQLException {
        String sqlString = "SELECT * FROM Account;";
        Statement statement = db.createStatement();
        ResultSet rs = statement.executeQuery(sqlString);

        System.out.println("Accounts:\n");
        while (rs.next()) {
            String username = rs.getString("username");
            String password = rs.getString("password");
            System.out.println("username: " + username);
            System.out.println("password: " + password + "\n");
        }
    }

    // Validate login request
    private void login_request(WebSocket conn, String username, String password) {
        String sql = "SELECT * FROM Account WHERE username = ?";

        try {
            PreparedStatement statement = db.prepareStatement(sql);
            statement.setString(1, username);
            ResultSet rs = statement.executeQuery();

            String user = rs.getString("username");
            if (user == null)
                throw new SQLException(); // Username is null if none matching found

            Account account = new Account(user, rs.getString("password"));
            rs.close();
            statement.close();

            if (account.getAccountPassword().equals(password))
                process_login(conn, account);
            else
                sendError(conn, "invalid_password");
        } catch (SQLException e) {
            sendError(conn, "user_not_found");
        } catch (Exception e) { // General error handling
            sendError(conn, "login_failure");
        }
    }

    // Validate signup request
    private void signup_request(WebSocket conn, String username, String password) {
        String sql = "SELECT COUNT(*) FROM Account WHERE username = ?";

        try {
            PreparedStatement statement = db.prepareStatement(sql);
            ResultSet rs = statement.executeQuery();

            int count = rs.getInt(1);
            rs.close();
            statement.close();

            if (count > 0)
                sendError(conn, "duplicate_username");
            else
                createAccount(conn, username, password);

        } catch (SQLException e) {
            System.out.println("App.signup_request() error");
            e.printStackTrace();
            sendError(conn, "signup_failure");
        }
    }

    // Insert new account to db
    private void createAccount(WebSocket conn, String username, String password) {
        String sql = "INSERT INTO Account(username,password) VALUES(?,?)";

        try {
            PreparedStatement statement = db.prepareStatement(sql);
            statement.setString(1, username);
            statement.setString(2, password);
            statement.executeUpdate();

            // System.out.println("Added account, now:"); //RM
            // selectAccounts(); //RM

            process_login(conn, new Account(username, password));
        } catch (Exception e) {
            System.out.println("App.createAccount() error");
            e.printStackTrace();
            sendError(conn, "signup_failure");
        }
    }

    // Associate account credentials with their websocket connection, and update
    // client
    private void process_login(WebSocket conn, Account account) {
        EventMessage msg = new EventMessage("login_success", account);
        Gson gson = new Gson();
        conn.setAttachment(account); // Set attachment to associate connection with its credentials
        conn.send(gson.toJson(msg));
    }

    // Send client error related to message received from them
    private void sendError(WebSocket conn, String error_name) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "error");
        json.addProperty("error_type", error_name);
        conn.send(json.toString());
    }

    private void msg_request(WebSocket conn, String content) {
        if (!(conn.getAttachment() instanceof Account)) {
            System.out.println("App.msg_request() error: Invalid message request, from non logged in user");
            sendError(conn, "invalid_request");
            return;
        }
        Account account = conn.getAttachment();
        JsonObject json = new JsonObject();
        json.addProperty("type", "msg");
        json.addProperty("from", account.getAccountName());
        json.addProperty("content", content);
        broadcast(json.toString());
    }

    private void typing_status_request(WebSocket conn) {
        if (!(conn.getAttachment() instanceof Account))
            return;
        Account account = conn.getAttachment();
        JsonObject json = new JsonObject();
        json.addProperty("type", "typing_status");
        json.addProperty("from", account.getAccountName());
        broadcast(json.toString());
    }
}