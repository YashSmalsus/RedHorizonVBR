package com.smalsus.redhorizonvbr.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class FriendList implements Serializable {

    @SerializedName("uId")
    String uId;
    @SerializedName("associateList")
    List<EventUser>associateList;


    public FriendList(){

    }
    public FriendList(String uId, List<EventUser> associateList) {
        this.uId = uId;
        this.associateList = associateList;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public List<EventUser> getAssociateList() {
        return associateList;
    }

}
