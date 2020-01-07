package com.smalsus.redhorizonvbr.view.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.smalsus.redhorizonvbr.APIConstance.HRConstant;
//import com.smalsus.redhorizonvbr.FollowersScreenFragments.FollowersFollowingActivity;
import com.smalsus.redhorizonvbr.FollowersFollowingActivity;
import com.smalsus.redhorizonvbr.HRpreference;
import com.smalsus.redhorizonvbr.ImageUtils;
import com.smalsus.redhorizonvbr.R;
import com.smalsus.redhorizonvbr.UserProfilePostsGallery.FollowersActivity;
import com.smalsus.redhorizonvbr.UserProfilePostsGallery.UserProfilePostsGalleryPagerAdapter;
import com.smalsus.redhorizonvbr.UserProfilePostsGallery.UserProfilePostsTab;
import com.smalsus.redhorizonvbr.adapters.Add_Attendeee_Adapter;
import com.smalsus.redhorizonvbr.adapters.CommentListAdapter;
import com.smalsus.redhorizonvbr.adapters.MyFeedListAdapter;
import com.smalsus.redhorizonvbr.databinding.ActivityUserProfileScreenBinding;
import com.smalsus.redhorizonvbr.datasource.factory.CommentListItemViewModelFactory;
import com.smalsus.redhorizonvbr.interfaces.NewsFeedItemClick;
import com.smalsus.redhorizonvbr.model.EventUser;
import com.smalsus.redhorizonvbr.model.NetworkState;
import com.smalsus.redhorizonvbr.model.SocialFeedModel;
import com.smalsus.redhorizonvbr.model.UserInfo;
import com.smalsus.redhorizonvbr.networkrequest.EventRequest;
import com.smalsus.redhorizonvbr.rest.RestApi;
import com.smalsus.redhorizonvbr.rest.RestApiFactory;
import com.smalsus.redhorizonvbr.view.fragments.HomeFragment;
import com.smalsus.redhorizonvbr.view.fragments.SocialNewsFeed;
import com.smalsus.redhorizonvbr.viewmodel.CommentItemViewModel;
import com.smalsus.redhorizonvbr.viewmodel.MySocialFeedViewModel;
import com.smalsus.redhorizonvbr.viewmodel.SocialNewsFeedViewModel;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserProfileScreen extends AppCompatActivity implements NewsFeedItemClick, ImageUtils.ImageAttachmentListener, TabLayout.OnTabSelectedListener {


    //Member Variables of Tabs and View Pager
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Double longitude;
    private Double latitude;
    static String TAG = "UserProfileScreen";
    Location gps_loc = null, network_loc = null, final_loc = null;
    public int friendsCount;

    public static final String USER_DETAILS_ID = "user_details_id";
    public static boolean isRefresh = false;
    private ActivityUserProfileScreenBinding activityUserProfileScreenBinding;
    private MyFeedListAdapter adapter;
    private CommentItemViewModel commentItemViewModel;
    private int optionID = 0;
    private int screenType = 0;
    private ImageUtils imageUtils;
    private List<SocialFeedModel> socialFeedModels;
    private String userId;
    private MySocialFeedViewModel mViewModel;

    private CollapsingToolbarLayout collapsingToolbarLayout;
    private AppBarLayout appBarLayout;
    private Toolbar toolbar;

    private CircularImageView circularImageView;

    private Bitmap bitmap;

    private ProgressBar progressBar;

    public int totalLikes;

    private RecyclerView recyclerView;

    private SocialNewsFeedViewModel socialNewsFeedViewModels;

    public int friendsListSize;



    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityUserProfileScreenBinding = DataBindingUtil.setContentView(this, R.layout.activity_user_profile_screen);
        recyclerView = findViewById(R.id.profilescreen_list_feed);
        userId = getIntent().getStringExtra(USER_DETAILS_ID);

        SharedPreferences sharedPreferences = getSharedPreferences("UserID", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userID", userId);
        editor.apply();
        screenType = getIntent().getIntExtra("ScreenType", 1);
        setActivityUserProfileScreenData();
        UserInfo token = HRpreference.getInstance(this).getUserInfo();

        userProfileMoreOptions();



        /*activityUserProfileScreenBinding.postCountView.setText(String.valueOf(socialNewsFeedViewModels.size()));*/
        UserProfilePostsTab userProfilePostsTab = new UserProfilePostsTab(userId, screenType);
        activityUserProfileScreenBinding.userProfileTabLayout.addTab(activityUserProfileScreenBinding.userProfileTabLayout.newTab().setText("Posts"));
        activityUserProfileScreenBinding.userProfileTabLayout.addTab(activityUserProfileScreenBinding.userProfileTabLayout.newTab().setText("Gallery"));
        activityUserProfileScreenBinding.userProfileTabLayout.setSelectedTabIndicatorColor(Color.parseColor("#151515"));

        UserProfilePostsGalleryPagerAdapter userProfilePostsGalleryPagerAdapter = new UserProfilePostsGalleryPagerAdapter(getSupportFragmentManager(), activityUserProfileScreenBinding.userProfileTabLayout.getTabCount(), userId, screenType);


        activityUserProfileScreenBinding.userProfilePager.setAdapter(userProfilePostsGalleryPagerAdapter);
        activityUserProfileScreenBinding.userProfilePager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(activityUserProfileScreenBinding.userProfileTabLayout));
        activityUserProfileScreenBinding.userProfileTabLayout.setOnTabSelectedListener(this);

        collapsingToolbarLayout = findViewById(R.id.collapsingToolbarLayout);
        appBarLayout = findViewById(R.id.appBarLayout);
        appBarLayout.addOnOffsetChangedListener(
                new AppBarLayout.OnOffsetChangedListener() {
                    @Override
                    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
                        if(i == 0)
                        {
                            activityUserProfileScreenBinding.onTheTopFlagText.setVisibility(View.GONE);
                            activityUserProfileScreenBinding.onTheTopUserNameTextView.setVisibility(View.GONE);
                        }
                        else
                        {
                            activityUserProfileScreenBinding.onTheTopFlagText.setVisibility(View.VISIBLE);
                            activityUserProfileScreenBinding.onTheTopUserNameTextView.setVisibility(View.VISIBLE);
                        }
                    }
                }
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isRefresh) {
            UserInfo token = HRpreference.getInstance(this).getUserInfo();
            mViewModel.getMyNewsFeed( userId,token.getAuthToken());
            isRefresh = false;
        }
    }

    private void setActivityUserProfileScreenData() {
        getUserDetails(userId);
    }

    private void setUserDetails(UserInfo userDetails) {
        UserInfo token = HRpreference.getInstance(this).getUserInfo();
        if (userId.equals(token.getId())) {
            activityUserProfileScreenBinding.friendButtonContaner.setVisibility(View.VISIBLE);
            activityUserProfileScreenBinding.changeProfilePicBtn.setVisibility(View.VISIBLE);
            activityUserProfileScreenBinding.changeCoverPicBtn.setVisibility(View.VISIBLE);
        } else {
            activityUserProfileScreenBinding.changeProfilePicBtn.setVisibility(View.GONE);
            activityUserProfileScreenBinding.changeCoverPicBtn.setVisibility(View.GONE);
            //activityUserProfileScreenBinding.addPostlayout.setVisibility(View.GONE);
        }

        int width = activityUserProfileScreenBinding.userCoverPicContainer.getMeasuredWidth();
        int height = activityUserProfileScreenBinding.userCoverPicContainer.getMeasuredHeight();
        Picasso.get().load(userDetails.getImageUrl()).into(activityUserProfileScreenBinding.userProfilePictureView);
        Picasso.get().load(userDetails.getCoverURL()).resize(width, height).into(activityUserProfileScreenBinding.userCoverPicContainer);
        activityUserProfileScreenBinding.onTheTopUserNameTextView.setText(String.format("@%s", userDetails.getUserName()));
        activityUserProfileScreenBinding.userNameView.setText(String.format("%s %s", userDetails.getfName(), userDetails.getlName()));
        friendsCount = getIntent().getExtras().getInt("friendsCount");
        String count = String.valueOf(friendsCount);
        activityUserProfileScreenBinding.myProfileFriendCount.setText(count);
        activityUserProfileScreenBinding.accountUserName.setText(String.format("@%s", userDetails.getUserName()));
        activityUserProfileScreenBinding.moreOptions.setEnabled(true);

        imageUtils = new ImageUtils(UserProfileScreen.this);

        activityUserProfileScreenBinding.changeProfilePicBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        optionID = 1;
                        imageUtils.imagepicker(1);
                    }
                }
        );

        activityUserProfileScreenBinding.changeCoverPicBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        optionID = 2;
                        imageUtils.imagepicker(1);
                    }
                }
        );

        activityUserProfileScreenBinding.friendButtonContaner.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(UserProfileScreen.this, FollowersActivity.class);
                        intent.putExtra("userName", userDetails.getUserName());
                        startActivity(intent);
                    }
                }
        );

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED)
        {

        }
        else
        {

        }
        try {
            gps_loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            network_loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }catch (Exception e)
        {
            e.printStackTrace();
        }

        if (gps_loc != null)
        {
            final_loc = gps_loc;
            latitude = userDetails.getLocation().getLat();
            longitude = userDetails.getLocation().getLongi();

        }
        else if (network_loc != null)
        {
            final_loc = network_loc;
            latitude = userDetails.getLocation().getLat();
            longitude = userDetails.getLocation().getLongi();
        }
        else
        {
            latitude = 0.0;
            longitude = 0.0;
        }

        try {
            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && addresses.size() > 0)
            {
                String countryCode = addresses.get(0).getCountryCode();
                int flagOffset = 0x1F1E6;
                int asciiOffset = 0x41;
                int firstChar = Character.codePointAt(countryCode, 0) - asciiOffset + flagOffset;
                int secondChar = Character.codePointAt(countryCode, 1) - asciiOffset + flagOffset;

                String flag = new String(Character.toChars(firstChar))
                        + new String(Character.toChars(secondChar)).toUpperCase();
                    activityUserProfileScreenBinding.flagText.setText(flag);
                    activityUserProfileScreenBinding.onTheTopFlagText.setText(flag);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    private void userProfileMoreOptions() {
        UserInfo token = HRpreference.getInstance(this).getUserInfo();
        activityUserProfileScreenBinding.moreOptions.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(UserProfileScreen.this);
                        String[] loggedInUserOptions = {"Turn off Comments", "Logout"};
                        String[] userFriendOptions = {"Unfriend", "Copy link to profile", "Block", "See Friendship"};
                        if (userId.equals(token.getId())) {
                            builder.setItems(loggedInUserOptions, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent;
                                    switch (which) {
                                        case 0:
                                            break;
                                        case 1:
                                            showApplicationClosedPupup();
                                            break;
                                    }
                                }
                            });
                        } else {
                            builder.setItems(userFriendOptions, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent;
                                    switch (which) {
                                        case 0:;
                                            break;
                                        case 1:
                                            break;
                                        case 2:
                                            break;
                                        case 3:
                                            break;
                                    }
                                }
                            });
                        }
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                }
        );
    }

    private void getUserDetails(String userId) {
        RestApiFactory.cteateService(RestApi.class).fetchUserDetials(userId)
                .enqueue(new Callback<UserInfo>() {
                    @Override
                    public void onResponse(@NonNull Call<UserInfo> call, @NonNull Response<UserInfo> response) {
                        if (response.isSuccessful()) {
                            assert response.body() != null;
                            setUserDetails(response.body());
                            totalLikes = response.body().getTotalLikes();
                            setCounts(totalLikes);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<UserInfo> call, @NonNull Throwable t) {
                        String errorMessage = t.getMessage();

                    }
                });
    }

    public void setCounts(int totalLikes)
    {
        String totalLikesString = String.valueOf(totalLikes);
        activityUserProfileScreenBinding.likesCount.setText(totalLikesString);
    }

    @Override
    public void newsFeedItemClicked(SocialFeedModel article, int type, int position) {

        // 1 - Comment , 2 -  Likes, 3 - Delete Post, 4 - Delete Comment
        if (type == 1) {
            onShowPopup(article.getId(), article.getLikes().size(), position);
        } else {
            if (article.getLikebyUser() == 0) {
                List<SocialFeedModel> modelPagedList = socialFeedModels;
                if (modelPagedList != null) {
                    int likeCount = modelPagedList.get(position).getLikesCount();
                    modelPagedList.get(position).setLikesCount(likeCount + 1);
                    modelPagedList.get(position).setLikebyUser(1);
                    postNewsFeedLikes(article.getId());
                    adapter.updateListItem(modelPagedList);
                    adapter.notifyItemChanged(position);
                }
            } else if(type == 2){
                List<SocialFeedModel> modelPagedList = socialFeedModels;
                if (modelPagedList != null) {
                    int likeCount = modelPagedList.get(position).getLikesCount();
                    modelPagedList.get(position).setLikesCount(likeCount - 1);
                    modelPagedList.get(position).setLikebyUser(0);
                    postNewsFeedLikes(article.getId());
                    adapter.updateListItem(modelPagedList);
                    adapter.notifyItemChanged(position);
                }
            }
            else if (type == 3)
            {
                List<SocialFeedModel> modelPagedList = socialFeedModels;
                SharedPreferences sharedPreferences = getSharedPreferences("userPostIDInformation", MODE_PRIVATE);
                String userToken = HRpreference.getInstance(getApplicationContext()).getUserInfo().getAuthToken();
                deleteThePost(sharedPreferences.getString("userPostId", ""), userToken);
                adapter.updateListItem(modelPagedList);
                adapter.notifyItemChanged(position);
            }
            /*else if (type == 4)
            {

            }*/
        }
    }


    public void onShowPopup(String id, int likeCount, final int position) {
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View inflatedView = layoutInflater.inflate(R.layout.comment_popup_layout_file, null, false);
        RecyclerView listView = inflatedView.findViewById(R.id.commentsListView);
        //LinearLayout likeCountContainer = inflatedView.findViewById(R.id.likeCountContainer);
        TextView likeCountsView = inflatedView.findViewById(R.id.likes_count_view);
        ImageButton closeBtn = inflatedView.findViewById(R.id.closePopupBtn);
        EditText inputCommentBox = inflatedView.findViewById(R.id.commentInputBox);
        ImageButton sendCommentButton = inflatedView.findViewById(R.id.sendCommentBtn);
        if (likeCount > 0) {
            likeCountsView.setText(String.format("%d User like this Post ", likeCount));
        }
        listView.setLayoutManager(new LinearLayoutManager(UserProfileScreen.this));
        Display display = getWindowManager().getDefaultDisplay();
        String token = HRpreference.getInstance(UserProfileScreen.this.getApplicationContext()).getUserInfo().getAuthToken();
        CommentListAdapter commentListAdapter = new CommentListAdapter(UserProfileScreen.this);
        listView.setAdapter(commentListAdapter);
        commentItemViewModel = ViewModelProviders.of(this, new CommentListItemViewModelFactory(token, id)).get(CommentItemViewModel.class);
        commentItemViewModel.init(token, id);
        commentItemViewModel.getArticleLiveData().observe(this, socialNewsFeedViewModels -> {
            commentListAdapter.submitList(socialNewsFeedViewModels);
        });
        final Point size = new Point();
        display.getSize(size);
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;
        PopupWindow popWindow = new PopupWindow(inflatedView, width, height - 50, true);
        popWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.top_grey_border_layout));
        popWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        popWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popWindow.setAnimationStyle(R.style.PopupAnimation);
        popWindow.showAtLocation(activityUserProfileScreenBinding.getRoot(), Gravity.BOTTOM, 0, 100);
        closeBtn.setOnClickListener(view -> popWindow.dismiss());
        popWindow.setOnDismissListener(() -> {
            if (commentListAdapter.getCurrentList() != null) {
                List<SocialFeedModel> modelPagedList = socialFeedModels;
                if (modelPagedList != null && modelPagedList.get(position) != null) {
                    modelPagedList.get(position).setCommentCount(commentListAdapter.getCurrentList().size());
                    adapter.updateListItem(modelPagedList);
                    adapter.notifyItemChanged(position);
                }
            }
            commentItemViewModel = null;
        });
        inputCommentBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() != 0)
                    sendCommentButton.setVisibility(View.VISIBLE);
                else {
                    sendCommentButton.setVisibility(View.INVISIBLE);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        sendCommentButton.setOnClickListener(view -> {
            postNewsFeedComment(id, inputCommentBox.getText().toString(), commentListAdapter);
            inputCommentBox.setText("");
        });

    }


    private void postNewsFeedComment(String id, String title, final CommentListAdapter commentListAdapter) {
        UserInfo userInfo = HRpreference.getInstance(getApplicationContext()).getUserInfo();
        EventRequest eventRequest = new EventRequest();
        eventRequest.postSocialNewsFeedComment(id, title, "", " ", userInfo.getAuthToken(), new okhttp3.Callback() {
            @Override
            public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull okhttp3.Call call, @NonNull okhttp3.Response response) throws IOException {
                int statusCode = response.code();
                if (statusCode == 200) {
                    commentItemViewModel.updateListData();
                }

            }
        });

    }


    private void postNewsFeedLikes(String id) {
        String token = HRpreference.getInstance(getApplicationContext()).getUserInfo().getAuthToken();
        EventRequest eventRequest = new EventRequest();
        eventRequest.likePost(id, token, new okhttp3.Callback() {
            @Override
            public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {
                String error = e.getLocalizedMessage();
            }

            @Override
            public void onResponse(@NonNull okhttp3.Call call, @NonNull okhttp3.Response response) throws IOException {
                int statusCode = response.code();
                if (statusCode == 200) {
                    //   mViewModel.updateListData();
                }

            }
        });

    }

    private void  deleteThePost(String postId, String token)
    {
        EventRequest eventRequest = new EventRequest();
        eventRequest.deleteUserPost(postId, token, new okhttp3.Callback() {
            @Override
            public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {
                Toast.makeText(UserProfileScreen.this, "Deletion Unsuccessful", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(@NonNull okhttp3.Call call, @NonNull okhttp3.Response response) throws IOException {
                int statusCode = response.code();
                if (statusCode == 200) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(UserProfileScreen.this, "Post Deleted", Toast.LENGTH_SHORT).show();
                            progressBar = findViewById(R.id.refreshAfterDeletion);
                        }
                    });
                }
            }
        });
    }

    public void image_attachment(int from, String filename, Bitmap file, Uri uri) {
        String path = Environment.getExternalStorageDirectory() + File.separator + "ImageAttach" + File.separator;
        imageUtils.createImage(file, filename, path, false);
        if (optionID == 1) {
            activityUserProfileScreenBinding.userProfilePictureView.setImageBitmap(file);
            //activityUserProfileScreenBinding.userProfileImage.setImageBitmap(file);
            uploadProfile(imageUtils.getPath(uri), false);
        } else if (optionID == 2) {
            activityUserProfileScreenBinding.userCoverPicContainer.setImageBitmap(file);
            uploadProfile(imageUtils.getPath(uri), true);
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


    private void uploadProfile(String path, final boolean isCover) {
        EventRequest request = new EventRequest();
        //activityUserProfileScreenBinding.userProfileProgress.setVisibility(View.VISIBLE);
        request.requestfor_Upload_Profile(new File(path), new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                call.cancel();
                runOnUiThread(() -> {
                    //activityUserProfileScreenBinding.userProfileProgress.setVisibility(View.GONE);
                });

            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {

                int statusCode = response.code();
                if (statusCode == 200) {
                    String res = response.body().string();
                    try {
                        JSONObject Object = new JSONObject(res);
                        String value = Object.getString("data");
                        upDateUser_Profile(HRConstant.IMAGE_UPLOAD_URL + value, isCover);
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
        request.requestforUpdateProfile(userInfo.getId(), path, HRpreference.getInstance(getApplicationContext()).getUserInfo().getAuthToken(), isCover, new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                call.cancel();
                runOnUiThread(() -> {
                    //activityUserProfileScreenBinding.userProfileProgress.setVisibility(View.GONE);
                });
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {

                runOnUiThread(() -> {
                    String message;
                    HRpreference hRpreference = HRpreference.getInstance(UserProfileScreen.this);
                    if (optionID == 1) {
                        UserInfo token = hRpreference.getUserInfo();
                        message = "User Profile Picture Successfully Updated";
                        token.setImageUrl(path);
                        hRpreference.saveUserInfo(token);
                    } else {
                        UserInfo token = hRpreference.getUserInfo();
                        message = "Cover Picture Successfully Updated";
                        token.setCoverURL(path);
                        hRpreference.saveUserInfo(token);
                    }
                    //activityUserProfileScreenBinding.userProfileProgress.setVisibility(View.GONE);
                    Toast.makeText(UserProfileScreen.this, message, Toast
                            .LENGTH_SHORT).show();
                });
            }
        });
    }


    private void showApplicationClosedPupup() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(UserProfileScreen.this);
        LayoutInflater layoutInflater = getLayoutInflater();
        View customView = layoutInflater.inflate(R.layout.confirm_connection_popup_layout, null);
        builder.setView(customView);
        final AlertDialog alertDialog = builder.create();
        TextView confirmMessageView = customView.findViewById(R.id.confirmMessage);
        ImageView profileImage = customView.findViewById(R.id.confirmUserProfile);
        profileImage.setImageResource(R.drawable.redhorizon_icon);
        TextView declineButton = customView.findViewById(R.id.declineButton);
        TextView confirmButton = customView.findViewById(R.id.confirmButton);
        String confirmMessgae = "<font color='red'>" + "Are you sure you want to Exit ?" + "</font>";
        confirmMessageView.setText(Html.fromHtml(confirmMessgae));
        declineButton.setOnClickListener(view -> alertDialog.dismiss());
        confirmButton.setOnClickListener(view -> {
            alertDialog.dismiss();
            Intent logoutIntent = new Intent(UserProfileScreen.this, LoginScreen.class);
            logoutIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            HRpreference.getInstance(UserProfileScreen.this).clearAllData();
            startActivity(logoutIntent);

        });
        alertDialog.show();
    }


    @Override
    public void onBackPressed() {
        if (screenType == 1) {
            Intent intent = new Intent(UserProfileScreen.this, HomeScreen.class);
            startActivity(intent);
            finish();
        } else {
            finish();
        }

    }

    @Override
    public void onTabSelected(TabLayout.Tab tab)
    {
        activityUserProfileScreenBinding.userProfilePager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab)
    {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab)
    {

    }
}

