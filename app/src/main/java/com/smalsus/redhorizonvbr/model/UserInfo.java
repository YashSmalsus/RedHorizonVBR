package com.smalsus.redhorizonvbr.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class UserInfo implements Serializable {
    @SerializedName("id")
    String id;
    @SerializedName("userName")
    String userName;
    @SerializedName("fName")
    String fName;
    @SerializedName("lName")
    String lName;
    @SerializedName("emailID")
    String emailID;
    @SerializedName("dob")
    String dob;
    @SerializedName("imageUrl")
    String imageUrl;
    @SerializedName("coverUrl")
    String coverURL;
    @SerializedName("isFinance")
    Boolean isFinance;
    @SerializedName("isMaterial")
    Boolean isMaterial;
    @SerializedName("isSocial")
    Boolean isSocial;
    @SerializedName("isSports")
    Boolean isSports;
    @SerializedName("isAerospace")
    Boolean isAerospace;
    @SerializedName("isEnergy")
    Boolean isEnergy;
    @SerializedName("isHealthCare")
    Boolean isHealthCare;
    @SerializedName("isInfoSevice")
    Boolean isInfoSevice;
    @SerializedName("isWebService")
    Boolean isWebService;
    @SerializedName("isRealEstate")
    Boolean isRealEstate;
    @SerializedName("isTele")
    Boolean isTele;
    @SerializedName("isUtility")
    Boolean isUtility;
    @SerializedName("isDiscretionary")
    Boolean isDiscretionary;
    @SerializedName("isConsumerStaples")
    Boolean isConsumerStaples;
    @SerializedName("authToken")
    String authToken;
    @SerializedName("location")
    UserLocation location;
    @SerializedName("associateCount")
    int associateCount;

    @SerializedName("totalLikes")
    int totalLikes;

    public UserInfo(String id, String userName, String fName, String lName, String emailID, String dob, String imageUrl, String coverURL, Boolean isFinance, Boolean isMaterial, Boolean isSocial, Boolean isSports, Boolean isAerospace, Boolean isEnergy, Boolean isHealthCare, Boolean isInfoSevice, Boolean isWebService, Boolean isRealEstate, Boolean isTele, Boolean isUtility, Boolean isDiscretionary, Boolean isConsumerStaples, String authToken, UserLocation location) {
        this.id = id;
        this.userName = userName;
        this.fName = fName;
        this.lName = lName;
        this.emailID = emailID;
        this.dob = dob;
        this.imageUrl = imageUrl;
        this.coverURL = coverURL;
        this.isFinance = isFinance;
        this.isMaterial = isMaterial;
        this.isSocial = isSocial;
        this.isSports = isSports;
        this.isAerospace = isAerospace;
        this.isEnergy = isEnergy;
        this.isHealthCare = isHealthCare;
        this.isInfoSevice = isInfoSevice;
        this.isWebService = isWebService;
        this.isRealEstate = isRealEstate;
        this.isTele = isTele;
        this.isUtility = isUtility;
        this.isDiscretionary = isDiscretionary;
        this.isConsumerStaples = isConsumerStaples;
        this.authToken = authToken;
        this.location = location;
    }

    public UserInfo() {

    }

    public UserLocation getLocation() {
        return location;
    }

    public void setLocation(UserLocation location) {
        this.location = location;
    }

    public String getCoverURL() {
        return coverURL;
    }

    public void setCoverURL(String coverURL) {
        this.coverURL = coverURL;
    }

    public String getEmailID() {
        return emailID;
    }

    public void setEmailID(String emailID) {
        this.emailID = emailID;
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

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }


    public Boolean getMaterial() {
        return isMaterial;
    }

    public void setMaterial(Boolean material) {
        isMaterial = material;
    }


    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public int getAssociateCount() {
        return associateCount;
    }

    public int getTotalLikes() {
        return totalLikes;
    }

    public void setTotalLikes(int totalLikes) {
        this.totalLikes = totalLikes;
    }
}