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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import org.java_websocket.WebSocket;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
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
        if (!(conn.getAttachment() instanceof Account)) {
            return;
        }
        Account account = conn.getAttachment();

        String str = "User: " + account + " has left the room!";
        System.out.println(str);
        log.writeToLog(str);

        update_last_active(account.getAccountUUID());
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

        switch (type) {
            case "typing_status":
                typing_status_request(conn);
                break;
            case "msg":
                msg_request(conn, json.get("content").getAsString());
                break;

            case "cmd":
                command_request(conn, json.get("content").getAsString());
                break;

            case "login_request":
                System.out.println("Creating login request with user: ("
                        + json.get("username").getAsString()
                        + ") and pwd: ("
                        + json.get("password").getAsString() + ")"); // RM

                log.writeToLog("Creating login request with user: ("
                        + json.get("username").getAsString()
                        + ") and pwd: ("
                        + json.get("password").getAsString() + ")");

                login_request(
                        conn,
                        json.get("username").getAsString(),
                        json.get("password").getAsString());
                break;

            case "signup_request":
                signup_request(
                        conn,
                        json.get("username").getAsString(),
                        json.get("password").getAsString());
                break;

            case "logout_request":
                process_logout(conn);
                break;

            default:
                sendError(conn, "invalid request type");
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
        String url = "jdbc:sqlite:./db/test.db";
        db = DriverManager.getConnection(url);
    }

    static void resetDB() throws SQLException {
        db = null;
        File f = new File("./db/test.db");
        if (f.exists()) {
            System.out.println("Deleted db at: " + f.getAbsolutePath());
            f.delete();
        }
        connectDB();
        createTable();
    }

    public static void createTable() throws SQLException {
        Statement statement = db.createStatement();
        statement.execute("PRAGMA foreign_keys = ON;");
        statement.close();

        String sql = "Create TABLE Account (\n"
                + "     uuid INTEGER PRIMARY KEY,"
                + "     username TEXT NOT NULL CHECK (length(username) > 2 AND length(username) < 31),\n"
                + "     password TEXT NOT NULL CHECK (length(password) > 2 AND length(password) < 31),\n"
                + "     last_active TEXT NOT NULL"
                + ");";

        statement = db.createStatement();
        statement.executeUpdate(sql);
        statement.close();

        sql = "Create TABLE IgnoreList (\n"
                + "     ignorer_uuid INTEGER NOT NULL,"
                + "     ignoree_uuid INTEGER NOT NULL,"
                + "     PRIMARY KEY(ignorer_uuid, ignoree_uuid),"
                + "     FOREIGN KEY(ignorer_uuid) REFERENCES Account(uuid),"
                + "     FOREIGN KEY(ignoree_uuid) REFERENCES Account(uuid)"
                + ");";
        statement = db.createStatement();
        statement.executeUpdate(sql);
    }

    // Prints all accounts in db
    private void selectAccounts() throws SQLException {
        String sql = "SELECT * FROM Account;";
        Statement statement = db.createStatement();
        ResultSet rs = statement.executeQuery(sql);

        System.out.println("Accounts:\n");
        while (rs.next()) {
            String uuid = rs.getString("uuid");
            String username = rs.getString("username");
            String password = rs.getString("password");
            System.out.println("uuid: " + uuid);
            System.out.println("username: " + username);
            System.out.println("password: " + password + "\n");
        }
    }

    // Validate login request
    private void login_request(WebSocket conn, String username, String password) {
        String sql = "SELECT * FROM Account WHERE username = ?;";

        try {
            PreparedStatement statement = db.prepareStatement(sql);
            statement.setString(1, username);
            ResultSet rs = statement.executeQuery();

            int uuid = rs.getInt("uuid");
            String user = rs.getString("username");
            if (user == null)
                throw new SQLException(); // Username is null if none matching found

            Account account = new Account(uuid, user, rs.getString("password"));
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
        String sql = "SELECT COUNT(*) FROM Account WHERE username = ?;";

        try {
            PreparedStatement statement = db.prepareStatement(sql);
            statement.setString(1, username);
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
        String sql = "INSERT INTO Account(username,password,last_active) VALUES(?,?,?);";

        SimpleDateFormat dateFormat = new SimpleDateFormat("E MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
        String dateString = dateFormat.format(new Date());

        try {
            PreparedStatement statement = db.prepareStatement(sql);
            statement.setString(1, username);
            statement.setString(2, password);
            statement.setString(3, dateString);
            statement.executeUpdate();
            statement.close();

            int uuid = get_uuid_from_username(username);

            process_login(conn, new Account(uuid, username, password));
        } catch (Exception e) {
            System.out.println("App.createAccount() error");
            e.printStackTrace();
            sendError(conn, "signup_failure");
        }
    }

    // If active user group implemented later, will need to remove them here when
    // they logout
    private void process_logout(WebSocket conn) {
        if (!(conn.getAttachment() instanceof Account)) {
            return;
        }
        Account user = conn.getAttachment();
        update_last_active(user.getAccountUUID());
        conn.setAttachment(null);
    }

    // Associate account credentials with their websocket connection, and update
    // client
    private void process_login(WebSocket conn, Account account) {
        EventMessage msg = new EventMessage("login_success", account);
        Gson gson = new Gson();
        conn.setAttachment(account); // Set attachment to associate connection with its credentials
        conn.send(gson.toJson(msg));

        ignore_list_ids(conn);
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
        json.addProperty("from_id", account.getAccountUUID());
        json.addProperty("content", content);
        broadcast(json.toString());

        update_last_active(account.getAccountUUID());
    }

    private void typing_status_request(WebSocket conn) {
        if (!(conn.getAttachment() instanceof Account))
            return;
        Account account = conn.getAttachment();
        JsonObject json = new JsonObject();
        json.addProperty("type", "typing_status");
        json.addProperty("from", account.getAccountName());
        json.addProperty("from_id", account.getAccountUUID());
        broadcast(json.toString());
    }

    private void command_request(WebSocket conn, String content) {
        if (!(conn.getAttachment() instanceof Account)) {
            sendError(conn, "invalid_request");
            return;
        }
        update_last_active(((Account) conn.getAttachment()).getAccountUUID());
        System.out.println("Received command: " + content); // Rm

        String[] words = content.split(" ");

        switch (words[0]) {
            case "ignore":
                ignore_request(conn, words);
                break;
            case "activity":
                get_user_activity(conn);
                break;
            default:
                sendError(conn, "Unrecognized command: " + words[0]);
        }

    }

    private void ignore_request(WebSocket conn, String[] words) {
        try {
            switch (words[1]) {
                case "add":
                    ignore_add(conn, words);
                    ignore_list_ids(conn);
                    break;
                case "remove":
                    ignore_remove(conn, words);
                    ignore_list_ids(conn);
                    break;
                case "list":
                    ignore_list_names(conn);
                    break;
                default:
                    System.out.println("Unsupported command arg: " + words[1]);
                    sendError(conn, "Unsupported command arg: " + words[1]);
            }
        } catch (Exception e) {
            sendError(conn, "Invalid ignore request");
        }
    }

    private void ignore_add(WebSocket conn, String[] words) {
        Account user = (Account) conn.getAttachment();
        int ignoree_uuid = 0;

        // Attempt to add as many users to ignore list as there are listed
        for (int i = 2; i < words.length; i++) {
            try {
                ignoree_uuid = get_uuid_from_username(words[i]);
            } catch (SQLException e) {
                sendError(conn, "Can't ignore non-existent user. " + e.getMessage());
                continue;
            }

            if (ignoree_uuid == user.getAccountUUID()) {
                sendError(conn, "Cannot ignore yourself");
                continue;
            }

            String sql = "INSERT INTO IgnoreList(ignorer_uuid,ignoree_uuid) VALUES(?,?);";

            try {
                PreparedStatement statement = db.prepareStatement(sql);
                statement.setInt(1, user.getAccountUUID());
                statement.setInt(2, ignoree_uuid);
                statement.executeUpdate();
                statement.close();
            } catch (SQLException e) {
                // Ignore list ID pair already exists
                sendError(conn, words[i] + " already ignored");
            }
        }
    }

    private void ignore_remove(WebSocket conn, String[] words) {
        Account user = (Account) conn.getAttachment();
        int ignoree_uuid = 0;

        // Attempt to add as many users to ignore list as there are listed
        for (int i = 2; i < words.length; i++) {
            try {
                ignoree_uuid = get_uuid_from_username(words[i]);
            } catch (SQLException e) {
                sendError(conn, "Can't ignore non-existent user. " + e.getMessage());
            }

            if (ignoree_uuid == user.getAccountUUID()) {
                sendError(conn, "Cannot ignore yourself");
                continue;
            }

            String sql = "DELETE FROM IgnoreList WHERE ignorer_uuid = ? AND ignoree_uuid = ?;";

            try {
                PreparedStatement statement = db.prepareStatement(sql);
                statement.setInt(1, user.getAccountUUID());
                statement.setInt(2, ignoree_uuid);
                statement.executeUpdate();
                statement.close();
                System.out.println("Success for del ignore pair (User: " + user.getAccountUUID() + " Ignoree: "
                        + ignoree_uuid + ")");
            } catch (SQLException e) {
                // Ignore list ID pair already exists
                System.out.println(
                        "Error on del ignore pair (User: " + user.getAccountUUID() + " Ignoree: " + ignoree_uuid + ")");
                sendError(conn, words[i] + " wasn't on ignore list");
            }
        }
    }

    private void ignore_list_names(WebSocket conn) {
        Account user = (Account) conn.getAttachment();
        String sql = "SELECT a.username FROM Account a "
                + "JOIN IgnoreList il ON a.uuid = il.ignoree_uuid "
                + "WHERE il.ignorer_uuid = ?;";
        try {
            PreparedStatement statement = db.prepareStatement(sql);
            statement.setInt(1, user.getAccountUUID());
            ResultSet rs = statement.executeQuery();

            JsonArray jsonArray = new JsonArray();
            while (rs.next()) {
                jsonArray.add(rs.getString("username"));
            }
            rs.close();
            statement.close();

            JsonObject json = new JsonObject();
            json.addProperty("type", "ignore_list_names");
            json.add("names", jsonArray);
            conn.send(json.toString());

        } catch (SQLException e) {
            sendError(conn, "Couldn't retrieve ignore list names");
        }
    }

    private void ignore_list_ids(WebSocket conn) {
        Account user = (Account) conn.getAttachment();
        String sql = "SELECT il.ignoree_uuid FROM IgnoreList il "
                + "WHERE il.ignorer_uuid = ?;";
        try {
            PreparedStatement statement = db.prepareStatement(sql);
            statement.setInt(1, user.getAccountUUID());
            ResultSet rs = statement.executeQuery();

            JsonArray jsonArray = new JsonArray();
            while (rs.next()) {
                jsonArray.add(rs.getInt("ignoree_uuid"));
            }

            rs.close();
            statement.close();

            JsonObject json = new JsonObject();
            json.addProperty("type", "ignore_list_ids");
            json.add("ids", jsonArray);
            conn.send(json.toString());

        } catch (SQLException e) {
            sendError(conn, "Couldn't retrieve ignore list ids");
        }
    }

    private int get_uuid_from_username(String username) throws SQLException {
        String sql = "SELECT uuid FROM Account WHERE username = ?;";
        PreparedStatement statement = db.prepareStatement(sql);
        statement.setString(1, username);
        ResultSet rs = statement.executeQuery();
        
        //Need this, since rs.getInt will return zero when no touple exists for the username
        if(!rs.next()) throw new SQLException("User " + username + " doesn't exist");
        
        int uuid = rs.getInt("uuid");
        System.out.println("Got id: " + uuid + " for user: " + username);

        rs.close();
        statement.close();

        return uuid;
    }

    private void update_last_active(int uuid) {
        String sql = "UPDATE Account SET last_active = ? WHERE uuid = ?;";

        try {
            PreparedStatement statement = db.prepareStatement(sql);
            statement.setString(1, new Date().toString());
            statement.setInt(2, uuid);
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            System.out.println("Error saving last_active time for uuid: " + uuid);
        }
    }

    private void get_user_activity(WebSocket conn) {
        Date curr_date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("E MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);

        String sql = "SELECT username, last_active FROM Account;";

        try {
            PreparedStatement statement = db.prepareStatement(sql);
            ResultSet rs = statement.executeQuery();

            StringBuilder sb = new StringBuilder();
            sb.append("User:   Last active (d:h:m:s):\n");

            while (rs.next()) {
                sb.append(rs.getString("username") + "   ");
                String date = rs.getString("last_active");
                System.out.println("Got date: " + date);
                Date last_active = dateFormat.parse(date);

                long diff = curr_date.getTime() - last_active.getTime();

                long days = TimeUnit.MILLISECONDS.toDays(diff);
                long hrs = TimeUnit.MILLISECONDS.toHours(diff) % 24;
                long mins = TimeUnit.MILLISECONDS.toMinutes(diff) % 60;
                long secs = TimeUnit.MILLISECONDS.toSeconds(diff) % 60;
                sb.append(String.format("%d:%02d:%02d:%02d\n", days, hrs, mins, secs));
                System.out.println(days + ":" + hrs + ":" + mins + ":" + secs);
            }

            rs.close();
            statement.close();

            JsonObject json = new JsonObject();
            json.addProperty("type", "all_user_activity");
            json.addProperty("activity", sb.toString());
            conn.send(json.toString());

        } catch (SQLException e) {
            System.out.println("Error in getting user activity:" + e.getMessage());
            sendError(conn, "Couldn't get user activity");
        } catch (ParseException e) {
            System.out.println("Error parsing date: " + e.getMessage());
            sendError(conn, "Error parsing date");
        }
    }
}