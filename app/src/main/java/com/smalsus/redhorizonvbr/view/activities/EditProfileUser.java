package com.smalsus.redhorizonvbr.view.activities;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.smalsus.redhorizonvbr.APIConstance.HRConstant;
import com.smalsus.redhorizonvbr.GlideUtils;
import com.smalsus.redhorizonvbr.HRpreference;
import com.smalsus.redhorizonvbr.ImageUtils;
import com.smalsus.redhorizonvbr.R;
import com.smalsus.redhorizonvbr.RedHorizonValidator;
import com.smalsus.redhorizonvbr.model.EventUser;
import com.smalsus.redhorizonvbr.model.UserInfo;
import com.smalsus.redhorizonvbr.networkrequest.EventRequest;
import com.smalsus.redhorizonvbr.services.GPSTracker;
import com.smalsus.redhorizonvbr.utils.Consts;
import com.smalsus.redhorizonvbr.view.fragments.ProfileScreenFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class EditProfileUser extends BaseActivity implements View.OnClickListener, ImageUtils.ImageAttachmentListener {
    ImageUtils imageUtils;
    private ImageView profile_user, coverPic;
    private ProgressDialog dialog;
    private ImageButton changeBGImageBtn, changeProfileBtn;
    private int optionID = 0;
    private GPSTracker gps;
    private double lati;
    private double longi;
    private TextView input_dob;
    private EditText firstname, lastname, email, username, password, phoneno;
    private Button btn_signup, cancelUpdateProfile;
    private Calendar myCalendar;
    private DatePickerDialog.OnDateSetListener date;
    private boolean isFinish = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_user);
        gps = new GPSTracker(getApplicationContext(), EditProfileUser.this);
        date = (view, year, month, dayOfMonth) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, month);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        };
        dialog = new ProgressDialog(this);
        firstname = findViewById(R.id.input_Fname);
        profile_user = findViewById(R.id.userProfileImage);
        imageUtils = new ImageUtils(this);
        coverPic = findViewById(R.id.coverPic);
        myCalendar = Calendar.getInstance();
        lastname = findViewById(R.id.input_Lname);
        cancelUpdateProfile = findViewById(R.id.cancelUpdateProfile);
        cancelUpdateProfile.setOnClickListener(this);
        email = findViewById(R.id.update_Email);
        phoneno = findViewById(R.id.update_Phone);
        btn_signup = findViewById(R.id.updateUserProfileBtn);
        btn_signup.setOnClickListener(this);
        input_dob = findViewById(R.id.updateDOB);
        input_dob.setOnClickListener(this);
        changeBGImageBtn = findViewById(R.id.changeBGImageBtn);
        changeBGImageBtn.setOnClickListener(this);
        changeProfileBtn = findViewById(R.id.changeProfileBtn);
        changeProfileBtn.setOnClickListener(this);
        UserInfo userInfo = HRpreference.getInstance(getApplicationContext()).getUserInfo();
        //  name_user.setText(HRpreference.getInstance(getApplicationContext()).getUserInfo().getfName());
        GlideUtils.loadImage(this, userInfo.getImageUrl(), profile_user, R.drawable.defaultuser, R.drawable.defaultuser);
        GlideUtils.loadImage(this, userInfo.getCoverURL(), coverPic, R.drawable.coverbg, R.drawable.coverbg);
        if (gps.canGetLocation()) {
            JSONObject location = new JSONObject();
            try {
                location.put(HRConstant.USER_LONGTITUDE, gps.getLongitude());
                location.put(HRConstant.USER_LATITUDE, gps.getLatitude());
                location.put(HRConstant.USER_CITY_LOCATION, gps.getLocality(this));
                location.put(HRConstant.USER_COUNTRY_LOCATION, gps.getCountryName(this));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            updateUserLocation(location);
        }
        setLayoutData();
    }

    private void updateLabel() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        input_dob.setText(sdf.format(myCalendar.getTime()));
    }

    private void setLayoutData() {
        UserInfo userInfo = HRpreference.getInstance(getApplicationContext()).getUserInfo();
        String dob = userInfo.getDob();
        Date strDate = null;
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            strDate = dateFormat.parse(dob);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        StringBuilder dateofbirth = new StringBuilder("");
        if (strDate != null) {
            myCalendar.setTime(strDate);
            DateFormat dateFormat2 = new SimpleDateFormat("dd/MM/yyyy");
            dateofbirth.append(dateFormat2.format(strDate));
        }

        firstname.setText(userInfo.getfName());
        lastname.setText(userInfo.getlName());
        email.setText(userInfo.getEmailID());
        input_dob.setText(dateofbirth.toString());
    }

    @Override
    protected View getSnackbarAnchorView() {
        return null;
    }


    @Override
    public void onClick(View view) {
        if (view == changeBGImageBtn) {
            optionID = 2;
            imageUtils.imagepicker(1);
        } else if (view == changeProfileBtn) {
            optionID = 1;
            imageUtils.imagepicker(1);
        } else if (view == input_dob) {
            new DatePickerDialog(EditProfileUser.this, android.app.AlertDialog.THEME_HOLO_LIGHT, date, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)).show();
        } else if (view == btn_signup) {
            if (validateInputData()) {
                isFinish = true;
                UserInfo userInfo = HRpreference.getInstance(getApplicationContext()).getUserInfo();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                updateUserData(userInfo.getId(), phoneno.getText().toString(), firstname.getText().toString(), lastname.getText().toString(), sdf.format(myCalendar.getTime()), userInfo.getAuthToken());
            }
        } else if (view == cancelUpdateProfile) {
            finish();
        }
    }

    public boolean validateInputData() {
        String fname = firstname.getText().toString().trim();
        String lname = lastname.getText().toString().trim();
        String Uemail = email.getText().toString().trim();
        String phoneNo = phoneno.getText().toString().trim();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        String UDob = sdf.format(myCalendar.getTime());
        if (!RedHorizonValidator.isEmpty(fname)) {
            if (!RedHorizonValidator.isEmpty(lname)) {
                if (!RedHorizonValidator.isValidEmail(Uemail)) {
                    if (!RedHorizonValidator.isEmpty(phoneNo)) {
                        if (!RedHorizonValidator.isEmpty(UDob)) {

                        } else {
                            showDialogWithOkButton("Error", "Please Enter your DOB");
                            return false;
                        }
                    } else {
                        showDialogWithOkButton("Error", "Please Enter your Phone No");
                        return false;
                    }


                } else {
                    showDialogWithOkButton("Error", "Please Enter your Valid Email ID");
                    return false;
                }
            } else {
                showDialogWithOkButton("Error", "Please Enter your Lastname");
                return false;
            }
        } else {
            showDialogWithOkButton("Error", "Please Enter your Firstname");
            return false;
        }
        return true;
    }

    private void updateUserData(String id, String Phoneno, String Fname, String Lname, String dob, final String token) {
        EventRequest request = new EventRequest();
        dialog.setMessage("please wait");
        dialog.show();
        request.requestforUpdateProfile(id, Phoneno, Fname, Lname, dob, token, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                dialog.dismiss();
                call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                int statusCode = response.code();
                dialog.dismiss();
                if (statusCode == 200) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(EditProfileUser.this, "Successfully User Updated", Toast
                                    .LENGTH_SHORT).show();
                            getUserDetails(token);
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(EditProfileUser.this, "Please Try Again", Toast
                                    .LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }


    public void showDialogWithOkButton(String title, String msg) {
        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(this);
        dialog.setTitle(title);
        dialog.setMessage(msg);
        dialog.setPositiveButton(getString(R.string.ok_txt), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                ;
            }
        });
        android.app.AlertDialog alertDialog = dialog.create();
        alertDialog.show();
    }

    public void image_attachment(int from, String filename, Bitmap file, Uri uri) {
        Bitmap bitmap = file;
        String fileName = filename;
        String path = Environment.getExternalStorageDirectory() + File.separator + "ImageAttach" + File.separator;
        imageUtils.createImage(file, filename, path, false);
        String filePath = getRealPathFromURI(uri.getPath());
        if (optionID == 1) {
            profile_user.setImageBitmap(file);
            UploadProfile(imageUtils.getPath(uri), false);
        } else if (optionID == 2) {
            coverPic.setImageBitmap(file);
            UploadProfile(imageUtils.getPath(uri), true);
        }

    }

    private String getRealPathFromURI(String contentURI) {
        Uri contentUri = Uri.parse(contentURI);
        Cursor cursor = this.getApplicationContext().getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(index);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        imageUtils.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        imageUtils.request_permission_result(requestCode, permissions, grantResults);
    }

    private void getUserDetails(String token) {
        dialog.setMessage("please wait");
        dialog.show();
        EventRequest request = new EventRequest();
        request.getUserDetails(token, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                dialog.dismiss();
                call.cancel();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final String myResponse = response.body().string();
                final int statusCode = response.code();
                dialog.dismiss();
                if (statusCode == 200) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Gson gson = new GsonBuilder().create();
                            UserInfo form = gson.fromJson(myResponse, UserInfo.class);
                            HRpreference.getInstance(getApplicationContext()).saveUserInfo(form);
                            ProfileScreenFragment.isProfileDataUpdate = true;
                            if (isFinish) {
                                finish();
                            }

                        }
                    });
                }
            }
        });
    }

    private void showConfirmMessage(ArrayList<String> videolIDist, List<EventUser> eventmember) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage("Do you  want to call");
        builder1.setCancelable(true);
        builder1.setPositiveButton(
                "Confirm",
                (dialog, id) -> {

                    dialog.cancel();

                });
        builder1.setNegativeButton(
                "Cancel",
                (dialog, id) -> dialog.cancel());
        AlertDialog alert11 = builder1.create();
        alert11.show();
    }


    private void UploadProfile(String path, final boolean isCover) {
        EventRequest request = new EventRequest();
        request.requestfor_Upload_Profile(new File(path), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                int statusCode = response.code();
                if (statusCode == 200) {
                    String res = response.body().string();
                    try {
                        JSONObject Object = new JSONObject(res);
                        JSONObject jsonObject1 = Object.getJSONObject("data");
                        String value = jsonObject1.getString("fileURL");
                        upDateUser_Profile(HRConstant.IMAGE_UPLOAD_URL + value, isCover);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String message;
                                if (optionID == 1)
                                    message = "User Profile Picture Successfully Updated";
                                else
                                    message = "Cover Picture Successfully Updated";
                                Toast.makeText(EditProfileUser.this, message, Toast
                                        .LENGTH_SHORT).show();
                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
    }

    private void upDateUser_Profile(String path, boolean isCover) {
        EventRequest request = new EventRequest();
        UserInfo userInfo = HRpreference.getInstance(getApplicationContext()).getUserInfo();
        request.requestforUpdateProfile(userInfo.getId(), path, HRpreference.getInstance(getApplicationContext()).getUserInfo().getAuthToken(), isCover, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                runOnUiThread(() -> getUserDetails(HRpreference.getInstance(getApplicationContext()).getUserInfo().getAuthToken()));
            }
        });
    }


    private void startPermissionsActivity(boolean checkOnlyAudio) {
        PermissionsActivity.startActivity(this, checkOnlyAudio, Consts.PERMISSIONS);
    }


    private void updateUserLocation(JSONObject location) {
        EventRequest request = new EventRequest();
        request.requestforUpdateLocation(HRpreference.getInstance(getApplicationContext()).getUserInfo().getId(), location, HRpreference.getInstance(getApplicationContext()).getUserInfo().getAuthToken(), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

            }
        });
    }

}
