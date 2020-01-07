package com.smalsus.redhorizonvbr.UserProfilePostsGallery;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class FriendsViewPagerAdapter extends FragmentStatePagerAdapter
{
    int tabCount;
    String userId;
    int screenType;

    public FriendsViewPagerAdapter(FragmentManager fm, int tabCount) {
        super(fm);
        this.tabCount= tabCount;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position)
        {
            case 0:
                return new FollowersTab();
            case 1:
                return new FollowingFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabCount;
    }
}
