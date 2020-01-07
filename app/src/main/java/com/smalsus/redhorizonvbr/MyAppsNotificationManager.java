package com.smalsus.redhorizonvbr;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.smalsus.redhorizonvbr.brodcasts.NotificationReceiver;
import com.smalsus.redhorizonvbr.model.ChatModelClass;
import com.smalsus.redhorizonvbr.view.activities.ChatScreen;
import com.smalsus.redhorizonvbr.view.activities.MultiPartyActivity;

public class MyAppsNotificationManager {
    public static final String NOTIFICATION_ID = "notification_id";
    @SuppressLint("StaticFieldLeak")
    private static MyAppsNotificationManager instance;
    private Context context;
    private NotificationManagerCompat notificationManagerCompat;
    private NotificationManager notificationManager;

    private MyAppsNotificationManager(Context context) {
        this.context = context;
        notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannelGroup(new NotificationChannelGroup(context.getString(R.string.chat_related_groupp_id), context.getString(R.string.chat_group_name)));
            notificationManager.createNotificationChannelGroup(new NotificationChannelGroup(context.getString(R.string.other_groupp_id), context.getString(R.string.other_group_name)));
        }
    }

    public static MyAppsNotificationManager getInstance(Context context) {
        if (instance == null) {
            instance = new MyAppsNotificationManager(context);
        }
        return instance;
    }


    void registerNotifiationChannel(String channelId, String channelName) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("");
            notificationChannel.setGroup(context.getString(R.string.other_groupp_id));
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }


    void registerChatNotifiationChannel(String channelId, String channelName) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setDescription("");
            notificationChannel.setGroup(context.getString(R.string.chat_related_groupp_id));
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    void registerCallNotificationChannel(String channelId, String channelName) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setDescription("");
            notificationChannel.setGroup(context.getString(R.string.other_groupp_id));
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    void triggerNotification(String channelId, String title, String text, int notificationId) {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.locationhr)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.redhorizon_icon))
                .setContentTitle(title)
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setChannelId(channelId)
                .setAutoCancel(true);
        notificationManagerCompat.notify(notificationId, builder.build());
    }

    void triggerOngoingNotifocation(Class targetNotificationActivity, String channelId, String title, String text, int notificationId, String bigText, boolean autoCancel, int pendingIntentFlag) {

        Intent intent = new Intent(context, targetNotificationActivity);
        intent.putExtra("count", title);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, pendingIntentFlag);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.video_call_ic)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                .setContentTitle(title)
                .setContentText(text)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(bigText))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setChannelId(channelId)
                .setAutoCancel(autoCancel)
                .setOngoing(true);
        notificationManagerCompat.notify(notificationId, builder.build());
    }

    void triggerIncomingNotification(String channelId, String title, String text, int notificationId, String bigText, boolean autoCancel, Intent pendingIntentFlag) {
        PendingIntent pendingIntent =
                PendingIntent.getActivity(context, notificationId, pendingIntentFlag, PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.video_call_ic)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                .setContentTitle(title)
                .setContentText(text)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(bigText))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setChannelId(channelId)
                .setAutoCancel(autoCancel)
                .setOngoing(true);
        notificationManagerCompat.notify(notificationId, builder.build());
    }


    public void triggerNotificationWithBackStack(Class targetNotificationActivity, String channelId, String title, String text, String bigText, int priority, boolean autoCancel, int notificationId, int pendingIntentFlag) {

        Intent intent = new Intent(context, targetNotificationActivity);
        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context);
        taskStackBuilder.addNextIntentWithParentStack(intent);
        intent.putExtra("count", title);
        PendingIntent pendingIntent = taskStackBuilder.getPendingIntent(0, pendingIntentFlag);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.locationhr)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.vbr))
                .setContentTitle(title)
                .setContentText(text)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(bigText))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setChannelId(channelId)
                .setOngoing(true);

        notificationManagerCompat.notify(notificationId, builder.build());
    }

    void updateWithPicture(Class targetNotificationActivity, String title, String text, String channelId, int notificationId, String bigpictureString, int pendingIntentflag) {

        Intent intent = new Intent(context, targetNotificationActivity);
        intent.putExtra("count", title);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, pendingIntentflag);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.notification_small_ic)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.vbr))
                .setContentTitle(title)
                .setContentText(text)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(bigpictureString))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setChannelId(channelId)
                .setAutoCancel(true);

        //   Bitmap androidImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.vbr);
        //  builder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(androidImage).setBigContentTitle(bigpictureString));
        notificationManager.notify(notificationId, builder.build());
    }

    public void cancelNotification(int notificationId) {
        notificationManager.cancel(notificationId);
    }


    void triggerChatNotification(Class targetNotificationActivity, ChatModelClass chatModelClass, String channelId, String title, String text, int notificationId) {

        Intent intent = new Intent(context, targetNotificationActivity);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(ChatScreen.UserDetials, chatModelClass.getUserBy());
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            builder = new NotificationCompat.Builder(context, channelId)
                    .setSmallIcon(R.drawable.notification_small_ic)
                    .setContentTitle(title)
                    .setContentText(text)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);
        } else {
            builder = new NotificationCompat.Builder(context, channelId)
                    .setSmallIcon(R.drawable.notification_small_ic)
                    .setContentTitle(title)
                    .setContentText(text)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentIntent(pendingIntent)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true);
        }
        notificationManager.notify(notificationId, builder.build());
    }

    void triggerEventNotification(Context mContext, String title, String message, int notificationId) {

        Intent activityIntent = new Intent(mContext, MultiPartyActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(mContext,
                0, activityIntent, 0);




        Intent broadcastIntent = new Intent(context, NotificationReceiver.class);
        broadcastIntent.putExtra(NotificationReceiver.NOTIFICATION_RECIVER_TYPE, 1);
        broadcastIntent.putExtra(NOTIFICATION_ID, notificationId);
        PendingIntent actionIntent = PendingIntent.getBroadcast(context,
                0, broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(context, context.getResources().getString(R.string.CALL_CHANNEL_ID))
                .setSmallIcon(R.drawable.notification_small_ic)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setColor(Color.RED)
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .setOnlyAlertOnce(true)
                .addAction(R.drawable.video_call_ic, "Join Meeting", contentIntent)
                .addAction(R.drawable.video_call_ic, "Dismiss", actionIntent)
                .build();

        notificationManager.notify(notificationId, notification);

    }


}
