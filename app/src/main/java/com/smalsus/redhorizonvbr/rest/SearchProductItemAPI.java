package com.smalsus.redhorizonvbr.rest;

import com.smalsus.redhorizonvbr.model.ProductItemModelClass;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface SearchProductItemAPI {
    @GET("product/search/{Querry}")
    Call<List<ProductItemModelClass>> fetchSubCategoryItemList(@Path("Querry") String querry);
}
