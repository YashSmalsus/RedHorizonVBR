package com.smalsus.redhorizonvbr.utils;

import com.smalsus.redhorizonvbr.R;

import java.util.ArrayList;


public class PushNotificationSender {

    public static void sendPushMessage(ArrayList<Integer> recipients, String senderName) {
        String outMessage = String.format(String.valueOf(R.string.text_push_notification_message), senderName);

    }
}