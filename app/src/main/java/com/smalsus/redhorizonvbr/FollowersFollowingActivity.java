package com.smalsus.redhorizonvbr;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.smalsus.redhorizonvbr.FollowersScreenFragments.FollowersFollowingPagerAdapter;

public class FollowersFollowingActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener{

    public static String userName = "user_name";
    private TabLayout tabLayout;
    private ViewPager viewPager;

    private static String FOLLOWERS = "FOLLOWERS";
    private static String FOLLOWING = "FOLLOWING";

    private TextView userNameTextViewHeader;
    @Override
     public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followers_following);

        tabLayout = findViewById(R.id.followersFollowingTabLayout);
        viewPager = findViewById(R.id.followersFollowingViewPager);

        userNameTextViewHeader = findViewById(R.id.followersFollowingUserName);
        userNameTextViewHeader.setText(getIntent().getStringExtra(String.format("@%s", userName)));

        tabLayout.addTab(tabLayout.newTab().setText(FOLLOWERS));
        tabLayout.addTab(tabLayout.newTab().setText(FOLLOWING));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setTabTextColors(ColorStateList.valueOf(Color.WHITE));
        tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#151515"));
        viewPager.setOffscreenPageLimit(3);
        viewPager.setPageMargin(20);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        FollowersFollowingPagerAdapter adapter = new FollowersFollowingPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());

        //Adding adapter to pager
        viewPager.setAdapter(adapter);

        //Adding onTabSelectedListener to swipe views
        tabLayout.setOnTabSelectedListener(this);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab)
    {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab)
    {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab)
    {

    }
}
