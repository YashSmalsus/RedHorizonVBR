package com.smalsus.redhorizonvbr.networkrequest;

import com.google.gson.JsonObject;
import com.smalsus.redhorizonvbr.APIConstance.HRConstant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class EventRequest {

    public void requestfor_Upload_Profile(final File file, Callback callback) {
        String url = HRConstant.IMAGE_UPLOAD_URL + "uploadFiles.php";
        final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
        OkHttpClient client;
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(30, TimeUnit.SECONDS);
        builder.readTimeout(30, TimeUnit.SECONDS);
        builder.writeTimeout(30, TimeUnit.SECONDS);
        client = builder.build();
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("userID", "10")
                .addFormDataPart("UserProfileImage", file.getName(), RequestBody.create(MEDIA_TYPE_PNG, file))
                .build();
        Request request = new Request.Builder()
                .url(url)
                .header("Content-Type", "application/json")
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(callback);
    }


    public void requestEventReminder(final String id, final String token, Callback callback) {
        String url;
        url = HRConstant.BASEURL + "eventReminder/"+id;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .addHeader("token", token)
                .addHeader("content-type", "application/json")
                .addHeader("cache-control", "no-cache")
                .build();
        client.newCall(request).enqueue(callback);
    }


    public void getEventinfo(String id, String token, String date, Callback callback) {
        String url = HRConstant.BaseURL + id + "/event";
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
                .addHeader("starteddatetime", date)
                .addHeader("cache-control", "no-cache")
                .build();
        client.newCall(request).enqueue(callback);
    }

    public void getEventinfobyDay(String id, String year, String month, String day, String token, Callback callback) {
        String url = HRConstant.BaseURL + id + "/event?year=" + year + "&month=" + month + "&date=" + day;
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

    public void getEventinfobydate(String id, String token, String date, Callback callback) {
        String url = HRConstant.BaseURL + id + "/event";
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
                .addHeader("date", date)
                .addHeader("cache-control", "no-cache")
                .build();
        client.newCall(request).enqueue(callback);
    }

    public void getUserDetails(String token, Callback callback) {
        String url = HRConstant.getUserDetails;
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

    public void requestforPostevent(final String userid, final JSONArray members, final String topic, final String subtopic, final String romeName, final String name, final Integer type, final String startDate, final String endDate, final String token, final String location
            , final Integer availability, final Integer reminder, final List<String> hashTag, final Boolean isAllDay, final Boolean isPrivate, final String desc, final Boolean isActive
            , Callback callback) {
        String url;
        url = HRConstant.BaseURL + "event";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("members", members);
            jsonObject.put("name", name);
            jsonObject.put("type", type);
            jsonObject.put("startDate", startDate);
            jsonObject.put("endDate", endDate);
            jsonObject.put("location", location);
            jsonObject.put("availability", availability);
            jsonObject.put("reminder", reminder);
            jsonObject.put("hashTag", hashTag);
            jsonObject.put("isAllDay", isAllDay);
            jsonObject.put("isPrivate", isPrivate);
            jsonObject.put("desc", desc);
            jsonObject.put("isActive", isActive);
            jsonObject.put("meetingAgenda", desc);
            jsonObject.put("topic", topic);
            jsonObject.put("subTopic", subtopic);
            jsonObject.put("chatRoomName", romeName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, jsonObject.toString());
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("token", token)
                .addHeader("content-type", "application/json")
                .build();

        client.newCall(request).enqueue(callback);
    }

    public void requestForUpdateEvent(final String eventId, final JSONArray members, final String name, final String topic, final String subtopic, final Integer type, final String startDate, final String endDate, final String token, final String location
            , final Integer availability, final Integer reminder , final List<String> hashTag, final Boolean isAllDay, final Boolean isPrivate, final String desc, final Boolean isActive
            , Callback callback) {

        final String url = HRConstant.BaseURL + "event/" + eventId;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("members", members);
            jsonObject.put("name", name);
            jsonObject.put("type", type);
            jsonObject.put("startDate", startDate);
            jsonObject.put("endDate", endDate);
            jsonObject.put("location", location);
            jsonObject.put("availability", availability);
            jsonObject.put("reminder", reminder);
            jsonObject.put("hashTag", hashTag);
            jsonObject.put("isAllDay", isAllDay);
            jsonObject.put("isPrivate", isPrivate);
            jsonObject.put("desc", desc);
            jsonObject.put("isActive", isActive);
            jsonObject.put("meetingAgenda", desc);
            jsonObject.put("topic", topic);
            jsonObject.put("subTopic", subtopic);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, jsonObject.toString());
        Request request = new Request.Builder()
                .url(url)
                .put(body)
                .addHeader("token", token)
                .addHeader("content-type", "application/json")
                .build();
        client.newCall(request).enqueue(callback);
    }

    public void requestforSentFriend(final String by, final String to, final String email, final String token, Callback callback) {
        String url;
        url = HRConstant.inviteURL;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("by", by);
            jsonObject.put("to", to);
            jsonObject.put("receiverEmail", email);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, jsonObject.toString());
        Request request = new Request.Builder()
                .url(url)
                .addHeader("token", token)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();
        client.newCall(request).enqueue(callback);
    }

    public void requestforAcceptInvite(final String id, final String token, Callback callback) {
        String url = null;
        RequestBody reqbody = RequestBody.create(null, new byte[0]);
        url = HRConstant.BaseURL + "invite/" + id + "/accept";
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

    public void requestForFriendList(final String id, final String token, Callback callback) {
        String url;
        url = HRConstant.BaseURL + id + "/associate";
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .addHeader("token", token)
                .addHeader("content-type", "application/json")
                .addHeader("cache-control", "no-cache")
                .build();
        client.newCall(request).enqueue(callback);
    }

    public void requestForGalleryImages(final String id, final String token,Callback callback)
    {
        String url;
        url = HRConstant.BASEURL + "social/newsFeed/" + id + "/gallery";
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .addHeader("token", token)
                .addHeader("content-type", "application/json")
                .build();
        client.newCall(request).enqueue(callback);
    }

    public void requestforUpdateProfile(final String id, final String phoneNo, final String fName, final String lName, final String dob, final String token, Callback callback) {
        String url = HRConstant.BaseURL + id;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("phoneNo", phoneNo);
            jsonObject.put("fName", fName);
            jsonObject.put("lName", lName);
            jsonObject.put("dob", dob);
//            jsonObject.put("videoId", videoId);
//            jsonObject.put("imageUrl", imageUrl);
//            jsonObject.put("isActive", isActive);
//            jsonObject.put("isFinance", isFinance);
//            jsonObject.put("isMaterial", isMaterial);
//            jsonObject.put("isSocial", isSocial);
//            jsonObject.put("isSports", isSports);
//            jsonObject.put("isAerospace", isAerospace);
//            jsonObject.put("isEnergy", isEnergy);
//            jsonObject.put("isHealthCare", isHealthCare);
//            jsonObject.put("isInfoSevice", isInfoSevice);
//            jsonObject.put("isWebService", isWebService);
//            jsonObject.put("isRealEstate", isRealEstate);
//            jsonObject.put("isTele", isTele);
//            jsonObject.put("isUtility", isUtility);
//            jsonObject.put("isDiscretionary", isDiscretionary);
//            jsonObject.put("isConsumerStaples", isConsumerStaples);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, jsonObject.toString());
        Request request = new Request.Builder()
                .url(url)
                .addHeader("token", token)
                .addHeader("Content-Type", "application/json")
                .put(body)
                .build();
        client.newCall(request).enqueue(callback);
    }

    public void requestforUpdateVideoID(final String id, final String videoId, final String token, Callback callback) {
        String url = null;
        url = HRConstant.BaseURL + id;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("videoId", videoId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, jsonObject.toString());
        Request request = new Request.Builder()
                .url(url)
                .addHeader("token", token)
                .addHeader("Content-Type", "application/json")
                .put(body)
                .build();
        client.newCall(request).enqueue(callback);
    }

    public void requestforUpdateProfile(final String id, final String imageUrl, final String token, final boolean isCover, Callback callback) {
        String url = HRConstant.BaseURL + id;
        JSONObject jsonObject = new JSONObject();
        try {
            if (!isCover)
                jsonObject.put("imageUrl", imageUrl);
            else
                jsonObject.put("coverUrl", imageUrl);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, jsonObject.toString());
        Request request = new Request.Builder()
                .url(url)
                .addHeader("token", token)
                .addHeader("Content-Type", "application/json")
                .put(body)
                .build();
        client.newCall(request).enqueue(callback);
    }


    public void requestforUpdateLocation(final String id, final JSONObject location, final String token, Callback callback) {
        String url = null;
        url = HRConstant.BaseURL + id;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("location", location);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, jsonObject.toString());
        Request request = new Request.Builder()
                .url(url)
                .addHeader("token", token)
                .addHeader("Content-Type", "application/json")
                .put(body)
                .build();
        client.newCall(request).enqueue(callback);
    }


    public void postSocialNewsFeed(final String text, final String imageUrl, final String videoUrl, final String token, Callback callback) {
        String url;
        url = HRConstant.BASEURL + "social/newsFeed";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("text", text);
            jsonObject.put("imageUrl", imageUrl);
            jsonObject.put("videoUrl", videoUrl);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, jsonObject.toString());
        Request request = new Request.Builder()
                .url(url)
                .addHeader("token", token)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();
        client.newCall(request).enqueue(callback);
    }

    public void updatePost(final String postText, final String imageURL, final String videoURL,final String postId, final String token, Callback callback)
    {
        String url;
        url = HRConstant.BASEURL + "social/newsFeed/" + postId;
        JSONObject jsonObject = new JSONObject();
        try
        {
            jsonObject.put("text",postText);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, jsonObject.toString());
        Request request = new Request.Builder()
                .url(url)
                .addHeader("token", token)
                .addHeader("Content-Type", "application/json")
                .put(body)
                .build();
        client.newCall(request).enqueue(callback);
    }

    public void postSocialNewsFeedComment(final String id,final String text, final String imageUrl, final String videoUrl, final String token, Callback callback) {
        String url;
        url = HRConstant.BASEURL + "social/newsFeed/comments/"+id;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("text", text);
            jsonObject.put("imageUrl", imageUrl);
            jsonObject.put("videoUrl", videoUrl);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, jsonObject.toString());
        Request request = new Request.Builder()
                .url(url)
                .addHeader("token", token)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();
        client.newCall(request).enqueue(callback);
    }

    public void likePost(final String id,final String token, Callback callback) {
        String url;
        url = HRConstant.BASEURL + "social/newsfeed/"+id+"/like";
        JSONObject jsonObject = new JSONObject();
        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, jsonObject.toString());
        Request request = new Request.Builder()
                .url(url)
                .addHeader("token", token)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();
        client.newCall(request).enqueue(callback);
    }

    public void deleteUserPost(final String postId, final String token, Callback callback)
    {
        String url;
        url = HRConstant.BASEURL + "social/newsFeed/" + postId;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .delete()
                .addHeader("token", token)
                .addHeader("Content-Type", "application/json")
                .build();
        client.newCall(request).enqueue(callback);
    }

    public void deleteComment(final String commentId, final String token, Callback callback)
    {
        String url;
        url = HRConstant.BASEURL + "social/newsFeed/comments" + commentId;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .delete()
                .addHeader("token", token)
                .addHeader("Content-Type", "application/json")
                .build();
        client.newCall(request).enqueue(callback);
    }

    public void deleteEvent(final String eventID, final String token, Callback callback)
    {
        String url;
        url = HRConstant.BASEURL + "user/event/" + eventID;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .delete()
                .addHeader("token", token)
                .addHeader("Content-Type", "application/json")
                .build();
        client.newCall(request).enqueue(callback);
    }

    public void unFriendUser(final String friendUserID, final String token, Callback callback)
    {
        String url;
        url = HRConstant.BASEURL + "user/" + friendUserID + "/associate";
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .delete()
                .addHeader("token", token)
                .addHeader("Content-Type", "application/json")
                .build();
        client.newCall(request).enqueue(callback);
    }
}
