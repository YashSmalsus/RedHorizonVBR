package com.smalsus.redhorizonvbr.datasource.factory;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.smalsus.redhorizonvbr.viewmodel.CommentItemViewModel;

public class CommentListItemViewModelFactory implements ViewModelProvider.Factory {
    private String mToken,mFeedId;


    public CommentListItemViewModelFactory(String param,String feedId) {
        mToken = param;
        mFeedId=feedId;
    }


    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (modelClass.equals(CommentItemViewModel.class))
            return (T) new CommentItemViewModel(mToken,mFeedId);
        else
            return null;
    }
}