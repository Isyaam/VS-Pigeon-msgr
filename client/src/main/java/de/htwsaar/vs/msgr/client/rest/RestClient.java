package de.htwsaar.vs.msgr.client.rest;

import de.htwsaar.vs.msgr.client.DataModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.ws.rs.core.MediaType;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;

public class RestClient {

    private static final Logger log = LogManager.getLogger(RestClient.class);


    /* *************************************************************************
     * SERVER SETTINGS
     * ************************************************************************/

    private static String SERVER_ADDRESS = "http://85.214.122.23:9090";
    private static String MESSAGES_PATH = "/messages";
    private static String USERS_PATH = "/users";
    private static String GROUPS_PATH = "/groups";


    /* *************************************************************************
     * FIELDS & CONSTRUCTOR
     * ************************************************************************/

    /** Represents the DataModel. */
    private DataModel _model;

    public RestClient(DataModel model) {
        _model = model;
    }


    /* *************************************************************************
     * GENERAL METHODS
     * ************************************************************************/

    /** @return A submittable header for REST. */
    private String getAuthenticationHeader() {
        String name;
        String pwHash;
        if (_model.getOwnName() == null || _model.getPwHash() == null) {
            name = "";
            pwHash = "";
        } else {
            name = _model.getOwnName();
            pwHash = _model.getPwHash();
        }
        return Base64.getEncoder()
                .encodeToString((name + ":" + pwHash)
                        .getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Sends a JSON as a given request to the server
     *
     * @param order       The JSON object containing the information.
     * @param requestType The type of the request.
     * @param subPath     The path (e.g. /messages)
     * @return The HttpURLConnection
     */
    private HttpURLConnection sendToServer(JSONObject order,
                                           String requestType,
                                           String subPath)
            throws URISyntaxException, IOException {
        URI uri = new URI(SERVER_ADDRESS + subPath);
        HttpURLConnection conn = (HttpURLConnection) uri.toURL()
                .openConnection();
        conn.setDoOutput(true);
        conn.setConnectTimeout(1000);

        conn.setRequestMethod(requestType);
        conn.addRequestProperty("Accept", MediaType.APPLICATION_JSON);
        conn.setRequestProperty("Content-Type", MediaType.APPLICATION_JSON);

        String encoded = getAuthenticationHeader();

        conn.setRequestProperty("Authorization", "Basic " + encoded);

        OutputStreamWriter out = new OutputStreamWriter(
                conn.getOutputStream());
        out.write(order.toString());
        out.flush();
        out.close();

        conn.disconnect();

        return conn;
    }

    /**
     * Requests a JSON as a given request to the server
     *
     * @param resource The path (e.g. /users/UID)
     * @return The HttpURLConnection
     */
    private HttpURLConnection getFromServer(String resource)
            throws URISyntaxException, IOException {
        URI uri = new URI(SERVER_ADDRESS + resource);
        HttpURLConnection conn = (HttpURLConnection) uri.toURL()
                .openConnection();
        conn.setDoOutput(true);
        conn.setConnectTimeout(1000);

        conn.setRequestMethod("GET");
        conn.addRequestProperty("Accept", MediaType.APPLICATION_JSON);
        conn.setRequestProperty("Content-Type", MediaType.APPLICATION_JSON);

        String encoded = getAuthenticationHeader();
        conn.setRequestProperty("Authorization", "Basic " + encoded);

        conn.disconnect();

        return conn;
    }

    /**
     * Parses the response received from the server and converts it to a
     * JSONObject.
     *
     * @param stream The InputStream from the server.
     * @return A JSONObject.
     */
    private JSONObject parseServerResponse(InputStream stream)
            throws IOException {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(stream));
        StringBuilder result = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            result.append(line);
        }
        String res = result.toString();
        return new JSONObject(res);
    }


    /* *************************************************************************
     * MESSAGES
     * ************************************************************************/

    /**
     * Sends message to a group or person.
     *
     * @param message    The message to be sent.
     * @param receiverId The id of the receiver (GID or UID).
     * @return The message (including time stamps and MID). If the message could
     * not be sent, it will be returned without time stamps and MID.
     */
    public Message sendMessage(String message, String receiverId) {

        JSONObject json = new JSONObject();
        json.put("sender_id", _model.getOwnUID());
        json.put("receiver_id", receiverId);
        json.put("message_content", message);

        Message mes = new Message();
        mes.setMessageContent(message);
        mes.setSenderId(_model.getOwnUID());
        mes.setReceiverId(receiverId);

        try {
            HttpURLConnection conn = sendToServer(
                    json, "POST", MESSAGES_PATH);

            if (conn.getResponseCode() == 200) {
                log.info("Sent message to receiver with UID " + receiverId);
                InputStream stream = (InputStream) conn.getContent();
                JSONObject response = parseServerResponse(stream);

                long timeCreated = response.getLong("time_created");
                long timeModified = response.getLong("time_modified");
                String messageId = response.getString("message_id");
                log.info("Server gave message the MID " + messageId);

                mes.setMessageId(messageId);
                mes.setTimeCreated(timeCreated);
                mes.setTimeModified(timeModified);

                return mes;
            } else {
                log.error("Could not sent message to receiver with id " +
                        receiverId);
                log.error("HTTP Response status: " + conn.getResponseCode());
                return mes;
            }
        } catch (IOException | URISyntaxException e) {
            System.out.println("Caught exception");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Modifies a message.
     *
     * @param message   The message.
     * @param messageId The ID of the message
     * @return The message (including time stamps, but no MID). If the message
     * could not be sent, it will be returned without a new modified time
     * stamp.
     */
    public Message modifyMessage(String message, String messageId) {

        JSONObject json = new JSONObject();
        json.put("message_content", message);

        Message mes = new Message();
        mes.setMessageContent(message);
        mes.setSenderId(_model.getOwnUID());
        mes.setMessageId(messageId);

        try {
            HttpURLConnection conn = sendToServer(
                    json, "PUT", MESSAGES_PATH + "/" +
                            messageId);

            if (conn.getResponseCode() == 200) {
                log.info("Modified message with id " + messageId);
                InputStream stream = (InputStream) conn.getContent();
                JSONObject response = parseServerResponse(stream);

                long timeModified = response.getLong("time_modified");

                mes.setTimeModified(timeModified);

                return mes;
            } else {
                log.error("Could not modify message with id " + messageId);
                log.error("HTTP Response status: " + conn.getResponseCode());
                return mes;
            }
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
            return null;

        }
    }

    /**
     * Deletes a message.
     *
     * @param messageId The message id.
     * @return The message (including time stamps, but no MID). If the message
     * could not be deleted, it will be returned without a new modified time
     * stamp.
     */
    public Message deleteMessage(String messageId) {

        JSONObject json = new JSONObject();
        json.put("message_content", "");

        Message mes = new Message();
        mes.setMessageContent("");
        mes.setSenderId(_model.getOwnUID());
        mes.setMessageId(messageId);

        try {
            HttpURLConnection conn = sendToServer(
                    json, "PUT", MESSAGES_PATH + "/" +
                            messageId);

            if (conn.getResponseCode() == 200) {
                log.info("Deleted message with id " + messageId);
                InputStream stream = (InputStream) conn.getContent();
                JSONObject response = parseServerResponse(stream);

                long timeModified = response.getLong("time_modified");

                mes.setTimeModified(timeModified);

                return mes;
            } else {
                log.error("Could not delete message with id " + messageId);
                log.error("HTTP Response status: " + conn.getResponseCode());
                return mes;
            }
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * Retrieves new messages.
     *
     * @return An array containing all new messages.
     */
    public Message[] retrieveNewMessages()
            throws SocketTimeoutException, ConnectException {

        try {
            HttpURLConnection conn = getFromServer(
                    MESSAGES_PATH + "/" +
                            _model.getLastServerRequestedMessage());

            if (conn.getResponseCode() == 200) {
                log.info("Received new messages.");
                InputStream stream = (InputStream) conn.getContent();
                JSONObject response = parseServerResponse(stream);

                JSONArray jsonMessages = response.getJSONArray("messages");
                Message[] messages = new Message[jsonMessages.length()];
                for (int i = 0; i < jsonMessages.length(); i++) {
                    JSONObject obj = (JSONObject) jsonMessages.get(i);
                    Message mes = new Message();
                    mes.setMessageId(obj.getString("message_id"));
                    mes.setSenderId(obj.getString("sender_id"));
                    mes.setReceiverId(obj.getString("receiver_id"));
                    mes.setMessageContent(obj.getString(
                            "message_content"));
                    mes.setTimeCreated(obj.getLong("time_created"));
                    mes.setTimeModified(obj.getLong("time_modified"));
                    messages[i] = mes;
                }

                return messages;

            } else {
                log.error("Could not retrieve messages");
                log.error("HTTP Response status: " + conn.getResponseCode());
                return null;
            }
        } catch (SocketTimeoutException e) {
            throw new SocketTimeoutException();
        } catch (ConnectException e) {
            throw new ConnectException();
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }


    /* *************************************************************************
     * USERS
     * ************************************************************************/

    /**
     * Creates a new user.
     *
     * @param passwordHash The hashed password.
     * @param userName     The user name.
     * @return An object of class User containing the UID.
     */
    public User createNewUser(String passwordHash, String userName) {

        JSONObject json = new JSONObject();
        json.put("pw_hash", passwordHash);
        json.put("user_name", userName);

        User user = new User();
        user.setUserName(userName);

        try {
            HttpURLConnection conn = sendToServer(
                    json, "POST", USERS_PATH);

            if (conn.getResponseCode() == 400) {
                log.error("User name is already taken. ");
                return null;
            } else if (conn.getResponseCode() == 200) {
                log.info("Created new user with name " + userName);
                InputStream stream = (InputStream) conn.getContent();
                JSONObject response = parseServerResponse(stream);

                String userId = response.getString("user_id");
                long timeCreated = response.getLong("time_created");
                long timeModified = response.getLong("time_modified");
                log.info("Server gave user the UID " + userId);

                user.setUserId(userId);
                user.setTimeCreated(timeCreated);
                user.setTimeModified(timeModified);
                return user;
            } else {
                log.error("Could not create new user with name " + userName);
                log.error("HTTP Response status: " + conn.getResponseCode());
                return null;
            }
        } catch (IOException | URISyntaxException e) {
            log.error("An exception occurred: ");
            e.printStackTrace();
            return null;
        }
    }

    /** Logs into server. */
    public User login(String username) {

        User user = new User();

        try {
            HttpURLConnection conn = getFromServer(
                    USERS_PATH + "/" + username);

            if (conn.getResponseCode() == 200) {
                log.info("Getting UID of " + username);
                InputStream stream = (InputStream) conn.getContent();
                JSONObject response = parseServerResponse(stream);

                String uid = response.getString("user_id");
                String name = response.getString("user_name");
                log.info("Got UID of user " + username);

                user.setUserId(uid);
                user.setUserName(name);
                user.setHashOkay(true);
                return user;
            } else {
                log.error("Could not login " + username);
                user.setHashOkay(false);
                log.error("HTTP Response status: " + conn.getResponseCode());
                return user;
            }
        } catch (IOException | URISyntaxException e) {
            System.out.println("Caught exception");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Retrieves a user's UID.
     *
     * @param userName The user name.
     */
    public User getUID(String userName) {

        User user = new User();
        user.setUserName(userName);

        try {
            HttpURLConnection conn = getFromServer(
                    USERS_PATH + "/" + userName);

            if (conn.getResponseCode() == 200) {
                log.info("Getting UID of " + userName);
                InputStream stream = (InputStream) conn.getContent();
                JSONObject response = parseServerResponse(stream);

                String uid = response.getString("user_id");
                String name = response.getString("user_name");
                log.info("Got UID of user " + userName);

                user.setUserId(uid);
                user.setUserName(name);
                return user;
            } else {
                log.error("Could not get UID of " + userName);
                log.error("HTTP Response status: " + conn.getResponseCode());
                return null;
            }
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Retrieves a user's info.
     *
     * @param userId The user's UID.
     */
    public User getUserInfo(String userId) {

        User user = new User();
        user.setUserId(userId);

        try {
            HttpURLConnection conn = getFromServer(
                    USERS_PATH + "/" + userId);

            if (conn.getResponseCode() == 200) {
                log.info("Getting info for UID " + userId);
                InputStream stream = (InputStream) conn.getContent();
                JSONObject response = parseServerResponse(stream);

                String uid = response.getString("user_id");
                String userName = response.getString("user_name");
                log.info("Got info for UID " + userId);

                user.setUserId(uid);
                user.setUserName(userName);
                return user;
            } else {
                log.error("Could not get info for " + userId);
                log.error("HTTP Response status: " + conn.getResponseCode());
                return null;
            }
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Modifies a user's info.
     *
     * @param userName The user name.
     */
    public User updateUserInfo(String userName) {

        User user = new User();
        user.setUserName(userName);

        JSONObject json = new JSONObject();
        json.put("pw_hash", _model.getPwHash());
        json.put("user_name", userName);

        try {
            HttpURLConnection conn = sendToServer(
                    json, "PUT", USERS_PATH + "/" +
                            _model.getOwnUID());

            if (conn.getResponseCode() == 200) {
                log.info("Updated user information of user with id " +
                        _model.getOwnUID() + " and username " + userName);
                InputStream stream = (InputStream) conn.getContent();
                JSONObject response = parseServerResponse(stream);

                String uid = response.getString("user_id");
                long timeModified = response.getLong("time_modified");

                user.setUserId(uid);
                user.setUserName(userName);
                user.setTimeModified(timeModified);
                return user;
            } else {
                log.error("Could not update user information of user with " +
                        "id" + _model.getOwnUID() + "and username " + userName);
                log.error("Error code: " + conn.getResponseCode());
                return null;
            }

        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Updates a user's password.
     *
     * @param password The user's new hashed password.
     */
    public User updateUserPassword(String password) {
        User user = new User();

        JSONObject json = new JSONObject();
        json.put("pw_hash", password);
        json.put("user_name", _model.getOwnName());

        try {
            HttpURLConnection conn = sendToServer(
                    json, "PUT", USERS_PATH + "/" +
                            _model.getOwnUID());

            if (conn.getResponseCode() == 200) {
                log.info("Updated user password of user with id " +
                        _model.getOwnUID() + " and username " +
                        _model.getOwnName());
                InputStream stream = (InputStream) conn.getContent();
                JSONObject response = parseServerResponse(stream);

                long timeModified = response.getLong("time_modified");

                user.setTimeModified(timeModified);
                return user;
            } else {
                log.error("Could not update user password of user with " +
                        "id" + _model.getOwnUID() + "and username " +
                        _model.getOwnName());
                return null;
            }

        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }

    /** Deletes a user. */
    public User deleteUser() {

        User user = new User();

        JSONObject json = new JSONObject();
        json.put("pw_hash", "");
        json.put("user_name", "");

        try {
            HttpURLConnection conn = sendToServer(
                    json, "PUT", USERS_PATH + "/" +
                            _model.getOwnUID());

            if (conn.getResponseCode() == 200) {
                log.info("Deleted user with id " + _model.getOwnUID());
                InputStream stream = (InputStream) conn.getContent();
                JSONObject response = parseServerResponse(stream);

                long timeModified = response.getLong("time_modified");

                user.setTimeModified(timeModified);
                return user;
            } else {
                log.error("Could not delete user with id " +
                        _model.getOwnUID());
                log.error("HTTP Response status: " + conn.getResponseCode());
                return null;
            }

        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }


    /* *************************************************************************
     * GROUPS
     * ************************************************************************/

    /**
     * Creates a new group.
     *
     * @param groupName The name of the group.
     * @param members   The list of members' UIDs.
     * @return A group object containing the GID and time stamps.
     */
    public Group createNewGroup(String groupName, ArrayList<String> members) {

        JSONObject json = new JSONObject();
        json.put("group_name", groupName);
        json.put("members", members.toArray());
        json.put("administrator", _model.getOwnUID());

        Group group = new Group();
        group.setGroupName(groupName);
        group.setAdministrator(_model.getOwnUID());
        group.setMembers(members);

        try {
            HttpURLConnection conn = sendToServer(
                    json, "POST", GROUPS_PATH);

            if (conn.getResponseCode() == 200) {
                log.info("Created new group with name " + groupName);
                InputStream stream = (InputStream) conn.getContent();
                JSONObject response = parseServerResponse(stream);

                String groupId = response.getString("group_id");
                long modifiedTime = response.getLong("time_modified");
                long createdTime = response.getLong("time_created");
                log.info("Server gave group the GID " + groupId);

                group.setGroupId(groupId);
                group.setCreatedTime(createdTime);
                group.setModifiedTime(modifiedTime);
                return group;
            } else {
                log.error("Could not create new group with name " +
                        groupName);
                return null;
            }
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Leaves a group.
     *
     * @param groupId The unique group id.
     */
    public Group leaveGroup(String groupId) {

        Group group = new Group();
        JSONObject json = new JSONObject();
        json.put("members", new String[]{});

        try {
            HttpURLConnection conn = sendToServer(
                    json, "PUT", GROUPS_PATH + "/" +
                            groupId);

            if (conn.getResponseCode() == 200) {
                log.info("User with id " + _model.getOwnUID() +
                        " left group.");
                InputStream stream = (InputStream) conn.getContent();
                JSONObject response = parseServerResponse(stream);

                long timeModified = response.getLong("time_modified");

                group.setModifiedTime(timeModified);
                return group;
            } else {
                log.error("User with id " + _model.getOwnUID() +
                        " could not leave group.");
                return null;
            }

        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Deletes a group.
     *
     * @param groupId The unique group id.
     */
    public Group deleteGroup(String groupId) {

        Group group = new Group();
        JSONObject json = new JSONObject();
        json.put("members", new String[]{});

        try {
            HttpURLConnection conn = sendToServer(
                    json, "PUT", GROUPS_PATH + "/" +
                            groupId);

            if (conn.getResponseCode() == 200) {
                log.info("User with id " + _model.getOwnUID() +
                        " deleted group.");
                InputStream stream = (InputStream) conn.getContent();
                JSONObject response = parseServerResponse(stream);

                long timeModified = response.getLong("time_modified");

                group.setModifiedTime(timeModified);
                return group;
            } else {
                log.error("User with id " + _model.getOwnUID() +
                        " could not delete group.");
                return null;
            }

        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Add member to group.
     *
     * @param groupId The unique group id.
     * @param newUID  The UID of the to be added member.
     */
    public Group addMemberToGroup(String groupId, String newUID) {
        Group group = new Group();

        ArrayList<String> members = _model.getCurrentGroupMembers();
        members.add(newUID);

        JSONObject json = new JSONObject();
        json.put("members", members);

        try {
            HttpURLConnection conn = sendToServer(
                    json, "PUT", GROUPS_PATH + "/" +
                            groupId);

            if (conn.getResponseCode() == 200) {
                log.info("Added user with id " + newUID +
                        " to group.");
                InputStream stream = (InputStream) conn.getContent();
                JSONObject response = parseServerResponse(stream);

                long timeModified = response.getLong("time_modified");

                group.setModifiedTime(timeModified);
                return group;
            } else {
                log.error("User with id " + newUID +
                        " could not be added to group.");
                return null;
            }

        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Remove member from group.
     *
     * @param groupId     The unique group id.
     * @param uidToRemove The UID of the to be removed user.
     */
    public Group removeMemberFromGroup(String groupId, String uidToRemove) {
        Group group = new Group();

        ArrayList<String> members = _model.getCurrentGroupMembers();
        members.remove(uidToRemove);

        JSONObject json = new JSONObject();
        json.put("members", members);

        try {
            HttpURLConnection conn = sendToServer(
                    json, "PUT", GROUPS_PATH + "/" +
                            groupId);

            if (conn.getResponseCode() == 200) {
                log.info("Removed user with id " + uidToRemove +
                        " from group.");
                InputStream stream = (InputStream) conn.getContent();
                JSONObject response = parseServerResponse(stream);

                long timeModified = response.getLong("time_modified");

                group.setModifiedTime(timeModified);
                return group;
            } else {
                log.error("User with id " + uidToRemove +
                        " could not be removed from group.");
                return null;
            }

        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Retrieves group changes.
     *
     * @return An array containing all group changes.
     */
    public Group[] retrieveGroupInformation() {

        JSONObject json = new JSONObject();
        json.put("last_requested", _model.getLastServerRequestedGroup());
        json.put("user_id", _model.getOwnUID());
        json.put("pw_hash", _model.getPwHash());


        try {
            HttpURLConnection conn = getFromServer(
                    GROUPS_PATH + "/" +
                            _model.getLastServerRequestedGroup());

            if (conn.getResponseCode() == 200) {
                log.info("Received group changes.");
                InputStream stream = (InputStream) conn.getContent();
                JSONObject response = parseServerResponse(stream);

                JSONArray jsonMessages = response.getJSONArray(
                        "changed_groups");
                Group[] groups = new Group[jsonMessages.length()];
                for (int i = 0; i < jsonMessages.length(); i++) {
                    JSONObject obj = (JSONObject) jsonMessages.get(i);
                    Group group = new Group();
                    group.setGroupId(obj.getString("group_id"));
                    group.setGroupName(obj.getString("group_name"));
                    group.setCreatedTime(obj.getLong("time_created"));
                    group.setModifiedTime(obj.getLong(
                            "time_modified"));
                    group.setAdministrator(obj.getString("administrator"));

                    JSONArray membersArray = obj.getJSONArray("members");
                    String[] membersUids = new String[membersArray.length()];
                    for (int j = 0; j < membersArray.length(); j++) {
                        String memberId = membersArray.getString(j);
                        membersUids[j] = memberId;
                    }

                    group.setMembers(
                            new ArrayList<>(Arrays.asList(membersUids)));
                    groups[i] = group;
                }

                return groups;

            } else {
                log.error("Could not retrieve group changes");
                log.error("HTTP Response status: " + conn.getResponseCode());
                return null;
            }
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }
}
