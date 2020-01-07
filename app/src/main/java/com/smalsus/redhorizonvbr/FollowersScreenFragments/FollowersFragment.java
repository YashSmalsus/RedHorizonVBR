package com.smalsus.redhorizonvbr.FollowersScreenFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.smalsus.redhorizonvbr.HRpreference;
import com.smalsus.redhorizonvbr.R;
import com.smalsus.redhorizonvbr.adapters.FollowersListAdapter;
import com.smalsus.redhorizonvbr.adapters.FriendListAdapter;
import com.smalsus.redhorizonvbr.model.EventUser;
import com.smalsus.redhorizonvbr.viewmodel.ContactListViewModel;

import java.util.ArrayList;
import java.util.List;

public class FollowersFragment extends Fragment
{

    private List<EventUser> followersList;
    private RecyclerView recyclerView;
    private ContactListViewModel contactListViewModel;
    private FollowersListAdapter friendListAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        followersList = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        recyclerView = container.findViewById(R.id.followersListRecyclerView);
        recyclerView.setLayoutManager(mLayoutManager);
        friendListAdapter = new FollowersListAdapter(getActivity(), followersList);
        recyclerView.setAdapter(friendListAdapter);
        return inflater.inflate(R.layout.followers_tab,container, false);
    }
}
