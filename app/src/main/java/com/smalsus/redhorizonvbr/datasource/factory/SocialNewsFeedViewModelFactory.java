package com.smalsus.redhorizonvbr.datasource.factory;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.smalsus.redhorizonvbr.viewmodel.ProductItemViewModel;
import com.smalsus.redhorizonvbr.viewmodel.SocialNewsFeedViewModel;

public class SocialNewsFeedViewModelFactory implements ViewModelProvider.Factory {
    private String mParam;
    private boolean isMyFeed;
    private String userId;


    public SocialNewsFeedViewModelFactory(String param,boolean isMyFeed,String userId) {
        mParam = param;
        this.isMyFeed=isMyFeed;
        this.userId=userId;
    }


    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (modelClass.equals(SocialNewsFeedViewModel.class))
            return (T) new SocialNewsFeedViewModel(mParam,isMyFeed,userId);
        else if (modelClass.equals(ProductItemViewModel.class))
            return (T) new ProductItemViewModel(mParam);
        else
            return null;
    }
}