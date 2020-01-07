package com.smalsus.redhorizonvbr.rest;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FileUploadAPiFactory {

    private static String BASE_URL = "http://renew360.com.wb7.my-hosting-panel.com/";
    private static Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();


    public static <S> S cteateService(Class<S> serviceClass) {
        return retrofit.create(serviceClass);
    }
}
