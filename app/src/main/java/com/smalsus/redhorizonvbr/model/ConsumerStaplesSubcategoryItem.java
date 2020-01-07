package com.smalsus.redhorizonvbr.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ConsumerStaplesSubcategoryItem implements Serializable {

    @SerializedName("name")
    private String name;
    @SerializedName("worth")
    private int worth;
    @SerializedName("imageURL")
    private String imageURL;
    @SerializedName("life")
    private int life;


    public ConsumerStaplesSubcategoryItem() {

    }

    public String getName() {
        return name;
    }

    public int getWorth() {
        return worth;
    }

    public String getImageURL() {
        return imageURL;
    }

    public int getLife() {
        return life;
    }
}

