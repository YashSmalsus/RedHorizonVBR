package com.smalsus.redhorizonvbr.FollowersScreenFragments;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class FollowersFollowingPagerAdapter extends FragmentStatePagerAdapter
{
    private int tabCounts;
    public FollowersFollowingPagerAdapter(FragmentManager fm, int tabCounts) {
        super(fm);
        this.tabCounts = tabCounts;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position)
        {
            case 0:
                FollowersFragment followersFragment = new FollowersFragment();
                return followersFragment;
            case 1:
                FollowingFragment followingFragment = new FollowingFragment();
                return followingFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabCounts;
    }
}
