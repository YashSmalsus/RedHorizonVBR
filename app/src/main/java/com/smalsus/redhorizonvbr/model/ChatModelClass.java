package com.smalsus.redhorizonvbr.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ChatModelClass implements Serializable {
    @SerializedName("id")
    String id ;
    @SerializedName("text")
    String text ;
    @SerializedName("by")
    EventUser userBy ;
    @SerializedName("to")
    String toUser ;
    @SerializedName("isGroupChat")
    boolean isGroupChat ;
    @SerializedName("at")
    String at;
    @SerializedName("notifyType")
    int notifyType;

    public ChatModelClass(String id, String text, EventUser userBy, String toUser, boolean isGroupChat, String at,int notifyType) {
        this.id = id;
        this.text = text;
        this.userBy = userBy;
        this.toUser = toUser;
        this.isGroupChat = isGroupChat;
        this.at = at;
        this.notifyType=notifyType;
    }

    public ChatModelClass() {
    }

    public String getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public EventUser getUserBy() {
        return userBy;
    }

    public String getToUser() {
        return toUser;
    }

    public boolean isGroupChat() {
        return isGroupChat;
    }

    public String getAt() {
        return at;
    }

    public int getNotifyType() {
        return notifyType;
    }
}
