package com.smalsus.redhorizonvbr.rest;

import com.smalsus.redhorizonvbr.model.MyCartProductItemModel;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface GetMyCart {
    @GET("/user/mycart")
    Call<MyCartProductItemModel> getMyCartData(@Header("token") String userkey);
}
