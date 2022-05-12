package de.htwsaar.vs.msgr.client.rest;

public class Message {

    /** Represents the UID of the sender. */
    private String senderId;

    /** Represent the UID or GID of the receiver. */
    private String receiverId;

    /** Represents the MID the server links to the message. */
    private String messageId;

    /** Represents the content of the message, aka the message itself. */
    private String messageContent;

    /** Represents the time the message has been created on the server. */
    private long timeCreated;

    /** Represents the time the message has been modified on the server. */
    private long timeModified;


    /* *************************************************************************
     * GETTERS
     * ************************************************************************/

    public String getSenderId() {
        return senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public String getMessageId() {
        return messageId;
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

    void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    void setTimeCreated(long timeCreated) {
        this.timeCreated = timeCreated;
    }

    void setTimeModified(long timeModified) {
        this.timeModified = timeModified;
    }

}
