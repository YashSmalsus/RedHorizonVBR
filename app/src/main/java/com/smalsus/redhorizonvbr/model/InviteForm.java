package com.smalsus.redhorizonvbr.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class InviteForm implements Serializable {

    @SerializedName("id")
    String id;
    @SerializedName("isInviteSent")
    Boolean isInviteSent;
    @SerializedName("status")
    String status;
    @SerializedName("sender")
   Sender sender;

    public void setSender(Sender sender) {
        this.sender = sender;
    }

    public InviteForm() {

    }

    public InviteForm(String id, Boolean isInviteSent, String status, Sender sender) {
        this.id = id;
        this.isInviteSent = isInviteSent;
        this.status = status;
        this.sender = sender;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Boolean getInviteSent() {
        return isInviteSent;
    }

    public void setInviteSent(Boolean inviteSent) {
        isInviteSent = inviteSent;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Sender getSender() {
        return sender;
    }



    public static class Sender implements Serializable {
        @SerializedName("userName")
        String userName;
        @SerializedName("fName")
        String fName;
        @SerializedName("lName")
        String lName;
        @SerializedName("imageUrl")
        Sender imageUrl;

        public Sender (){

        }

        public Sender(String userName, String fName, String lName, Sender imageUrl) {
            this.userName = userName;
            this.fName = fName;
            this.lName = lName;
            this.imageUrl = imageUrl;
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

        public Sender getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(Sender imageUrl) {
            this.imageUrl = imageUrl;
        }
    }


}
