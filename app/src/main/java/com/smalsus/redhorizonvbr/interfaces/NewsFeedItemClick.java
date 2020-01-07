package com.smalsus.redhorizonvbr.interfaces;
import com.smalsus.redhorizonvbr.model.SocialFeedModel;

public interface NewsFeedItemClick {
// 1 - Commnet  , 2 for Likes, 3 for Delete
    void newsFeedItemClicked(SocialFeedModel socialFeedModel,int type,int position);
}
