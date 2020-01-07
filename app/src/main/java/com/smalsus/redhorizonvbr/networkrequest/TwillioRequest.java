package com.smalsus.redhorizonvbr.networkrequest;

import com.smalsus.redhorizonvbr.APIConstance.HRConstant;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class TwillioRequest {

    public void getTwillioAuthToken( final String token,boolean isVideo, Callback callback) {
        String url;
        if(isVideo){
            url = HRConstant.BASEURL +"user/twilio/video/login";
        }else{
            url=HRConstant.BASEURL +"user/twilio/voice/login";
        }

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("token", token)
                .addHeader("content-type", "application/json")
                .addHeader("cache-control", "no-cache")
                .build();
        client.newCall(request).enqueue(callback);
    }
}
