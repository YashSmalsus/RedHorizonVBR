package com.smalsus.redhorizonvbr.UserProfilePostsGallery;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.smalsus.redhorizonvbr.HRpreference;
import com.smalsus.redhorizonvbr.R;
import com.smalsus.redhorizonvbr.model.EventUser;
import com.smalsus.redhorizonvbr.model.FriendList;
import com.smalsus.redhorizonvbr.networkrequest.EventRequest;
import com.smalsus.redhorizonvbr.view.activities.UserProfileScreen;
import com.smalsus.redhorizonvbr.viewmodel.ContactListViewModel;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class FollowersTab extends Fragment
{
    private List<String> fullNames, userNames;
    private List<Integer> profilePictures;
    private ContactListViewModel contactListViewModel;
    FollowersListAdapter followersListAdapter;
    private List<EventUser> userlist;
    List<String> userNamesList;

    private RecyclerView recyclerView;
    private TextView textView;
    EditText editText;
    Toolbar toolbar;



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.followers_fragment_tab, container, false);
        textView = view.findViewById(R.id.textView);

        recyclerView = view.findViewById(R.id.followersListRecyclerView);
        userNamesList = new ArrayList<>();
        EventRequest request = new EventRequest();
        request.requestForFriendList(HRpreference.getInstance(getContext()).getUserInfo().getId(), HRpreference.getInstance(getContext()).getUserInfo().getAuthToken(), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                int statusCode = response.code();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (statusCode == 200) {
                            String myresponse = null;
                            try {
                                myresponse = response.body().string();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            try {
                                Type listType = new TypeToken<List<FriendList>>() {
                                }.getType();
                                List<FriendList> yourList = new Gson().fromJson(myresponse, listType);
                                userlist = new ArrayList<>();
                                userlist.addAll(yourList.get(0).getAssociateList());
                                for (int i = 0; i < userlist.size(); i++)
                                {
                                    userNamesList.add(userlist.get(i).getUserName());
                                }
                                Intent intent = new Intent(getContext(), UserProfileScreen.class);
                                intent.putExtra("friendsCount", userlist.size());
                                textView.setVisibility(View.GONE);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
                            recyclerView.setLayoutManager(mLayoutManager);
                            followersListAdapter = new FollowersListAdapter(getActivity(), userlist, userNamesList);
                            recyclerView.setAdapter(followersListAdapter);
                        }
                    }
                });
            }
        });
        return view;
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.search_for_your_friends_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.searchForYourFriends);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                followersListAdapter.getFilter().filter(newText);
                return false;
            }
        });
    }
}

