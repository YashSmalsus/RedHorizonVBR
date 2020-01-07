package com.smalsus.redhorizonvbr.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.smalsus.redhorizonvbr.model.NetworkState;
import com.smalsus.redhorizonvbr.model.WearableStorePOJO;
import com.smalsus.redhorizonvbr.model.WerableStorePlaces;
import com.smalsus.redhorizonvbr.networkrequest.NearByPlaceReq;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class LocationViewModel extends ViewModel {

    private MutableLiveData<NetworkState> networkState;
    private MutableLiveData<List<WerableStorePlaces>> nearByPlaceList;


    public LocationViewModel() {
        nearByPlaceList = new MutableLiveData<>();
    }


//    private void init() {
////        Executor executor = Executors.newFixedThreadPool(5);
////
////        NearMeDataFactory nearMeDataFactory = new NearMeDataFactory();
////        networkState = Transformations.switchMap(nearMeDataFactory.getMutableLiveData(),
////                dataSource -> dataSource.getNetworkState());
////
////        PagedList.Config pagedListConfig =
////                (new PagedList.Config.Builder())
////                        .setEnablePlaceholders(false)
////                        .setPageSize(20).build();
////
////        heroList = (new LivePagedListBuilder(nearMeDataFactory, pagedListConfig))
////                .setFetchExecutor(executor)
////                .build();
//    }

    public LiveData<NetworkState> getNetworkState() {
        if (networkState == null)
            networkState = new MutableLiveData<>();
        return networkState;
    }


    public void getNearMeData(String lat, String longtitude, String key) {
        networkState.postValue(NetworkState.LOADING);
        NearByPlaceReq nearByPlaceReq = new NearByPlaceReq();
        nearByPlaceReq.getNearByData(lat, longtitude, key, 10000, null, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, IOException e) {
                String errorMessage = e == null ? "unknown error" : e.getMessage();
                networkState.postValue(new NetworkState(NetworkState.Status.FAILED, errorMessage));
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                int statusCode = response.code();
                if (statusCode == 200) {
                    assert response.body() != null;
                    String myResponse = response.body().string();
                    Gson gson = new GsonBuilder().create();
                    WearableStorePOJO placesPOJO = gson.fromJson(myResponse, WearableStorePOJO.class);
                    nearByPlaceList.postValue(placesPOJO.getCustomA());
                    networkState.postValue(NetworkState.LOADED);
                }
            }
        });
    }

    public LiveData<List<WerableStorePlaces>> getArticleLiveData(String lat, String longtitude, String key) {
        if (nearByPlaceList != null)
            nearByPlaceList = new MutableLiveData<>();
        getNearMeData(lat, longtitude, key);
        return nearByPlaceList;
    }

}
