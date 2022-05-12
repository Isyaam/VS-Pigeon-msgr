package de.htwsaar.vs.msgr.client;

import de.htwsaar.vs.msgr.client.helper.JavaDatabase;
import de.htwsaar.vs.msgr.client.rest.Group;
import de.htwsaar.vs.msgr.client.rest.Message;
import de.htwsaar.vs.msgr.client.rest.RestClient;
import de.htwsaar.vs.msgr.client.rest.User;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.util.ArrayList;

public class DataModel {

    private static final Logger log = LogManager.getLogger(DataModel.class);


    /* *************************************************************************
     * PROPERTIES & FIELDS
     * ************************************************************************/

    /** Represents the contact list's list. */
    public ObservableList<String> contactList =
            FXCollections.observableArrayList();

    /** Represents the chat's content. */
    public ObservableList<String> messageList =
            FXCollections.observableArrayList();

    /** Represents the UID of the current user. */
    private String ownUID;

    /** Represents the name of the current user. */
    private String ownName;

    /** Represents the password hash of the current user. */
    private String pwHash;

    /** Represents the ID (UID or GUID) of the selected chat. */
    private String chatID;

    /** Represents the name of the current chat. */
    private String chatName;

    /** Represents the UID of the current group's admin. */
    private String adminUID;

    /** Represents the name of the current group's admin. */
    private String adminName;

    /** Used to connect to the underlying SQL database. */
    private JavaDatabase _db;

    /** Used to communicate with the server. */
    private RestClient _client;

    /** Represents the online status of the user. */
    private BooleanProperty _isUserOnline = new SimpleBooleanProperty(false);


    /* *************************************************************************
     * THREADS
     * ************************************************************************/

    /** Checks continuously the server for new messages. */
    private Thread checkMessagesThread = new Thread(() -> {

        while (true) {
            try {
                Thread.sleep(5000);
                Message[] messages = _client.retrieveNewMessages();
                _isUserOnline.setValue(true);
                if (messages == null)
                    log.debug("No new messages.");
                else {
                    log.debug("Retrieved " + messages.length +
                            " new message(s).");
                    addNewMessagesToDatabase(messages);
                }

            } catch (SocketTimeoutException e) {
                log.error("Connection timeout. You are offline.");
                _isUserOnline.setValue(false);

            } catch (ConnectException e) {
                log.error("Connection refused. You are offline.");
                _isUserOnline.setValue(false);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    });


    /* *************************************************************************
     * CONSTRUCTORS & INITIALIZING METHODS
     * ************************************************************************/

    /** Creates a new DataModel. */
    public DataModel() {
        _db = generateDatabase();
        _client = new RestClient(this);

        checkMessagesThread.setDaemon(true);
        checkMessagesThread.setName("checkMessagesThread");
        checkMessagesThread.start();

    }

    /** Generates the database (if needed) by taking it from the file system. */
    private JavaDatabase generateDatabase() {
        try {
            JavaDatabase db = new JavaDatabase();
            db.createTable();
            return db;
        } catch (SQLException e) {
            log.error(e.getStackTrace());
            log.error("Database could not be generated.");
            return null;
        }
    }


    /* *************************************************************************
     * METHODS
     * ************************************************************************/

    /** @return UNIX Epoch time. */
    public long getUnixEpochTime() {
        return Instant.now().getEpochSecond();
    }

    /** @return The time stamp of the last server request from groups. */
    public long getLastServerRequestedGroup() {
        long maxGroup = 0;
        try {
            Connection conn = _db.connectionToDerby();
            Statement stmt = conn.createStatement();

            ResultSet rs = stmt.executeQuery(
                    "select MAX(timeMod) as timeMod from groups");
            while (rs.next()) {
                maxGroup = (rs.getLong("timeMod"));
            }

        } catch (SQLException e) {
            log.error(e.getStackTrace());
            log.error("Could not retrieve group members.");
        }

        return maxGroup;

    }

    /** @return The time stamp of the last server request from messages. */
    public long getLastServerRequestedMessage() {
        long maxMessage = 0;
        try {
            Connection conn = _db.connectionToDerby();
            Statement stmt = conn.createStatement();

            ResultSet rs = stmt.executeQuery(
                    "select MAX(timeMod) as timeMod from messages");
            while (rs.next()) {
                maxMessage = (rs.getLong("timeMod"));
            }
        } catch (SQLException e) {
            log.error(e.getStackTrace());
            log.error("Could not retrieve group members.");
        }

        return maxMessage;
    }

    /** @return The current group members list. */
    public ArrayList<String> getCurrentGroupMembers() {
        ArrayList<String> uid = new ArrayList<>();
        ArrayList<String> res = new ArrayList<>();
        try {
            Connection conn = _db.connectionToDerby();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(
                    "select * from isIn where GID ='" + chatID + "'");

            while (rs.next()) {
                uid.add(rs.getString("UID"));
            }

            for (int i = 0; i < uid.size(); i++) {
                rs = stmt.executeQuery(
                        "select * from users where UID='" +
                                uid.get(i) + "'");
                while (rs.next()) {
                    res.add(rs.getString("name"));
                }
            }
            conn.close();
            stmt.close();
            rs.close();
        } catch (SQLException e) {
            log.error(e.getStackTrace());
            log.error("Could not retrieve group members.");
        }
        return res;
    }

    /** Adds new messages to the database. */
    public void addNewMessagesToDatabase(Message[] newMessages) {
        try {
            Connection conn = _db.connectionToDerby();
            Statement stmt = conn.createStatement();
            for (Message message : newMessages) {
                stmt.executeUpdate(
                        "insert into messages values('" +
                                message.getMessageId() + "'," +
                                message.getTimeCreated() + "," +
                                message.getTimeModified() + ",'" +
                                message.getMessageContent() + ",'" +
                                message.getSenderId() + "'," +
                                message.getReceiverId() + "')");
            }
            conn.close();
            stmt.close();
        } catch (SQLException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * @param name The name of a wanted user.
     * @return The UID of the user (if he exists, else null)
     */
    public String searchUser(String name) {
        User user = _client.getUID(name);
        if (user == null) {
            return null;
        } else {
            return user.getUserId();
        }
    }

    /**
     * Registers a User in the Server.
     *
     * @param name The user's name.
     * @param pwHash The user's password hash.
     */
    public int registerUser(String name, String pwHash) {
        if (_client.createNewUser(pwHash, name) == null) {
            return 0;
        }
        return 1;
    }

    /**
     * Logs in into server.
     *
     * @param username The name of the to be logged in user.
     */
    public int loginToServer(String username) {
        User user = _client.login(username);
        if (user == null) {
            return -1;
        }
        if (!user.isHashOkay()) {
            return 0;
        } else {
            ownUID = user.getUserId();
            return 1;
        }
    }

    /** Adds a new user to the database. */
    public void addUser(String uid, String name) {
        try {
            Connection conn = _db.connectionToDerby();
            Statement stmt = conn.createStatement();

            // First value needs every time a high value if the program starts
            stmt.executeUpdate(
                    "insert into users values('" + uid + "','"
                            + name + "')");
            conn.close();
            stmt.close();

            contactList.setAll(retrieveUserList());


        } catch (SQLException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
    }

    /** Adds a new Group to the database */
    public void addGroup(String groupName, ArrayList<String> members) {
        Group group = _client.createNewGroup(groupName, members);
        ArrayList<String> res = new ArrayList<>();
        if (group != null) {
            try {
                Connection conn = _db.connectionToDerby();
                Statement stmt = conn.createStatement();

                stmt.executeUpdate(
                        "insert into groups values('"
                                + group.getGroupId() + "',"
                                + group.getCreatedTime() + ","
                                + group.getModifiedTime() + ", '"
                                + group.getGroupName() + "','"
                                + group.getAdministrator() + "')");
                ResultSet rs = null;
                for (String name : members) {
                    rs = stmt.executeQuery("select * from users where " +
                            " name= '" + name + "'");
                    while (rs.next()) {
                        res.add(rs.getString("UID"));
                    }
                }
                for (String uid : res) {
                    stmt.executeUpdate("insert into isin values('"
                            + uid + "','"
                            + group.getGroupId() + "')");
                }
                conn.close();
                stmt.close();
                rs.close();
            } catch (SQLException e) {
                log.error(e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Adds a new User to Group.
     *
     * TODO does not work as intended yet.
     */
    public void addUserToGroup(String groupID, String newUID) {
        ArrayList<String> res = new ArrayList<>();
        try {
            Connection conn = _db.connectionToDerby();
            Statement stmt = conn.createStatement();
            ResultSet rs = null;
            for (String uid : res) {
                stmt.executeUpdate("insert into isin values('"
                        + uid + "','"
                        + getChatID() + "')");
            }
            conn.close();
            stmt.close();
            rs.close();
        } catch (SQLException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
    }

    /** Loads user list for group information. */
    public String filterUser(String name) {
        String res = null;
        try {
            Connection conn = _db.connectionToDerby();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("select * from users where " +
                    " name='" + name + "'");
            while (rs.next()) {
                res = rs.getString("name");
            }
            conn.close();
            stmt.close();
            rs.close();
        } catch (SQLException e) {
            log.error(e.getStackTrace());
            log.error("Could not retrieve user list.");
        }
        return res;
    }

    /** @return The list of users. */
    public ArrayList<String> retrieveUserList() {
        ArrayList<String> res = new ArrayList<>();
        try {
            Connection conn = _db.connectionToDerby();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("select * from users");
            while (rs.next()) {
                if (!rs.getString("name").equals(ownName))
                    res.add(rs.getString("name"));
            }

            rs = stmt.executeQuery("select * from groups");
            while (rs.next()) {
                res.add(rs.getString("name"));
            }
            conn.close();
            stmt.close();
            rs.close();
        } catch (SQLException e) {
            log.error(e.getStackTrace());
            log.error("Could not retrieve user list.");
        }
        return res;
    }

    /** Loads information of a user. */
    public void loadUserInfo(String name) {
        try {
            Connection conn = _db.connectionToDerby();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("select * from users where " +
                    "name='" + name + "'");
            while (rs.next()) {
                ownUID = rs.getString("UID");
                ownName = rs.getString("name");
            }

            conn.close();
            stmt.close();
            rs.close();
        } catch (SQLException e) {
            log.error(e.getStackTrace());
            log.error("Could not retrieve users list.");
        }
    }

    /** @return Administrator name of a group. */
    public String findAdmin(String adminUID) {
        try {
            Connection conn = _db.connectionToDerby();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("select * from users where " +
                    "uid='" + adminUID + "'");
            while (rs.next()) {
                adminName = rs.getString("name");
            }

            conn.close();
            stmt.close();
            rs.close();
        } catch (SQLException e) {
            log.error(e.getStackTrace());
            log.error("Could not retrieve users list.");
        }
        return adminName;
    }

    /** Loads information from selected chat. */
    public void loadChatInfo(String name) {
        try {
            Connection conn = _db.connectionToDerby();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(
                    "select * from users where " +
                            "name = '" + name + "'");
            while (rs.next()) {
                chatID = rs.getString("UID");
                chatName = rs.getString("name");
            }
            rs = stmt.executeQuery("select * from groups where " +
                    "name = '" + name + "'");
            while (rs.next()) {
                chatID = rs.getString("GID");
                chatName = rs.getString("name");
                adminUID = rs.getString("adminUID");
            }

            conn.close();
            stmt.close();
            rs.close();
        } catch (SQLException e) {
            log.error(e.getStackTrace());
            log.error("Could not retrieve contacts list.");
        }
    }

    /** Loads outgoing messages for contacts. */
    public ArrayList<String> retrieveMessageList(String chatId) {
        ArrayList<String> res = new ArrayList<>();
        try {
            Connection conn = _db.connectionToDerby();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(
                    "select text from messages where" +
                            " IDrecv = '" + ownUID + "' and IDsend ='" +
                            chatId + "'or " + "IDrecv ='" + chatId +
                            "' and IDsend = '" + ownUID + "'");
            while (rs.next()) {
                res.add(rs.getString("text"));
            }
            conn.close();
            stmt.close();
            rs.close();
        } catch (SQLException e) {
            log.error(e.getStackTrace());
            log.error("Could not retrieve messages list.");
        }
        return res;
    }

    /**
     * Save messages without MID for offline messages in database or save
     * response from server in the database.
     */
    public void sendMessage(String message) {
        try {
            Message mes = _client.sendMessage(message, chatID);
            if (mes == null) {
                Connection conn = _db.connectionToDerby();
                Statement stmt = conn.createStatement();

                stmt.executeUpdate(
                        "insert into messages values( '', "
                                + getUnixEpochTime() + " , "
                                + getUnixEpochTime() +
                                ", '" + message +
                                "','" + ownUID +
                                "','" + chatID + "')");
                conn.close();
                stmt.close();
            } else {
                Connection conn = _db.connectionToDerby();
                Statement stmt = conn.createStatement();


                stmt.executeUpdate(
                        "insert into messages values( '"
                                + mes.getMessageId() + "', "
                                + mes.getTimeCreated() + " , "
                                + mes.getTimeModified() + ", '"
                                + mes.getMessageContent() + "','"
                                + mes.getSenderId() + "','"
                                + mes.getReceiverId() + "')");
                conn.close();
                stmt.close();
            }
            messageList.setAll(retrieveMessageList(chatID));

        } catch (SQLException e) {
            log.error(e.getStackTrace());
        }
    }


    /* *************************************************************************
     * GETTERS AND SETTERS FOR PRIVATE FIELDS, PROPERTIES AND VARIABLES
     * ************************************************************************/

    /** @return The database. */
    public JavaDatabase getDatabase() {
        return _db;
    }

    /** @return The REST client. */
    public RestClient getClient() {
        return _client;
    }

    /** @return UID from login user */
    public String getOwnUID() {
        return ownUID;
    }

    public void setOwnUID(String var) {
        ownUID = var;
    }

    /** @return name from login user */
    public String getOwnName() {
        return ownName;
    }

    public void setOwnName(String var) {
        ownName = var;
    }

    /** @return pwHash from login user */
    public String getPwHash() {
        return pwHash;
    }

    public void setPwHash(String var) {
        pwHash = var;
    }

    /** @return UID from selected user in chat */
    public String getChatID() {
        return chatID;
    }

    /** @return Name from selected user in chat */
    public String getChatName() {
        return chatName;
    }

    /** @return uid from admin from selected group in chat */
    public String getAdminUID() {
        return adminUID;
    }

    /** @return Admin Name from selected group in chat */
    public String getAdminName() {
        return adminName;
    }

    /** @return A boolean property representing the online status. */
    public BooleanProperty isUserOnlineProperty() {
        return _isUserOnline;
    }

}
