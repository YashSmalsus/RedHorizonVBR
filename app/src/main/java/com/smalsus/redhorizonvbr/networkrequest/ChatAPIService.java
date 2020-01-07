package com.smalsus.redhorizonvbr.networkrequest;

import com.smalsus.redhorizonvbr.APIConstance.HRConstant;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class ChatAPIService {
    public void postChat(final String tOId, final String imageUrl, final String message, String videoUrl, final String token, final boolean isGroupChat, Callback callback) {
        String url;
        url = HRConstant.BASEURL + "chat";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("text", message);
            jsonObject.put("to", tOId);
            jsonObject.put("isGroupChat", isGroupChat);
            if (imageUrl != null && !imageUrl.isEmpty())
                jsonObject.put("imageUrl", imageUrl);
            if (videoUrl != null && !videoUrl.isEmpty())
                jsonObject.put("videoUrl", videoUrl);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, jsonObject.toString());
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("content-type", "application/json")
                .addHeader("token", token)
                .addHeader("cache-control", "no-cache")
                .build();
        client.newCall(request).enqueue(callback);

    }

    public void getChatHistory(String id, String token, Callback callback) {
        String url = HRConstant.BASEURL + "chat/" + id;
        OkHttpClient client;
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(30, TimeUnit.SECONDS);
        builder.readTimeout(30, TimeUnit.SECONDS);
        builder.writeTimeout(30, TimeUnit.SECONDS);
        client = builder.build();
        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("token", token)
                .addHeader("cache-control", "no-cache")
                .build();
        client.newCall(request).enqueue(callback);
    }
}
