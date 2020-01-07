package com.smalsus.redhorizonvbr.viewmodel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.smalsus.redhorizonvbr.model.ConsumerStaplesSubcategoryItem;
import com.smalsus.redhorizonvbr.model.NetworkState;
import com.smalsus.redhorizonvbr.rest.ConsumerStaplesSunItemAPI;
import com.smalsus.redhorizonvbr.rest.RestApiFactory;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConsumerStaplesSubItemViewModel  extends ViewModel {
    private MutableLiveData<List<ConsumerStaplesSubcategoryItem>> users;
    private MutableLiveData<NetworkState> networkState;

    public LiveData<List<ConsumerStaplesSubcategoryItem>> getStaplesSubItem(int value) {
        if (users == null) {
            users = new MutableLiveData<>();
            getFriendList(value);
        }
        return users;
    }

    public LiveData<NetworkState> getNetworkState(){
        if (networkState == null) {
            networkState = new MutableLiveData<>();
        }
        return networkState;
    }

    private void getFriendList(int value) {
        RestApiFactory.cteateService(ConsumerStaplesSunItemAPI.class).fetchConsumerStaples(value)
                .enqueue(new Callback<List<ConsumerStaplesSubcategoryItem>>() {
                    @Override
                    public void onResponse(Call<List<ConsumerStaplesSubcategoryItem>> call, Response<List<ConsumerStaplesSubcategoryItem>> response) {
                        if (response.isSuccessful()) {
                            networkState.postValue(NetworkState.LOADED);
                            users.postValue(response.body());

                        } else {
                            networkState.postValue(new NetworkState(NetworkState.Status.FAILED, response.message()));
                        }
                    }

                    @Override
                    public void onFailure(Call<List<ConsumerStaplesSubcategoryItem>> call, Throwable t) {
                        String errorMessage = t == null ? "unknown error" : t.getMessage();
                        networkState.postValue(new NetworkState(NetworkState.Status.FAILED, errorMessage));
                    }
                });


    }
}

