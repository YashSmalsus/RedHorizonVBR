package com.smalsus.redhorizonvbr.view.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.smalsus.redhorizonvbr.App;
import com.smalsus.redhorizonvbr.HRpreference;
import com.smalsus.redhorizonvbr.R;
import com.smalsus.redhorizonvbr.databinding.ActivityNotificationScreenBinding;
import com.smalsus.redhorizonvbr.model.EventUser;
import com.smalsus.redhorizonvbr.networkrequest.CallApiRequest;
import com.smalsus.redhorizonvbr.util.SoundPoolManager;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class NotificationScreen extends AppCompatActivity implements
        AdapterView.OnItemSelectedListener {

    public static final String TWILLIO_INCOMING_CALL = "incoming_calll_action";
    public static final String DISCONNECT_CALL_ACTION = "disconnect_call_action";
    public static final String INCOMING_CALL_NOTIFICATION_ID = "incoming_call_notification_id";
    public static final String CALL_GROUP_ID = "CALL_GROUP_ID";
    public static final String CALLIE_USER_ID = "CALLIE_USER_ID";
    public static final String CALL_TYPE = "CALL_TYPE";
    public static final String INCOMIG_CALL_ID = "incoming_calling_id";
    String[] country = {"30 min", "45 min", "1 Hours", "2 Hours", "Maybe later"};
    private ActivityNotificationScreenBinding activityNotificationScreenBinding;
    private EventUser eventUser;
    private String groupId, callID;
    private int callType;
    private SoundPoolManager soundPoolManager;
    private boolean isCallPicked = false;
    private Handler handler = new Handler();
    private Runnable runnable;
    private int notificationId;
    private boolean isReceiverRegistered = false;
    private AudioCallReciever audioCallReciever;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN |
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_FULLSCREEN |
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        activityNotificationScreenBinding = DataBindingUtil.setContentView(this, R.layout.activity_notification_screen);
        soundPoolManager = SoundPoolManager.getInstance(this);
        activityNotificationScreenBinding.pioedittxt5.setOnItemSelectedListener(this);
        ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, country);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        activityNotificationScreenBinding.pioedittxt5.setAdapter(aa);
        audioCallReciever = new AudioCallReciever();
        Intent intent = getIntent();
        handleIncomingCallInvite(intent);
        setCallPickHandle();
        activityNotificationScreenBinding.notificationback.setOnClickListener(view -> {

        });
        activityNotificationScreenBinding.acceptInvitation.setOnClickListener(view -> {
            isCallPicked = true;
            String userToken = HRpreference.getInstance(getApplicationContext()).getUserInfo().getAuthToken();
            joinCallRequest(userToken, callID);
            soundPoolManager.stopRinging();
            if (callType == 1) {
                Intent intent1 = new Intent(NotificationScreen.this, SingleVideoCalling.class);
                intent1.putExtra(SingleVideoCalling.CALLING_USER_DETAILS, eventUser);
                intent1.putExtra(SingleVideoCalling.CHAT_ROOM_NAME, groupId);
                intent1.putExtra(SingleVideoCalling.CALLING_ID, callID);
                startActivity(intent1);
                finish();
            } else if (callType == 2) {
                Intent intent1 = new Intent(NotificationScreen.this, SingleAudioDemoActivity.class);
                intent1.putExtra(SingleAudioDemoActivity.CALLING_USER_DETAILS, eventUser);
                intent1.putExtra(SingleAudioDemoActivity.CHAT_ROOM_NAME, groupId);
                intent1.putExtra(SingleAudioDemoActivity.CALLING_ID, callID);
                startActivity(intent1);
                finish();
            }

        });

        activityNotificationScreenBinding.declineInvitaion.setOnClickListener(view -> {
            isCallPicked = true;
            cancelNotification(notificationId);
            String userToken = HRpreference.getInstance(getApplicationContext()).getUserInfo().getAuthToken();
            diclineCallRequest(userToken, callID);
        });

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIncomingCallInvite(intent);
    }

    private void setCallPickHandle() {

        handler.postDelayed(runnable = () -> {
            if (!isCallPicked) {
                showMissedCallNotification();
                finish();
            }
        }, 1000 * 30);
    }

    private void handleIncomingCallInvite(Intent intent) {
        if (intent != null && intent.getAction() != null) {
            if (intent.getAction().equals(TWILLIO_INCOMING_CALL)) {
                soundPoolManager.playRinging();
                eventUser = (EventUser) intent.getSerializableExtra(CALLIE_USER_ID);
                groupId = intent.getStringExtra(CALL_GROUP_ID);
                callType = intent.getIntExtra(CALL_TYPE, 0);
                callID = intent.getStringExtra(INCOMIG_CALL_ID);
                notificationId = intent.getIntExtra(INCOMING_CALL_NOTIFICATION_ID, 0);
                String name = eventUser.getfName() + " " + eventUser.getlName();
                String message = "<font color='red'> <b>@" + name + "</b></font>" + "wants to have a video call with you Are you Available??";
                activityNotificationScreenBinding.confirmMessage.setText(Html.fromHtml(message));
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private void joinCallRequest(String token, String groupId) {
        CallApiRequest callApiRequest = new CallApiRequest();
        callApiRequest.requestforJoinCall(groupId, token, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                String error = e.getLocalizedMessage();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

            }
        });

    }

    private void diclineCallRequest(String token, String groupId) {
        CallApiRequest callApiRequest = new CallApiRequest();
        callApiRequest.requestFoEndCall(callID, token, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                String error = e.getLocalizedMessage();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                finish();
            }
        });

    }

    private void showMissedCallNotification() {
        ((App) getApplication()).triggerAutoCancelNotification(
                getString(R.string.CALL_CHANNEL_ID),
                getString(R.string.app_name),
                "You have Missed a call from " + eventUser.getfName() + " " + eventUser.getlName(),
                1001);
    }

    @Override
    protected void onDestroy() {
        soundPoolManager.release();
        if (runnable != null)
            handler.removeCallbacks(runnable);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void cancelNotification(int id) {
        ((App) getApplication()).cancelNotification(id);
    }

    private void registerReceiver() {
        if (!isReceiverRegistered) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(DISCONNECT_CALL_ACTION);
            LocalBroadcastManager.getInstance(this).registerReceiver(
                    audioCallReciever, intentFilter);
            isReceiverRegistered = true;
        }
    }

    private void unregisterReceiver() {
        if (isReceiverRegistered) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(audioCallReciever);
            isReceiverRegistered = false;
        }
    }


    private class AudioCallReciever extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null && action.equals(DISCONNECT_CALL_ACTION)) {
                if (!isCallPicked) {
                    showMissedCallNotification();
                    finish();
                }
            }
        }
    }

}
