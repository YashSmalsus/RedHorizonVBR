package com.smalsus.redhorizonvbr.model;

import android.os.Parcel;
import android.os.Parcelable;

public class LikesModel implements Parcelable {

    private String at;
    private UserModel by;

    public LikesModel(){

    }

    protected LikesModel(Parcel in) {
        at = in.readString();
        by = in.readParcelable(UserModel.class.getClassLoader());
    }

    public static final Creator<LikesModel> CREATOR = new Creator<LikesModel>() {
        @Override
        public LikesModel createFromParcel(Parcel in) {
            return new LikesModel(in);
        }

        @Override
        public LikesModel[] newArray(int size) {
            return new LikesModel[size];
        }
    };

    public String getAt() {
        return at;
    }

    public UserModel getBy() {
        return by;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(at);
        parcel.writeParcelable(by, i);
    }
}
