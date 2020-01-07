package com.smalsus.redhorizonvbr.rest;

import com.smalsus.redhorizonvbr.model.ConsumerStaplesModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ConsumerStaplesRestAPI {
    @GET("/vendor/Category")
    Call<List<ConsumerStaplesModel>> fetchConsumerStaples();
}
