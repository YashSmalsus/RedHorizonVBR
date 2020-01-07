package com.smalsus.redhorizonvbr.model;

import com.google.gson.annotations.SerializedName;
import com.smalsus.redhorizonvbr.APIConstance.HRConstant;

import java.io.Serializable;

public class UserLocation  implements Serializable {
    @SerializedName("lat")
    Double lat;
    @SerializedName("long")
    Double longi;
    @SerializedName(HRConstant.USER_COUNTRY_LOCATION)
    String userCountry;
    @SerializedName(HRConstant.USER_CITY_LOCATION)
    String userCity;


    public UserLocation(Double lat, Double longi) {
        this.lat = lat;
        this.longi = longi;
    }


    public UserLocation() {
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLongi() {
        return longi;
    }

    public void setLongi(Double longi) {
        this.longi = longi;
    }

    public String getUserCountry() {
        return userCountry;
    }

    public String getUserCity() {
        return userCity;
    }
}