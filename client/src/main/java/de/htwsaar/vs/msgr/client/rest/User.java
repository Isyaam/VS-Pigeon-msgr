package de.htwsaar.vs.msgr.client.rest;

public class User {

    /** Represents the UID. */
    private String userId;

    /** Represents the user name. */
    private String userName;

    /** Indicates if the provided hash matches to the user. */
    private boolean hashOkay;

    /** Represents the time the user has been created on the server. */
    private long timeCreated;

    /** Represents the time the user has been modified on the server. */
    private long timeModified;


    /* *************************************************************************
     * GETTERS
     * ************************************************************************/

    public String getUserId() {
        return userId;
    }

    public boolean isHashOkay() {
        return hashOkay;
    }

    public String getUserName() {
        return userName;
    }

    public long getTimeCreated() {
        return timeCreated;
    }

    public long getTimeModified() {
        return timeModified;
    }


    /* *************************************************************************
     * SETTERS
     * ************************************************************************/

    void setUserName(String userName) {
        this.userName = userName;
    }

    void setUserId(String userId) {
        this.userId = userId;
    }

    void setHashOkay(boolean hashOkay) {
        this.hashOkay = hashOkay;
    }

    void setTimeCreated(long timeCreated) {
        this.timeCreated = timeCreated;
    }

    void setTimeModified(long timeModified) {
        this.timeModified = timeModified;
    }

}