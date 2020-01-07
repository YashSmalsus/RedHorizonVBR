package com.smalsus.redhorizonvbr;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.smalsus.redhorizonvbr.model.ChatModelClass;
import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;

public class App extends Application {
    private static App instance;
    MyAppsNotificationManager myAppsNotificationManager;

    public static App getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initApplication();
        TwitterConfig config = new TwitterConfig.Builder(this)
                .logger(new DefaultLogger(Log.DEBUG))//enable logging when app is in debug mode
                .twitterAuthConfig(new TwitterAuthConfig(getResources().getString(R.string.Twitter_CONSUMER_KEY), getResources().getString(R.string.Twitter_CONSUMER_SECRET)))//pass the created app Consumer KEY and Secret also called API Key and Secret
                .debug(true)
                .build();
        Twitter.initialize(config);
        myAppsNotificationManager = MyAppsNotificationManager.getInstance(this);
        myAppsNotificationManager.registerChatNotifiationChannel(
                getString(R.string.CHAT_CHANNEL_ID),
                getString(R.string.CHAT_CHANNEL_NAME)
        );

        myAppsNotificationManager.registerCallNotificationChannel(
                getString(R.string.CALL_CHANNEL_ID),
                getString(R.string.CALL_CHANNEL_NAME));

        myAppsNotificationManager.registerNotifiationChannel(getString(R.string.CHANNEL_ID),
                getString(R.string.CHANNEL)
        );
    }

    private void initApplication() {
        instance = this;
    }


    public void updateNotification(Class targetNotificationActivity, String title, String text, String channelId, String bigpictureString, int notificationId, int pendingIntentflag) {
        myAppsNotificationManager.updateWithPicture(targetNotificationActivity, title, text, channelId, notificationId, bigpictureString, pendingIntentflag);
    }

    public void cancelNotification(int notificaitonId) {
        myAppsNotificationManager.cancelNotification(notificaitonId);
    }

    public void triggerOnGoingNotification(Class targetNotificationActivity, String channelId, String title, String text, String bigText, int priority, boolean autoCancel, int notificationId, int pendingIntentFlag) {
        myAppsNotificationManager.triggerOngoingNotifocation(targetNotificationActivity, channelId, title, text, notificationId, bigText, autoCancel, pendingIntentFlag);
    }

    public void triggerChatNotification(Class targetNotificationActivity, ChatModelClass chatModelClass, String channelId, String title, String text, int notificationId) {
        myAppsNotificationManager.triggerChatNotification(targetNotificationActivity, chatModelClass, channelId, title, text, notificationId);
    }

    public void triggerAutoCancelNotification(String channelId, String title, String text,  int notificationId){
        myAppsNotificationManager.triggerNotification(channelId,title,text,notificationId);
    }
    public void triggerIncomingCallNotification(String channelId, String title, String text, int notificationId, String bigText, boolean autoCancel, Intent pendingIntentFlag){
        myAppsNotificationManager.triggerIncomingNotification(channelId,title,text,notificationId,bigText,autoCancel,pendingIntentFlag);
    }

    public void triggerEventNotification(Context channelId, String title, String text, int notificationId){
        myAppsNotificationManager.triggerEventNotification(channelId,title,text,notificationId);
    }

}