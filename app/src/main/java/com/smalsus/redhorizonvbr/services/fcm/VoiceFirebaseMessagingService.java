package com.smalsus.redhorizonvbr.services.fcm;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.smalsus.redhorizonvbr.App;
import com.smalsus.redhorizonvbr.HRpreference;
import com.smalsus.redhorizonvbr.R;
import com.smalsus.redhorizonvbr.model.ChatModelClass;
import com.smalsus.redhorizonvbr.model.EventUser;
import com.smalsus.redhorizonvbr.model.GetEvent;
import com.smalsus.redhorizonvbr.utils.CalenderUtils;
import com.smalsus.redhorizonvbr.view.activities.ChatScreen;
import com.smalsus.redhorizonvbr.view.activities.NotificationScreen;
import com.smalsus.redhorizonvbr.view.activities.SingleVideoCalling;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class VoiceFirebaseMessagingService extends FirebaseMessagingService {
    private static final String VOICE_CHANNEL = "default";

    private NotificationManager notificationManager;

    @Override
    public void onCreate() {
        super.onCreate();
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData().size() > 0) {
            Map<String, String> data = remoteMessage.getData();
            RemoteMessage.Notification notification = remoteMessage.getNotification();
            //  final int notificationId = (int) System.currentTimeMillis();
//            boolean valid = Voice.handleMessage(remoteMessage.getData(), new MessageListener() {
//                @Override
//                public void onCallInvite(@NonNull CallInvite callInvite) {
//                    final int notificationId = (int) System.currentTimeMillis();
//                    VoiceFirebaseMessagingService.this.notify(callInvite, notificationId);
//                    VoiceFirebaseMessagingService.this.sendCallInviteToActivity(callInvite, notificationId);
//                }
//
//                @Override
//                public void onCancelledCallInvite(@NonNull CancelledCallInvite cancelledCallInvite) {
//                    VoiceFirebaseMessagingService.this.cancelNotification(cancelledCallInvite);
//                    VoiceFirebaseMessagingService.this.sendCancelledCallInviteToActivity(
//                            cancelledCallInvite);
//                }
//
//            });


            List<String> dataArray = new ArrayList<>(data.values());
            if (dataArray.size() > 0) {
                String allData = dataArray.get(0);
                try {
                    JSONObject dataJsonObj = new JSONObject(allData);
                    Gson gson = new Gson();
                    Type type = new TypeToken<EventUser>() {
                    }.getType();
                    EventUser eventList = null;
                    if (dataJsonObj.has("by")) {
                        eventList = gson.fromJson(dataJsonObj.getString("by"), type);
                    }
                    String chatId = "";
                    String callID = "";
                    int chatType = 0;
                    if (dataJsonObj.has("additionalData")) {
                        JSONObject additionalData = dataJsonObj.getJSONObject("additionalData");
                        chatId = additionalData.getString("roomId");
                        chatType = additionalData.getInt("CallType");
                        callID = additionalData.getString("_id");
                    }

                    int notifyType = 0;
                    if (dataJsonObj.has("notifyType")) {
                        notifyType = dataJsonObj.getInt("notifyType");
                    }
                    if (notifyType == 5) {
                        Type chatDataType = new TypeToken<ChatModelClass>() {
                        }.getType();
                        ChatModelClass chatData = gson.fromJson(allData, chatDataType);
                        boolean isGroupChat = dataJsonObj.getBoolean("isGroupChat");
                        sendChatNotification(chatData, isGroupChat);
                    } else if (notifyType == 3) {
                        long notificationTime = dataJsonObj.getLong("datetime");
                        sendCallNotificationActivity(eventList, chatId, chatType, callID, notificationTime);
                    } else if (notifyType == 7) {
                        sendDisconnectCallBrodcast(eventList);
                    } else if (notifyType == 1) {
                        if (dataJsonObj.has("eventReminder")) {
                            String additionalData = dataJsonObj.getString("eventReminder");
                            Type chatDataType = new TypeToken<GetEvent>() {
                            }.getType();
                            GetEvent chatData = gson.fromJson(additionalData, chatDataType);
                            sendEventReminderNotification(chatData);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


        }

    }


//    private void notify(CallInvite callInvite, int notificationId) {
//        Intent intent = new Intent(this, VoiceActivity.class);
//        intent.setAction(VoiceActivity.ACTION_INCOMING_CALL);
//        intent.putExtra(VoiceActivity.INCOMING_CALL_NOTIFICATION_ID, notificationId);
//        intent.putExtra(VoiceActivity.INCOMING_CALL_INVITE, callInvite);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        PendingIntent pendingIntent =
//                PendingIntent.getActivity(this, notificationId, intent, PendingIntent.FLAG_CANCEL_CURRENT);
//        /*
//         * Pass the notification id and call sid to use as an identifier to cancel the
//         * notification later
//         */
//        Bundle extras = new Bundle();
//        extras.putInt(NOTIFICATION_ID_KEY, notificationId);
//        extras.putString(CALL_SID_KEY, callInvite.getCallSid());
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel callInviteChannel = new NotificationChannel(VOICE_CHANNEL,
//                    "Primary Voice Channel", NotificationManager.IMPORTANCE_DEFAULT);
//            callInviteChannel.setLightColor(Color.GREEN);
//            callInviteChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
//            notificationManager.createNotificationChannel(callInviteChannel);
//
//            Notification notification =
//                    buildNotification(callInvite.getFrom() + " is calling.",
//                            pendingIntent,
//                            extras);
//            notificationManager.notify(notificationId, notification);
//        } else {
//            NotificationCompat.Builder notificationBuilder =
//                    new NotificationCompat.Builder(this)
//                            .setSmallIcon(R.drawable.call_button_active)
//                            .setContentTitle(getString(R.string.app_name))
//                            .setContentText(callInvite.getFrom() + " is calling.")
//                            .setAutoCancel(true)
//                            .setExtras(extras)
//                            .setContentIntent(pendingIntent)
//                            .setGroup("test_app_notification")
//                            .setColor(Color.rgb(214, 10, 37));
//
//            notificationManager.notify(notificationId, notificationBuilder.build());
//        }
//    }

//    private void cancelNotification(CancelledCallInvite cancelledCallInvite) {
//        SoundPoolManager.getInstance((this)).stopRinging();
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            StatusBarNotification[] activeNotifications =
//                    notificationManager.getActiveNotifications();
//            for (StatusBarNotification statusBarNotification : activeNotifications) {
//                Notification notification = statusBarNotification.getNotification();
//                Bundle extras = notification.extras;
//                String notificationCallSid = extras.getString(CALL_SID_KEY);
//                if (cancelledCallInvite.getCallSid().equals(notificationCallSid)) {
//                    notificationManager.cancel(extras.getInt(NOTIFICATION_ID_KEY));
//                }
//            }
//        } else {
//            notificationManager.cancelAll();
//        }
//    }

    /*
     * Send the CallInvite to the VoiceActivity. Start the activity if it is not running already.
     */
//    private void sendCallInviteToActivity(CallInvite callInvite, int notificationId) {
//        Intent intent = new Intent(this, VoiceActivity.class);
//        intent.setAction(VoiceActivity.ACTION_INCOMING_CALL);
//        intent.putExtra(VoiceActivity.INCOMING_CALL_NOTIFICATION_ID, notificationId);
//        intent.putExtra(VoiceActivity.INCOMING_CALL_INVITE, callInvite);
//        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        this.startActivity(intent);
//    }

    private void sendChatNotification(ChatModelClass chatModelClass, boolean isGroupChat) {
        Intent intent = new Intent(ChatScreen.NEW_CHAT_MESSAGE_ACTION);
        intent.putExtra(ChatScreen.NEW_CHAT_MESSAGE_DATA, chatModelClass);
        if (!HRpreference.getInstance(getApplicationContext()).getUserInfo().getId().equals(chatModelClass.getUserBy().getId()))
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        if (HRpreference.getInstance(getApplicationContext()).getISChatScreenVisible()) {
            if (!HRpreference.getInstance(getApplicationContext()).getChatUserID().equals(chatModelClass.getUserBy().getId())) {
                genrateLocalChatNotification(chatModelClass);
            }
        } else {
            genrateLocalChatNotification(chatModelClass);
        }
    }

    /*
     * Send the CancelledCallInvite to the VoiceActivity
     */
//    private void sendCancelledCallInviteToActivity(CancelledCallInvite cancelledCallInvite) {
//        Intent intent = new Intent(VoiceActivity.ACTION_CANCEL_CALL);
//        intent.putExtra(VoiceActivity.CANCELLED_CALL_INVITE, cancelledCallInvite);
//        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
//    }


    @TargetApi(Build.VERSION_CODES.O)
    public Notification buildNotification(String text, PendingIntent pendingIntent, Bundle extras) {
        return new Notification.Builder(getApplicationContext(), VOICE_CHANNEL)
                .setSmallIcon(R.drawable.call_button_active)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(text)
                .setContentIntent(pendingIntent)
                .setExtras(extras)
                .setAutoCancel(true)
                .build();
    }


    private void sendCallNotificationActivity(EventUser callInvite, String groupId, int chatType, String callId, long notificationTime) {
        long currentTime = Calendar.getInstance().getTimeInMillis();
        if (CalenderUtils.minuteAgo(currentTime, notificationTime) < 1) {
            final int notificationId = (int) System.currentTimeMillis();
            Intent intent = new Intent(this, NotificationScreen.class);
            intent.setAction(NotificationScreen.TWILLIO_INCOMING_CALL);
            intent.putExtra(NotificationScreen.CALL_GROUP_ID, groupId);
            intent.putExtra(NotificationScreen.CALLIE_USER_ID, callInvite);
            intent.putExtra(NotificationScreen.INCOMIG_CALL_ID, callId);
            intent.putExtra(NotificationScreen.CALL_TYPE, chatType);
            intent.putExtra(NotificationScreen.INCOMING_CALL_NOTIFICATION_ID, notificationId);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            new Handler(Looper.getMainLooper()).postDelayed(() -> startActivity(intent), 1000);
            String title = "Incoming call from  " + callInvite.getfName() + " " + callInvite.getlName();
            ((App) getApplication()).triggerIncomingCallNotification(
                    getString(R.string.CALL_CHANNEL_ID),
                    getString(R.string.app_name),
                    title, notificationId, "", false,
                    intent);
        } else {
            ((App) getApplication()).triggerAutoCancelNotification(
                    getString(R.string.CALL_CHANNEL_ID),
                    getString(R.string.app_name),
                    "You have Missed a call from " + callInvite.getfName() + " " + callInvite.getlName(),
                    1001);
        }

    }


    private void genrateLocalChatNotification(ChatModelClass chatModelClass) {
        ((App) getApplication()).triggerChatNotification(ChatScreen.class,
                chatModelClass,
                getString(R.string.CHAT_CHANNEL_ID),
                getString(R.string.app_name),
                chatModelClass.getUserBy().getfName() + " " + chatModelClass.getUserBy().getlName() + " send new message to you",
                getResources().getInteger(R.integer.new_message_notificationId));
    }

    private void sendEventReminderNotification(GetEvent chatData) {
        final int notificationId = (int) System.currentTimeMillis();
        if (!HRpreference.getInstance(getApplicationContext()).getUserInfo().getId().equals(chatData.getEventUser().getId()))
            ((App) getApplication()).triggerEventNotification(this, chatData.getTopic(), chatData.getSubTopic(), notificationId);

    }

    private void sendDisconnectCallBrodcast(EventUser eventUser) {
        String currentUserId = HRpreference.getInstance(getApplicationContext()).getUserInfo().getId();
        if (!eventUser.getId().equals(currentUserId)) {
            Intent intent = new Intent(SingleVideoCalling.DISCONNECT_CALL_ACTION);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }
    }
}
