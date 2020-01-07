package com.smalsus.redhorizonvbr.rest;

import com.smalsus.redhorizonvbr.model.ProductItemModelClass;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface ProductlistRestAPI {
    @GET("/vendor/product/list")
    Call<List<ProductItemModelClass>> fetchFeed(@Query("page") long page, @Query("pageSize") int pageSize, @Header("token") String userkey);
}
