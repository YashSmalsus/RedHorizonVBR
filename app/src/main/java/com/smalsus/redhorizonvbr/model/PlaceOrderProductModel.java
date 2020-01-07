package com.smalsus.redhorizonvbr.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PlaceOrderProductModel {
    @SerializedName("id")
    String id;
    @SerializedName("quantity")
    int quantity;

    public void setId(String id) {
        this.id = id;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
