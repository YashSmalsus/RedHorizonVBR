package com.smalsus.redhorizonvbr.networkrequest;

import com.smalsus.redhorizonvbr.APIConstance.HRConstant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class PaymentRequest {

    public void payByCard(final int paymentType, final String data, Callback callback) {
        String url;
        url = HRConstant.BASEURL + "transaction/pay";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("paymentType", paymentType);
            jsonObject.put("data", data);
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

    public void addToCard(final String productID, Callback callback) {
        String url;
        url = HRConstant.BASEURL + "user/mycart";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", productID);
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

    public void placeOrderRequest(final JSONArray productID, final String address,int payment,String headerToken, Callback callback) {
        String url;
        url = HRConstant.BASEURL + "user/order";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Product", productID);
            jsonObject.put("Address",address);
            jsonObject.put("payment",payment);
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
                .addHeader("token",headerToken)
                .build();
        client.newCall(request).enqueue(callback);

    }


}
