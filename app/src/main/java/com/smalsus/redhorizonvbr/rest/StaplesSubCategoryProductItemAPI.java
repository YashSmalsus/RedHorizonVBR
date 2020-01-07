package com.smalsus.redhorizonvbr.rest;

import com.smalsus.redhorizonvbr.model.ProductItemModelClass;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface StaplesSubCategoryProductItemAPI {
    @GET("/product/{SubCategory}/{Category}")
    Call<List<ProductItemModelClass>> fetchSubCategoryItemList(@Path("Category") int category, @Path("SubCategory") int subCategory);
}
