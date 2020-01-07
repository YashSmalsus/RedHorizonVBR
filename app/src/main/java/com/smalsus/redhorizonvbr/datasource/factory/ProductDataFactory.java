package com.smalsus.redhorizonvbr.datasource.factory;

import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;

import com.smalsus.redhorizonvbr.datasource.ProductDataSource;

public class ProductDataFactory extends DataSource.Factory {

    private MutableLiveData<ProductDataSource> mutableLiveData;
    private ProductDataSource feedDataSource;
    private String userToken;

    public ProductDataFactory(String userToken) {
        this.mutableLiveData = new MutableLiveData<>();
        this.userToken = userToken;
    }

    @Override
    public DataSource create() {
        feedDataSource = new ProductDataSource(userToken);
        mutableLiveData.postValue(feedDataSource);
        return feedDataSource;
    }


    public MutableLiveData<ProductDataSource> getMutableLiveData() {
        return mutableLiveData;
    }
}
