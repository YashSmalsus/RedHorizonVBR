package com.smalsus.redhorizonvbr.view.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.smalsus.redhorizonvbr.HRpreference;
import com.smalsus.redhorizonvbr.R;
import com.smalsus.redhorizonvbr.adapters.Add_Attendeee_Adapter;
import com.smalsus.redhorizonvbr.model.EventUser;
import com.smalsus.redhorizonvbr.model.FriendList;
import com.smalsus.redhorizonvbr.networkrequest.EventRequest;
import com.smalsus.redhorizonvbr.networkrequest.HrApiRequest;
import com.smalsus.redhorizonvbr.view.fragments.GroupListFragment;

import org.json.JSONArray;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class CreateGroup extends AppCompatActivity implements View.OnClickListener {
    private ProgressDialog dialog;
    private ListView userlist;
    private ImageButton backbtn;
    private FloatingActionButton fab;
    private EditText groupname;
    private JSONArray jsonArray;
    private List<EventUser> friendlist;
    private Add_Attendeee_Adapter add_attendeee_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        friendlist = new ArrayList<>();
        backbtn = findViewById(R.id.backbtn);
        userlist = findViewById(R.id.userlist);
        backbtn.setOnClickListener(this);
        dialog = new ProgressDialog(this);
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(this);
        getFriendList(HRpreference.getInstance(getApplicationContext()).getUserInfo().getId(), HRpreference.getInstance(getApplicationContext()).getUserInfo().getAuthToken());
    }


    @Override
    public void onClick(View v) {
        if (v == backbtn) {
            finish();
        } else if (v == fab) {
            Collection<EventUser> userCollection = new HashSet<>(add_attendeee_adapter.getSelectedItems());
            List<EventUser> userList = new ArrayList<>(userCollection);
            jsonArray = new JSONArray();
            for (int i = 0; i < userList.size(); i++) {
                jsonArray.put(userList.get(i).getId());
            }
            jsonArray.put(HRpreference.getInstance(getApplicationContext()).getUserInfo().getId());
            if (userList.size() == 0) {
                Toast.makeText(this, "At least 1 contact must be selected", Toast.LENGTH_SHORT).show();
            } else {
                showPopUp();
            }
        }
    }

    private void CreatGroup(String name, String admin, JSONArray members, String token) {
        HrApiRequest request = new HrApiRequest();
        dialog.setMessage("please wait");
        dialog.show();
        request.requestForCreateGroup(name, admin, members, token, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                dialog.dismiss();
                call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) {
                dialog.dismiss();
                int statusCode = response.code();
                if (statusCode == 200) {
                    runOnUiThread(() -> {
                        GroupListFragment.isRefresh = true;
                        finish();
                    });


                } else {
                    runOnUiThread(() -> Toast.makeText(CreateGroup.this, "Please Try Again", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    public void showPopUp() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater layoutInflater = getLayoutInflater();
        View customView = layoutInflater.inflate(R.layout.groupnameitem, null);
        groupname = customView.findViewById(R.id.groupname);
        Button okbtn = customView.findViewById(R.id.popupbtn);
        final AlertDialog alertDialog = builder.create();
        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() { // define the 'Cancel' button
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();

            }
        });
        okbtn.setOnClickListener(v -> {
            if (!groupname.getText().toString().isEmpty()) {
                CreatGroup(groupname.getText().toString(), HRpreference.getInstance(getApplicationContext()).getUserInfo().getId(), jsonArray, HRpreference.getInstance(getApplicationContext()).getUserInfo().getAuthToken());

            } else {
                Toast.makeText(CreateGroup.this, "Please Enter Group name", Toast.LENGTH_SHORT).show();
            }
        });
        alertDialog.setView(customView);
        alertDialog.show();

    }

    private void getFriendList(String id, String token) {
        EventRequest request = new EventRequest();
        dialog.setMessage("please wait");
        dialog.show();
        request.requestForFriendList(id, token, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                dialog.dismiss();
                call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                dialog.dismiss();
                int statusCode = response.code();
                if (statusCode == 200) {
                    String myresponse = response.body().string();
                    try {
                        Type listType = new TypeToken<List<FriendList>>() {
                        }.getType();
                        List<FriendList> yourList = new Gson().fromJson(myresponse, listType);
                        friendlist.addAll(yourList.get(0).getAssociateList());

                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }
                runOnUiThread(() -> {
                    add_attendeee_adapter = new Add_Attendeee_Adapter(getApplicationContext(), friendlist);
                    userlist.setAdapter(add_attendeee_adapter);

                    add_attendeee_adapter.setSelectedItemsCountsChangedListener(count -> {

                    });
                    add_attendeee_adapter.notifyDataSetChanged();
                });
            }
        });
    }


}
