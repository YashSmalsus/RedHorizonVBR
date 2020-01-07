package com.smalsus.redhorizonvbr.UserProfilePostsGallery;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.tabs.TabLayout;
import com.smalsus.redhorizonvbr.R;
import com.smalsus.redhorizonvbr.databinding.FollowersActivityDataBinding;
import com.smalsus.redhorizonvbr.model.EventUser;

import java.util.List;

public class FollowersActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener, FollowingFragment.OnFragmentInteractionListener {

    private FollowersActivityDataBinding followersActivityDataBinding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        followersActivityDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_followers);
        followersActivityDataBinding.userNameHeading.setText(getIntent().getExtras().getString("userName"));
        followersActivityDataBinding.backButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                }
        );

        followersActivityDataBinding.followersFollowingTabLayout.addTab(followersActivityDataBinding.followersFollowingTabLayout.newTab().setText("followers"));
        followersActivityDataBinding.followersFollowingTabLayout.addTab(followersActivityDataBinding.followersFollowingTabLayout.newTab().setText("following"));
        followersActivityDataBinding.followersFollowingTabLayout.setSelectedTabIndicatorColor(Color.parseColor("#151515"));
        FriendsViewPagerAdapter friendsViewPagerAdapter = new FriendsViewPagerAdapter(getSupportFragmentManager(),followersActivityDataBinding.followersFollowingTabLayout.getTabCount());
        followersActivityDataBinding.followersFollowingViewPager.setAdapter(friendsViewPagerAdapter);
        followersActivityDataBinding.followersFollowingViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(followersActivityDataBinding.followersFollowingTabLayout));
        followersActivityDataBinding.followersFollowingTabLayout.setOnTabSelectedListener(this);

    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        followersActivityDataBinding.followersFollowingViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
