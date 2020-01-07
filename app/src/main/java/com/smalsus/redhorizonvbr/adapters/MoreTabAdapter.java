package com.smalsus.redhorizonvbr.adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.smalsus.redhorizonvbr.view.fragments.CallLogFragment;
import com.smalsus.redhorizonvbr.view.fragments.MyRecordingFragment;

public class MoreTabAdapter   extends FragmentStatePagerAdapter {
    int mNumOfTabs;
    public MoreTabAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                CallLogFragment tab2 = new CallLogFragment();
                return tab2;
            case 1:
                MyRecordingFragment tab3 = new MyRecordingFragment();
                return tab3;
            default:
                return null;
        }
    }
    @Override
    public int getCount() {
        return mNumOfTabs;
    }

}
