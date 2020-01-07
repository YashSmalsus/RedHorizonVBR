package com.smalsus.redhorizonvbr.networkrequest;

import android.util.Log;
import com.smalsus.redhorizonvbr.APIConstance.HRConstant;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.concurrent.TimeUnit;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class HrApiRequest {


    public void requestforLogin(final String emailid, final String password, final boolean isPhone, Callback callback) {
       String url = HRConstant.loginURl;
        JSONObject jsonObject = new JSONObject();
        try {
            if (isPhone)
                jsonObject.put("phoneNo", emailid);
            else
                jsonObject.put("emailID", emailid);
            jsonObject.put("password", password);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, jsonObject.toString());
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        client.newCall(request).enqueue(callback);
    }

    public void requestforCreateUSer(final String phoneNo, final String fName, final String lName, final int gender, final String dob, final String emailID, final String userName, final String password, boolean isSocial, Callback callback) {
        String url;
        if (!isSocial)
            url = HRConstant.BASEURL + "user";
        else
            url = HRConstant.BASEURL + "user/login/social";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("phoneNo", phoneNo);
            jsonObject.put("fName", fName);
            jsonObject.put("lName", lName);
            jsonObject.put("gender", gender);
            jsonObject.put("dob", dob);
            jsonObject.put("emailID", emailID);
            jsonObject.put("userName", userName);
            jsonObject.put("password", password);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, jsonObject.toString());
        Log.d("json", jsonObject.toString());
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("content-type", "application/json")
                .addHeader("cache-control", "no-cache")
                .build();
        client.newCall(request).enqueue(callback);
    }

    public void getUserList(String token, Callback callback) {
        String url = HRConstant.BaseURL + "list";
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

    public void getSearchuserList(String query,Callback callback) {
        String url = HRConstant.BASEURL + "friend/search/"+query;
        OkHttpClient client;
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(30, TimeUnit.SECONDS);
        builder.readTimeout(30, TimeUnit.SECONDS);
        builder.writeTimeout(30, TimeUnit.SECONDS);
        client = builder.build();
        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("cache-control", "no-cache")
                .build();
        client.newCall(request).enqueue(callback);
    }


    public void getSuggestedUserList(String token, Callback callback) {
        String url = HRConstant.BaseURL + "suggested";
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

    public void requestForCreateGroup(final String name, final String admin, final JSONArray members, final String token, Callback callback) {
        String url = HRConstant.BaseURL + "group";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name", name);
            jsonObject.put("admin", admin);
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

    public void requestforinvite(final String userid, final String receiverID, final String receiverEmail, final String token, Callback callback) {
        String url = null;
        url = HRConstant.loginURl;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("by", userid);
            jsonObject.put("to", receiverID);
            jsonObject.put("receiverEmail", receiverEmail);

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

    public void requestforForgetPassword(final String emailid, Callback callback) {
      String  url = HRConstant.BASEURL+"";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("emailID", emailid);

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
                .addHeader("cache-control", "no-cache")
                .build();
        client.newCall(request).enqueue(callback);
    }

    public void getGroupList(String id, String token, Callback callback) {
        String url = HRConstant.BaseURL + id + "/group";
        OkHttpClient client = new OkHttpClient();
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

    public void getFriendInvite(String id, String token, Callback callback) {
        String url = HRConstant.BaseURL + id + "/invite/received";
        OkHttpClient client = new OkHttpClient();
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(30, TimeUnit.SECONDS);
        builder.readTimeout(30, TimeUnit.SECONDS);
        builder.writeTimeout(30, TimeUnit.SECONDS);
        client = builder.build();
        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("token", token)
                .addHeader("Content-Type", "application/json")
                .build();
        client.newCall(request).enqueue(callback);
    }

    public void requestForChat(final String text, final String imageUrl, final String videoUrl, final String reciver, final String isGroupChat, final String token, Callback callback) {
      String  url = HRConstant.BASEURL+"chat";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("text", text);
            jsonObject.put("imageUrl", imageUrl);
            jsonObject.put("videoUrl", videoUrl);
            jsonObject.put("text", text);
            jsonObject.put("to", reciver);
            jsonObject.put("isGroupChat", isGroupChat);
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
        String url = HRConstant.BASEURL+"chat/" + id;
        OkHttpClient client = new OkHttpClient();
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(30, TimeUnit.SECONDS);
        builder.readTimeout(30, TimeUnit.SECONDS);
        builder.writeTimeout(30, TimeUnit.SECONDS);
        client = builder.build();
        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("token", token)
                .addHeader("Content-Type", "application/json")
                .build();
        client.newCall(request).enqueue(callback);
    }


}
