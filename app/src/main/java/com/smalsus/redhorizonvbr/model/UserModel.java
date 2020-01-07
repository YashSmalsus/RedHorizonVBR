package com.smalsus.redhorizonvbr.model;

import android.os.Parcel;
import android.os.Parcelable;

public class UserModel implements Parcelable {

    private String id;
    private String userName;
    private String fName;
    private String lName;
    private int gender;
    private String emailID;
    private String imageUrl;
    private String videoId;

    public UserModel(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public String getfName() {
        return fName;
    }

    public String getlName() {
        return lName;
    }

    public int getGender() {
        return gender;
    }

    public String getEmailID() {
        return emailID;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getVideoId() {
        return videoId;
    }

    public static Creator<UserModel> getCREATOR() {
        return CREATOR;
    }

    protected UserModel(Parcel in) {
        id = in.readString();
        userName = in.readString();
        fName = in.readString();
        lName = in.readString();
        gender = in.readInt();
        emailID = in.readString();
        imageUrl = in.readString();
        videoId = in.readString();
    }

    public static final Creator<UserModel> CREATOR = new Creator<UserModel>() {
        @Override
        public UserModel createFromParcel(Parcel in) {
            return new UserModel(in);
        }

        @Override
        public UserModel[] newArray(int size) {
            return new UserModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(userName);
        parcel.writeString(fName);
        parcel.writeString(lName);
        parcel.writeInt(gender);
        parcel.writeString(emailID);
        parcel.writeString(imageUrl);
        parcel.writeString(videoId);
    }
}
