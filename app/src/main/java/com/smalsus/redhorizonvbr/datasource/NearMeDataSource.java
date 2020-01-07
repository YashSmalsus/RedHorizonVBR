package com.smalsus.redhorizonvbr.datasource;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.PageKeyedDataSource;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.smalsus.redhorizonvbr.model.NetworkState;
import com.smalsus.redhorizonvbr.model.WearableStorePOJO;
import com.smalsus.redhorizonvbr.model.WerableStorePlaces;
import com.smalsus.redhorizonvbr.networkrequest.NearByPlaceReq;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class NearMeDataSource extends PageKeyedDataSource<String, WerableStorePlaces> {

    private static final String TAG = NearMeDataSource.class.getSimpleName();
    private MutableLiveData networkState;
    private MutableLiveData initialLoading;
    private NearByPlaceReq nearByPlaceReq;

    public NearMeDataSource() {
        networkState = new MutableLiveData();
        initialLoading = new MutableLiveData();
        nearByPlaceReq = new NearByPlaceReq();
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<String> params, @NonNull LoadInitialCallback<String, WerableStorePlaces> callback) {
        initialLoading.postValue(NetworkState.LOADING);
        networkState.postValue(NetworkState.LOADING);
        nearByPlaceReq.getNearByData("28.6286332", "77.378112", "electronics_store", 10000,null, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                String errorMessage = e== null ? "unknown error" : e.getMessage();
                networkState.postValue(new NetworkState(NetworkState.Status.FAILED, errorMessage));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                int statusCode = response.code();
                if (statusCode == 200) {
                    String myResponse = response.body().string();
                    Gson gson = new GsonBuilder().create();
                    WearableStorePOJO placesPOJO = gson.fromJson(myResponse, WearableStorePOJO.class);
                    callback.onResult(placesPOJO.customA, null, placesPOJO.next_page_token);
                    initialLoading.postValue(NetworkState.LOADED);
                    networkState.postValue(NetworkState.LOADED);
                }
            }
        });
    }

    @Override
    public void loadBefore(@NonNull LoadParams<String> params, @NonNull LoadCallback<String, WerableStorePlaces> callback) {

    }

    @Override
    public void loadAfter(@NonNull LoadParams<String> params, @NonNull LoadCallback<String, WerableStorePlaces> callback) {
        networkState.postValue(NetworkState.LOADING);
        nearByPlaceReq.getNearByData("28.6286332", "77.378112", "resturant", 10000,params.key, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                String errorMessage = e== null ? "unknown error" : e.getMessage();
                networkState.postValue(new NetworkState(NetworkState.Status.FAILED, errorMessage));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                int statusCode = response.code();
                if (statusCode == 200) {
                    String myResponse = response.body().string();
                    Gson gson = new GsonBuilder().create();
                    WearableStorePOJO placesPOJO = gson.fromJson(myResponse, WearableStorePOJO.class);
                    callback.onResult(placesPOJO.customA, placesPOJO.next_page_token);
                    initialLoading.postValue(NetworkState.LOADED);
                    networkState.postValue(NetworkState.LOADED);
                }
            }
        });
    }


    public MutableLiveData getNetworkState() {
        return networkState;
    }

    public MutableLiveData getInitialLoading() {
        return initialLoading;
    }




}
