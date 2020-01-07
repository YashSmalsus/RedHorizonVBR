package com.smalsus.redhorizonvbr.view.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.smalsus.redhorizonvbr.App;
import com.smalsus.redhorizonvbr.BuildConfig;
import com.smalsus.redhorizonvbr.HRpreference;
import com.smalsus.redhorizonvbr.R;
import com.smalsus.redhorizonvbr.adapters.AddParticipantsAdapter;
import com.smalsus.redhorizonvbr.databinding.ActivitySingleAudioDemoBinding;
import com.smalsus.redhorizonvbr.interfaces.AddCallParticipantsListner;
import com.smalsus.redhorizonvbr.model.EventUser;
import com.smalsus.redhorizonvbr.model.FriendList;
import com.smalsus.redhorizonvbr.networkrequest.CallApiRequest;
import com.smalsus.redhorizonvbr.networkrequest.EventRequest;
import com.smalsus.redhorizonvbr.networkrequest.TwillioRequest;
import com.smalsus.redhorizonvbr.util.RingtonePlayer;
import com.smalsus.redhorizonvbr.util.SoundPoolManager;
import com.smalsus.redhorizonvbr.utils.UtitlityClass;
import com.squareup.picasso.Picasso;
import com.twilio.video.AudioCodec;
import com.twilio.video.CameraCapturer;
import com.twilio.video.ConnectOptions;
import com.twilio.video.EncodingParameters;
import com.twilio.video.LocalAudioTrack;
import com.twilio.video.LocalParticipant;
import com.twilio.video.LocalVideoTrack;
import com.twilio.video.OpusCodec;
import com.twilio.video.RemoteAudioTrack;
import com.twilio.video.RemoteAudioTrackPublication;
import com.twilio.video.RemoteDataTrack;
import com.twilio.video.RemoteDataTrackPublication;
import com.twilio.video.RemoteParticipant;
import com.twilio.video.RemoteVideoTrack;
import com.twilio.video.RemoteVideoTrackPublication;
import com.twilio.video.Room;
import com.twilio.video.ScreenCapturer;
import com.twilio.video.TwilioException;
import com.twilio.video.Video;
import com.twilio.video.VideoTrack;
import com.twilio.video.VideoView;

import org.json.JSONArray;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.view.View.GONE;

public class SingleAudioDemoActivity extends AppCompatActivity implements AddCallParticipantsListner {

    private static final int CAMERA_MIC_PERMISSION_REQUEST_CODE = 5;
    private static final int REQUEST_MEDIA_PROJECTION = 1024;

    private static final String TAG = "SingleAudioDemoActivity";
    private static final String LOCAL_AUDIO_TRACK_NAME = "mic";
    public static String CHAT_ROOM_NAME = "CHAT_ROOM_NAME";
    public static String CALLING_ID = "calling_id";
    public static String CALLING_USER_DETAILS = "CALLING_USER_DETAILS";
    public static String INCOMING_CALL_ACTION = "incoming_calll_action";
    public static String DISCONNECT_CALL_ACTION = "disconnect_call_action";
    public static String OUTGOING_CALL_ACTION = "outgoing_call_action";
    private ActivitySingleAudioDemoBinding activitySingleAudioDemoBinding;
    private String roomName, callId;
    private EventUser eventUser;
    private SharedPreferences preferences;
    private AudioManager audioManager;
    private boolean isSpeakerPhoneEnabled = false;
    private CameraCapturer cameraCapturerCompat;
    private LocalAudioTrack localAudioTrack;
    private LocalVideoTrack localVideoTrack;
    private String accessToken;
    private int previousAudioMode;
    private Room room;
    private AudioCodec audioCodec;
    private EncodingParameters encodingParameters;
    private boolean disconnectedFromOnDestroy;
    private String remoteParticipantIdentity;
    private boolean previousMicrophoneMute;
    private SoundPoolManager soundPoolManager;
    private RingtonePlayer ringtonePlayer;
    private boolean isCallPicked = false;
    private Handler handler = new Handler();
    private Runnable runnable;
    private AudioCallReciever audioCallReciever;
    private boolean isReceiverRegistered = false;
    private AddParticipantsAdapter add_attendeee_adapter;
    private List<EventUser> friendlist;
    private List<FriendList> yourFriendList;
    private LocalParticipant localParticipant;
    private final ScreenCapturer.Listener screenCapturerListener = new ScreenCapturer.Listener() {
        @Override
        public void onScreenCaptureError(@NonNull String errorDescription) {
            stopScreenCapture();
            Toast.makeText(SingleAudioDemoActivity.this, R.string.screen_capture_error,
                    Toast.LENGTH_LONG).show();
        }

        @Override
        public void onFirstFrameAvailable() {
            Log.d(TAG, "First frame from screen capturer available");
        }
    };
    private Stack<VideoView> availableParticipantContainers = new Stack<>();
    private Map<String, VideoView> participantViewGroupMap = new HashMap<>();
    private Map<String, View> participantVideoView = new HashMap<>();
    private int videoCount = 0;
    private boolean isVideoShowing = false;
    private AlertDialog addMemberDialog;
    private List<String> connectedUsers;
    private int connectedUserCount = 0;
    private VideoView primaryVideoView;
    private ScreenCapturer screenCapturer;
    private boolean isScreeenShare = false;
    private boolean isVideoEnable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activitySingleAudioDemoBinding = DataBindingUtil.setContentView(this, R.layout.activity_single_audio_demo);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        connectedUsers = new ArrayList<>();
        friendlist = new ArrayList<>();
        HRpreference hRpreference = HRpreference.getInstance(getApplicationContext());
        hRpreference.saveIsCallScreenVisible(true);
        // 1 - Video Call , 2- Audio Call , 3 - Multipart Video Call
        hRpreference.saveCallScreenType(2);
        Intent intent = getIntent();
        roomName = intent.getStringExtra(CHAT_ROOM_NAME);
        callId = intent.getStringExtra(CALLING_ID);
        eventUser = (EventUser) intent.getSerializableExtra(CALLING_USER_DETAILS);
        audioCallReciever = new AudioCallReciever();
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        soundPoolManager = SoundPoolManager.getInstance(this);
        setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.setSpeakerphoneOn(isSpeakerPhoneEnabled);
        ringtonePlayer = new RingtonePlayer(this, R.raw.beep);
        if (intent.getAction() != null && intent.getAction().equals(OUTGOING_CALL_ACTION)) {
            handleOutGoingCall();
        }
        if (!checkPermissionForCameraAndMicrophone()) {
            requestPermissionForCameraAndMicrophone();
        } else {
            createAudioAndVideoTracks();
            setAccessToken();
        }

        setUserData();
        initializeParticipantContainers();
        intializeUI();
        setDisconnectAction();
        add_attendeee_adapter = new AddParticipantsAdapter(this, friendlist);
        add_attendeee_adapter.setSelectedItemsCountsChangedListener(this);
        activitySingleAudioDemoBinding.addParticipants.setOnClickListener(view -> showpopup());
        getFriendList(HRpreference.getInstance(this).getUserInfo().getId(), HRpreference.getInstance(this).getUserInfo().getAuthToken());

    }

    private void setCallPickHandle() {

        handler.postDelayed(runnable = () -> {
            if (!isCallPicked) {
                Toast.makeText(SingleAudioDemoActivity.this, "User Not Available", Toast.LENGTH_SHORT).show();
                finish();
            }
        }, 1000 * 30);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

    }

    private void handleOutGoingCall() {
        // soundPoolManager.playCallPlaceRinging();
        ringtonePlayer.play(true);
        setCallPickHandle();

    }

    private void intializeUI() {
        activitySingleAudioDemoBinding.muteAudioCallAction.setOnClickListener(muteClickListener());
        activitySingleAudioDemoBinding.speakerEnable.setOnClickListener(speakerClickListner());
        activitySingleAudioDemoBinding.startScreenSharing.setOnClickListener(startScreenShareListner());
        activitySingleAudioDemoBinding.switchToVideo.setOnClickListener(videoEnableDisableClickListner());


    }

    private void setActionButtonVisibility() {
        activitySingleAudioDemoBinding.muteAudioCallAction.setVisibility(View.VISIBLE);
        activitySingleAudioDemoBinding.startScreenSharing.setVisibility(View.VISIBLE);
        activitySingleAudioDemoBinding.addParticipants.setVisibility(View.VISIBLE);
        activitySingleAudioDemoBinding.switchToVideo.setVisibility(View.VISIBLE);
        activitySingleAudioDemoBinding.speakerEnable.setVisibility(View.VISIBLE);
    }

    private void setUserData() {
        connectedUsers.add(eventUser.getId());
        activitySingleAudioDemoBinding.connectingStatus.setText("Connecting");
        activitySingleAudioDemoBinding.callerName.setText(String.format("%s %s", eventUser.getfName(), eventUser.getlName()));
    }

    private void initializeParticipantContainers() {

        availableParticipantContainers.clear();
        availableParticipantContainers.push(activitySingleAudioDemoBinding.firstUserVideo);
        availableParticipantContainers.push(activitySingleAudioDemoBinding.secondUserVideoView);
        availableParticipantContainers.push(activitySingleAudioDemoBinding.fourthUserVideo);
        availableParticipantContainers.push(activitySingleAudioDemoBinding.fourthUserVideo);
    }


    private void createAudioAndVideoTracks() {
        // Share your microphone
        localAudioTrack = LocalAudioTrack.create(this, true, LOCAL_AUDIO_TRACK_NAME);
        cameraCapturerCompat = new CameraCapturer(this, getAvailableCameraSource());

    }

    private void setDisconnectAction() {
        activitySingleAudioDemoBinding.audioCallHangupAction.setOnClickListener(disconnectClickListener());
    }

    private CameraCapturer.CameraSource getAvailableCameraSource() {
        return (CameraCapturer.isSourceAvailable(CameraCapturer.CameraSource.FRONT_CAMERA)) ?
                (CameraCapturer.CameraSource.FRONT_CAMERA) :
                (CameraCapturer.CameraSource.BACK_CAMERA);
    }

    private void setAccessToken() {
        if (!BuildConfig.USE_TOKEN_SERVER) {
            getAuthToken(HRpreference.getInstance(getApplicationContext()).getUserInfo().getAuthToken());
        }
    }

    private boolean checkPermissionForCameraAndMicrophone() {
        int resultCamera = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        int resultMic = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        return resultCamera == PackageManager.PERMISSION_GRANTED &&
                resultMic == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissionForCameraAndMicrophone() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA) ||
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.RECORD_AUDIO)) {
            Toast.makeText(this,
                    R.string.permissions_needed,
                    Toast.LENGTH_LONG).show();

        } else {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO},
                    CAMERA_MIC_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == CAMERA_MIC_PERMISSION_REQUEST_CODE) {
            boolean cameraAndMicPermissionGranted = true;
            for (int grantResult : grantResults) {
                cameraAndMicPermissionGranted &= grantResult == PackageManager.PERMISSION_GRANTED;
            }
            if (cameraAndMicPermissionGranted) {
                createAudioAndVideoTracks();
                setAccessToken();
            } else {
                Toast.makeText(this,
                        R.string.permissions_needed,
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    private void connectToRoom(String roomName) {
        configureAudio(true);
        enableVolumeControl(true);
        ConnectOptions.Builder connectOptionsBuilder = new ConnectOptions.Builder(accessToken).enableDominantSpeaker(true)
                .roomName(roomName);
        if (localAudioTrack != null) {
            connectOptionsBuilder
                    .audioTracks(Collections.singletonList(localAudioTrack));
        }
        connectOptionsBuilder.preferAudioCodecs(Collections.singletonList(audioCodec));

        connectOptionsBuilder.encodingParameters(encodingParameters);
        room = Video.connect(this, connectOptionsBuilder.build(), roomListener());
        genrateLocalNotification();
        //  setDisconnectAction();
    }

    private void configureAudio(boolean enable) {
        if (enable) {
            previousAudioMode = audioManager.getMode();
            // Request audio focus before making any device switch
            requestAudioFocus();

            audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
            previousMicrophoneMute = audioManager.isMicrophoneMute();
            audioManager.setMicrophoneMute(false);
        } else {
            audioManager.setMode(previousAudioMode);
            audioManager.abandonAudioFocus(null);
            audioManager.setMicrophoneMute(previousMicrophoneMute);
        }
    }

    private void requestAudioFocus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            AudioAttributes playbackAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build();
            AudioFocusRequest focusRequest =
                    new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)
                            .setAudioAttributes(playbackAttributes)
                            .setAcceptsDelayedFocusGain(true)
                            .setOnAudioFocusChangeListener(
                                    i -> {
                                    })
                            .build();
            audioManager.requestAudioFocus(focusRequest);
        } else {
            audioManager.requestAudioFocus(null, AudioManager.STREAM_VOICE_CALL,
                    AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
        }
    }

    private void enableVolumeControl(boolean volumeControl) {
        if (volumeControl) {
            setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
        } else {
            setVolumeControlStream(getVolumeControlStream());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        audioCodec = getAudioCodec();
        audioManager.setSpeakerphoneOn(isSpeakerPhoneEnabled);
        registerReceiver();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    public AudioCodec getAudioCodec() {
        return new OpusCodec();
    }

    private void getAuthToken(String token) {
        TwillioRequest twillioRequest = new TwillioRequest();
        twillioRequest.getTwillioAuthToken(token, true, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, IOException e) {
                call.cancel();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                int statusCode = response.code();
                if (statusCode == 200) {
                    if (response.body() != null) {
                        SingleAudioDemoActivity.this.accessToken = response.body().string();
                    }
                    runOnUiThread(() -> connectToRoom(roomName));
                }

            }

        });
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver();
        if (runnable != null)
            handler.removeCallbacks(runnable);

        if (room != null && room.getState() != Room.State.DISCONNECTED) {
            room.disconnect();
            disconnectedFromOnDestroy = true;
        }
        if (localAudioTrack != null) {
            localAudioTrack.release();
            localAudioTrack = null;
        }
        soundPoolManager.release();
        enableVolumeControl(false);
        configureAudio(false);
        cancelNotification();
        HRpreference.getInstance(getApplicationContext()).saveIsCallScreenVisible(false);
        ringtonePlayer.stop();
        super.onDestroy();
    }

    private void addParticipantsView() {
        if (connectedUsers.size() == 1) {
            setOneUserView();
        } else if (connectedUsers.size() == 2) {
            setTwoUserView();
        } else if (connectedUsers.size() == 3) {
            setThreeUserView();
        } else if (connectedUsers.size() == 4) {
            setFourUserView();
        } else {
            setFourUserView();
        }
    }

    private void setOneUserView() {
        activitySingleAudioDemoBinding.singlUserView.setVisibility(View.VISIBLE);
        activitySingleAudioDemoBinding.twoUserContainer.setVisibility(GONE);
        activitySingleAudioDemoBinding.threeUserContainer.setVisibility(GONE);
        activitySingleAudioDemoBinding.fourUserContainer.setVisibility(GONE);
        EventUser firstUser = getEventUserById(connectedUsers.get(0));
        if (firstUser != null) {
            activitySingleAudioDemoBinding.callerName.setText(String.format("%s %s", firstUser.getfName(), firstUser.getlName()));
        }


    }

    private void setTwoUserView() {
        activitySingleAudioDemoBinding.singlUserView.setVisibility(GONE);
        activitySingleAudioDemoBinding.twoUserContainer.setVisibility(View.VISIBLE);
        activitySingleAudioDemoBinding.threeUserContainer.setVisibility(GONE);
        activitySingleAudioDemoBinding.fourUserContainer.setVisibility(GONE);
        EventUser firstUser = getEventUserById(connectedUsers.get(0));
        EventUser secondUser = getEventUserById(connectedUsers.get(1));
        StringBuilder connectedUserTitle = new StringBuilder();
        if (firstUser != null) {
            connectedUserTitle.append(firstUser.getfName()).append(" , ");
        }
        if (secondUser != null) {
            connectedUserTitle.append(secondUser.getfName()).append("");
        }
        activitySingleAudioDemoBinding.callerName.setText(String.format("%s ", connectedUserTitle.toString()));

    }

    private void setThreeUserView() {
        activitySingleAudioDemoBinding.singlUserView.setVisibility(GONE);
        activitySingleAudioDemoBinding.twoUserContainer.setVisibility(GONE);
        activitySingleAudioDemoBinding.threeUserContainer.setVisibility(View.VISIBLE);
        activitySingleAudioDemoBinding.fourUserContainer.setVisibility(GONE);
        EventUser firstUser = getEventUserById(connectedUsers.get(0));
        EventUser secondUser = getEventUserById(connectedUsers.get(1));
        EventUser thirdUser = getEventUserById(connectedUsers.get(2));
        StringBuilder connectedUserTitle = new StringBuilder();
        if (firstUser != null) {
            connectedUserTitle.append(firstUser.getfName()).append(" , ");
        }
        if (secondUser != null) {
            connectedUserTitle.append(secondUser.getfName()).append(" , ");
        }
        if (thirdUser != null) {
            connectedUserTitle.append(thirdUser.getfName());
        }
        activitySingleAudioDemoBinding.callerName.setText(String.format("%s ", connectedUserTitle.toString()));

    }

    private void setFourUserView() {
        activitySingleAudioDemoBinding.singlUserView.setVisibility(GONE);
        activitySingleAudioDemoBinding.twoUserContainer.setVisibility(GONE);
        activitySingleAudioDemoBinding.threeUserContainer.setVisibility(GONE);
        activitySingleAudioDemoBinding.fourUserContainer.setVisibility(View.VISIBLE);
        EventUser firstUser = getEventUserById(connectedUsers.get(0));
        EventUser secondUser = getEventUserById(connectedUsers.get(1));
        EventUser thirdUser = getEventUserById(connectedUsers.get(2));
        EventUser fourthUser = getEventUserById(connectedUsers.get(3));

        StringBuilder connectedUserTitle = new StringBuilder();
        if (firstUser != null) {
            connectedUserTitle.append(firstUser.getfName()).append(" , ");
        }
        if (secondUser != null) {
            connectedUserTitle.append(secondUser.getfName()).append(" , ");
        }
        if (thirdUser != null) {
            connectedUserTitle.append(thirdUser.getfName()).append(" , ");
        }
        if (fourthUser != null) {
            connectedUserTitle.append(fourthUser.getfName()).append(" , ");
        }
        activitySingleAudioDemoBinding.callerName.setText(String.format("%s ", connectedUserTitle.toString()));
    }


    private void addRemoteParticipant(RemoteParticipant remoteParticipant) {
        remoteParticipantIdentity = remoteParticipant.getIdentity();
        removeFromFrienList(remoteParticipantIdentity);
        if (!connectedUsers.contains(remoteParticipant.getIdentity())) {
            connectedUsers.add(remoteParticipant.getIdentity());
            addParticipantsView();
        }
        if (!isCallPicked) {
            activitySingleAudioDemoBinding.connectingStatus.setText(getResources().getString(R.string.connected_status));
            activitySingleAudioDemoBinding.chronometer.setVisibility(View.VISIBLE);
            activitySingleAudioDemoBinding.chronometer.setBase(SystemClock.elapsedRealtime());
            activitySingleAudioDemoBinding.chronometer.start();
            setActionButtonVisibility();
            // soundPoolManager.stopCallPlaceRinging();
            ringtonePlayer.stop();
            isCallPicked = true;
            UpdateNotifocation();
        }

        remoteParticipant.setListener(remoteParticipantListener());
    }

    private void removeRemoteParticipant(RemoteParticipant remoteParticipant) {
        // Toast.makeText(this, String.format("RemoteParticipant %s left", remoteParticipant.getIdentity()), Toast.LENGTH_LONG);
        addToFrienList(remoteParticipant.getIdentity());
        if (connectedUsers.size() > 0) {
            addParticipantsView();
        } else {
            finish();
        }
    }

    private void renderRemoteVideo(VideoTrack videoTrack2) {
        if (primaryVideoView.getTag() != null) {
            VideoTrack videoTrack = (VideoTrack) primaryVideoView.getTag();
            videoTrack.removeRenderer(primaryVideoView);
            primaryVideoView.setTag(videoTrack2);
            videoTrack2.addRenderer(primaryVideoView);
        }


    }

    private void addRemoteParticipantVideo(RemoteParticipant remoteParticipant, VideoTrack videoTrack) {
        if (!isVideoShowing) {
            primaryVideoView = findViewById(R.id.primary_video_view);
            primaryVideoView.setTag(videoTrack);
            activitySingleAudioDemoBinding.shareScreenContainer.setVisibility(View.VISIBLE);
            primaryVideoView.setVisibility(View.VISIBLE);
            activitySingleAudioDemoBinding.userProfilesView.setVisibility(GONE);
            videoTrack.addRenderer(primaryVideoView);
            addParticipantsInView(remoteParticipant, videoTrack);
            isVideoShowing = true;
            videoCount = videoCount + 1;
        } else {
            addParticipantsInView(remoteParticipant, videoTrack);
            videoCount = videoCount + 1;
        }


    }

    private void addParticipantsInView(RemoteParticipant remoteParticipant, VideoTrack videoTrack) {
        View layout2 = LayoutInflater.from(this).inflate(R.layout.participnats_user_view, activitySingleAudioDemoBinding.participantsViewContainer, false);
        EventUser eventUser = getEventUserById(remoteParticipant.getIdentity());
        ImageView profilePicImageView = layout2.findViewById(R.id.profilePictureView);
        TextView userName = layout2.findViewById(R.id.userNameView);
        if (eventUser != null) {
            userName.setText(String.format("%s %s", eventUser.getfName(), eventUser.getlName()));
        }
        layout2.setTag(videoTrack);
        layout2.setOnClickListener(view -> {
            VideoTrack videoTrack3 = (VideoTrack) view.getTag();
            renderRemoteVideo(videoTrack3);
        });
        activitySingleAudioDemoBinding.participantsViewContainer.addView(layout2);
        participantVideoView.put(remoteParticipant.getIdentity(), layout2);
    }

    private void removeParticipantVideo(RemoteParticipant remoteParticipant) {
        if (videoCount > 0) {
            View remoteUserView = participantVideoView.get(remoteParticipant.getIdentity());
            activitySingleAudioDemoBinding.participantsViewContainer.removeView(remoteUserView);
            participantVideoView.remove(remoteParticipant.getIdentity());
            Map.Entry<String, View> entry = participantVideoView.entrySet().iterator().next();
            View value = entry.getValue();
            VideoTrack videoTrack = (VideoTrack) value.getTag();
            renderRemoteVideo(videoTrack);
        } else {
            if (primaryVideoView.getTag() != null) {
                VideoTrack videoTrack = (VideoTrack) primaryVideoView.getTag();
                videoTrack.removeRenderer(primaryVideoView);
                primaryVideoView.setVisibility(GONE);
            }
            activitySingleAudioDemoBinding.userProfilesView.setVisibility(View.VISIBLE);
            primaryVideoView.setVisibility(GONE);
            activitySingleAudioDemoBinding.shareScreenContainer.setVisibility(GONE);
            isVideoShowing = false;
        }


    }


    private Room.Listener roomListener() {
        return new Room.Listener() {
            @Override
            public void onConnected(@NonNull Room room) {
                localParticipant = room.getLocalParticipant();
                connectedUserCount = connectedUserCount + room.getRemoteParticipants().size();
                for (RemoteParticipant remoteParticipant : room.getRemoteParticipants()) {
                    addRemoteParticipant(remoteParticipant);
                }
            }

            @Override
            public void onReconnecting(@NonNull Room room, @NonNull TwilioException twilioException) {
                // //videoStatusTextView.setText(String.format("Reconnecting to %s", room.getName()));

            }

            @Override
            public void onReconnected(@NonNull Room room) {
                //videoStatusTextView.setText(String.format("Connected to %s", room.getName()));
            }

            @Override
            public void onConnectFailure(@NonNull Room room, @NonNull TwilioException e) {
                //videoStatusTextView.setText("Failed to connect");
                configureAudio(false);
                enableVolumeControl(false);
            }

            @Override
            public void onDisconnected(Room room, TwilioException e) {
                SingleAudioDemoActivity.this.room = null;
                configureAudio(false);
                enableVolumeControl(false);
            }

            @Override
            public void onDominantSpeakerChanged(@NonNull Room room, @Nullable RemoteParticipant remoteParticipant) {


            }

            @Override
            public void onParticipantConnected(@NonNull Room room, @NonNull RemoteParticipant remoteParticipant) {
                addRemoteParticipant(remoteParticipant);

            }

            @Override
            public void onParticipantDisconnected(@NonNull Room room, @NonNull RemoteParticipant remoteParticipant) {
                connectedUsers.remove(remoteParticipant.getIdentity());
                removeRemoteParticipant(remoteParticipant);
            }

            @Override
            public void onRecordingStarted(@NonNull Room room) {

            }

            @Override
            public void onRecordingStopped(@NonNull Room room) {

            }
        };
    }

    private void genrateLocalNotification() {
        ((App) getApplication()).triggerOnGoingNotification(SingleAudioDemoActivity.class,
                getString(R.string.CHANNEL_ID),
                getString(R.string.app_name),
                eventUser.getfName() + " " + eventUser.getlName(),
                "Connecting ",
                NotificationCompat.PRIORITY_HIGH,
                true,
                getResources().getInteger(R.integer.calling_notificationId),
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void UpdateNotifocation() {
        ((App) getApplication()).updateNotification(SingleAudioDemoActivity.class,
                getString(R.string.CHANNEL_ID),
                getString(R.string.app_name),
                eventUser.getfName() + " " + eventUser.getlName(),
                "Ongoing audio call",
                getResources().getInteger(R.integer.calling_notificationId),
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void cancelNotification() {
        ((App) getApplication()).cancelNotification(getResources().getInteger(R.integer.calling_notificationId));
    }

    private RemoteParticipant.Listener remoteParticipantListener() {
        return new RemoteParticipant.Listener() {
            @Override
            public void onAudioTrackPublished(RemoteParticipant remoteParticipant,
                                              RemoteAudioTrackPublication remoteAudioTrackPublication) {
                Log.i(TAG, String.format("onAudioTrackPublished: " +
                                "[RemoteParticipant: identity=%s], " +
                                "[RemoteAudioTrackPublication: sid=%s, enabled=%b, " +
                                "subscribed=%b, name=%s]",
                        remoteParticipant.getIdentity(),
                        remoteAudioTrackPublication.getTrackSid(),
                        remoteAudioTrackPublication.isTrackEnabled(),
                        remoteAudioTrackPublication.isTrackSubscribed(),
                        remoteAudioTrackPublication.getTrackName()));
                //videoStatusTextView.setText("onAudioTrackPublished");
            }

            @Override
            public void onAudioTrackUnpublished(RemoteParticipant remoteParticipant,
                                                RemoteAudioTrackPublication remoteAudioTrackPublication) {
                Log.i(TAG, String.format("onAudioTrackUnpublished: " +
                                "[RemoteParticipant: identity=%s], " +
                                "[RemoteAudioTrackPublication: sid=%s, enabled=%b, " +
                                "subscribed=%b, name=%s]",
                        remoteParticipant.getIdentity(),
                        remoteAudioTrackPublication.getTrackSid(),
                        remoteAudioTrackPublication.isTrackEnabled(),
                        remoteAudioTrackPublication.isTrackSubscribed(),
                        remoteAudioTrackPublication.getTrackName()));
                //videoStatusTextView.setText("onAudioTrackUnpublished");
            }

            @Override
            public void onDataTrackPublished(RemoteParticipant remoteParticipant,
                                             RemoteDataTrackPublication remoteDataTrackPublication) {
                Log.i(TAG, String.format("onDataTrackPublished: " +
                                "[RemoteParticipant: identity=%s], " +
                                "[RemoteDataTrackPublication: sid=%s, enabled=%b, " +
                                "subscribed=%b, name=%s]",
                        remoteParticipant.getIdentity(),
                        remoteDataTrackPublication.getTrackSid(),
                        remoteDataTrackPublication.isTrackEnabled(),
                        remoteDataTrackPublication.isTrackSubscribed(),
                        remoteDataTrackPublication.getTrackName()));
                //videoStatusTextView.setText("onDataTrackPublished");
            }

            @Override
            public void onDataTrackUnpublished(RemoteParticipant remoteParticipant,
                                               RemoteDataTrackPublication remoteDataTrackPublication) {
                Log.i(TAG, String.format("onDataTrackUnpublished: " +
                                "[RemoteParticipant: identity=%s], " +
                                "[RemoteDataTrackPublication: sid=%s, enabled=%b, " +
                                "subscribed=%b, name=%s]",
                        remoteParticipant.getIdentity(),
                        remoteDataTrackPublication.getTrackSid(),
                        remoteDataTrackPublication.isTrackEnabled(),
                        remoteDataTrackPublication.isTrackSubscribed(),
                        remoteDataTrackPublication.getTrackName()));
                //videoStatusTextView.setText("onDataTrackUnpublished");
            }

            @Override
            public void onVideoTrackPublished(RemoteParticipant remoteParticipant,
                                              RemoteVideoTrackPublication remoteVideoTrackPublication) {
                Log.i(TAG, String.format("onVideoTrackPublished: " +
                                "[RemoteParticipant: identity=%s], " +
                                "[RemoteVideoTrackPublication: sid=%s, enabled=%b, " +
                                "subscribed=%b, name=%s]",
                        remoteParticipant.getIdentity(),
                        remoteVideoTrackPublication.getTrackSid(),
                        remoteVideoTrackPublication.isTrackEnabled(),
                        remoteVideoTrackPublication.isTrackSubscribed(),
                        remoteVideoTrackPublication.getTrackName()));
                //videoStatusTextView.setText("onVideoTrackPublished");
            }

            @Override
            public void onVideoTrackUnpublished(RemoteParticipant remoteParticipant,
                                                RemoteVideoTrackPublication remoteVideoTrackPublication) {
                Log.i(TAG, String.format("onVideoTrackUnpublished: " +
                                "[RemoteParticipant: identity=%s], " +
                                "[RemoteVideoTrackPublication: sid=%s, enabled=%b, " +
                                "subscribed=%b, name=%s]",
                        remoteParticipant.getIdentity(),
                        remoteVideoTrackPublication.getTrackSid(),
                        remoteVideoTrackPublication.isTrackEnabled(),
                        remoteVideoTrackPublication.isTrackSubscribed(),
                        remoteVideoTrackPublication.getTrackName()));
                //videoStatusTextView.setText("onVideoTrackUnpublished");
            }

            @Override
            public void onAudioTrackSubscribed(RemoteParticipant remoteParticipant,
                                               RemoteAudioTrackPublication remoteAudioTrackPublication,
                                               RemoteAudioTrack remoteAudioTrack) {
                Log.i(TAG, String.format("onAudioTrackSubscribed: " +
                                "[RemoteParticipant: identity=%s], " +
                                "[RemoteAudioTrack: enabled=%b, playbackEnabled=%b, name=%s]",
                        remoteParticipant.getIdentity(),
                        remoteAudioTrack.isEnabled(),
                        remoteAudioTrack.isPlaybackEnabled(),
                        remoteAudioTrack.getName()));
                //videoStatusTextView.setText("onAudioTrackSubscribed");
            }

            @Override
            public void onAudioTrackUnsubscribed(RemoteParticipant remoteParticipant,
                                                 RemoteAudioTrackPublication remoteAudioTrackPublication,
                                                 RemoteAudioTrack remoteAudioTrack) {
                Log.i(TAG, String.format("onAudioTrackUnsubscribed: " +
                                "[RemoteParticipant: identity=%s], " +
                                "[RemoteAudioTrack: enabled=%b, playbackEnabled=%b, name=%s]",
                        remoteParticipant.getIdentity(),
                        remoteAudioTrack.isEnabled(),
                        remoteAudioTrack.isPlaybackEnabled(),
                        remoteAudioTrack.getName()));
                //videoStatusTextView.setText("onAudioTrackUnsubscribed");
            }

            @Override
            public void onAudioTrackSubscriptionFailed(RemoteParticipant remoteParticipant,
                                                       RemoteAudioTrackPublication remoteAudioTrackPublication,
                                                       TwilioException twilioException) {
                Log.i(TAG, String.format("onAudioTrackSubscriptionFailed: " +
                                "[RemoteParticipant: identity=%s], " +
                                "[RemoteAudioTrackPublication: sid=%b, name=%s]" +
                                "[TwilioException: code=%d, message=%s]",
                        remoteParticipant.getIdentity(),
                        remoteAudioTrackPublication.getTrackSid(),
                        remoteAudioTrackPublication.getTrackName(),
                        twilioException.getCode(),
                        twilioException.getMessage()));
                //videoStatusTextView.setText("onAudioTrackSubscriptionFailed");
            }

            @Override
            public void onDataTrackSubscribed(RemoteParticipant remoteParticipant,
                                              RemoteDataTrackPublication remoteDataTrackPublication,
                                              RemoteDataTrack remoteDataTrack) {
                Log.i(TAG, String.format("onDataTrackSubscribed: " +
                                "[RemoteParticipant: identity=%s], " +
                                "[RemoteDataTrack: enabled=%b, name=%s]",
                        remoteParticipant.getIdentity(),
                        remoteDataTrack.isEnabled(),
                        remoteDataTrack.getName()));
                //videoStatusTextView.setText("onDataTrackSubscribed");
            }

            @Override
            public void onDataTrackUnsubscribed(RemoteParticipant remoteParticipant,
                                                RemoteDataTrackPublication remoteDataTrackPublication,
                                                RemoteDataTrack remoteDataTrack) {
                Log.i(TAG, String.format("onDataTrackUnsubscribed: " +
                                "[RemoteParticipant: identity=%s], " +
                                "[RemoteDataTrack: enabled=%b, name=%s]",
                        remoteParticipant.getIdentity(),
                        remoteDataTrack.isEnabled(),
                        remoteDataTrack.getName()));
                //videoStatusTextView.setText("onDataTrackUnsubscribed");
            }

            @Override
            public void onDataTrackSubscriptionFailed(RemoteParticipant remoteParticipant,
                                                      RemoteDataTrackPublication remoteDataTrackPublication,
                                                      TwilioException twilioException) {
                Log.i(TAG, String.format("onDataTrackSubscriptionFailed: " +
                                "[RemoteParticipant: identity=%s], " +
                                "[RemoteDataTrackPublication: sid=%b, name=%s]" +
                                "[TwilioException: code=%d, message=%s]",
                        remoteParticipant.getIdentity(),
                        remoteDataTrackPublication.getTrackSid(),
                        remoteDataTrackPublication.getTrackName(),
                        twilioException.getCode(),
                        twilioException.getMessage()));
                //videoStatusTextView.setText("onDataTrackSubscriptionFailed");
            }

            @Override
            public void onVideoTrackSubscribed(RemoteParticipant remoteParticipant,
                                               RemoteVideoTrackPublication remoteVideoTrackPublication,
                                               RemoteVideoTrack remoteVideoTrack) {
                Log.i(TAG, String.format("onVideoTrackSubscribed: " +
                                "[RemoteParticipant: identity=%s], " +
                                "[RemoteVideoTrack: enabled=%b, name=%s]",
                        remoteParticipant.getIdentity(),
                        remoteVideoTrack.isEnabled(),
                        remoteVideoTrack.getName()));
                addRemoteParticipantVideo(remoteParticipant, remoteVideoTrack);
            }

            @Override
            public void onVideoTrackUnsubscribed(RemoteParticipant remoteParticipant,
                                                 RemoteVideoTrackPublication remoteVideoTrackPublication,
                                                 RemoteVideoTrack remoteVideoTrack) {
                videoCount = videoCount - 1;
                removeParticipantVideo(remoteParticipant);
            }

            @Override
            public void onVideoTrackSubscriptionFailed(RemoteParticipant remoteParticipant,
                                                       RemoteVideoTrackPublication remoteVideoTrackPublication,
                                                       TwilioException twilioException) {
                Log.i(TAG, String.format("onVideoTrackSubscriptionFailed: " +
                                "[RemoteParticipant: identity=%s], " +
                                "[RemoteVideoTrackPublication: sid=%b, name=%s]" +
                                "[TwilioException: code=%d, message=%s]",
                        remoteParticipant.getIdentity(),
                        remoteVideoTrackPublication.getTrackSid(),
                        remoteVideoTrackPublication.getTrackName(),
                        twilioException.getCode(),
                        twilioException.getMessage()));
                //videoStatusTextView.setText("onVideoTrackSubscriptionFailed");
//                Snackbar.make(connectActionFab,
//                        String.format("Failed to subscribe to %s video track",
//                                remoteParticipant.getIdentity()),
//                        Snackbar.LENGTH_LONG)
//                        .show();
            }

            @Override
            public void onAudioTrackEnabled(RemoteParticipant remoteParticipant,
                                            RemoteAudioTrackPublication remoteAudioTrackPublication) {

            }

            @Override
            public void onAudioTrackDisabled(RemoteParticipant remoteParticipant,
                                             RemoteAudioTrackPublication remoteAudioTrackPublication) {

            }

            @Override
            public void onVideoTrackEnabled(RemoteParticipant remoteParticipant,
                                            RemoteVideoTrackPublication remoteVideoTrackPublication) {

            }

            @Override
            public void onVideoTrackDisabled(RemoteParticipant remoteParticipant,
                                             RemoteVideoTrackPublication remoteVideoTrackPublication) {

            }
        };
    }

    private View.OnClickListener muteClickListener() {
        return v -> {

            if (localAudioTrack != null) {
                boolean enable = !localAudioTrack.isEnabled();
                localAudioTrack.enable(enable);
                int icon = enable ?
                        R.drawable.mic_enable : R.drawable.min_disable;
                activitySingleAudioDemoBinding.muteAudioCallAction.setImageDrawable(ContextCompat.getDrawable(
                        SingleAudioDemoActivity.this, icon));
            }
        };
    }

    private View.OnClickListener speakerClickListner() {
        return v -> {

            if (!isSpeakerPhoneEnabled) {
                isSpeakerPhoneEnabled = true;
                audioManager.setSpeakerphoneOn(isSpeakerPhoneEnabled);
            } else {
                isSpeakerPhoneEnabled = false;
                audioManager.setSpeakerphoneOn(isSpeakerPhoneEnabled);
            }
            int icon = isSpeakerPhoneEnabled ?
                    R.drawable.speaker_off : R.drawable.speaker_on;
            activitySingleAudioDemoBinding.speakerEnable.setImageDrawable(ContextCompat.getDrawable(
                    SingleAudioDemoActivity.this, icon));
        };
    }

    private View.OnClickListener startScreenShareListner() {
        return v -> {
            if (!isScreeenShare) {
                if (screenCapturer == null) {
                    requestScreenCapturePermission();
                } else {
                    startScreenCapture();
                }
                isScreeenShare = true;
            } else {
                isScreeenShare = false;
                stopScreenCapture();
            }
        };
    }

    private View.OnClickListener videoEnableDisableClickListner() {
        return v -> {
            if (isVideoEnable) {
                unPublishedLocalTrack();
                activitySingleAudioDemoBinding.switchToVideo.setImageResource(R.drawable.no_video);
                activitySingleAudioDemoBinding.startScreenSharing.setEnabled(true);
                isVideoEnable = false;
            } else {
                enableVideoCall();
                activitySingleAudioDemoBinding.startScreenSharing.setEnabled(false);
                activitySingleAudioDemoBinding.switchToVideo.setImageResource(R.drawable.video_call_enable);
                isVideoEnable = true;
            }
        };
    }

    private View.OnClickListener disconnectClickListener() {
        return v -> {
            if (room != null) {
                room.disconnect();
            }
            ringtonePlayer.stop();
            String userToken = HRpreference.getInstance(getApplicationContext()).getUserInfo().getAuthToken();
            diclineCallRequest(userToken, callId);
            finish();
        };
    }

    @Override
    public void onBackPressed() {
        if (BaseActivity.getNumberOfTasks() > 2) {
            Intent intent = new Intent(SingleAudioDemoActivity.this, HomeScreen.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        } else {
            moveTaskToBack(true);
        }

    }

    private void diclineCallRequest(String token, String callID) {
        CallApiRequest callApiRequest = new CallApiRequest();
        callApiRequest.requestFoEndCall(callID, token, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, IOException e) {
                String error = e.getLocalizedMessage();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

            }
        });

    }

    private void registerReceiver() {
        if (!isReceiverRegistered) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(DISCONNECT_CALL_ACTION);
            LocalBroadcastManager.getInstance(this).registerReceiver(
                    audioCallReciever, intentFilter);
            isReceiverRegistered = true;
        }
    }

    private void unregisterReceiver() {
        if (isReceiverRegistered) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(audioCallReciever);
            isReceiverRegistered = false;
        }
    }

    private void unPublishedLocalTrack() {
        if (localVideoTrack != null) {
            if (localParticipant != null) {
                localParticipant.unpublishTrack(localVideoTrack);
            }
            localVideoTrack.release();
            localVideoTrack = null;
        }
    }


    private void stopScreenCapture() {
        unPublishedLocalTrack();
        activitySingleAudioDemoBinding.startScreenSharing.setImageResource(R.drawable.screenshare_disable);
    }


    private void requestScreenCapturePermission() {
        MediaProjectionManager mediaProjectionManager = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            mediaProjectionManager = (MediaProjectionManager)
                    getSystemService(Context.MEDIA_PROJECTION_SERVICE);
            startActivityForResult(mediaProjectionManager.createScreenCaptureIntent(),
                    REQUEST_MEDIA_PROJECTION);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_MEDIA_PROJECTION) {
            if (resultCode != AppCompatActivity.RESULT_OK) {
                Toast.makeText(this, R.string.screen_capture_permission_not_granted,
                        Toast.LENGTH_LONG).show();
                return;
            }
            screenCapturer = new ScreenCapturer(this, resultCode, data, screenCapturerListener);
            startScreenCapture();
        }
    }

    private void enableVideoCall() {
        if (localVideoTrack == null && checkPermissionForCameraAndMicrophone()) {
            localVideoTrack = LocalVideoTrack.create(this, true, cameraCapturerCompat);
            if (localParticipant != null) {
                localParticipant.publishTrack(localVideoTrack);
                activitySingleAudioDemoBinding.startScreenSharing.setImageResource(R.drawable.screen_share_enable);
            }
        }
    }

    private void startScreenCapture() {
        unPublishedLocalTrack();
        if (localVideoTrack == null && checkPermissionForCameraAndMicrophone()) {
            localVideoTrack = LocalVideoTrack.create(this, true, screenCapturer);
            if (localParticipant != null) {
                localParticipant.publishTrack(localVideoTrack);
                activitySingleAudioDemoBinding.startScreenSharing.setImageResource(R.drawable.screen_share_enable);
            }
        }
    }

    private void showpopup() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(SingleAudioDemoActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.calling_popup_layout, null);
        alertDialog.setView(convertView);
        ListView lv = convertView.findViewById(R.id.userlist);
        Button callButton = convertView.findViewById(R.id.popupCallButton);
        callButton.setVisibility(GONE);
        Button popupCancelButton = convertView.findViewById(R.id.popupCancelButton);
        addMemberDialog = alertDialog.create();
        popupCancelButton.setOnClickListener(view -> {
            add_attendeee_adapter.clearSelection();
            addMemberDialog.dismiss();
        });

        lv.setAdapter(add_attendeee_adapter);
        UtitlityClass.setListViewHeightBasedOnChildren(lv);
        addMemberDialog.show();
    }

    private void getFriendList(String id, String token) {
        EventRequest request = new EventRequest();
        request.requestForFriendList(id, token, new Callback() {
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
                        Type listType = new TypeToken<List<FriendList>>() {
                        }.getType();
                        yourFriendList = new Gson().fromJson(myresponse, listType);
                        notifyFrindListAdapter(yourFriendList);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        });
    }

    private void removeFromFrienList(String id) {
        for (EventUser eventUser : friendlist) {
            if (eventUser.getId().equals(id))
                friendlist.remove(eventUser);
            break;
        }
        add_attendeee_adapter.notifyDataSetChanged();
    }

    private void addToFrienList(String id) {
        EventUser eventUser = getEventUserById(id);
        friendlist.add(eventUser);
        add_attendeee_adapter.notifyDataSetChanged();
    }

    private void notifyFrindListAdapter(List<FriendList> friendLists) {
        for (EventUser eventUserOb : friendLists.get(0).getAssociateList()) {
            if (!eventUser.getId().equals(eventUserOb.getId()))
                friendlist.add(eventUserOb);
        }
        runOnUiThread(() -> add_attendeee_adapter.notifyDataSetChanged());
    }

    private EventUser getEventUserById(String id) {
        for (EventUser eventUserOb : yourFriendList.get(0).getAssociateList()) {
            if (id.equals(eventUserOb.getId()))
                return eventUserOb;
        }
        return null;
    }


    @Override
    public void addCallerClicked(EventUser eventUser) {
        showConfirmMessage(eventUser);
    }

    private void showConfirmMessage(EventUser eventUser) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage("Do you  want to Add " + eventUser.getfName() + " " + eventUser.getlName() + " in Call");
        builder1.setCancelable(true);
        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        JSONArray member = new JSONArray();
                        member.put(eventUser.getId());
                        String userToken = HRpreference.getInstance(SingleAudioDemoActivity.this).getUserInfo().getAuthToken();
                        addMemberToCall(callId, member, userToken);
                        addMemberDialog.dismiss();
                        add_attendeee_adapter.remove(eventUser);
                        add_attendeee_adapter.notifyDataSetChanged();
                        dialog.cancel();
                    }
                });
        builder1.setNegativeButton(
                "No",
                (dialog, id) -> dialog.cancel());
        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    private void addMemberToCall(String callId, JSONArray member, String token) {
        CallApiRequest request = new CallApiRequest();
        request.requestForAddMemeberToCall(callId, member, token, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                call.cancel();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {


            }
        });
    }

    private class AudioCallReciever extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null && action.equals(DISCONNECT_CALL_ACTION)) {
                if (!isCallPicked) {
                    Toast.makeText(SingleAudioDemoActivity.this, "Call Declined", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    if (connectedUsers.size() == 0)
                        finish();
                }
            }
        }
    }

}