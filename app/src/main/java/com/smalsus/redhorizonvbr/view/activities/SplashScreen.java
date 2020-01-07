package com.smalsus.redhorizonvbr.view.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.smalsus.redhorizonvbr.HRpreference;
import com.smalsus.redhorizonvbr.R;
import com.smalsus.redhorizonvbr.model.UserInfo;
import com.smalsus.redhorizonvbr.networkrequest.CallApiRequest;
import com.smalsus.redhorizonvbr.networkrequest.TwillioRequest;
import com.smalsus.redhorizonvbr.utils.RSAUtil;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SplashScreen extends AppCompatActivity {
    private static final String TAG = "SplashActivity";
    private static final int REQUEST = 112;
    private static final String TWILIO_ACCESS_TOKEN_SERVER_URL = "TWILIO_ACCESS_TOKEN_SERVER_URL";
    private static int SPLASH_TIME_OUT = 1000;
//    RegistrationListener registrationListener = registrationListener();
    private List<String> wantedPermissions = new ArrayList<>();
    private String accessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getChannel();
        setContentView(R.layout.activity_main);

//        FirebaseInstanceId.getInstance().getInstanceId()
//                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
//                        if (!task.isSuccessful()) {
//                            Log.w(TAG, "getInstanceId failed", task.getException());
//
//                            return;
//                        }
//                        String token = task.getResult().getToken();
//                    }
//                });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (HRpreference.getInstance(getApplicationContext()).getUserInfo() == null)
                checkAndRequestPermissions();
            else
                checkLoginStatus();
        } else {
            checkLoginStatus();
        }

    }

    private void checkLoginStatus() {
        if (HRpreference.getInstance(getApplicationContext()).getLoginStatus()) {
            getAuthToken(HRpreference.getInstance(getApplicationContext()).getUserInfo().getAuthToken());
            // getUserDetails(HRpreference.getInstance(getApplicationContext()).getUserInfo().getAuthToken());
        } else {
            goToNextActivity(1);
        }
    }


    private void goToNextActivity(int activityType) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent i;
                        if (activityType == 2)
                            i = new Intent(SplashScreen.this, HomeScreen.class);
                        else
                            i = new Intent(SplashScreen.this, LoginScreen.class);
                        startActivity(i);
                        finish();
                    }
                }, SPLASH_TIME_OUT);
            }
        });

    }

    public void disconnectFromFacebook() {
        if (AccessToken.getCurrentAccessToken() == null) {
            return;
        }
        new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE, new GraphRequest
                .Callback() {
            @Override
            public void onCompleted(GraphResponse graphResponse) {

                LoginManager.getInstance().logOut();

            }
        }).executeAsync();
    }


    @TargetApi(Build.VERSION_CODES.M)
    private void checkAndRequestPermissions() {
        if (wantedPermissions.size() > 0)
            wantedPermissions.clear();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
            wantedPermissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
            wantedPermissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_DENIED)
            wantedPermissions.add(Manifest.permission.RECORD_AUDIO);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED)
            wantedPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        if (!wantedPermissions.isEmpty())
            ActivityCompat.requestPermissions(this, wantedPermissions.toArray(new String[wantedPermissions.size()]), REQUEST);
        else
            checkLoginStatus();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST) {
            if (grantResults.length > 0)
                checkAllPermissionGranted(grantResults);
        }
    }

    private void checkAllPermissionGranted(@NonNull int[] grantResults) {
        int i = 0;
        for (int a = 0; a < grantResults.length; a++) {
            if (grantResults[a] == PackageManager.PERMISSION_GRANTED)
                i++;
        }
        if (i == wantedPermissions.size()) {
            checkLoginStatus();
        } else {
            checkAndRequestPermissions();
        }

    }

    /*
     * Register your FCM token with Twilio to receive incoming call invites
     *
     * If a valid google-services.json has not been provided or the FirebaseInstanceId has not been
     * initialized the fcmToken will be null.
     *
     * In the case where the FirebaseInstanceId has not yet been initialized the
     * VoiceFirebaseInstanceIDService.onTokenRefresh should result in a LocalBroadcast to this
     * activity which will attempt registerForCallInvites again.
     *
     */
    private void registerForCallInvites() {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }
                        String token = task.getResult().getToken();
                        if (token != null) {
                            UserInfo userInfo = HRpreference.getInstance(getApplicationContext()).getUserInfo();
                            String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                            postFCMToken(deviceId, token, userInfo.getId(), userInfo.getAuthToken());

                           // Voice.register(SplashScreen.this.accessToken, Voice.RegistrationChannel.FCM, token, registrationListener);
                        } else {
                            goToNextActivity(2);
                        }

                    }
                });


    }

    private void getAuthToken(String token) {

        TwillioRequest twillioRequest = new TwillioRequest();

        twillioRequest.getTwillioAuthToken(token, false, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        goToNextActivity(1);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                int statusCode = response.code();
                if (statusCode == 200) {
                    String myresponse = response.body().string();
                    SplashScreen.this.accessToken = myresponse;
                    HRpreference.getInstance(getApplicationContext()).saveUserTwillioToken(accessToken);
                    registerForCallInvites();
                } else if (statusCode == 401) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            disconnectFromFacebook();
                            goToNextActivity(1);
                        }
                    });
                }

            }

        });
    }


//    private RegistrationListener registrationListener() {
//        return new RegistrationListener() {
//            @Override
//            public void onRegistered(String accessToken, String fcmToken) {
//                Log.d(TAG, "Successfully registered FCM " + fcmToken);
//
//            }
//
//            @Override
//            public void onError(RegistrationException error, String accessToken, String fcmToken) {
//                String message = String.format("Registration Error: %d, %s", error.getErrorCode(), error.getMessage());
//                Log.e(TAG, message);
//                goToNextActivity(2);
//                //Snackbar.make(coordinatorLayout, message, SNACKBAR_DURATION).show();
//            }
//        };
//    }

    private void postFCMToken(String deviceID, String fcmToken, String userId, String authToken) {

        CallApiRequest callApiRequest = new CallApiRequest();
        callApiRequest.postFirebaseServerToken(deviceID, fcmToken, userId, authToken, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                goToNextActivity(2);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                goToNextActivity(2);
            }
        });
    }

    private void getChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.getNotificationChannel(getString(R.string.CHAT_CHANNEL_ID)).setImportance(NotificationManager.IMPORTANCE_HIGH);
        }
    }


}

