package com.smalsus.redhorizonvbr.rest;

import com.google.gson.JsonObject;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface FileUploadApi {
    @Multipart
    @POST("uploadFiles.php")
    Call<JsonObject> uploadImage(@Part MultipartBody.Part file, @Query("userID") String userId);
}
