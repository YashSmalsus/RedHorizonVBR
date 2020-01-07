package com.smalsus.redhorizonvbr.rest;

import com.smalsus.redhorizonvbr.model.ProductDetailsModel;
import com.smalsus.redhorizonvbr.model.ProductItemModelClass;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface PrductDetailsApi {
    @GET("/user/product/detail/{ProductID}")
    Call<ProductDetailsModel> fetchProductDetials(@Path("ProductID") String productID);


    @GET("/user/product/detail/{ProductID}")
    Call<ProductItemModelClass> fetchProductDetial(@Path("ProductID") String productID);
}
