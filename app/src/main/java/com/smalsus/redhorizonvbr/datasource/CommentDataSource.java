package com.smalsus.redhorizonvbr.datasource;

import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.PageKeyedDataSource;
import com.smalsus.redhorizonvbr.model.NetworkState;
import com.smalsus.redhorizonvbr.model.SocialFeedModel;
import com.smalsus.redhorizonvbr.rest.CommentListRestAPI;
import com.smalsus.redhorizonvbr.rest.RestApiFactory;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentDataSource extends PageKeyedDataSource<Long, SocialFeedModel> {
    private static final String TAG = FeedDataSource.class.getSimpleName();
    private MutableLiveData networkState;
    private MutableLiveData initialLoading;
    private String userToken;
    private String feedId;

    public CommentDataSource(String userToken, String feedId) {
        networkState = new MutableLiveData();
        initialLoading = new MutableLiveData();
        this.userToken = userToken;
        this.feedId=feedId;
    }


    public MutableLiveData getNetworkState() {
        return networkState;
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<Long> params,
                            @NonNull LoadInitialCallback<Long, SocialFeedModel> callback) {

        initialLoading.postValue(NetworkState.LOADING);
        networkState.postValue(NetworkState.LOADING);

        RestApiFactory.cteateService(CommentListRestAPI.class).fetchComment(feedId,1, params.requestedLoadSize, userToken)
                .enqueue(new Callback<List<SocialFeedModel>>() {
                    @Override
                    public void onResponse(Call<List<SocialFeedModel>> call, Response<List<SocialFeedModel>> response) {
                        if (response.isSuccessful()) {
                            callback.onResult(response.body(), null, 2l);
                            initialLoading.postValue(NetworkState.LOADED);
                            networkState.postValue(NetworkState.LOADED);

                        } else {
                            initialLoading.postValue(new NetworkState(NetworkState.Status.FAILED, response.message()));
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

    @Override
    public void loadBefore(@NonNull LoadParams<Long> params,
                           @NonNull LoadCallback<Long, SocialFeedModel> callback) {

    }

    @Override
    public void loadAfter(@NonNull LoadParams<Long> params,
                          @NonNull LoadCallback<Long, SocialFeedModel> callback) {

        Log.i(TAG, "Loading Rang " + params.key + " Count " + params.requestedLoadSize);

        networkState.postValue(NetworkState.LOADING);
        RestApiFactory.cteateService(CommentListRestAPI.class).fetchComment(feedId,params.key, params.requestedLoadSize, userToken).enqueue(new Callback<List<SocialFeedModel>>() {
            @Override
            public void onResponse(Call<List<SocialFeedModel>> call, Response<List<SocialFeedModel>> response) {
                if (response.isSuccessful()) {
                    List<SocialFeedModel> newsFeedDataModelList = response.body();
                    long nextKey = newsFeedDataModelList.size() > 20 ? null : params.key + 1;
                    callback.onResult(response.body(), nextKey);
                    networkState.postValue(NetworkState.LOADED);
                } else
                    networkState.postValue(new NetworkState(NetworkState.Status.FAILED, response.message()));
            }

            @Override
            public void onFailure(Call<List<SocialFeedModel>> call, Throwable t) {
                String errorMessage = t == null ? "unknown error" : t.getMessage();
                networkState.postValue(new NetworkState(NetworkState.Status.FAILED, errorMessage));
            }
        });
    }
}
