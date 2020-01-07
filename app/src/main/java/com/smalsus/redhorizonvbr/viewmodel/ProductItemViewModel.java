package com.smalsus.redhorizonvbr.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import com.smalsus.redhorizonvbr.datasource.factory.ProductDataFactory;
import com.smalsus.redhorizonvbr.model.NetworkState;
import com.smalsus.redhorizonvbr.model.ProductItemModelClass;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ProductItemViewModel extends ViewModel {
    private LiveData<NetworkState> networkState;
    private LiveData<PagedList<ProductItemModelClass>> articleLiveData;
    private ProductDataFactory feedDataFactory;

    public ProductItemViewModel(String userToken) {
        init(userToken);
    }

    private void init(String userToken) {
        Executor executor = Executors.newFixedThreadPool(5);

        feedDataFactory = new ProductDataFactory(userToken);
        networkState = Transformations.switchMap(feedDataFactory.getMutableLiveData(),
                dataSource -> dataSource.getNetworkState());

        PagedList.Config pagedListConfig =
                (new PagedList.Config.Builder())
                        .setEnablePlaceholders(false)
                        .setPageSize(20).build();

        articleLiveData = (new LivePagedListBuilder(feedDataFactory, pagedListConfig))
                .setFetchExecutor(executor)
                .build();
    }


    public LiveData<NetworkState> getNetworkState() {
        return networkState;
    }

    public LiveData<PagedList<ProductItemModelClass>> getArticleLiveData() {
        return articleLiveData;
    }

    public void updateListData() {
        if (feedDataFactory != null && feedDataFactory.getMutableLiveData().getValue() != null) {
            feedDataFactory.getMutableLiveData().getValue().invalidate();
        }
    }


}

