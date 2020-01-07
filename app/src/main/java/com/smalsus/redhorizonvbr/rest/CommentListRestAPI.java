package com.smalsus.redhorizonvbr.rest;

import com.smalsus.redhorizonvbr.model.SocialFeedModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface CommentListRestAPI {
    @GET("/social/newsFeed/comments/{feedId}")
    Call<List<SocialFeedModel>> fetchComment(@Path ("feedId") String feedId,@Query("page") long page,
                                          @Query("pageSize") int pageSize, @Header("token") String userkey);
}
