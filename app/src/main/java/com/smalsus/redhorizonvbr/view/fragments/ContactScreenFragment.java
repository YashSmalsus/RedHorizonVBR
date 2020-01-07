package com.smalsus.redhorizonvbr.view.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.tabs.TabLayout;
import com.smalsus.redhorizonvbr.R;
import com.smalsus.redhorizonvbr.adapters.ContactGroupTabAdapter;
import com.smalsus.redhorizonvbr.model.UpdateGroupListEvent;
import com.smalsus.redhorizonvbr.view.activities.CreateGroup;
import com.smalsus.redhorizonvbr.view.activities.InviteFriendLIstScreen;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class ContactScreenFragment extends Fragment implements View.OnClickListener {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private ImageButton addbtn;
    private int currentPage;
    private CharSequence[] tiles = {"CONTACTS", "GROUPS"};
    private OnFragmentInteractionListener mListener;
    public ContactScreenFragment() {
        // Required empty public constructor
    }
    public static ContactScreenFragment newInstance(String param1, String param2) {
        ContactScreenFragment fragment = new ContactScreenFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.fragment_contact_screen, container, false);
        addbtn=view.findViewById(R.id.addbtn);
        addbtn.setOnClickListener(this);
        TabLayout tabLayout =  view.findViewById(R.id.tab_layout);
        final ViewPager viewPager =  view.findViewById(R.id.pager);
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.connections_friends_icon));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.connections_groups_icon));
        //tabLayout.addTab(tabLayout.newTab().setText("Contact"));
        //tabLayout.addTab(tabLayout.newTab().setText("Group"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        ContactGroupTabAdapter adapter = new ContactGroupTabAdapter(getActivity().getSupportFragmentManager(), tiles, tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        tabLayout.setTabTextColors(ColorStateList.valueOf(Color.WHITE));
        tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#151515"));
        viewPager.setOffscreenPageLimit(3);
        viewPager.setPageMargin(20);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                viewPager.setCurrentItem(tab.getPosition());
                currentPage=tab.getPosition();
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(UpdateGroupListEvent event) {
       // getGroupList(HRpreference.getInstance(getContext().getApplicationContext()).getUserInfo().getId(), HRpreference.getInstance(getContext().getApplicationContext()).getUserInfo().getAuthToken());
        Toast.makeText(getContext(), event.message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View view) {
        if (view == addbtn) {
            if (currentPage == 0) {
                Intent intent = new Intent(getContext(), InviteFriendLIstScreen.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(getContext(), CreateGroup.class);
                startActivity(intent);
            }
        }
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
