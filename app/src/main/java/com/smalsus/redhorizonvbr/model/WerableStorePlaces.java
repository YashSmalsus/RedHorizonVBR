package com.smalsus.redhorizonvbr.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class WerableStorePlaces implements Serializable {

    @SerializedName("id")
    public String id;
    @SerializedName("geometry")
    public Geometry geometry;
    @SerializedName("vicinity")
    public String vicinity;
    @SerializedName("name")
    public String name;
    @SerializedName("rating")
    public String rating;
    @SerializedName("photos")
    public List<Photo> locationPhoto;


    public class Geometry implements Serializable {

        @SerializedName("location")
        public LocationA locationA;

    }

    public String getId() {
        return id;
    }

    public class LocationA implements Serializable {

        @SerializedName("lat")
        public String lat;
        @SerializedName("lng")
        public String lng;


    }

    public class Photo implements Serializable {
        @SerializedName("photo_reference")
        public String photo_reference;

    }


}