package com.smalsus.redhorizonvbr.networkrequest;

import android.util.Base64;

import com.smalsus.redhorizonvbr.APIConstance.HRConstant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class CallApiRequest {

    public void requestforJoinCall(final String id, final String token, Callback callback) {
        String url;
        RequestBody reqbody = RequestBody.create(null, new byte[0]);
        url = HRConstant.BASEURL+"chat/media/" + id + "/join";
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .post(reqbody)
                .addHeader("token", token)
                .addHeader("content-type", "application/json")
                .addHeader("cache-control", "no-cache")
                .build();
        client.newCall(request).enqueue(callback);
    }

    public void requestForAddMemeberToCall(final String id, final JSONArray members, final String token, Callback callback) {
        String url;
        url = HRConstant.BASEURL+"chat/media/" + id + "/member";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("members", members);
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

    public void requestFoEndCall(final String id, final String token, Callback callback) {
        String url;
        url = HRConstant.BASEURL+"chat/media/" + id + "/leave";
        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, "");
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("content-type", "application/x-www-form-urlencoded")
                .addHeader("token", token)
                .addHeader("cache-control", "no-cache")
                .build();
        client.newCall(request).enqueue(callback);

    }


    public void requestForStartCall(final String event, JSONObject roomChunk, JSONArray members, final String token, Callback callback) {
        String url;
        url = HRConstant.BASEURL+"chat/media";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("members", members);
            jsonObject.put("roomChunk", roomChunk);
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

    public void getCallerInfo(String id, String token, Callback callback) {
        String url = HRConstant.BASEURL + "user/videoId/" + id;
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

    public void sendRequestMessage(String toPhoneNumber, String message, Callback callback) {
        OkHttpClient client = new OkHttpClient();
        String url = new StringBuilder().append(HRConstant.TwallioBaseURL).append("AC73022141dff40f59f0185da32acfbb57").append("/SMS/Messages").toString();
        String base64EncodedCredentials = "Basic " + Base64.encodeToString((new StringBuilder().append("AC73022141dff40f59f0185da32acfbb57").append(":").append("09ad8e5ad5c239a698a5c4f144f275bf").toString()).getBytes(), Base64.NO_WRAP);

        RequestBody body = new FormBody.Builder()
                .add("From", "+18653240597")
                .add("To", toPhoneNumber)
                .add("Body", message)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .header("Authorization", base64EncodedCredentials)
                .build();
        client.newCall(request).enqueue(callback);
    }


    public void postFirebaseServerToken(String deviceId, String token, String userID, String authToken, Callback callback) {
        String url = HRConstant.BASEURL + "user/" + userID + "/messaging";
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("deviceId", deviceId)
                .add("messagingId", token)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("token", authToken)
                .addHeader("cache-control", "no-cache")
                .build();
        client.newCall(request).enqueue(callback);
    }

    public void getTierFriendList(String authToken, Callback callback) {
        String url = HRConstant.BASEURL + "user/tier";
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("token", authToken)
                .addHeader("cache-control", "no-cache")
                .build();
        client.newCall(request).enqueue(callback);
    }
}
