package com.smalsus.redhorizonvbr.rest;

import com.smalsus.redhorizonvbr.model.PlaceOrderProductModel;

import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface AddCartAPI {

    @FormUrlEncoded
    @POST("/user/mycart")
    Call<Object> addTOCart(@Field("id") String username, @Header("token") String token);

    @FormUrlEncoded
    @POST("/user/mycart/deleteitem")
    Call<Object> deleteFromCart(@Field("id") String username, @Header("token") String token);



    @FormUrlEncoded
    @POST("/user/order")
    Call<Object> placeOrder(@Header("token") String token, @Field("Product") List<PlaceOrderProductModel> Product, @Field("Address") String Address,@Field("payment") int payment);


}
