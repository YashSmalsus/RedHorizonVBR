package com.smalsus.redhorizonvbr.model;

import android.graphics.drawable.Drawable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class EventUser  implements Serializable {
    @SerializedName("id")
    String id;
    @SerializedName("userName")
    String userName;
    @SerializedName("fName")
    String fName;
    @SerializedName("lName")
    String lName;
    @SerializedName("gender")
    String gender;
    @SerializedName("imageUrl")
    String imageUrl;
    @SerializedName("videoId")
    String videoId;
    @SerializedName("coverUrl")
    String coverURL;
    @SerializedName("location")
    UserLocation locationMap;
    private boolean isExpanded = false;

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }

    public UserLocation getLocationMap() {
        return locationMap;
    }

    public void setLocationMap(UserLocation locationMap) {
        this.locationMap = locationMap;
    }

    public EventUser(String id, String userName, String fName, String lName, String gender, String imageUrl, String videoId, UserLocation location) {
        this.id = id;
        this.userName = userName;
        this.fName = fName;
        this.lName = lName;
        this.gender = gender;
        this.imageUrl = imageUrl;
        this.videoId = videoId;
        this.locationMap = location;
    }

    public EventUser() {

    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getfName() {
        return fName;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public String getlName() {
        return lName;
    }

    public void setlName(String lName) {
        this.lName = lName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }


    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCoverURL() {
        return coverURL;
    }

    public void setCoverURL(String coverURL) {
        this.coverURL = coverURL;
    }
}


