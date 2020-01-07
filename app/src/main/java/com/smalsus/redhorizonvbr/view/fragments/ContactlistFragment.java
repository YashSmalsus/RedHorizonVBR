package com.smalsus.redhorizonvbr.view.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.smalsus.redhorizonvbr.GlideUtils;
import com.smalsus.redhorizonvbr.HRpreference;
import com.smalsus.redhorizonvbr.R;
import com.smalsus.redhorizonvbr.adapters.FriendListAdapter;
import com.smalsus.redhorizonvbr.databinding.ContactListFragmentBinding;
import com.smalsus.redhorizonvbr.model.EventUser;
import com.smalsus.redhorizonvbr.view.activities.CreateGroup;
import com.smalsus.redhorizonvbr.view.activities.InviteFriendLIstScreen;
import com.smalsus.redhorizonvbr.view.activities.UserProfileScreen;
import com.smalsus.redhorizonvbr.viewmodel.ContactListViewModel;

import java.util.ArrayList;
import java.util.List;

public class ContactlistFragment extends Fragment implements View.OnClickListener, FriendListAdapter.ContactItemClickListener {
    private ProgressDialog dialog;
    private RecyclerView userlist;
    private Button creategroup, invitefriend;
    private OnFragmentInteractionListener mListener;
    private List<EventUser> friendlist;
    private FriendListAdapter friendListAdapter;
    private ContactListFragmentBinding contactListFragmentBinding;
    private ContactListViewModel contactListViewModel;
    private RelativeLayout relativeLayout;

    public ContactlistFragment() {

    }

    public static ContactlistFragment newInstance(String param1, String param2) {
        ContactlistFragment fragment = new ContactlistFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        friendlist = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        contactListFragmentBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_contactlist, container, false);
        View view = contactListFragmentBinding.getRoot();
        userlist = view.findViewById(R.id.contactList);
        creategroup = view.findViewById(R.id.creategroup);
        relativeLayout = view.findViewById(R.id.noCreatedYet);
        creategroup.setOnClickListener(this);
        invitefriend = view.findViewById(R.id.invitefriend);
        invitefriend.setOnClickListener(this);
        dialog = new ProgressDialog(getContext());
//        if(friendlist.size() == 0)
//        {
//            userlist.setVisibility(View.GONE);
//            relativeLayout.setVisibility(View.VISIBLE);
//        }
       /* else
        {*/
            //relativeLayout.setVisibility(View.GONE);
            //userlist.setVisibility(View.VISIBLE);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
            userlist.setLayoutManager(mLayoutManager);
            friendListAdapter = new FriendListAdapter(getActivity(), friendlist);
            userlist.setAdapter(friendListAdapter);
            friendListAdapter.setClickListener(this);
        /*}*/

        contactListFragmentBinding.swipeRefreshLayout.setRefreshing(true);
        contactListFragmentBinding.swipeRefreshLayout.setOnRefreshListener(this::getFriendList);
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            //  mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        contactListViewModel = ViewModelProviders.of(this).get(ContactListViewModel.class);
        contactListViewModel.getUsers(HRpreference.getInstance(getContext()).getUserInfo().getId(), HRpreference.getInstance(getContext()).getUserInfo().getAuthToken()).observe(this, eventUsers -> {
            contactListFragmentBinding.swipeRefreshLayout.setRefreshing(false);
            friendListAdapter.updateEmployeeListItems(eventUsers);
        });

    }
    private void getFriendList(){
        contactListViewModel.getUsers(HRpreference.getInstance(getContext()).getUserInfo().getId(), HRpreference.getInstance(getContext()).getUserInfo().getAuthToken());
    }
//
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
    public void onClick(View view) {
        if (view == creategroup) {
            Intent intent = new Intent(getContext(), CreateGroup.class);
            startActivity(intent);
        } else if (view == invitefriend) {
            Intent intent = new Intent(getContext(), InviteFriendLIstScreen.class);
            startActivity(intent);
        }

    }


    @Override
    public void onItemClick(String userID, EventUser eventUser, int id, int chatType) {
        if (id == 1) {
            showConfirmMessage(userID, eventUser, chatType);
        } else if (id == 2) {
            showDetails(userID, eventUser, chatType);
        }
    }

    private void showConfirmMessage(String userID, EventUser eventUser, int chatType) {

        mListener.contactListFragIntraction(true, false, userID, eventUser, chatType);

//        AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
//        builder1.setMessage("Do you  want to call to " + eventUser.getfName()+" "+eventUser.getlName());
//        builder1.setCancelable(true);
//        builder1.setPositiveButton(
//                "Yes",
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//
//                        dialog.cancel();
//                    }
//                });
//        builder1.setNegativeButton(
//                "No",
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        dialog.cancel();
//                    }
//                });
//        AlertDialog alert11 = builder1.create();
//        alert11.show();
    }

    private void showDetails(String id, EventUser eventUser, int callType) {
        Intent intent = new Intent(getContext(), UserProfileScreen.class);
        intent.putExtra(UserProfileScreen.USER_DETAILS_ID, eventUser.getId());
        intent.putExtra("ScreenType",2);
        startActivity(intent);

    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void contactListFragIntraction(boolean isVIDEO, boolean isVBR, String userIDs, EventUser eventmember, int chatType);
    }


}
