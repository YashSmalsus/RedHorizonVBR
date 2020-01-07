package com.smalsus.redhorizonvbr.networkrequest;


import com.smalsus.redhorizonvbr.APIConstance.HRConstant;

import java.util.concurrent.TimeUnit;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class NearByPlaceReq {


    public void getNearByData(final String latitude, final String longtitude, String nearByPlace, final int radius, final String pagetoken, Callback callback) {
        String url = getNearByURL(latitude, longtitude, nearByPlace, radius, pagetoken);
        OkHttpClient client = new OkHttpClient();
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(30, TimeUnit.SECONDS);
        builder.readTimeout(30, TimeUnit.SECONDS);
        builder.writeTimeout(30, TimeUnit.SECONDS);
        client = builder.build();
        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("content-type", "application/x-www-form-urlencoded")
                .build();
        client.newCall(request).enqueue(callback);
    }


    private String getNearByURL(String latitude, String longitude, String nearbyPlace, final int proximity_radius, final String pagetoken) {
        StringBuilder googlePlacesUrl = new
                StringBuilder(HRConstant.NearBYGoogle);
        googlePlacesUrl.append("location=" + latitude + "," + longitude);
        googlePlacesUrl.append("&radius=" + proximity_radius);
        if (nearbyPlace != null)
            googlePlacesUrl.append("&type=" + nearbyPlace);
        googlePlacesUrl.append("&sensor=true");
        googlePlacesUrl.append("&key=" + HRConstant.GOOGLE_API_KEY);
        if (pagetoken != null)
            googlePlacesUrl.append("&pagetoken=" + pagetoken);
        return (googlePlacesUrl.toString());
    }
}
