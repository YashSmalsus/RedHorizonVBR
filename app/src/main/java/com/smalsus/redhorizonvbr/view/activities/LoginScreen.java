package com.smalsus.redhorizonvbr.view.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings;
import android.text.Html;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.smalsus.redhorizonvbr.APIConstance.Consts;
import com.smalsus.redhorizonvbr.APIConstance.HRConstant;
import com.smalsus.redhorizonvbr.HRpreference;
import com.smalsus.redhorizonvbr.R;
import com.smalsus.redhorizonvbr.RedHorizonValidator;
import com.smalsus.redhorizonvbr.model.UserInfo;
import com.smalsus.redhorizonvbr.networkrequest.CallApiRequest;
import com.smalsus.redhorizonvbr.networkrequest.EventRequest;
import com.smalsus.redhorizonvbr.networkrequest.HrApiRequest;
import com.smalsus.redhorizonvbr.networkrequest.TwillioRequest;
import com.smalsus.redhorizonvbr.services.GPSTracker;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class LoginScreen extends BaseActivity implements View.OnClickListener {
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    CallbackManager callbackManager;
    LoginButton loginWithFBBtn;
    //    RegistrationListener registrationListener = registrationListener();
    private EditText email, userpassword;
    private Button loginbtn;
    private Button link_signup;
    private ImageButton fb, loginWithGoogle;
    private ProgressDialog dialog;
    private GoogleSignInClient googleSignInClient;
    private TwitterLoginButton twitterLoginButton;
    //twitter auth client required for custom login
    private TwitterAuthClient client;
    private GPSTracker gpsTracker;
    private TextView forgetPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);
        email = findViewById(R.id.input_email);
        userpassword = findViewById(R.id.input_password);
        loginbtn = findViewById(R.id.btn_login);
        loginWithGoogle = findViewById(R.id.loginWithGoogle);
        forgetPassword = findViewById(R.id.forgetPassword);
        forgetPassword.setOnClickListener(this);
        loginbtn.setOnClickListener(this);
        loginWithGoogle.setOnClickListener(this);
        SignInButton googleSignInButton = findViewById(R.id.sign_in_google);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());
        TextView privacyPolicyTitle = findViewById(R.id.privacyPolicyTitle);
        String privacyPolicy = getResources().getString(R.string.privacy_policy_title);
        String newString = privacyPolicy.replaceAll("Privacy Policy", "<font color='yellow'>" + "Privacy Policy" + "</font>");
        privacyPolicyTitle.setText(Html.fromHtml(newString));
        link_signup = findViewById(R.id.btn_signup);
        link_signup.setOnClickListener(this);
        dialog = new ProgressDialog(this);
        callbackManager = CallbackManager.Factory.create();
        fb = findViewById(R.id.loginFBButton);
        fb.setOnClickListener(this);
        loginWithFBBtn = findViewById(R.id.loginWithFB);
        loginWithFBBtn.setPermissions(Arrays.asList("public_profile", "email", "user_friends", "user_birthday", "user_gender"));
        loginWithFBBtn.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                dialog.setMessage("Loading");
                dialog.show();
                GraphRequest graphRequest = GraphRequest.newMeRequest(loginResult.getAccessToken(), (object, response) -> {
                    dialog.dismiss();
                    getUserDataUsingFBLogin(object);
                });

                Bundle parameter = new Bundle();
                parameter.putString("fields", "id,email,first_name,last_name,middle_name,name,friends,birthday,gender");
                graphRequest.setParameters(parameter);
                graphRequest.executeAsync();
            }

            @Override
            public void onCancel() {
                dialog.dismiss();
            }

            @Override
            public void onError(FacebookException error) {
                dialog.dismiss();
            }
        });

        // Twitter Login Implementation
        client = new TwitterAuthClient();
        twitterLoginButton = findViewById(R.id.twitterLoginBtn);

        //NOTE : calling default twitter login in OnCreate/OnResume to initialize twitter callback
        //  defaultLoginTwitter();
    }

    public void customLoginTwitter(View view) {
        if (getTwitterSession() == null) {
            client.authorize(this, new com.twitter.sdk.android.core.Callback<TwitterSession>() {
                @Override
                public void success(Result<TwitterSession> result) {
                    TwitterSession twitterSession = result.data;
                    fetchTwitterEmail(twitterSession);
                }

                @Override
                public void failure(TwitterException exception) {
                    Toast.makeText(LoginScreen.this, "Failed to authenticate. Please try again.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "User already authenticated", Toast.LENGTH_SHORT).show();
            fetchTwitterEmail(getTwitterSession());
        }
    }


    private TwitterSession getTwitterSession() {
        TwitterSession session = TwitterCore.getInstance().getSessionManager().getActiveSession();
        /*TwitterAuthToken authToken = session.getAuthToken();
        String token = authToken.token;
        String secret = authToken.secret;*/

        return session;
    }

    public void fetchTwitterEmail(final TwitterSession twitterSession) {

        client.requestEmail(twitterSession, new com.twitter.sdk.android.core.Callback<String>() {
            @Override
            public void success(Result<String> result) {

                //   userDetailsLabel.setText("User Id : " + twitterSession.getUserId() + "\nScreen Name : " + twitterSession.getUserName() + "\nEmail Id : " + result.data);
            }

            @Override
            public void failure(TwitterException exception) {
                Toast.makeText(LoginScreen.this, "User already authenticated", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void getUserDataUsingFBLogin(JSONObject object) {
        try {
            String uEmail = object.has("email") ? object.getString("email") : "";
            String fname = object.has("first_name") ? object.getString("first_name") : "";
            String lname = object.has("last_name") ? object.getString("last_name") : "";
            String birthday = object.has("birthday") ? object.getString("birthday") : "";
            String genderString = object.has("gender") ? object.getString("gender") : "";
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            Date d = sdf.parse(birthday);
            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy/MM/dd");
            String dateOfBirth = sdf2.format(d);
            String[] userNameArray = uEmail.split("@");
            String userName = userNameArray.length > 0 ? userNameArray[0] : "";
            int gender = (genderString != null) && (genderString.equalsIgnoreCase("male")) ? 1 : 2;
            createUSer("999999999999", fname, lname, gender, dateOfBirth, uEmail, userName, "12345");

        } catch (Exception e) {
            Log.d("Error", e.getLocalizedMessage());
        }
    }

    private void createUSer(String Phoneno, String Fname, String Lname, int gender, String Dob, String Emailid, String Username, String Pasword) {
        HrApiRequest request = new HrApiRequest();
        dialog.setMessage("please wait");
        dialog.show();
        request.requestforCreateUSer(Phoneno, Fname, Lname, gender, Dob, Emailid, Username, Pasword, true, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                dialog.dismiss();
                call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                int statusCode = response.code();
                final String myResponse = response.body().string();
                dialog.dismiss();
                if (statusCode == 200) {
                    runOnUiThread(() -> {
                        Gson gson = new GsonBuilder().create();
                        UserInfo form = gson.fromJson(myResponse, UserInfo.class);
                        HRpreference.getInstance(getApplicationContext()).saveUserInfo(form);
//                        startSignUpNewUser(createUserWithEnteredData());
                        getAuthToken(form.getAuthToken());
                        //  updateUserLocation();
                    });
                }
            }
        });
    }


    @Override
    protected View getSnackbarAnchorView() {
        return null;
    }

    private void loginUser(String emailid, String password, boolean isPhone) {
        gpsTracker = new GPSTracker(this, LoginScreen.this);
        if (!gpsTracker.canGetLocation()) {
            gpsTracker.showSettingsAlert();
            return;
        }
        HrApiRequest request = new HrApiRequest();
        dialog.setMessage("please wait");
        dialog.show();
        request.requestforLogin(emailid, password, isPhone, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                dialog.dismiss();
                runOnUiThread(() -> showDialogWithOkButton("Login Failed :", "Please Try Again "));
                call.cancel();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final int statusCode = response.code();
                final String myResponse = response.body().string();

                if (statusCode == 200) {
                    runOnUiThread(() -> {
                        Gson gson = new GsonBuilder().create();
                        UserInfo form = gson.fromJson(myResponse, UserInfo.class);
                        HRpreference.getInstance(getApplicationContext()).saveUserInfo(form);
//                        startSignUpNewUser(createUserWithEnteredData());
                        getAuthToken(form.getAuthToken());
                        // updateUserLocation();
                    });
                } else {
                    dialog.dismiss();
                    runOnUiThread(() -> showDialogWithOkButton("Login Failed :", "Please Enter your valid Email and Password"));
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == loginbtn) {
            hideKeyBoard();
            if (validateInputData()) {
                checkLocationPermission();
            }
        } else if (v == link_signup) {
            hideKeyBoard();
            Intent intent = new Intent(this, CreateUserScreen.class);
            startActivity(intent);
        } else if (v == fb) {
            loginWithFBBtn.performClick();
        } else if (v == loginWithGoogle) {
            Intent signInIntent = googleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, 102);
        } else if (v == forgetPassword) {
            showForgetPasswordPupup();
        }

    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Consts.EXTRA_LOGIN_RESULT_CODE) {
            hideProgressDialog();
            boolean isLoginSuccess = data.getBooleanExtra(Consts.EXTRA_LOGIN_RESULT, false);
            String errorMessage = data.getStringExtra(Consts.EXTRA_LOGIN_ERROR_MESSAGE);
            if (isLoginSuccess) {
                // saveUserData(userForSave);
//                signInCreatedUser(userForSave, false);
            } else {
                Toast.makeText(this, getString(R.string.login_chat_login_error) + errorMessage, Toast.LENGTH_SHORT).show();
            }
        } else if (resultCode == RESULT_OK && requestCode == 102) {
            try {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                GoogleSignInAccount account = task.getResult(ApiException.class);
            } catch (ApiException e) {
                Log.w("LoginActivity", "signInResult:failed code=" + e.getStatusCode());
            }
        } else if (resultCode == RESULT_OK && requestCode == 64206) {
            if (callbackManager != null)
                callbackManager.onActivityResult(requestCode, resultCode, data);

        } else if (requestCode == RESULT_OK) {
            if (client != null)
                client.onActivityResult(requestCode, resultCode, data);
            twitterLoginButton.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("LoginActivity", "signInResult:failed code=" + e.getStatusCode());

        }
    }


    public void showDialogWithOkButton(String title, String msg) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(title);
        //dialog.setCancelable(false);
        dialog.setMessage(msg);
        dialog.setPositiveButton(getString(R.string.ok_txt), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        });
        AlertDialog alertDialog = dialog.create();
        alertDialog.show();
    }


    public boolean validateInputData() {
        String userName = email.getText().toString().trim();
        String pwd = userpassword.getText().toString().trim();
        if (RedHorizonValidator.isValidEmail(userName) || RedHorizonValidator.isValidMobile(userName)) {
            if (!RedHorizonValidator.isEmpty(pwd)) {

            } else {
                showDialogWithOkButton("Error", "Please Enter your Passwword");
                return false;
            }
        } else {
            showDialogWithOkButton("Error", "Please Enter your Valid EmailId or Phone");
            return false;
        }
        return true;
    }

    private void gotodashboard() {
        HRpreference.getInstance(getApplicationContext()).saveLoginStatus(true);
        Intent intent = new Intent(this, HomeScreen.class);
        startActivity(intent);
        finish();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    String userName = email.getText().toString();
                    boolean isPhone = !RedHorizonValidator.isValidEmail(userName);
                    loginUser(email.getText().toString(), userpassword.getText().toString(), isPhone);
                }
                break;
        }


    }

    private void hideKeyBoard() {
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputManager != null && getCurrentFocus() != null) {
            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }

    }


    public void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);
        } else {
            String userName = email.getText().toString().trim();
            boolean isPhone = !RedHorizonValidator.isValidEmail(userName);
            loginUser(email.getText().toString(), userpassword.getText().toString(), isPhone);
        }
    }

    private void updateUserLocation() {
        if (gpsTracker != null) {
            JSONObject location = new JSONObject();
            try {
                location.put(HRConstant.USER_LONGTITUDE, gpsTracker.getLongitude());
                location.put(HRConstant.USER_LATITUDE, gpsTracker.getLatitude());
                location.put(HRConstant.USER_COUNTRY_LOCATION, gpsTracker.getCountryName(this));
                location.put(HRConstant.USER_CITY_LOCATION, gpsTracker.getLocality(this));
                EventRequest request = new EventRequest();
                request.requestforUpdateLocation(HRpreference.getInstance(getApplicationContext()).getUserInfo().getId(), location, HRpreference.getInstance(getApplicationContext()).getUserInfo().getAuthToken(), new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        call.cancel();
                        dialog.dismiss();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        runOnUiThread(() -> {
                            dialog.dismiss();
                            gotodashboard();

                        });
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            runOnUiThread(() -> {
                dialog.dismiss();
                gotodashboard();
            });
        }
    }

    private void registerForCallInvites(String accessToken) {
        final String fcmToken = FirebaseInstanceId.getInstance().getToken();
        if (fcmToken != null) {
//            Voice.register(accessToken, Voice.RegistrationChannel.FCM, fcmToken, registrationListener);
            UserInfo userInfo = HRpreference.getInstance(getApplicationContext()).getUserInfo();
            String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
            postFCMToken(deviceId, fcmToken, userInfo.getId(), userInfo.getAuthToken());
            updateUserLocation();
        } else {
            updateUserLocation();
        }
    }


    private void getAuthToken(String token) {

        TwillioRequest twillioRequest = new TwillioRequest();

        twillioRequest.getTwillioAuthToken(token, false, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                dialog.dismiss();
                call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                int statusCode = response.code();
                if (statusCode == 200) {
                    String myresponse = response.body().string();
                    HRpreference.getInstance(getApplicationContext()).saveUserTwillioToken(myresponse);
                    registerForCallInvites(myresponse);
                }

            }

        });
    }


    private void showForgetPasswordPupup() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater layoutInflater = getLayoutInflater();
        View customView = layoutInflater.inflate(R.layout.forget_password_layout, null);
        builder.setView(customView);
        final AlertDialog alertDialog = builder.create();
        // reference the textview of custom_popup_dialog
        EditText confirmMessageView = customView.findViewById(R.id.emailIdField);
        TextView declineButton = customView.findViewById(R.id.declineButton);
        TextView confirmButton = customView.findViewById(R.id.confirmButton);
        declineButton.setOnClickListener(view -> alertDialog.dismiss());
        confirmButton.setOnClickListener(view -> {
            String emailId = confirmMessageView.getText().toString();
            if (RedHorizonValidator.isValidEmail(emailId)) {
                forGetPassword(emailId);
                alertDialog.dismiss();
            } else {
                alertDialog.dismiss();
                showDialogWithOkButton("Error", "Please Enter your Valid EmailId");
            }

        });
        alertDialog.show();
    }

    private void forGetPassword(String emailId) {

        HrApiRequest twillioRequest = new HrApiRequest();
        twillioRequest.requestforForgetPassword(emailId, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                int statusCode = response.code();
                if (statusCode == 200) {
                    String myresponse = response.body().string();
                    runOnUiThread(() -> Toast.makeText(LoginScreen.this, "We have send a password reset link on your emailId",Toast.LENGTH_LONG).show());
                } else {
                    runOnUiThread(() -> Toast.makeText(LoginScreen.this, "Please Enter your Valid EmailId",Toast.LENGTH_LONG).show());

                }
            }

        });
    }

//    private RegistrationListener registrationListener() {
//        return new RegistrationListener() {
//            @Override
//            public void onRegistered(String accessToken, String fcmToken) {
//                Log.d("LoginScreen", "Successfully registered FCM " + fcmToken);
//            }
//
//            @Override
//            public void onError(RegistrationException error, String accessToken, String fcmToken) {
//                String message = String.format("Registration Error: %d, %s", error.getErrorCode(), error.getMessage());
//                Log.e("LoginScreen", message);
//                //Snackbar.make(coordinatorLayout, message, SNACKBAR_DURATION).show();
//            }
//        };
//    }

    private void postFCMToken(String deviceID, String fcmToken, String userId, String authToken) {

        CallApiRequest callApiRequest = new CallApiRequest();
        callApiRequest.postFirebaseServerToken(deviceID, fcmToken, userId, authToken, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

            }
        });
    }
    public void showOrHidePassword(View view)
    {
        ImageView show = findViewById(R.id.show_hide_login_image_button);
        if(view.getId() == R.id.show_hide_login_image_button)
        {
            if(userpassword.getTransformationMethod().equals(PasswordTransformationMethod.getInstance()))
            {
                show.setImageResource(R.drawable.ic_visibility_off_black_24dp);
                userpassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }
            else
            {
                show.setImageResource(R.drawable.ic_visibility_on_black_24dp);
                userpassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        }
    }
}
