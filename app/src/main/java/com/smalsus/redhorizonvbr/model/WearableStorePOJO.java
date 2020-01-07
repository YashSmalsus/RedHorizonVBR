package com.smalsus.redhorizonvbr.model;

import androidx.paging.PagedList;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WearableStorePOJO  {
    @SerializedName("results")
    public List<WerableStorePlaces> customA;
    @SerializedName("status")
    public String status;
    @SerializedName("next_page_token")
    public String next_page_token;

    public WearableStorePOJO(PagedList<WerableStorePlaces> customA, String status,String next_page_token) {
        this.customA = customA;
        this.status = status;
        this.next_page_token=next_page_token;
    }

    public List<WerableStorePlaces> getCustomA() {
        return customA;
    }

    public String getStatus() {
        return status;
    }

    public String getNext_page_token() {
        return next_page_token;
    }
}
