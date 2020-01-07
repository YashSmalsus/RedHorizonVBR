package com.smalsus.redhorizonvbr.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class NewsFeedDataModel implements Parcelable {

    private List<SocialFeedModel> articles;


    public List<SocialFeedModel> getArticles() {
        return articles;
    }

    protected NewsFeedDataModel(Parcel in) {
        articles = in.createTypedArrayList(SocialFeedModel.CREATOR);
    }



    public static final Creator<NewsFeedDataModel> CREATOR = new Creator<NewsFeedDataModel>() {
        @Override
        public NewsFeedDataModel createFromParcel(Parcel in) {
            return new NewsFeedDataModel(in);
        }

        @Override
        public NewsFeedDataModel[] newArray(int size) {
            return new NewsFeedDataModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeTypedList(articles);
    }
}
