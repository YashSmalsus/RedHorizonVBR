package com.smalsus.redhorizonvbr.viewmodel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;
import com.smalsus.redhorizonvbr.datasource.factory.FeedDataFactory;
import com.smalsus.redhorizonvbr.model.NetworkState;
import com.smalsus.redhorizonvbr.model.SocialFeedModel;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class SocialNewsFeedViewModel extends ViewModel {
    private LiveData<NetworkState> networkState;
    private LiveData<PagedList<SocialFeedModel>> articleLiveData;
    private FeedDataFactory feedDataFactory;
    public SocialNewsFeedViewModel(String userToken,boolean isMyNewsFeed,String userId) {
        init(userToken,isMyNewsFeed,userId);
    }

    private void init(String userToken,boolean isMyNewsFeed,String userId) {
        Executor executor = Executors.newFixedThreadPool(5);
        feedDataFactory = new FeedDataFactory(userToken,isMyNewsFeed,userId);
        networkState = Transformations.switchMap(feedDataFactory.getMutableLiveData(),
                dataSource -> dataSource.getNetworkState());

        PagedList.Config pagedListConfig =
                (new PagedList.Config.Builder())
                        .setEnablePlaceholders(false)
                        .setInitialLoadSizeHint(10)
                        .setPageSize(20).build();

        articleLiveData = (new LivePagedListBuilder(feedDataFactory, pagedListConfig))
                .setFetchExecutor(executor)
                .build();
    }


    public LiveData<NetworkState> getNetworkState() {
        return networkState;
    }

    public LiveData<PagedList<SocialFeedModel>> getArticleLiveData() {
        return articleLiveData;
    }

    public void updateListData(){
        if(feedDataFactory!=null && feedDataFactory.getMutableLiveData().getValue()!=null) {
            feedDataFactory.getMutableLiveData().getValue().invalidate();
        }
    }




}
