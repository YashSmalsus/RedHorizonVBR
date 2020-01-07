package com.smalsus.redhorizonvbr.view.activities;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.smalsus.redhorizonvbr.HRpreference;
import com.smalsus.redhorizonvbr.R;
import com.smalsus.redhorizonvbr.adapters.ChatlistAdapter;
import com.smalsus.redhorizonvbr.databinding.ActivityChatScreenBinding;
import com.smalsus.redhorizonvbr.model.ChatModelClass;
import com.smalsus.redhorizonvbr.model.EventUser;
import com.smalsus.redhorizonvbr.model.GroupModel;
import com.smalsus.redhorizonvbr.model.UserInfo;
import com.smalsus.redhorizonvbr.networkrequest.CallApiRequest;
import com.smalsus.redhorizonvbr.networkrequest.ChatAPIService;
import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ChatScreen extends AppCompatActivity {

    public static String NEW_CHAT_MESSAGE_ACTION = "NEW_CHAT_MESSAGE_ACTION";
    public static String NEW_CHAT_MESSAGE_DATA = "NEW_CHAT_MESSAGE_DATA";
    public static String CallingUserID = "CallingUserID";
    public static String UserDetials = "UserDetials";
    public static String ISGROUP_CHAT="is_group_chat";
    public static final String GROUP_DETAILS_DATA="group_details_data";
    private ActivityChatScreenBinding activityChatScreenBinding;
    private EventUser eventUser;
    private GroupModel groupModelData;
    private List<ChatModelClass> chatModelClassList;
    private ChatlistAdapter chatlistAdapter;
    private UserInfo userInfo;
    private ChatBrodcastReciever chatBrodcastReciever;
    private boolean isReceiverRegistered = false;
    private String  chatUserId;
    private boolean isGroupChat=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityChatScreenBinding = DataBindingUtil.setContentView(this, R.layout.activity_chat_screen);
        activityChatScreenBinding.chatList.setLayoutManager(new LinearLayoutManager(this));
        chatModelClassList = new ArrayList<>();
        chatBrodcastReciever = new ChatBrodcastReciever();
        chatlistAdapter = new ChatlistAdapter(this, chatModelClassList);
        activityChatScreenBinding.chatList.setAdapter(chatlistAdapter);
        HRpreference.getInstance(this).saveChatScreenVisible(true);
        Intent intent = getIntent();
        isGroupChat=intent.getBooleanExtra(ISGROUP_CHAT,false);
        Bundle extras = intent.getExtras();
        if (extras != null) {
            if(isGroupChat){
               groupModelData= (GroupModel) extras.getSerializable(GROUP_DETAILS_DATA);
                chatUserId=groupModelData.getId();
            }else{
                String value = extras.getString("keyName");
                eventUser = (EventUser) extras.getSerializable(UserDetials);
                chatUserId=eventUser.getId();
            }

        }
        userInfo = HRpreference.getInstance(this.getApplicationContext()).getUserInfo();
        setlayoutData();
        getChatHistory(chatUserId, userInfo.getAuthToken());
        activityChatScreenBinding.sendMessageBtn.setOnClickListener(view -> {
            postChat(chatUserId, userInfo.getAuthToken(), activityChatScreenBinding.inputBox.getText().toString(), "", "", isGroupChat);
            activityChatScreenBinding.inputBox.setText("");
        });

        activityChatScreenBinding.inputBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() != 0) {
                    activityChatScreenBinding.sendMessageBtn.setVisibility(View.VISIBLE);
                    //activityChatScreenBinding.sendVoiceClip.setVisibility(View.GONE);
                } else {
                    activityChatScreenBinding.sendMessageBtn.setVisibility(View.GONE);
                    //activityChatScreenBinding.sendVoiceClip.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        chatlistAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                activityChatScreenBinding.chatList.smoothScrollToPosition(chatlistAdapter.getItemCount());
            }
        });

        activityChatScreenBinding.chatScreenBackBtn.setOnClickListener(view -> finish());
        activityChatScreenBinding.callActionButton.setOnClickListener(view -> initiateVoiceCall());

    }

    private void setlayoutData(){
        if(!isGroupChat){
            HRpreference.getInstance(this).saveChatUserID(chatUserId);
            activityChatScreenBinding.chatUserName.setText(String.format("%s %s", eventUser.getfName(), eventUser.getlName()));
            Picasso.get().load(userInfo.getImageUrl()).resize(50, 50).into(activityChatScreenBinding.chatuserProfile);
        }else{
            HRpreference.getInstance(this).saveChatUserID(chatUserId);
            activityChatScreenBinding.chatUserName.setText(String.format("%s ", groupModelData.getName()));
           // Picasso.get().load(userInfo.getImageUrl()).resize(50, 50).into(activityChatScreenBinding.chatuserProfile);
        }

    }

    private void getChatHistory(String id, String token) {
        ChatAPIService chatAPIService = new ChatAPIService();
        chatAPIService.getChatHistory(id, token, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                int statusCode = response.code();
                if (statusCode == 200) {
                    String myresponse = response.body().string();
                    try {
                        Gson gson = new Gson();
                        Type type = new TypeToken<List<ChatModelClass>>() {
                        }.getType();
                        List<ChatModelClass> eventList = gson.fromJson(myresponse, type);
                        chatModelClassList.addAll(eventList);
                        runOnUiThread(() -> {
                            Collections.reverse(chatModelClassList);
                            chatlistAdapter.notifyItemInserted(chatModelClassList.size() - 1);

                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void postChat(String toId, String token, String text, String imageUrl, String videoUrl, boolean isGroupChat) {

        String currentTime = Calendar.getInstance().getTime().toString();

        EventUser eventUser1 = new EventUser(userInfo.getId(), userInfo.getUserName(), userInfo.getfName(), userInfo.getlName(), "", userInfo.getImageUrl(), "", userInfo.getLocation());
        ChatModelClass chatModelClass = new ChatModelClass(" ", text, eventUser1, toId, isGroupChat, currentTime, 0);
        chatModelClassList.add(chatModelClass);
        chatlistAdapter.notifyItemInserted(chatModelClassList.size() - 1);


        ChatAPIService chatAPIService = new ChatAPIService();
        chatAPIService.postChat(toId, imageUrl, text, videoUrl, token, isGroupChat, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                String error = e.getLocalizedMessage();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                int statusCode = response.code();
                if (statusCode == 200) {
                    String myresponse = response.body().string();
                }
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver();
    }

    private void registerReceiver() {
        if (!isReceiverRegistered) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(NEW_CHAT_MESSAGE_ACTION);
            LocalBroadcastManager.getInstance(this).registerReceiver(
                    chatBrodcastReciever, intentFilter);
            isReceiverRegistered = true;
        }
    }

    private void unregisterReceiver() {
        if (isReceiverRegistered) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(chatBrodcastReciever);
            isReceiverRegistered = false;
        }
    }

    private void handleNewMessage(Intent intent) {
        ChatModelClass chatModelClass = (ChatModelClass) intent.getSerializableExtra(NEW_CHAT_MESSAGE_DATA);
        if (chatModelClass.getUserBy().getId().equals(eventUser.getId())) {
            chatModelClassList.add(chatModelClass);
            chatlistAdapter.notifyItemInserted(chatModelClassList.size() - 1);
        }
    }

    @Override
    protected void onDestroy() {
        HRpreference.getInstance(this).saveChatScreenVisible(false);
        HRpreference.getInstance(this).removeChatUserID();
        super.onDestroy();
    }

    private class ChatBrodcastReciever extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(NEW_CHAT_MESSAGE_ACTION)) {
                handleNewMessage(intent);
            }
        }
    }

    private void initiateVoiceCall(){
        if(isGroupChat){
            String confirmMessgae = "Please Confirm Connection With " + "<font color='red'>" + groupModelData.getName() + "</font>";
            showPopUp(groupModelData.getEventmember().get(0),2,confirmMessgae);
        }else{
            String confirmMessgae = "Please Confirm Connection With " + "<font color='red'>" + eventUser.getfName() + "</font>";
            showPopUp(eventUser,2,confirmMessgae);
        }

    }



    private void showPopUp(final EventUser eventUser, final int chatType,String message) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater layoutInflater = getLayoutInflater();
        View customView = layoutInflater.inflate(R.layout.confirm_connection_popup_layout, null);
        builder.setView(customView);
        final AlertDialog alertDialog = builder.create();
        // reference the textview of custom_popup_dialog
        TextView confirmMessageView = customView.findViewById(R.id.confirmMessage);
        ImageView profileImage = customView.findViewById(R.id.confirmUserProfile);
        TextView declineButton = customView.findViewById(R.id.declineButton);
        TextView confirmButton = customView.findViewById(R.id.confirmButton);
        confirmMessageView.setText(Html.fromHtml(message));
        declineButton.setOnClickListener(view -> alertDialog.dismiss());
        confirmButton.setOnClickListener(view -> {
            String roomID = UUID.randomUUID().toString();
            JSONObject jsonObject = new JSONObject();
            JSONArray memeber = new JSONArray();
            try {
                jsonObject.put("roomId", roomID);
                jsonObject.put("CallType", chatType);
                jsonObject.put("shouldRecord", true);
                memeber.put(eventUser.getId());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String userId = HRpreference.getInstance(getApplicationContext()).getUserInfo().getAuthToken();
           sentRequestForStartCall("Single Video Call", jsonObject, memeber, userId, roomID, eventUser, chatType);
            alertDialog.dismiss();
        });
        alertDialog.show();

    }


    private void sentRequestForStartCall(String event, JSONObject room, JSONArray members, String token, final String roomName, final EventUser eventUser, final int callType) {
        CallApiRequest request = new CallApiRequest();
        request.requestForStartCall(event, room, members, token, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                int statusCode = response.code();
                if (statusCode == 200) {
                    String myresponse = response.body().string();
                    try {
                        JSONObject jsonObject = new JSONObject(myresponse);
                        String callingId = jsonObject.getString("id");
                        if (callType == 1) {
                            Intent intent = new Intent(ChatScreen.this, SingleVideoCalling.class);
                            intent.putExtra(SingleVideoCalling.CALLING_USER_DETAILS, eventUser);
                            intent.putExtra(SingleVideoCalling.CHAT_ROOM_NAME, roomName);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(ChatScreen.this, SingleAudioDemoActivity.class);
                            intent.putExtra(SingleAudioDemoActivity.CALLING_USER_DETAILS, eventUser);
                            intent.putExtra(SingleAudioDemoActivity.CHAT_ROOM_NAME, roomName);
                            intent.putExtra(SingleAudioDemoActivity.CALLING_ID, callingId);
                            intent.setAction(SingleAudioDemoActivity.OUTGOING_CALL_ACTION);
                            startActivity(intent);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            }
        });
    }
}
