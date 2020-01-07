package com.smalsus.redhorizonvbr.view.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.smalsus.redhorizonvbr.HRpreference;
import com.smalsus.redhorizonvbr.R;
import com.smalsus.redhorizonvbr.adapters.GrouplistAdapter;
import com.smalsus.redhorizonvbr.databinding.GroupListFragmentBinding;
import com.smalsus.redhorizonvbr.model.EventUser;
import com.smalsus.redhorizonvbr.model.GroupModel;
import com.smalsus.redhorizonvbr.model.UserInfo;
import com.smalsus.redhorizonvbr.view.activities.ChatScreen;
import com.smalsus.redhorizonvbr.view.activities.GroupDetailsScreen;
import com.smalsus.redhorizonvbr.viewmodel.GroupListViewModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.smalsus.redhorizonvbr.view.activities.GroupDetailsScreen.GROUPDETAILSDATA;

public class GroupListFragment extends Fragment implements GrouplistAdapter.ItemClickListener {
    public static boolean isRefresh = false;
    private List<GroupModel> groupList;
    private GrouplistAdapter grouplistAdapter;
    private OnFragmentInteractionListener mListener;
    private GroupListViewModel groupListViewModel;
    private GroupListFragmentBinding groupListFragmentBinding;

    public GroupListFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static GroupListFragment newInstance() {
        GroupListFragment fragment = new GroupListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        groupList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        groupListFragmentBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_group_list, container, false);
        View view = groupListFragmentBinding.getRoot();
        RecyclerView grouplist = view.findViewById(R.id.grouplist);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        grouplist.setLayoutManager(mLayoutManager);
        grouplistAdapter = new GrouplistAdapter(getContext(), groupList);
        grouplist.setAdapter(grouplistAdapter);
        grouplistAdapter.setClickListener(GroupListFragment.this);
        groupListFragmentBinding.swipeRefreshLayout.setRefreshing(true);
        groupListFragmentBinding.swipeRefreshLayout.setOnRefreshListener(this::getGroupList);
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed() {
        if (mListener != null) {
            //  mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        groupListViewModel = ViewModelProviders.of(this).get(GroupListViewModel.class);
        UserInfo userInfo = HRpreference.getInstance(getContext()).getUserInfo();
        groupListViewModel.getGroup(userInfo.getId(), userInfo.getAuthToken()).observe(this, groupModels -> {
            grouplistAdapter.setDatalist(groupModels);
            groupListFragmentBinding.swipeRefreshLayout.setRefreshing(false);
        });
        getGroupList();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    private void getGroupList() {
        UserInfo userInfo = HRpreference.getInstance(getContext()).getUserInfo();
        groupListViewModel.getGroupList(userInfo.getId(), userInfo.getAuthToken());
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isRefresh) {
            getGroupList();
            isRefresh = false;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }


    @Override
    public void onItemClick(int position, int type) {
//        showDetails(position);
        if (type == 1) {
            Intent intent = new Intent(getContext(), ChatScreen.class);
            intent.putExtra(ChatScreen.GROUP_DETAILS_DATA, groupList.get(position));
            intent.putExtra(ChatScreen.ISGROUP_CHAT, true);
            startActivity(intent);
        } else {
            Intent intent = new Intent(getContext(), GroupDetailsScreen.class);
            intent.putExtra(GROUPDETAILSDATA, groupList.get(position));
            startActivity(intent);
        }

    }

    private void showDetails(int id) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.grouplistdetails, null);
        TextView groupname = dialogView.findViewById(R.id.group_name);
        TextView groupAdmin = dialogView.findViewById(R.id.group_admin);
        TextView groupmember = dialogView.findViewById(R.id.member_group);
        Button call_btn = dialogView.findViewById(R.id.call_group_btn);
        groupname.setText(groupList.get(id).getName());
        groupAdmin.setText(groupList.get(id).getEventUser().getfName());
        StringBuilder membersname = new StringBuilder();
        for (int i = 0; i < groupList.get(id).getEventmember().size(); i++) {
            if (i < groupList.get(id).getEventmember().size() - 1) {
                membersname.append(groupList.get(id).getEventmember().get(i).getfName()).append(" ,");
            } else {
                membersname.append(groupList.get(id).getEventmember().get(i).getfName()).append(" ");
            }
        }
        groupmember.setText(membersname.toString());
        dialogBuilder.setView(dialogView);
        AlertDialog alertDialog = dialogBuilder.create();
        call_btn.setOnClickListener(view -> {
            ArrayList<String> videolIDist = new ArrayList<>();
            for (int i = 0; i < groupList.get(id).getEventmember().size(); i++) {
                String videoIDv = groupList.get(id).getEventmember().get(i).getVideoId();
                videolIDist.add(videoIDv);
            }
            if (videolIDist.size() > 0) {
                mListener.onFragmentInteraction(5, false, false, videolIDist, groupList.get(id).getEventmember());
                alertDialog.dismiss();
            }

        });
        alertDialog.show();
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(int screntType, boolean isVIDEO, boolean isVBR, ArrayList<String> userIDs, List<EventUser> eventmember);
    }

}
