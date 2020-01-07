package com.smalsus.redhorizonvbr.view.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.loader.content.CursorLoader;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.smalsus.redhorizonvbr.APIConstance.HRConstant;
import com.smalsus.redhorizonvbr.HRpreference;
import com.smalsus.redhorizonvbr.R;
import com.smalsus.redhorizonvbr.RedHorizonValidator;
import com.smalsus.redhorizonvbr.UserProfilePostsGallery.UserProfilePostsTab;
import com.smalsus.redhorizonvbr.databinding.CreateSocialPostScreenBinding;
import com.smalsus.redhorizonvbr.datasource.factory.FeedDataFactory;
import com.smalsus.redhorizonvbr.interfaces.NewsFeedItemClick;
import com.smalsus.redhorizonvbr.model.UserInfo;
import com.smalsus.redhorizonvbr.networkrequest.EventRequest;
import com.smalsus.redhorizonvbr.view.fragments.SocialNewsFeed;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class CreateSocialPostScreen extends AppCompatActivity {
    private CreateSocialPostScreenBinding createSocialPostScreenBinding;
    private Uri attachmentImageUri = null;

    public static int actionRequiredToBePerformed;

    public  String userId;
    public  String postText;
    public  String postImageURL;

    public static boolean isRefresh = false;

    private FeedDataFactory feedDataFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createSocialPostScreenBinding = DataBindingUtil.setContentView(this, R.layout.activity_create_social_post_screen);
        userId = getIntent().getExtras().getString("userId");
        actionRequiredToBePerformed = getIntent().getExtras().getInt("actionToBePerformed");
        switch (actionRequiredToBePerformed)
        {
            case 1:
                UserInfo userInfo = HRpreference.getInstance(this).getUserInfo();
                Picasso.get().load(userInfo.getImageUrl()).resize(50, 50).into(createSocialPostScreenBinding.userProfileImage);


                createSocialPostScreenBinding.attachmentButton.setOnClickListener(view -> {
                    Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, 100);
                });
                createSocialPostScreenBinding.donepost.setOnClickListener(view -> {
                    createSocialPostScreenBinding.postLoader.setVisibility(View.VISIBLE);
                    String title = createSocialPostScreenBinding.inputSubject.getText().toString();
                    if (RedHorizonValidator.isEmpty(title)) {
                        Toast.makeText(this, "Please Enter title", Toast.LENGTH_SHORT).show();
                    } else {
                        if (attachmentImageUri != null) {
                            uploadFile(attachmentImageUri);
                        } else {
                            postNewsFeed("");
                        }
                    }

                });

                createSocialPostScreenBinding.inputSubject.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (createSocialPostScreenBinding.inputSubject.getText().toString().isEmpty())
                                {
                                    createSocialPostScreenBinding.donepost.animate().alpha(0.5f);
                                }
                                else
                                {
                                    createSocialPostScreenBinding.donepost.animate().alpha(1);
                                }
                            }
                        }
                );

                createSocialPostScreenBinding.createPostBackbtn.setOnClickListener(view -> finish());
                break;
            case 2:
                postText = getIntent().getExtras().getString("postText");
                postImageURL = getIntent().getExtras().getString("postImageURL");
                UserInfo userInfo1 = HRpreference.getInstance(this).getUserInfo();
                Picasso.get().load(userInfo1.getImageUrl()).resize(50, 50).into(createSocialPostScreenBinding.userProfileImage);
                createSocialPostScreenBinding.inputSubject.setText(postText);
                createSocialPostScreenBinding.donepost.setText("Update Post");
                createSocialPostScreenBinding.attachmentButton.setOnClickListener(view -> {
                    Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, 100);
                });
                createSocialPostScreenBinding.createPostBackbtn.setOnClickListener(view -> finish());
                createSocialPostScreenBinding.donepost.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String title = createSocialPostScreenBinding.inputSubject.getText().toString();
                                if (RedHorizonValidator.isEmpty(title)) {
                                    Toast.makeText(CreateSocialPostScreen.this, "Please Enter title", Toast.LENGTH_SHORT).show();
                                } else {
                                    if (attachmentImageUri != null) {
                                        uploadFile(attachmentImageUri);
                                    } else {
                                        updatePostFeed("");
                                    }
                                }
                            }
                        }
                );
                break;
        }

    }
    private void postNewsFeed(String path) {
        String title = createSocialPostScreenBinding.inputSubject.getText().toString();
        String token = HRpreference.getInstance(getApplicationContext()).getUserInfo().getAuthToken();
        EventRequest eventRequest = new EventRequest();
        eventRequest.postSocialNewsFeed(title, path, " ", token, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> createSocialPostScreenBinding.postLoader.setVisibility(View.GONE));
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                int statusCode = response.code();
                if (statusCode == 200) {
                    runOnUiThread(() -> {
                        createSocialPostScreenBinding.postLoader.setVisibility(View.GONE);
                        Toast.makeText(CreateSocialPostScreen.this, "Successfully post", Toast.LENGTH_SHORT).show();

                        finish();
                    });
                }

            }
        });
    }

    private void updatePostFeed(String path)
    {
        String title = createSocialPostScreenBinding.inputSubject.getText().toString();
        String token = HRpreference.getInstance(getApplicationContext()).getUserInfo().getAuthToken();
        EventRequest eventRequest = new EventRequest();
        eventRequest.updatePost(title, path, " ", userId, token, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> createSocialPostScreenBinding.postLoader.setVisibility(View.GONE));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                int statusCode = response.code();
                if (statusCode == 200) {
                    runOnUiThread(() -> {
                        createSocialPostScreenBinding.postLoader.setVisibility(View.GONE);
                        Toast.makeText(CreateSocialPostScreen.this, "Successfully Updated", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                }
            }
        });
    }

    public void updateChanges()
    {
        if(feedDataFactory!=null && feedDataFactory.getMutableLiveData().getValue()!=null) {
            feedDataFactory.getMutableLiveData().getValue().invalidate();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            try {
                attachmentImageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(attachmentImageUri);
                final Bitmap selectedImageB = BitmapFactory.decodeStream(imageStream);
                createSocialPostScreenBinding.imagePreviewView.setImageBitmap(selectedImageB);
                createSocialPostScreenBinding.imageContainer.setVisibility(View.VISIBLE);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(CreateSocialPostScreen.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }
        }
    }


    private void uploadFile(Uri fileUri) {
        EventRequest eventRequest = new EventRequest();
        File file = new File(getRealPathFromURI(fileUri));
        eventRequest.requestfor_Upload_Profile(file, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> {
                    Toast.makeText(CreateSocialPostScreen.this, "Image Uploading Failed", Toast.LENGTH_SHORT).show();
                    createSocialPostScreenBinding.progressBar.setVisibility(View.GONE);
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                int statusCode = response.code();
                if (statusCode == 200) {
                    assert response.body() != null;
                    String res = response.body().string();
                    try {
                        JSONObject Object = new JSONObject(res);
                        JSONObject jsonObject1 = Object.getJSONObject("data");
                        String value = jsonObject1.getString("fileURL");
                        String imageFileUploadUrl = HRConstant.IMAGE_UPLOAD_URL + value;
                        postNewsFeed(imageFileUploadUrl);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            }
        });
    }

    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(this, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }
}
