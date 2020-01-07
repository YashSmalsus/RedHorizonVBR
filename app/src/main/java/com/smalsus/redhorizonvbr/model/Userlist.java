package com.smalsus.redhorizonvbr.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Userlist implements Serializable {
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
    @SerializedName("location")
    UserLocation locationMap;

    public UserLocation getLocationMap() {
        return locationMap;
    }

    public void setLocationMap(UserLocation locationMap) {
        this.locationMap = locationMap;
    }

    public Userlist(String id, String userName, String fName, String lName, String gender, String imageUrl) {
        this.id = id;
        this.userName = userName;
        this.fName = fName;
        this.lName = lName;
        this.gender = gender;
        this.imageUrl = imageUrl;
    }
    public  Userlist(String id){




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


}
