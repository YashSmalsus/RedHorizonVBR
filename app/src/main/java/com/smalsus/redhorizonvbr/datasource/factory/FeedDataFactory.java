package com.smalsus.redhorizonvbr.datasource.factory;


import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;

import com.smalsus.redhorizonvbr.datasource.FeedDataSource;

public class FeedDataFactory extends DataSource.Factory {

    private MutableLiveData<FeedDataSource> mutableLiveData;
    private FeedDataSource feedDataSource;
    private String userToken;
    private boolean isMyNewsFeed;
    private String userId;

    public FeedDataFactory(String userToken,boolean isMyNewsFeedFactory,String userId) {
        this.mutableLiveData = new MutableLiveData<FeedDataSource>();
        this.userToken=userToken;
        this.isMyNewsFeed=isMyNewsFeedFactory;
        this.userId=userId;
    }

    @Override
    public DataSource create() {
        feedDataSource = new FeedDataSource(userToken,isMyNewsFeed,userId);
        mutableLiveData.postValue(feedDataSource);
        return feedDataSource;
    }


    public MutableLiveData<FeedDataSource> getMutableLiveData() {
        return mutableLiveData;
    }
}
