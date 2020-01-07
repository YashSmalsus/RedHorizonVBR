package com.smalsus.redhorizonvbr.brodcasts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.smalsus.redhorizonvbr.MyAppsNotificationManager;

public class NotificationReceiver extends BroadcastReceiver {

    public static final String NOTIFICATION_RECIVER_TYPE = "notification_reciever_type";

    @Override
    public void onReceive(Context context, Intent intent) {
        int type = intent.getIntExtra(NOTIFICATION_RECIVER_TYPE, 0);
        int notificationID = intent.getIntExtra(MyAppsNotificationManager.NOTIFICATION_ID, 0);
        if (type == 1) {
            MyAppsNotificationManager.getInstance(context).cancelNotification(notificationID);
        }

    }
}
