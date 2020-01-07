package com.smalsus.redhorizonvbr.view.activities;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.smalsus.redhorizonvbr.APIConstance.Consts;
import com.smalsus.redhorizonvbr.HRpreference;
import com.smalsus.redhorizonvbr.ImageUtils;
import com.smalsus.redhorizonvbr.R;
import com.smalsus.redhorizonvbr.databinding.HomeScreenBinding;
import com.smalsus.redhorizonvbr.model.EventUser;
import com.smalsus.redhorizonvbr.model.UserInfo;
import com.smalsus.redhorizonvbr.networkrequest.CallApiRequest;
import com.smalsus.redhorizonvbr.networkrequest.EventRequest;
import com.smalsus.redhorizonvbr.utils.PermissionsChecker;
import com.smalsus.redhorizonvbr.view.fragments.CallLogFragment;
import com.smalsus.redhorizonvbr.view.fragments.ConsumerDiscrationary;
import com.smalsus.redhorizonvbr.view.fragments.ConsumerStaples;
import com.smalsus.redhorizonvbr.view.fragments.ContactScreenFragment;
import com.smalsus.redhorizonvbr.view.fragments.ContactlistFragment;
import com.smalsus.redhorizonvbr.view.fragments.DashboardFragment;
import com.smalsus.redhorizonvbr.view.fragments.DiscoverFragement;
import com.smalsus.redhorizonvbr.view.fragments.EventDashboardFragment;
import com.smalsus.redhorizonvbr.view.fragments.GroupListFragment;
import com.smalsus.redhorizonvbr.view.fragments.HomeFragment;
import com.smalsus.redhorizonvbr.view.fragments.MerchantDash;
import com.smalsus.redhorizonvbr.view.fragments.MoreScreenFragment;
import com.smalsus.redhorizonvbr.view.fragments.MyRecordingFragment;
import com.smalsus.redhorizonvbr.view.fragments.NearMeFragment;
import com.smalsus.redhorizonvbr.view.fragments.NotificationFragment;
import com.smalsus.redhorizonvbr.view.fragments.ProfileScreenFragment;
import com.smalsus.redhorizonvbr.view.fragments.QrCodeScannerGenrator;
import com.smalsus.redhorizonvbr.view.fragments.SearchFragment;
import com.smalsus.redhorizonvbr.view.fragments.SocialNewsFeed;
import com.smalsus.redhorizonvbr.view.fragments.VBR_Screen_Fragment;
import com.smalsus.redhorizonvbr.view.fragments.Vbr_Fragment;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class HomeScreen extends BaseActivity implements VBR_Screen_Fragment.OnFragmentInteractionListener, EventDashboardFragment.OnFragmentInteractionListener, ProfileScreenFragment.OnFragmentInteractionListener, ContactScreenFragment.OnFragmentInteractionListener, MoreScreenFragment.OnFragmentInteractionListener, NotificationFragment.OnFragmentInteractionListener, CallLogFragment.OnFragmentInteractionListener, MyRecordingFragment.OnFragmentInteractionListener
        , ContactlistFragment.OnFragmentInteractionListener, GroupListFragment.OnFragmentInteractionListener, ImageUtils.ImageAttachmentListener, Vbr_Fragment.OnFragmentInteractionListener, View.OnClickListener, NearMeFragment.OnFragmentInteractionListener, HomeFragment.OnFragmentInteractionListener, DashboardFragment.OnFragmentInteractionListener, QrCodeScannerGenrator.OnFragmentInteractionListener, DiscoverFragement.OnFragmentInteractionListener, MerchantDash.OnFragmentInteractionListener, ConsumerDiscrationary.OnFragmentInteractionListener, ConsumerStaples.OnFragmentInteractionListener, SearchFragment.OnFragmentInteractionListener {


    public static final String INCOMING_VIDEO_CALL_INVITE = "INCOMING_VIDEO_CALL_INVITE";
    public static final String CANCELLED_VIDEO_CALL_INVITE = "CANCELLED_CALL_INVITE";
    //    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
//            = item -> {
//        switch (item.getItemId()) {
//            case R.id.navigation_home:
//                VBR_Screen_Fragment vbr_screen_fragment = new VBR_Screen_Fragment();
//                vbr_screen_fragment.setSharedPrefArg(sharedPrefsHelper);
//                loadFragment(vbr_screen_fragment);
//                return true;
//            case R.id.navigation_dashboard:
//                EventDashboardFragment eventDashboardFragment = new EventDashboardFragment();
//                loadFragment(eventDashboardFragment);
//                return true;
//            case R.id.navigation_notifications:
//                ProfileScreenFragment profileScreenFragment = new ProfileScreenFragment();
//                loadFragment(profileScreenFragment);
//                return true;
//            case R.id.navigation_contact:
//                ContactScreenFragment contactScreenFragment = new ContactScreenFragment();
//                loadFragment(contactScreenFragment);
//                return true;
//            case R.id.navigation_more:
//                MoreScreenFragment moreScreenFragment = new MoreScreenFragment();
//                loadFragment(moreScreenFragment);
//                return true;
//        }
//        return false;
//    };
    private PermissionsChecker checker;
    private boolean isReceiverRegistered = false;
    private ImageView vbrnavigationImage;
    private ImageView myBookingNavigatioImage;
    private ImageView profileNavigatioImage;
    private ImageView contactnavigationImage;
    private ImageView moreNavigationImage;
    private ImageView nearMeTabIcon, qrTabIcon, socialTabIcon, networktabIcon;
    private TextView vbrNavigationTitle, myBookingNavigatioTitle, profileNavigatioTitle, contactNavigationTitle, moreNavigationTitle, nearMetabtitle, qrTabTitle, socialTabTitle, networkTabTitle;
    private LinearLayout vbrNaivagationContainer, myBookingNavigationContainer, profileNavigationContainer, contactNavigationContainer, morenavigationContainer, nearMeTab, qrTabContainer, socialTabContainer, networkTabContainer;
    private int previousID = 3;
    private HorizontalScrollView horizontalScrollView;
    private VoiceBroadcastReceiver voiceBroadcastReceiver;

    private HomeScreenBinding homeScreenBinding;

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        // transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        homeScreenBinding = DataBindingUtil.setContentView(this, R.layout.activity_home_screen);
//        setContentView(R.layout.activity_home_screen);
        checker = new PermissionsChecker(getApplicationContext());
//        BottomNavigationView navigation = findViewById(R.id.navigation);
//        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
//        navigation.setSelectedItemId(R.id.navigation_notifications);

        findViews();
        HomeFragment profileScreenFragment = new HomeFragment();
        loadFragment(profileScreenFragment);
        voiceBroadcastReceiver = new VoiceBroadcastReceiver();
        homeScreenBinding.tapToReturnCall.setOnClickListener(view -> {
            HRpreference hRpreference = HRpreference.getInstance(getApplicationContext());
            // 1 - Video Call , 2- Audio Call , 3 - Multipart Video Call
            if (hRpreference.getCallScreenType() == 1) {
                Intent intent = new Intent(HomeScreen.this, SingleVideoCalling.class);
                startActivity(intent);
            } else if (hRpreference.getCallScreenType() == 2) {
                Intent intent = new Intent(HomeScreen.this, SingleAudioDemoActivity.class);
                startActivity(intent);
            }
        });
    }

    private void findViews() {
        vbrNaivagationContainer = findViewById(R.id.navigation_VBR);
        myBookingNavigationContainer = findViewById(R.id.navigation_dashboard);
        profileNavigationContainer = findViewById(R.id.navigation_profile);
        contactNavigationContainer = findViewById(R.id.navigation_contact);
        morenavigationContainer = findViewById(R.id.navigation_notification);
        socialTabContainer = findViewById(R.id.socialTabContainer);
        socialTabContainer.setOnClickListener(this);
        vbrNaivagationContainer.setOnClickListener(this);
        myBookingNavigationContainer.setOnClickListener(this);
        profileNavigationContainer.setOnClickListener(this);
        contactNavigationContainer.setOnClickListener(this);
        morenavigationContainer.setOnClickListener(this);
        vbrnavigationImage = findViewById(R.id.vbrNavigationImage);
        myBookingNavigatioImage = findViewById(R.id.navigationDashboardImage);
        profileNavigatioImage = findViewById(R.id.navigation_profileImage);
        contactnavigationImage = findViewById(R.id.navigation_contactImage);
        moreNavigationImage = findViewById(R.id.navigation_moreImage);
        vbrNavigationTitle = findViewById(R.id.vbrNavigationTitle);
        myBookingNavigatioTitle = findViewById(R.id.navigationDashboardImageTitle);
        profileNavigatioTitle = findViewById(R.id.navigation_profileTitle);
        contactNavigationTitle = findViewById(R.id.navigation_contactTitle);
        socialTabTitle = findViewById(R.id.socialTabTitle);
        moreNavigationTitle = findViewById(R.id.navigation_moreTitle);
        nearMeTab = findViewById(R.id.near_me_tab);
        nearMeTab.setOnClickListener(this);
        nearMeTabIcon = findViewById(R.id.nearme_tab_ic);
        nearMetabtitle = findViewById(R.id.nearmeTabTitle);
        qrTabContainer = findViewById(R.id.qrCodeTabContainer);
        qrTabContainer.setOnClickListener(this);
        socialTabIcon = findViewById(R.id.socialTabIcon);
        ImageView scollBtn = findViewById(R.id.scollBtn);
        scollBtn.setOnClickListener(this);
        horizontalScrollView = findViewById(R.id.horizontalScrollView);
        qrTabIcon = findViewById(R.id.qrTabIcon);
        qrTabTitle = findViewById(R.id.qrTabTitle);
        networkTabContainer = findViewById(R.id.networkTabContainer);
        networktabIcon = findViewById(R.id.networktabIcon);
        networkTabTitle = findViewById(R.id.networkTabTitle);
        networkTabContainer.setOnClickListener(this);
        homeScreenBinding.bottomLayoutTab.merchantTabContainer.setOnClickListener(this);
    }

    private void setVBRActive() {
        vbrNaivagationContainer.setBackgroundColor(getResources().getColor(R.color.black));
        vbrNavigationTitle.setTextColor(getResources().getColor(R.color.tab_selected_text_color));
        vbrnavigationImage.setImageResource(R.drawable.fourteen_ninety_two_active);
    }

    private void setMyBookingActive() {
        myBookingNavigatioTitle.setTextColor(getResources().getColor(R.color.tab_selected_text_color));
        myBookingNavigatioImage.setImageResource(R.drawable.dashboard_active);
        myBookingNavigationContainer.setBackgroundColor(getResources().getColor(R.color.black));
    }

    private void setMyProfileActive() {
        profileNavigatioTitle.setTextColor(getResources().getColor(R.color.tab_selected_text_color));
        profileNavigatioImage.setImageResource(R.drawable.home_active);
        profileNavigationContainer.setBackgroundColor(getResources().getColor(R.color.black));
    }

    private void setContactActive() {
        contactNavigationTitle.setTextColor(getResources().getColor(R.color.tab_selected_text_color));
        contactnavigationImage.setImageResource(R.drawable.contact_active);
        contactNavigationContainer.setBackgroundColor(getResources().getColor(R.color.black));
    }

    private void setNetworkActive() {
        networkTabTitle.setTextColor(getResources().getColor(R.color.tab_selected_text_color));
        networktabIcon.setImageResource(R.drawable.network_active);
        networkTabContainer.setBackgroundColor(getResources().getColor(R.color.black));
    }

    private void setMoreActive() {
        moreNavigationTitle.setTextColor(getResources().getColor(R.color.tab_selected_text_color));
        moreNavigationImage.setImageResource(R.drawable.notifications_active);
        morenavigationContainer.setBackgroundColor(getResources().getColor(R.color.black));
    }

    private void setNearMeActive() {
        nearMetabtitle.setTextColor(getResources().getColor(R.color.tab_selected_text_color));
        nearMeTabIcon.setImageResource(R.drawable.near_me_active);
        nearMeTab.setBackgroundColor(getResources().getColor(R.color.black));
    }

    private void setWebActive() {
        qrTabTitle.setTextColor(getResources().getColor(R.color.tab_selected_text_color));
        qrTabIcon.setImageResource(R.drawable.qr_active);
        qrTabContainer.setBackgroundColor(getResources().getColor(R.color.black));
    }

    private void setSocialTabActive() {
        socialTabTitle.setTextColor(getResources().getColor(R.color.tab_selected_text_color));
        socialTabIcon.setImageResource(R.drawable.dashboard_active);
        socialTabContainer.setBackgroundColor(getResources().getColor(R.color.black));
    }

    private void setMerchantTabActive() {
        homeScreenBinding.bottomLayoutTab.merchantTabContainer.setBackgroundColor(getResources().getColor(R.color.black));
        homeScreenBinding.bottomLayoutTab.merchantTabIcon.setImageResource(R.drawable.merchandise_active);
        homeScreenBinding.bottomLayoutTab.merchantTabTitle.setTextColor(getResources().getColor(R.color.tab_selected_text_color));
    }

    private void setAllDeActive(int currentID) {
        if (previousID == 1) {
            vbrNaivagationContainer.setBackgroundColor(Color.WHITE);
            vbrNavigationTitle.setTextColor(Color.BLACK);
            vbrnavigationImage.setImageResource(R.drawable.fourteen_ninety_two_deactive);
        } else if (previousID == 2) {
            myBookingNavigatioTitle.setTextColor(Color.BLACK);
            myBookingNavigatioImage.setImageResource(R.drawable.dashboard_deactive);
            myBookingNavigationContainer.setBackgroundColor(Color.WHITE);
        } else if (previousID == 3) {
            profileNavigatioTitle.setTextColor(Color.BLACK);
            profileNavigatioImage.setImageResource(R.drawable.home_deactive);
            profileNavigationContainer.setBackgroundColor(Color.WHITE);
        } else if (previousID == 4) {
            contactNavigationTitle.setTextColor(Color.BLACK);
            contactnavigationImage.setImageResource(R.drawable.contact_deactive);
            contactNavigationContainer.setBackgroundColor(Color.WHITE);
        } else if (previousID == 5) {
            moreNavigationTitle.setTextColor(Color.BLACK);
            moreNavigationImage.setImageResource(R.drawable.notifications_deactive);
            morenavigationContainer.setBackgroundColor(Color.WHITE);
        } else if (previousID == 6) {
            nearMetabtitle.setTextColor(Color.BLACK);
            nearMeTabIcon.setImageResource(R.drawable.near_me_deactive);
            nearMeTab.setBackgroundColor(Color.WHITE);
        } else if (previousID == 7) {
            qrTabTitle.setTextColor(Color.BLACK);
            qrTabIcon.setImageResource(R.drawable.qr_deactive);
            qrTabContainer.setBackgroundColor(Color.WHITE);
        } else if (previousID == 8) {
            socialTabTitle.setTextColor(Color.BLACK);
            socialTabIcon.setImageResource(R.drawable.dashboard_deactive);
            socialTabContainer.setBackgroundColor(Color.WHITE);
        } else if (previousID == 9) {
            networkTabTitle.setTextColor(Color.BLACK);
            networktabIcon.setImageResource(R.drawable.network_deactive);
            networkTabContainer.setBackgroundColor(Color.WHITE);
        } else if (previousID == 10) {
            homeScreenBinding.bottomLayoutTab.merchantTabContainer.setBackgroundColor(Color.WHITE);
            homeScreenBinding.bottomLayoutTab.merchantTabIcon.setImageResource(R.drawable.merchandise_deactive);
            homeScreenBinding.bottomLayoutTab.merchantTabTitle.setTextColor(Color.BLACK);
        }
        previousID = currentID;

    }

    @Override
    protected View getSnackbarAnchorView() {
        return null;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }


    private void startPermissionsActivity(boolean checkOnlyAudio) {
        PermissionsActivity.startActivity(this, checkOnlyAudio, Consts.PERMISSIONS);
    }


    private void sentRequestForStartCall(String event, JSONObject room, JSONArray members, String token, final String roomName, final EventUser eventUser, final int callType) {
        CallApiRequest request = new CallApiRequest();
        request.requestForStartCall(event, room, members, token, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                int statusCode = response.code();
                if (statusCode == 200) {
                    String myresponse = response.body().string();
                    try {
                        JSONObject jsonObject = new JSONObject(myresponse);
                        String callingId = jsonObject.getString("id");
                        if (callType == 1) {
                            Intent intent = new Intent(HomeScreen.this, SingleVideoCalling.class);
                            intent.putExtra(SingleVideoCalling.CALLING_USER_DETAILS, eventUser);
                            intent.putExtra(SingleVideoCalling.CHAT_ROOM_NAME, roomName);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(HomeScreen.this, SingleAudioDemoActivity.class);
                            intent.putExtra(SingleAudioDemoActivity.CALLING_USER_DETAILS, eventUser);
                            intent.putExtra(SingleAudioDemoActivity.CHAT_ROOM_NAME, roomName);
                            intent.putExtra(SingleAudioDemoActivity.CALLING_ID, callingId);
                            intent.setAction(SingleAudioDemoActivity.OUTGOING_CALL_ACTION);
                            startActivity(intent);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            }
        });
    }


    private void requestForSetReminder(String id, String token, final String roomId) {
        EventRequest request = new EventRequest();
        request.requestEventReminder(id, token, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                int statusCode = response.code();
                if (statusCode == 200) {
                    runOnUiThread(() -> {
                        Toast.makeText(HomeScreen.this, "Event Start", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(HomeScreen.this, MultiPartyActivity.class);
                        intent.putExtra("CHAT_ROOM_NAME", roomId);
                        startActivity(intent);
                    });
                }
            }
        });
    }

    private void addMemberToCall(String id, JSONArray member, String token) {
        CallApiRequest request = new CallApiRequest();
        request.requestForAddMemeberToCall(id, member, token, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                int statusCode = response.code();
                if (statusCode == 200) {

                }
            }
        });
    }

    private void endCall(String id, String callDetails, String token) {
        CallApiRequest request = new CallApiRequest();
        request.requestFoEndCall(id, token, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                int statusCode = response.code();
                if (statusCode == 200) {

                }
            }
        });
    }

    private void JoinCall(String id, String token) {
        CallApiRequest request = new CallApiRequest();
        request.requestforJoinCall(id, token, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                int statusCode = response.code();
                if (statusCode == 200) {
                    String myresponse = response.body().string();
                    Log.d("sd", myresponse);


                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (BaseActivity.getNumberOfTasks() > 2) {
            HomeScreen.this.finish();
        } else {
            showApplicationClosedPupup();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.navigation_VBR:
                if (previousID != 1) {
                    VBR_Screen_Fragment vbr_screen_fragment = new VBR_Screen_Fragment();
                    loadFragment(vbr_screen_fragment);
                    setVBRActive();
                    setAllDeActive(1);
                }
                break;
            case R.id.navigation_dashboard:
                if (previousID != 2) {
                    EventDashboardFragment eventDashboardFragment = new EventDashboardFragment();
                    loadFragment(eventDashboardFragment);
                    setMyBookingActive();
                    setAllDeActive(2);
                }
                break;
            case R.id.navigation_profile:
                if (previousID != 3) {
                    HomeFragment eventDashboardFragment = new HomeFragment();
                    loadFragment(eventDashboardFragment);
                    setMyProfileActive();
                    setAllDeActive(3);
                }
                break;
            case R.id.navigation_contact:
                if (previousID != 4) {
                    ContactScreenFragment contactScreenFragment = new ContactScreenFragment();
                    loadFragment(contactScreenFragment);
                    setContactActive();
                    setAllDeActive(4);
                }
                break;
            case R.id.navigation_notification:
                if (previousID != 5) {
                    NotificationFragment moreScreenFragment = new NotificationFragment();
                    loadFragment(moreScreenFragment);
                    setMoreActive();
                    setAllDeActive(5);
                }
                break;
            case R.id.near_me_tab:
                if (previousID != 6) {
                    NearMeFragment nearMeFragment = new NearMeFragment();
                    loadFragment(nearMeFragment);
                    setNearMeActive();
                    setAllDeActive(6);
                }
                break;
            case R.id.scollBtn:
                horizontalScrollView.postDelayed(() -> {
                    horizontalScrollView.scrollTo(horizontalScrollView.getScrollX() + 200, horizontalScrollView.getScrollY());
                    //  horizontalScrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
                }, 100L);
                break;
            case R.id.qrCodeTabContainer:
                if (previousID != 7) {
                    QrCodeScannerGenrator qrCodeScannerGenrator = new QrCodeScannerGenrator();
                    loadFragment(qrCodeScannerGenrator);
                    setWebActive();
                    setAllDeActive(7);
                }
                break;
            case R.id.socialTabContainer:
                if (previousID != 8) {
                    setSocialTabActive();
                    setAllDeActive(8);
                    SocialNewsFeed socialNewsFeed = new SocialNewsFeed();
                    loadFragment(socialNewsFeed);

                }
                break;
            case R.id.networkTabContainer:
                if (previousID != 9) {
                    setNetworkActive();
                    setAllDeActive(9);
                    DiscoverFragement discoverFragement = new DiscoverFragement();
                    loadFragment(discoverFragement);
                }
                break;
            case R.id.merchantTabContainer:
                if (previousID != 10) {
                    setMerchantTabActive();
                    setAllDeActive(10);
                    ConsumerStaples merchantDash = new ConsumerStaples();
                    loadFragment(merchantDash);
                }
                break;
        }
    }


    @Override
    public void onFragmentInteraction(int screntType, boolean isVIDEO, boolean isVBR, ArrayList<String> userIDs, List<EventUser> eventmember) {


    }

    @Override
    public void image_attachment(int from, String filename, Bitmap file, Uri uri) {

    }

    @Override
    public void contactListFragIntraction(boolean isVIDEO, boolean isVBR, String userIDs, EventUser eventmember, int chatType) {
        if (chatType == 1 || chatType == 2) {
            showPopUp(eventmember, chatType);
        }
//         else if (chatType == 2) {
//            Intent intent = new Intent(HomeScreen.this, VoiceActivity.class);
//            intent.putExtra("CallingUserID", userIDs);
//            intent.putExtra("UserDetials", eventmember);
//            startActivity(intent);
//        }
        else if (chatType == 3) {
            Intent intent = new Intent(HomeScreen.this, ChatScreen.class);
            intent.putExtra("CallingUserID", userIDs);
            intent.putExtra("UserDetials", eventmember);
            intent.putExtra(ChatScreen.ISGROUP_CHAT, false);
            startActivity(intent);
        }


    }


    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver();
        if (HRpreference.getInstance(getApplicationContext()).getISCallProress()) {
            homeScreenBinding.tapToReturnCall.setVisibility(View.VISIBLE);
        } else {
            homeScreenBinding.tapToReturnCall.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    private void showApplicationClosedPupup() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater layoutInflater = getLayoutInflater();
        View customView = layoutInflater.inflate(R.layout.confirm_connection_popup_layout, null);
        builder.setView(customView);
        final AlertDialog alertDialog = builder.create();
        // reference the textview of custom_popup_dialog
        TextView confirmMessageView = customView.findViewById(R.id.confirmMessage);
        ImageView profileImage = customView.findViewById(R.id.confirmUserProfile);
        profileImage.setImageResource(R.drawable.redhorizon_icon);
        TextView declineButton = customView.findViewById(R.id.declineButton);
        TextView confirmButton = customView.findViewById(R.id.confirmButton);
        String confirmMessgae = "<font color='red'>" + "Are you sure you want to exit?" + "</font>";
        confirmMessageView.setText(Html.fromHtml(confirmMessgae));
        declineButton.setOnClickListener(view -> alertDialog.dismiss());
        confirmButton.setOnClickListener(view -> finish());
        alertDialog.show();
    }


    public void showPopUp(final EventUser eventUser, final int chatType) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater layoutInflater = getLayoutInflater();
        View customView = layoutInflater.inflate(R.layout.confirm_connection_popup_layout, null);
        builder.setView(customView);
        final AlertDialog alertDialog = builder.create();
        // reference the textview of custom_popup_dialog
        TextView confirmMessageView = customView.findViewById(R.id.confirmMessage);
        ImageView profileImage = customView.findViewById(R.id.confirmUserProfile);
        TextView declineButton = customView.findViewById(R.id.declineButton);
        TextView confirmButton = customView.findViewById(R.id.confirmButton);
        String confirmMessgae = "Please Confirm Connection With " + "<font color='red'>" + eventUser.getfName() + " " + eventUser.getlName() + "</font>";
        confirmMessageView.setText(Html.fromHtml(confirmMessgae));
        declineButton.setOnClickListener(view -> alertDialog.dismiss());
        confirmButton.setOnClickListener(view -> {
            String roomID = UUID.randomUUID().toString();
            JSONObject jsonObject = new JSONObject();
            JSONArray memeber = new JSONArray();
            try {
                jsonObject.put("roomId", roomID);
                jsonObject.put("CallType", chatType);
                jsonObject.put("shouldRecord", true);
                memeber.put(eventUser.getId());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String userId = HRpreference.getInstance(getApplicationContext()).getUserInfo().getAuthToken();
            sentRequestForStartCall("Single Video Call", jsonObject, memeber, userId, roomID, eventUser, chatType);
            alertDialog.dismiss();
        });
        alertDialog.show();

    }

    private void unregisterReceiver() {
        if (isReceiverRegistered) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(voiceBroadcastReceiver);
            isReceiverRegistered = false;
        }
    }

    private void registerReceiver() {
        if (!isReceiverRegistered) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(INCOMING_VIDEO_CALL_INVITE);
            intentFilter.addAction(CANCELLED_VIDEO_CALL_INVITE);
            LocalBroadcastManager.getInstance(this).registerReceiver(
                    voiceBroadcastReceiver, intentFilter);
            isReceiverRegistered = true;
        }
    }

    @Override
    public void qrCodeScannerTabIntaction() {
        Intent intent = new Intent(HomeScreen.this, GenrateQRCodeActivity.class);
        startActivity(intent);
    }

    @Override
    public void onVBRFragmentInteraction(String roomId, String userID, String eventId) {
        UserInfo userInfo = HRpreference.getInstance(this).getUserInfo();
        if (userInfo.getId().equals(userID)) {
            requestForSetReminder(eventId, userInfo.getAuthToken(), roomId);
        } else {
            Intent intent = new Intent(this, MultiPartyActivity.class);
            intent.putExtra("CHAT_ROOM_NAME", roomId);
            startActivity(intent);
        }

//        Intent intent = new Intent(this, UnityPlayerActivity.class);
//        intent.putExtra("arguments", "data from android");
//        startActivity(intent);
    }

    private class VoiceBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(INCOMING_VIDEO_CALL_INVITE) || action.equals(CANCELLED_VIDEO_CALL_INVITE)) {
                // handleIncomingCallIntent(intent);
            }
        }
    }

}
