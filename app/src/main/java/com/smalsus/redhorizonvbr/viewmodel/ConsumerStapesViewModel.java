package com.smalsus.redhorizonvbr.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.smalsus.redhorizonvbr.model.ConsumerStaplesModel;
import com.smalsus.redhorizonvbr.model.NetworkState;
import com.smalsus.redhorizonvbr.rest.ConsumerStaplesRestAPI;
import com.smalsus.redhorizonvbr.rest.RestApiFactory;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConsumerStapesViewModel extends ViewModel {
    private MutableLiveData<List<ConsumerStaplesModel>> users;
    private MutableLiveData<NetworkState> networkState;

    public LiveData<List<ConsumerStaplesModel>> getStaples() {
        if (users == null) {
            users = new MutableLiveData<>();
            getFriendList();
        }
        return users;
    }

    public LiveData<NetworkState> getNetworkState(){
        if (networkState == null) {
            networkState = new MutableLiveData<>();
        }
        return networkState;
    }

    private void getFriendList() {

        RestApiFactory.cteateService(ConsumerStaplesRestAPI.class).fetchConsumerStaples()
                .enqueue(new Callback<List<ConsumerStaplesModel>>() {
                    @Override
                    public void onResponse(Call<List<ConsumerStaplesModel>> call, Response<List<ConsumerStaplesModel>> response) {
                        if (response.isSuccessful()) {
                            networkState.postValue(NetworkState.LOADED);
                            users.postValue(response.body());

                        } else {
                            networkState.postValue(new NetworkState(NetworkState.Status.FAILED, response.message()));
                        }
                    }

                    @Override
                    public void onFailure(Call<List<ConsumerStaplesModel>> call, Throwable t) {
                        String errorMessage = t == null ? "unknown error" : t.getMessage();
                        networkState.postValue(new NetworkState(NetworkState.Status.FAILED, errorMessage));
                    }
                });


    }
}
