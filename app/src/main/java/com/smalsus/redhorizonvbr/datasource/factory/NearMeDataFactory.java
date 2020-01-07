package com.smalsus.redhorizonvbr.datasource.factory;


import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;

import com.smalsus.redhorizonvbr.datasource.NearMeDataSource;

public class NearMeDataFactory extends DataSource.Factory {

    private MutableLiveData<NearMeDataSource> mutableLiveData;
    private NearMeDataSource nearMeDataSource;

    public NearMeDataFactory() {
        this.mutableLiveData = new MutableLiveData<NearMeDataSource>();
    }

    @Override
    public DataSource create() {
        nearMeDataSource = new NearMeDataSource();
        mutableLiveData.postValue(nearMeDataSource);
        return nearMeDataSource;
    }


    public MutableLiveData<NearMeDataSource> getMutableLiveData() {
        return mutableLiveData;
    }
}
