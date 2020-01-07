package com.smalsus.redhorizonvbr.adapters;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.smalsus.redhorizonvbr.view.fragments.ContactlistFragment;
import com.smalsus.redhorizonvbr.view.fragments.GroupListFragment;

public class ContactGroupTabAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;
    CharSequence[] tiles;
    public ContactGroupTabAdapter(FragmentManager fm, CharSequence[] mTiles,int NumOfTabs) {
        super(fm);
        this.tiles = mTiles;
        this.mNumOfTabs = NumOfTabs;
    }
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                ContactlistFragment tab1 = new ContactlistFragment();
                return tab1;
            case 1:
              GroupListFragment tab2 = new GroupListFragment();
                return tab2;
            default:
                return null;
        }
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return tiles[position];
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }

}
