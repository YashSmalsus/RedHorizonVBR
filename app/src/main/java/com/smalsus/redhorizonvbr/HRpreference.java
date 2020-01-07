package com.smalsus.redhorizonvbr;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.smalsus.redhorizonvbr.db.entity.UserDeliveryAddress;
import com.smalsus.redhorizonvbr.model.UserInfo;

public class HRpreference {
    private static HRpreference HRpreference;
    private SharedPreferences sharedPreferences;
    public static final String HR_PREFRENCE_NAME = "HR_PREFRENCE_NAME";
    private static final String USER_DATA_KEY = "user_data_key";
    private static final  String IS_LOGGED_IN_KEY="IS_LOGGED_IN_KEY";
    private static final  String TWILLIO_AUTH_TOKEN="twillio_auth_token";
    private static final  String IS_CALL_IN_PROGRESS="is_call_in_progress";
    private static final String  IS_CHAT_SCREEN_VISIBLE="is_chat_screen_visible";
    private static final String   CURRENT_CHAT_USER_ID="current_user_id";
    private static final String   USER_CURRENT_ADDRESS="user_current_address";
    private static final String   CALL_SCREEN_TYPE="call_screen_type";
    public static HRpreference getInstance(Context context) {
        if (HRpreference == null) {
            HRpreference = new HRpreference(context);
        }
        return HRpreference;
    }

    private HRpreference(Context context) {
        sharedPreferences = context.getSharedPreferences(HR_PREFRENCE_NAME, Context.MODE_PRIVATE);
    }
    public void saveUserInfo(UserInfo form) {
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(form);
        prefsEditor.putString(USER_DATA_KEY, json);
        prefsEditor.commit();
    }

    public void saveUserAddress(UserDeliveryAddress form) {
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(form);
        prefsEditor.putString(USER_CURRENT_ADDRESS, json);
        prefsEditor.apply();
    }

    public void saveUserTwillioToken(String token){
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        prefsEditor.putString(TWILLIO_AUTH_TOKEN, token);
        prefsEditor.apply();
    }
    public String getTwiilioToken() {
        if (sharedPreferences != null) {
            return sharedPreferences.getString(TWILLIO_AUTH_TOKEN,null);
        }
        return null;
    }

    public void saveChatUserID(String id){
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        prefsEditor.putString(CURRENT_CHAT_USER_ID, id);
        prefsEditor.apply();
    }
    public void removeChatUserID(){
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        prefsEditor.remove(CURRENT_CHAT_USER_ID);
        prefsEditor.apply();
    }
    public String getChatUserID() {
        if (sharedPreferences != null) {
            return sharedPreferences.getString(CURRENT_CHAT_USER_ID,null);
        }
        return null;
    }

    public boolean getISCallProress() {
        if (sharedPreferences != null) {
            return sharedPreferences.getBoolean(IS_CALL_IN_PROGRESS,false);
        }
        return false;
    }

    public void saveIsCallScreenVisible(boolean value){
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        prefsEditor.putBoolean(IS_CALL_IN_PROGRESS,value);
        prefsEditor.apply();
    }

    public int  getCallScreenType() {
        if (sharedPreferences != null) {
            return sharedPreferences.getInt(CALL_SCREEN_TYPE,0);
        }
        return 0;
    }

    public void saveCallScreenType(int value){
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        prefsEditor.putInt(CALL_SCREEN_TYPE,value);
        prefsEditor.apply();
    }


    public boolean getISChatScreenVisible() {
        if (sharedPreferences != null) {
            return sharedPreferences.getBoolean(IS_CHAT_SCREEN_VISIBLE,false);
        }
        return false;
    }

    public void saveChatScreenVisible(boolean value){
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        prefsEditor.putBoolean(IS_CHAT_SCREEN_VISIBLE,value);
        prefsEditor.apply();
    }


    public UserInfo getUserInfo() {
        Gson gson = new Gson();
        String json = sharedPreferences.getString(USER_DATA_KEY, "");
        return gson.fromJson(json, UserInfo.class);
    }

    public UserDeliveryAddress getUserAddressInfo() {
        Gson gson = new Gson();
        String json = sharedPreferences.getString(USER_CURRENT_ADDRESS, "");
        return gson.fromJson(json, UserDeliveryAddress.class);
    }

    public void saveLoginStatus(boolean value) {
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        prefsEditor.putBoolean(IS_LOGGED_IN_KEY, value);
        prefsEditor.apply();
    }

    public boolean getLoginStatus() {
        if (sharedPreferences != null) {
            return sharedPreferences.getBoolean(IS_LOGGED_IN_KEY, false);
        }
        return false;
    }

    public void clearAllData(){
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        prefsEditor.clear();
        prefsEditor.apply();
    }
}
