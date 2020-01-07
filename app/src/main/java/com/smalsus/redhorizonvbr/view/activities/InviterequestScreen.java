package com.smalsus.redhorizonvbr.view.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.smalsus.redhorizonvbr.HRpreference;
import com.smalsus.redhorizonvbr.R;
import com.smalsus.redhorizonvbr.adapters.InviteAdapter;
import com.smalsus.redhorizonvbr.model.InviteForm;
import com.smalsus.redhorizonvbr.networkrequest.EventRequest;
import com.smalsus.redhorizonvbr.networkrequest.HrApiRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class InviterequestScreen extends AppCompatActivity implements View.OnClickListener , InviteAdapter.ItemClickListener {
    private ImageButton invitebackbtn;
    private RecyclerView invitelist;
    private InviteAdapter inviteAdapter;
    private ProgressDialog dialog;
    private List<InviteForm> InviteList;
    private List<InviteForm.Sender>senderList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inviterequest_screen);
        InviteList = new ArrayList<>();
        senderList= new ArrayList<>();
        dialog = new ProgressDialog(this);
        invitebackbtn = findViewById(R.id.invitebackbtn);
        invitebackbtn.setOnClickListener(this);
        invitelist = findViewById(R.id.invitelist);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        invitelist.setLayoutManager(mLayoutManager);
        GetInviteList(HRpreference.getInstance(getApplicationContext()).getUserInfo().getId(), HRpreference.getInstance(getApplicationContext()).getUserInfo().getAuthToken());
    }

    @Override
    public void onClick(View v) {
        if (v == invitebackbtn) {
            finish();
        }
    }

    private void GetInviteList(String id, String token) {
        HrApiRequest request = new HrApiRequest();
        dialog.setMessage("please wait");
        dialog.show();
        request.getFriendInvite(id, token, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                dialog.dismiss();
                call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                dialog.dismiss();
                if(senderList.size()>0)
                    senderList.clear();
                int statusCode = response.code();
                if (statusCode == 200) {
                    String myresponse = response.body().string();
                    try {

                        JSONArray jsonArray = new JSONArray(myresponse);
                        for(int a=0;a<jsonArray.length();a++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(a);
                            InviteForm inviteForm = new InviteForm();
                            inviteForm.setId(jsonObject.getString("id"));
                            inviteForm.setInviteSent(jsonObject.getBoolean("isInviteSent"));
                            inviteForm.setStatus(jsonObject.getString("status"));
                            JSONObject jsonObject1=jsonObject.getJSONObject("sender");
                            InviteForm.Sender sender =new InviteForm.Sender();
                            sender.setUserName(jsonObject1.getString("userName"));
                            sender.setfName(jsonObject1.getString("fName"));
                            sender.setlName(jsonObject1.getString("lName"));
                            InviteList.add(inviteForm);
                            senderList.add(sender);
                        }
                        }
                    catch(Exception e){
                                e.printStackTrace();
                            }
                        } else {

                        }
                        runOnUiThread(() -> {
                            inviteAdapter = new InviteAdapter(getApplicationContext(), InviteList,senderList);
                            invitelist.setAdapter(inviteAdapter);
//                                inviteAdapter.setClickListener(InviterequestScreen.this);
                            inviteAdapter.notifyDataSetChanged();

                        });
                    }
                });
            }
    private void acceptInvite(String id, String token) {
        EventRequest request = new EventRequest();
        dialog.setMessage("please wait");
        dialog.show();
        request.requestforAcceptInvite(id, token, new Callback() {
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
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            GetInviteList(HRpreference.getInstance(getApplicationContext()).getUserInfo().getId(), HRpreference.getInstance(getApplicationContext()).getUserInfo().getAuthToken());

                        }
                    });


                }
            }
        });
    }


    @Override
    public void onItemClick(View view, String Id) {
        acceptInvite(Id,HRpreference.getInstance(getApplicationContext()).getUserInfo().getAuthToken());

    }
}
