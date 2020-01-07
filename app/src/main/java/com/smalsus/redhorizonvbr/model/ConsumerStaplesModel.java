package com.smalsus.redhorizonvbr.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ConsumerStaplesModel implements Serializable {

    @SerializedName("_id")
    private String _id;
    @SerializedName("Category")
    private String categoryName;
    @SerializedName("Value")
    private int value;
    @SerializedName("__v")
    private int __v;
    @SerializedName("subcategory")
    private List<ConsumerStaplesSubcategoryItem> subcategoryItemList;




    public String get_id() {
        return _id;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public int getValue() {
        return value;
    }

    public int get__v() {
        return __v;
    }

    public List<ConsumerStaplesSubcategoryItem> getSubcategoryItemList() {
        return subcategoryItemList;
    }
}
