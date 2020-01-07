package com.smalsus.redhorizonvbr.adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.smalsus.redhorizonvbr.model.EventUser;
import com.smalsus.redhorizonvbr.view.fragments.Vbr_Fragment;
import com.smalsus.redhorizonvbr.view.fragments.Vbr_map;

import java.util.List;

public class VbrTabChanger  extends FragmentStatePagerAdapter {
    int mNumOfTabs;
    private List<EventUser> eventUserList;
    public VbrTabChanger(FragmentManager fm, int NumOfTabs, List<EventUser> eventUserList) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.eventUserList=eventUserList;
    }
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                Vbr_map tab1 =  Vbr_map.newInstance(eventUserList);
                return tab1;
            case 1:
                Vbr_Fragment tab2 = new Vbr_Fragment();
                return tab2;
            default:
                return null;
        }
    }
    @Override
    public int getCount() {
        return mNumOfTabs;
    }

}
