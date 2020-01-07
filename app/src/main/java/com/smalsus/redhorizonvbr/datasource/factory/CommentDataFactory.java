package com.smalsus.redhorizonvbr.datasource.factory;

import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;
import com.smalsus.redhorizonvbr.datasource.CommentDataSource;
public class CommentDataFactory  extends DataSource.Factory {

    private MutableLiveData<CommentDataSource> mutableLiveData;
    private CommentDataSource feedDataSource;
    private String userToken;
    private String feedId;

    public CommentDataFactory(String userToken,String feedId) {
        this.mutableLiveData = new MutableLiveData<>();
        this.userToken=userToken;
        this.feedId=feedId;
    }

    @Override
    public DataSource create() {
        feedDataSource = new CommentDataSource(userToken,feedId);
        mutableLiveData.postValue(feedDataSource);
        return feedDataSource;
    }


    public MutableLiveData<CommentDataSource> getMutableLiveData() {
        return mutableLiveData;
    }

}