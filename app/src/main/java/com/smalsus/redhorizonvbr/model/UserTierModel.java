package com.smalsus.redhorizonvbr.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class UserTierModel extends UserInfo implements Serializable {
    @SerializedName("tier")
    String tier;

}
