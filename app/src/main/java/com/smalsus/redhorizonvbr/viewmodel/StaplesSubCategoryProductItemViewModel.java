package com.smalsus.redhorizonvbr.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.smalsus.redhorizonvbr.model.NetworkState;
import com.smalsus.redhorizonvbr.model.ProductItemModelClass;
import com.smalsus.redhorizonvbr.rest.RestApiFactory;
import com.smalsus.redhorizonvbr.rest.SearchProductItemAPI;
import com.smalsus.redhorizonvbr.rest.StaplesSubCategoryProductItemAPI;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StaplesSubCategoryProductItemViewModel extends ViewModel {
    private MutableLiveData<List<ProductItemModelClass>> users;
    private MutableLiveData<NetworkState> networkState;

    public LiveData<List<ProductItemModelClass>> getStaplesSubItem(int cat,int subCat) {
        if (users == null) {
            users = new MutableLiveData<>();
            fetchSubCategoryItemList(cat,subCat);
        }
        return users;
    }

    public LiveData<NetworkState> getNetworkState(){
        if (networkState == null) {
            networkState = new MutableLiveData<>();
        }
        return networkState;
    }

    private void fetchSubCategoryItemList(int category,int subCategory) {
        RestApiFactory.cteateService(StaplesSubCategoryProductItemAPI.class).fetchSubCategoryItemList(category,subCategory)
                .enqueue(new Callback<List<ProductItemModelClass>>() {
                    @Override
                    public void onResponse(Call<List<ProductItemModelClass>> call, Response<List<ProductItemModelClass>> response) {
                        if (response.isSuccessful()) {
                            networkState.postValue(NetworkState.LOADED);
                            users.postValue(response.body());

                        } else {
                            networkState.postValue(new NetworkState(NetworkState.Status.FAILED, response.message()));
                        }
                    }

                    @Override
                    public void onFailure(Call<List<ProductItemModelClass>> call, Throwable t) {
                        String errorMessage = t == null ? "unknown error" : t.getMessage();
                        networkState.postValue(new NetworkState(NetworkState.Status.FAILED, errorMessage));
                    }
                });


    }
    public void searchItem(String querry) {
        RestApiFactory.cteateService(SearchProductItemAPI.class).fetchSubCategoryItemList(querry)
                .enqueue(new Callback<List<ProductItemModelClass>>() {
                    @Override
                    public void onResponse(Call<List<ProductItemModelClass>> call, Response<List<ProductItemModelClass>> response) {
                        if (response.isSuccessful()) {
                            networkState.postValue(NetworkState.LOADED);
                            users.postValue(response.body());

                        } else {
                            networkState.postValue(new NetworkState(NetworkState.Status.FAILED, response.message()));
                        }
                    }

                    @Override
                    public void onFailure(Call<List<ProductItemModelClass>> call, Throwable t) {
                        String errorMessage = t == null ? "unknown error" : t.getMessage();
                        networkState.postValue(new NetworkState(NetworkState.Status.FAILED, errorMessage));
                    }
                });


    }
}


