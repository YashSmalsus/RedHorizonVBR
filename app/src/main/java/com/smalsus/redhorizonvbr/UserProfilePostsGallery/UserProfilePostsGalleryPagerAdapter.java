package com.smalsus.redhorizonvbr.UserProfilePostsGallery;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class UserProfilePostsGalleryPagerAdapter extends FragmentStatePagerAdapter
{
    int tabCount;
    String userId;
    int screenType;

    public UserProfilePostsGalleryPagerAdapter(FragmentManager fm, int tabCount, String userId, int screenType) {
        super(fm);
        this.tabCount= tabCount;
        this.userId = userId;
        this.screenType = screenType;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position)
        {
            case 0:
                UserProfilePostsTab userProfilePostsTab = new UserProfilePostsTab(userId, screenType);
                return userProfilePostsTab;
            case 1:
                UserProfileGalleryTab userProfileGalleryTab = new UserProfileGalleryTab(userId, screenType);
                return userProfileGalleryTab;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabCount;
    }
}
