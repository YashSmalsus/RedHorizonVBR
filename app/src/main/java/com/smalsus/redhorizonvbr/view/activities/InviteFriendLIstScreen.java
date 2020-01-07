package com.smalsus.redhorizonvbr.view.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.smalsus.redhorizonvbr.HRpreference;
import com.smalsus.redhorizonvbr.R;
import com.smalsus.redhorizonvbr.adapters.UserlistAdapter;
import com.smalsus.redhorizonvbr.model.Userlist;
import com.smalsus.redhorizonvbr.networkrequest.EventRequest;
import com.smalsus.redhorizonvbr.networkrequest.HrApiRequest;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class InviteFriendLIstScreen extends AppCompatActivity implements UserlistAdapter.ItemClickListener, View.OnClickListener {
    private List<Userlist> contactList;
    private UserlistAdapter userlistAdapter;
    private ProgressDialog dialog;
    private RecyclerView userlist;
    private ImageButton backbtninvite, inviteFromSocial;
    private EditText searchText;
    private TextView noUserFoundMessageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_friend_list_screen);
        dialog = new ProgressDialog(this);
        contactList = new ArrayList<>();
        userlist = findViewById(R.id.userlist);
        searchText = findViewById(R.id.searchEditText);
        backbtninvite = findViewById(R.id.backbtninvite);
        inviteFromSocial = findViewById(R.id.inviteFromSocial);
        noUserFoundMessageView = findViewById(R.id.noUserFoundMessageView);
        inviteFromSocial.setOnClickListener(this);
        backbtninvite.setOnClickListener(this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        userlist.setLayoutManager(mLayoutManager);
        getSuggestedUsers(HRpreference.getInstance(getApplicationContext()).getUserInfo().getAuthToken());
        searchText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch();
                return true;
            }
            return false;
        });

        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                filter(editable.toString());

            }
        });

    }

    private void performSearch() {
        getSearchUserList(searchText.getText().toString());
        searchText.clearFocus();
        InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(searchText.getWindowToken(), 0);
    }

    void filter(String text) {

        List<Userlist> temp = new ArrayList();
        for (Userlist d : contactList) {
            if (d.getfName() != null) {
                if (d.getfName().toLowerCase().contains(text.toLowerCase())) {
                    temp.add(d);
                }
            }
        }
        if (temp.size() > 0){
            userlistAdapter.updateList(temp);
            noUserFoundMessageView.setVisibility(View.GONE);
        }

    }


    private void getSearchUserList(String query) {
        HrApiRequest request = new HrApiRequest();
        dialog.setMessage("please wait");
        dialog.show();
        request.getSearchuserList(query, new Callback() {
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
                        Gson gson = new Gson();
                        Type type = new TypeToken<List<Userlist>>() {
                        }.getType();
                        List<Userlist> searchList = gson.fromJson(myresponse, type);
                        runOnUiThread(() -> {
                            userlistAdapter.updateList(searchList);
                            if (searchList.size() == 0)
                                noUserFoundMessageView.setVisibility(View.VISIBLE);
                            else {
                                noUserFoundMessageView.setVisibility(View.GONE);
                            }

                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    runOnUiThread(() -> {
                        List<Userlist> searchList = new ArrayList<>();
                        userlistAdapter.updateList(searchList);
                        if (searchList.size() == 0)
                            noUserFoundMessageView.setVisibility(View.VISIBLE);
                        else {
                            noUserFoundMessageView.setVisibility(View.GONE);
                        }

                    });
                }

            }
        });
    }

    private void getSuggestedUsers(String token) {
        HrApiRequest request = new HrApiRequest();
        dialog.setMessage("please wait");
        dialog.show();
        request.getSuggestedUserList(token, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                dialog.dismiss();
                call.cancel();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                dialog.dismiss();
                int statusCode = response.code();
                if (statusCode == 200) {
                    String myresponse = response.body().string();
                    try {
                        Gson gson = new Gson();
                        Type type = new TypeToken<List<Userlist>>() {
                        }.getType();
                        contactList = gson.fromJson(myresponse, type);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                runOnUiThread(() -> {
                    userlistAdapter = new UserlistAdapter(InviteFriendLIstScreen.this, contactList);
                    userlist.setAdapter(userlistAdapter);
                    userlistAdapter.setClickListener(InviteFriendLIstScreen.this);
                    userlistAdapter.notifyDataSetChanged();
                    if (contactList.size() == 0)
                        noUserFoundMessageView.setVisibility(View.VISIBLE);
                    else {
                        noUserFoundMessageView.setVisibility(View.GONE);
                    }
                });
            }
        });
    }

    private void sendFriendRequest(String to, String email) {
        EventRequest request = new EventRequest();
        dialog.setMessage("please wait");
        dialog.show();
        request.requestforSentFriend(HRpreference.getInstance(getApplicationContext()).getUserInfo().getId(), to, email, HRpreference.getInstance(getApplicationContext()).getUserInfo().getAuthToken(), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                dialog.dismiss();

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                int code = response.code();
                dialog.dismiss();
                if (code == 200) {
                    runOnUiThread(() -> Toast.makeText(InviteFriendLIstScreen.this, "Request Successfully Send", Toast.LENGTH_SHORT).show());

                } else if (code == 405) {
                    runOnUiThread(() -> Toast.makeText(InviteFriendLIstScreen.this, "Invite already sent", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    @Override
    public void onItemClick(View view, String Id, String email) {
        showConfirmMessage(Id, email);
    }

    private void showConfirmMessage(String ID, String email) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage("Do you  want to send Friend Request");
        builder1.setCancelable(true);
        builder1.setPositiveButton(
                "Confirm",
                (dialog, id) -> {
                    sendFriendRequest(ID, email);
                    dialog.cancel();
                });
        builder1.setNegativeButton(
                "Cancel",
                (dialog, id) -> dialog.cancel());
        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    @Override
    public void onClick(View view) {
        if (view == backbtninvite) {
            finish();
        } else if (view == inviteFromSocial) {
            Intent intent = new Intent(this, InviteFacebookFriend.class);
            startActivity(intent);
        }
    }
}
