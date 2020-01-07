package com.smalsus.redhorizonvbr.rest;

import com.smalsus.redhorizonvbr.model.ConsumerStaplesModel;
import com.smalsus.redhorizonvbr.model.ConsumerStaplesSubcategoryItem;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ConsumerStaplesSunItemAPI {
    @GET("/product/{CategoryValue}")
    Call<List<ConsumerStaplesSubcategoryItem>> fetchConsumerStaples(@Path("CategoryValue") int categoryValue);
}
