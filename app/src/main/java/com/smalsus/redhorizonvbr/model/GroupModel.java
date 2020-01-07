package com.smalsus.redhorizonvbr.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class GroupModel implements Serializable {
    @SerializedName("id")
    private String id;
    @SerializedName("name")
    private String name;
    @SerializedName("admin")
    private EventUser eventUser;
    @SerializedName("createdAt")
    private String createdAt;
    @SerializedName("image")
    private String image;
    @SerializedName("members")
    private List<EventUser> eventmember;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public EventUser getEventUser() {
        return eventUser;
    }

    public void setEventUser(EventUser eventUser) {
        this.eventUser = eventUser;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public List<EventUser> getEventmember() {
        return eventmember;
    }

    public void setEventmember(List<EventUser> eventmember) {
        this.eventmember = eventmember;
    }
    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
