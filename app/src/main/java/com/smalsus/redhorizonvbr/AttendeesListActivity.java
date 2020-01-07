package com.smalsus.redhorizonvbr;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.smalsus.redhorizonvbr.databinding.AttendeesListActivityBinding;
import com.smalsus.redhorizonvbr.model.EventUser;
import com.smalsus.redhorizonvbr.model.FriendList;
import com.smalsus.redhorizonvbr.networkrequest.EventRequest;
import com.smalsus.redhorizonvbr.view.activities.AddEventScreen;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AttendeesListActivity extends AppCompatActivity implements onSelectAttendeeContactListener{

    private AttendeesListActivityBinding activityBinding;
    private List<EventUser> userlist;
    private AttendeesSelectorAdaptor attendeesSelectorAdaptor;
    private onSelectAttendeeContactListener onSelectAttendeeContactListener;
    public String names = "";
    public StringBuilder stringBuilder = new StringBuilder();
    public ArrayList<String> stringArrayList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityBinding = DataBindingUtil.setContentView(this, R.layout.activity_attendees_list);
        userlist = new ArrayList<>();
        EventRequest request = new EventRequest();
        request.requestForFriendList(HRpreference.getInstance(this).getUserInfo().getId(), HRpreference.getInstance(this).getUserInfo().getAuthToken(), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                int statusCode = response.code();
                runOnUiThread(new Runnable() {
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
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(AttendeesListActivity.this, RecyclerView.VERTICAL, false);
                            activityBinding.attendeesList.setLayoutManager(layoutManager);
                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(AttendeesListActivity.this, RecyclerView.VERTICAL, false);
                            attendeesSelectorAdaptor = new AttendeesSelectorAdaptor(userlist, AttendeesListActivity.this);
                            activityBinding.attendeesList.setAdapter(attendeesSelectorAdaptor);
                        }
                    }
                });
            }
        });
        activityBinding.backToAddEventScreen.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                }
        );
    }

    @Override
    public void onGetSelectedAttendeeUserName(EventUser selectedUserName)
    {

        activityBinding.attendeesIconsContainer.setVisibility(View.VISIBLE);
        activityBinding.iconsListSeparator.setVisibility(View.VISIBLE);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.add_event_contact_added_item_list, null);
        TextView usrName=rowView.findViewById(R.id.selectedContactName);
        CircularImageView circularImageView = rowView.findViewById(R.id.selectedContactUserProfileImage);
        rowView.setTag(selectedUserName.getId());
        usrName.setText(String.format("%s %s", selectedUserName.getfName(), selectedUserName.getlName()));
        Picasso.get().load(selectedUserName.getImageUrl()).into(circularImageView);
        activityBinding.selectedAttendeeNames.setText(stringBuilder.toString());
        //rowView.findViewWithTag(rowView.getTag());
        activityBinding.attendeesIconsContainer.addView(rowView);
        if (!stringArrayList.contains(selectedUserName.getUserName()))
        {
            stringArrayList.add(selectedUserName.getUserName());
        }
        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activityBinding.attendeesIconsContainer.removeView(v);
                stringArrayList.remove(selectedUserName.getUserName());
            }
        });

        //Converting ArrayList into StringBuilder
        for (int i = 0; i < stringArrayList.size(); i++)
        {
            stringBuilder.append(stringArrayList.get(i)).append(", ");
        }

        activityBinding.sendAttendeesList.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(AttendeesListActivity.this, AddEventScreen.class);
                        intent.putExtra("attendeesList", stringBuilder.toString());
                        startActivity(intent);
                    }
                }
        );
    }

    public void removeSelectedAttendee(View view)
    {
      //  activityBinding.attendeesIconsContainer.removeView((View) view.getParent());
    }
}
