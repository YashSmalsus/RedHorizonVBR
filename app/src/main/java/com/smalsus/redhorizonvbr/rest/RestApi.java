package com.smalsus.redhorizonvbr.rest;
import com.smalsus.redhorizonvbr.model.SocialFeedModel;
import com.smalsus.redhorizonvbr.model.UserInfo;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RestApi {
    @GET("/social/newsFeed")
    Call<List<SocialFeedModel>> fetchFeed(@Query("page") long page,
                         @Query("pageSize") int pageSize, @Header("token") String userkey);

    @GET("/social/newsFeed/{userID}")
    Call<List<SocialFeedModel>> fetchMyFeed(@Path("userID")String userID,@Query("page") long page,
                                          @Query("pageSize") int pageSize, @Header("token") String userkey);


    @GET("/social/newsFeed/{userID}")
    Call<List<SocialFeedModel>> fetchMyFeedData(@Path("userID")String userID,@Header("token") String userkey);


    @GET("/user/profile/{userID}")
    Call<UserInfo> fetchUserDetials(@Path("userID") String userID);
}
