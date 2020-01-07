package com.smalsus.redhorizonvbr.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class PlacesPOJO {

    @SerializedName("results")
    public List<CustomPlace> customA ;
    @SerializedName("status")
    public String status;

    public PlacesPOJO(List<CustomPlace> customA, String status) {
        this.customA = customA;
        this.status = status;
    }

    public List<CustomPlace> getCustomA() {
        return customA;
    }

    public String getStatus() {
        return status;
    }

    public class CustomPlace implements Serializable{

        @SerializedName("geometry")
        public Geometry geometry;
        @SerializedName("vicinity")
        public String vicinity;
        @SerializedName("name")
        public String name;
        @SerializedName("rating")
        public String rating;

    }
    public class Geometry implements Serializable{

        @SerializedName("location")
        public LocationA locationA;

    }

    public class LocationA implements Serializable {

        @SerializedName("lat")
        public String lat;
        @SerializedName("lng")
        public String lng;


    }

}

