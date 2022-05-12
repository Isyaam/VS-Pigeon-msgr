package de.htwsaar.vs.msgr.client.rest;

import java.util.ArrayList;

public class Group {

    /** Represents the group's GID. */
    private String groupId;

    /** Represents the group's name. */
    private String groupName;

    /** Contains the UIDs of the members. */
    private ArrayList<String> members;

    /** Represents the administrator of the group. */
    private String administrator;

    /** Represents the creation time of the group. */
    private long createdTime;

    /** Represents the last modification timestamp of the group. */
    private long modifiedTime;


    /* *************************************************************************
     * GETTERS
     * ************************************************************************/

    public String getGroupId() {
        return groupId;
    }

    public long getModifiedTime() {
        return modifiedTime;
    }

    public String getGroupName() {
        return groupName;
    }

    public ArrayList<String> getMembers() {
        return members;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public String getAdministrator() {
        return administrator;
    }


    /* *************************************************************************
     * SETTERS
     * ************************************************************************/

    void setGroupId(String gid) {
        this.groupId = gid;
    }

    void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
    }

    void setModifiedTime(long modifiedTime) {
        this.modifiedTime = modifiedTime;
    }

    void setMembers(ArrayList<String> members) {
        this.members = members;
    }

    void setAdministrator(String administrator) {
        this.administrator = administrator;
    }

}
