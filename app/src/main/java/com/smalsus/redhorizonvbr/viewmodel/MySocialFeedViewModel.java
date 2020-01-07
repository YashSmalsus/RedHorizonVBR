package com.smalsus.redhorizonvbr.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.smalsus.redhorizonvbr.model.NetworkState;
import com.smalsus.redhorizonvbr.model.SocialFeedModel;
import com.smalsus.redhorizonvbr.rest.RestApi;
import com.smalsus.redhorizonvbr.rest.RestApiFactory;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MySocialFeedViewModel extends ViewModel {

    private MutableLiveData<NetworkState> networkState;
    private MutableLiveData<List<SocialFeedModel>> articleLiveData;


    public LiveData<List<SocialFeedModel>> getNewsFeedData(String id, String token) {
        if (articleLiveData == null) {
            articleLiveData = new MutableLiveData<>();
            getMyNewsFeed(id, token);
        }
        return articleLiveData;
    }

    public LiveData<NetworkState> getNetworkState() {
        if (networkState == null) {
            networkState = new MutableLiveData<>();
        }
        return networkState;

    }

    public void getMyNewsFeed(String userId, String userToken) {
        Call<List<SocialFeedModel>> call;
        call = RestApiFactory.cteateService(RestApi.class).fetchMyFeedData(userId, userToken);

        call.enqueue(new Callback<List<SocialFeedModel>>() {
            @Override
            public void onResponse(Call<List<SocialFeedModel>> call, Response<List<SocialFeedModel>> response) {
                if (response.isSuccessful()) {
                    articleLiveData.postValue(response.body());

                } else {
                    networkState.postValue(new NetworkState(NetworkState.Status.FAILED, response.message()));
                }
            }

            @Override
            public void onFailure(Call<List<SocialFeedModel>> call, Throwable t) {
                String errorMessage = t == null ? "unknown error" : t.getMessage();
                networkState.postValue(new NetworkState(NetworkState.Status.FAILED, errorMessage));
            }
        });
    }
}

