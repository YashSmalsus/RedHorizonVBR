package com.smalsus.redhorizonvbr.view.activities;

import android.Manifest;
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
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.smalsus.redhorizonvbr.App;
import com.smalsus.redhorizonvbr.BuildConfig;
import com.smalsus.redhorizonvbr.HRpreference;
import com.smalsus.redhorizonvbr.R;
import com.smalsus.redhorizonvbr.dialog.Dialog;
import com.smalsus.redhorizonvbr.model.EventUser;
import com.smalsus.redhorizonvbr.networkrequest.CallApiRequest;
import com.smalsus.redhorizonvbr.networkrequest.TwillioRequest;
import com.smalsus.redhorizonvbr.utils.HorizontalDottedProgress;
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
import com.twilio.video.VideoCodec;
import com.twilio.video.VideoTextureView;
import com.twilio.video.VideoTrack;
import com.twilio.video.Vp8Codec;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.view.View.GONE;

public class SingleVideoCalling extends BaseActivity implements View.OnClickListener {
    private static final int MAX_PARTICIPANTS = 4;

    private static final int REQUEST_MEDIA_PROJECTION = 1024;
    private static final int CAMERA_MIC_PERMISSION_REQUEST_CODE = 5;
    private static final String TAG = "MultiPartyActivity";
    private static final String LOCAL_AUDIO_TRACK_NAME = "mic";
    private static final String LOCAL_VIDEO_TRACK_NAME = "camera";
    public static String DISCONNECT_CALL_ACTION = "disconnect_call_action";
    public static String CHAT_ROOM_NAME = "CHAT_ROOM_NAME";
    public static String CALLING_USER_DETAILS = "CALLING_USER_DETAILS";
    public static String CALLING_ID = "calling_id";
    private String roomName, callId;
    private EventUser eventUser;

    private String accessToken;

    private Room room;
    private LocalParticipant localParticipant;

    private AudioCodec audioCodec;
    private VideoCodec videoCodec;
    /*
     * Encoding parameters represent the sender side bandwidth constraints.
     */
    private EncodingParameters encodingParameters;
    /*
     * Android shared preferences used for settings
     */
    private SharedPreferences preferences;
    /*
     * Android application UI elements
     */
    private CameraCapturer cameraCapturerCompat;
    private LocalAudioTrack localAudioTrack;
    private LocalVideoTrack localVideoTrack;
    private ImageButton switchCameraActionFab, hangupVideo_action_fab, localVideoActionFab, muteActionFab, startChatActionFab, startCallRecording;
    private ProgressBar reconnectingProgressBar;
    private AlertDialog connectDialog;
    private AudioManager audioManager;
    private String remoteParticipantIdentity;
    private int previousAudioMode;
    private boolean previousMicrophoneMute;
    private boolean disconnectedFromOnDestroy;
    private boolean isSpeakerPhoneEnabled = true;
    private Stack<ParticipantView> availableParticipantContainers = new Stack<>();
    private Map<String, ParticipantView> participantViewGroupMap = new HashMap<>();
    private ParticipantView localParticipantsView;
    private ImageView currentDominantSpeakerImg;
    private VideoTextureView localVideoTextureView;
    private final ScreenCapturer.Listener screenCapturerListener = new ScreenCapturer.Listener() {
        @Override
        public void onScreenCaptureError(String errorDescription) {
            Log.e(TAG, "Screen capturer error: " + errorDescription);
            stopScreenCapture();
            Toast.makeText(SingleVideoCalling.this, R.string.screen_capture_error,
                    Toast.LENGTH_LONG).show();
        }

        @Override
        public void onFirstFrameAvailable() {
            Log.d(TAG, "First frame from screen capturer available");
        }
    };
    private HorizontalDottedProgress horizontalDottedProgressBar;
    private TextView callerName, conectivityStatus;
    private Chronometer chronometer;
    private Handler handler = new Handler();
    private Runnable runnable;
    private boolean isCallPicked = false;
    private ScreenCapturer screenCapturer;
    private boolean isScreeenShare = false;
    private VideoCallReciever videoCallReciever;
    private boolean isReceiverRegistered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_video_calling);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        videoCallReciever = new VideoCallReciever();
        reconnectingProgressBar = findViewById(R.id.reconnecting_video_progress_bar);
        switchCameraActionFab = findViewById(R.id.switchCameraFAb);
        localVideoActionFab = findViewById(R.id.hold_video_action_fab);
        startChatActionFab = findViewById(R.id.chatStartButton);
        muteActionFab = findViewById(R.id.mute_voice_action_fab);
        hangupVideo_action_fab = findViewById(R.id.hangupVideo_action_fab);
        startCallRecording = findViewById(R.id.startCallRecording);
        startCallRecording.setOnClickListener(this);
        horizontalDottedProgressBar = findViewById(R.id.horizontalDottedProgressBar);
        callerName = findViewById(R.id.callerName);
        chronometer = findViewById(R.id.videoCallTimer);
        conectivityStatus = findViewById(R.id.conectivityStatus);

        // IS CALL IN PROGRESS ENABLE TRUE
        HRpreference hRpreference=HRpreference.getInstance(getApplicationContext());
        hRpreference.saveIsCallScreenVisible(true);
        // 1 - Video Call , 2- Audio Call , 3 - Multipart Video Call
        hRpreference.saveCallScreenType(1);

        initializeParticipantContainers();
        /*
         * Get shared preferences to read settings
         */
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        /*
         * Enable changing the volume using the up/down keys during a conversation
         */
        setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);

        /*
         * Needed for setting/abandoning audio focus during call
         */

        Intent intent = getIntent();
        roomName = intent.getStringExtra(CHAT_ROOM_NAME);
        callId = intent.getStringExtra(CALLING_ID);
        eventUser = (EventUser) intent.getSerializableExtra(CALLING_USER_DETAILS);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.setSpeakerphoneOn(isSpeakerPhoneEnabled);

        /*
         * Check camera and microphone permissions. Needed in Android M.
         */
        if (!checkPermissionForCameraAndMicrophone()) {
            requestPermissionForCameraAndMicrophone();
        } else {
            createAudioAndVideoTracks();
            setAccessToken();
        }
        intializeUI();
        setUserData();
    }

    @Override
    protected View getSnackbarAnchorView() {
        return null;
    }

    private void setUserData() {
        if (eventUser != null) {
            callerName.setText(String.format("%s %s", eventUser.getfName(), eventUser.getlName()));
            conectivityStatus.setText("Connecting...");
            horizontalDottedProgressBar.setVisibility(View.VISIBLE);
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

    public AudioCodec getAudioCodec() {
        return new OpusCodec();
    }

    public VideoCodec getVideoCodec() {
        return new Vp8Codec();
    }

    private void publishedLocalVideoTrack() {
        if (localVideoTrack == null && checkPermissionForCameraAndMicrophone()) {
            localVideoTrack = LocalVideoTrack.create(this,
                    true,
                    cameraCapturerCompat,
                    LOCAL_VIDEO_TRACK_NAME);
            localVideoTrack.addRenderer(localVideoTextureView);

            /*
             * If connected to a Room then share the local video track.
             */
            if (localParticipant != null) {
                localParticipant.publishTrack(localVideoTrack);
            }
        }
    }

    private void setCallPickHandle() {

        handler.postDelayed(runnable = new Runnable() {
            public void run() {
                if (!isCallPicked) {
                    Toast.makeText(SingleVideoCalling.this, "User Not Availble", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }, 1000 * 60);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver();
        setDisconnectAction();
        audioCodec = getAudioCodec();
        videoCodec = getVideoCodec();
        //  publishedLocalVideoTrack();
        audioManager.setSpeakerphoneOn(isSpeakerPhoneEnabled);
        if (room != null) {
            reconnectingProgressBar.setVisibility((room.getState() != Room.State.RECONNECTING) ?
                    GONE :
                    View.VISIBLE);
            if (room.getDominantSpeaker() != null) {
                currentDominantSpeakerImg.setVisibility(GONE);
            }
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

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        unregisterReceiver();
        unPublishedLocalTrack();
        configureAudio(false);
        enableVolumeControl(false);
        if (room != null && room.getState() != Room.State.DISCONNECTED) {
            room.disconnect();
            disconnectedFromOnDestroy = true;
        }
        if (localAudioTrack != null) {
            localAudioTrack.release();
            localAudioTrack = null;
        }
        if (localVideoTrack != null) {
            localVideoTrack.release();
            localVideoTrack = null;
        }

        cancelNotification();
        HRpreference.getInstance(getApplicationContext()).saveIsCallScreenVisible(false);
        super.onDestroy();
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

    private void createAudioAndVideoTracks() {
        localAudioTrack = LocalAudioTrack.create(this, true, LOCAL_AUDIO_TRACK_NAME);
        cameraCapturerCompat = new CameraCapturer(this, getAvailableCameraSource());
        localVideoTrack = LocalVideoTrack.create(this,
                true,
                cameraCapturerCompat,
                LOCAL_VIDEO_TRACK_NAME);
        localVideoTrack.addRenderer(localVideoTextureView);
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

    private void connectToRoom(String roomName) {
        configureAudio(true);
        enableVolumeControl(true);
        ConnectOptions.Builder connectOptionsBuilder = new ConnectOptions.Builder(accessToken).enableDominantSpeaker(true)
                .roomName(roomName);
        if (localAudioTrack != null) {
            connectOptionsBuilder
                    .audioTracks(Collections.singletonList(localAudioTrack));
        }
        if (localVideoTrack != null) {
            connectOptionsBuilder.videoTracks(Collections.singletonList(localVideoTrack));
        }
        connectOptionsBuilder.preferAudioCodecs(Collections.singletonList(audioCodec));
        connectOptionsBuilder.encodingParameters(encodingParameters);
        room = Video.connect(this, connectOptionsBuilder.build(), roomListener());
        genrateLocalNotification();
        setDisconnectAction();
    }

    private void setViewVisbility() {
        localVideoActionFab.setVisibility(View.VISIBLE);
        muteActionFab.setVisibility(View.VISIBLE);
        startChatActionFab.setVisibility(View.VISIBLE);
        switchCameraActionFab.setVisibility(View.VISIBLE);
        startCallRecording.setVisibility(View.VISIBLE);
    }

    private void intializeUI() {
        localVideoActionFab.setOnClickListener(localVideoClickListener());
        muteActionFab.setOnClickListener(muteClickListener());
        startChatActionFab.setOnClickListener(startChatListener());
        switchCameraActionFab.setOnClickListener(switchCameraClickListener());
        currentDominantSpeakerImg = null;
    }

    /*
     * The actions performed during disconnect.
     */
    private void setDisconnectAction() {
        hangupVideo_action_fab.setOnClickListener(disconnectClickListener());
    }

    /*
     * Creates an connect UI dialog
     */
    private void showConnectDialog() {
        EditText roomEditText = new EditText(this);
        connectDialog = Dialog.createConnectDialog(roomEditText,
                connectClickListener(roomEditText),
                cancelConnectDialogClickListener(),
                this);
        connectDialog.show();
    }

    private void initializeParticipantContainers() {
        localParticipantsView = findViewById(R.id.local_single_participant_container);
        localVideoTextureView = localParticipantsView.getVideoView();
        localVideoTextureView.setMirror(true);
        participantViewGroupMap.clear();
        availableParticipantContainers.clear();
        ParticipantView bottomRightPartipantContainer = findViewById(R.id.participantsview);
        VideoTextureView remoteParticipantVideoTextureView3 = bottomRightPartipantContainer.getVideoView();
        remoteParticipantVideoTextureView3.setVisibility(GONE);
        bottomRightPartipantContainer.setVisibility(GONE);
        availableParticipantContainers.push(bottomRightPartipantContainer);
    }

    private ParticipantView getAvailableParticipantContainer() {
        if (availableParticipantContainers.size() == 0) {
            throw new RuntimeException(String.format("This example app doesn't support more than %d RemoteParticipants", MAX_PARTICIPANTS));
        }
        // Just remove the first element
        return availableParticipantContainers.pop();
    }

    /*
     * Called when remote participant joins the room
     */
    private void addRemoteParticipant(RemoteParticipant remoteParticipant) {
        remoteParticipantIdentity = remoteParticipant.getIdentity();
        /*
         * Add remote participant renderer
         */
        if (remoteParticipant.getRemoteVideoTracks().size() > 0) {
            RemoteVideoTrackPublication remoteVideoTrackPublication =
                    remoteParticipant.getRemoteVideoTracks().get(0);

            if (remoteVideoTrackPublication.isTrackSubscribed()) {
                addRemoteParticipantVideo(remoteParticipant, remoteVideoTrackPublication.getRemoteVideoTrack());
            }
        }
        remoteParticipant.setListener(remoteParticipantListener());
    }

    private void genrateLocalNotification() {
        ((App) getApplication()).triggerOnGoingNotification(SingleVideoCalling.class,
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
        conectivityStatus.setText("Connected");
        ((App) getApplication()).updateNotification(SingleVideoCalling.class,
                getString(R.string.CHANNEL_ID),
                getString(R.string.app_name),
                eventUser.getfName() + " " + eventUser.getlName(),
                "VideoCall in Progress",
                getResources().getInteger(R.integer.calling_notificationId),
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void cancelNotification() {
        ((App) getApplication()).cancelNotification(getResources().getInteger(R.integer.calling_notificationId));
    }

    /*
     * Set primary view as renderer for participant video track
     */
    private void addRemoteParticipantVideo(RemoteParticipant remoteParticipant, VideoTrack videoTrack) {
        ParticipantView participantContainer = participantViewGroupMap.get(remoteParticipant.getSid());
        if (participantContainer == null) {
            participantContainer = getAvailableParticipantContainer();
        }
        VideoTextureView videoTextureView = participantContainer.getVideoView();

        videoTextureView.setTag(videoTrack);
        localParticipantsView.setVisibility(View.VISIBLE);
        localVideoTextureView.setVisibility(View.VISIBLE);
        final float scale = this.getResources().getDisplayMetrics().density;
        int width = (int) (120 * scale + 0.5f);
        int height = (int) (150 * scale + 0.5f);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, height);
        params.addRule(RelativeLayout.ABOVE, R.id.bottomActionsButton);
        localParticipantsView.setLayoutParams(params);
        participantContainer.setVisibility(View.VISIBLE);
        horizontalDottedProgressBar.setVisibility(GONE);
        videoTextureView.setVisibility(View.VISIBLE);
        videoTrack.addRenderer(videoTextureView);
        UpdateNotifocation();
        chronometer.setVisibility(View.VISIBLE);
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.start();
        participantViewGroupMap.put(remoteParticipant.getSid(), participantContainer);
        setViewVisbility();
    }

    /*
     * Called when remote participant leaves the room
     */
    private void removeRemoteParticipant(RemoteParticipant remoteParticipant) {
        if (!remoteParticipant.getIdentity().equals(remoteParticipantIdentity)) {
            return;
        }
        if (!remoteParticipant.getRemoteVideoTracks().isEmpty()) {
            RemoteVideoTrackPublication remoteVideoTrackPublication =
                    remoteParticipant.getRemoteVideoTracks().get(0);
            if (remoteVideoTrackPublication.isTrackSubscribed()) {
                removeParticipantVideo(remoteParticipant);
            }
        }
    }

    private void removeParticipantVideo(RemoteParticipant remoteParticipant) {
        ParticipantView participantContainer = participantViewGroupMap.get(remoteParticipant.getSid());
        VideoTextureView videoTextureView = participantContainer.getVideoView();

        VideoTrack videoTrack = (VideoTrack) videoTextureView.getTag();
        videoTrack.removeRenderer(videoTextureView);
        videoTextureView.setVisibility(GONE);

        participantViewGroupMap.remove(remoteParticipant.getSid());
        availableParticipantContainers.add(participantContainer);

        ImageView dominantSpeakerImg = participantContainer.getDominantSpeakerImg();
        dominantSpeakerImg.setVisibility(View.GONE);
        //  finish();
    }

    /*
     * Room events listener
     */
    private Room.Listener roomListener() {
        return new Room.Listener() {
            @Override
            public void onConnected(Room room) {
                localParticipant = room.getLocalParticipant();
                setTitle(room.getName());
                for (RemoteParticipant remoteParticipant : room.getRemoteParticipants()) {
                    addRemoteParticipant(remoteParticipant);
                }
            }

            @Override
            public void onReconnecting(@NonNull Room room, @NonNull TwilioException twilioException) {
                // //videoStatusTextView.setText(String.format("Reconnecting to %s", room.getName()));
                reconnectingProgressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onReconnected(@NonNull Room room) {
                //videoStatusTextView.setText(String.format("Connected to %s", room.getName()));
                reconnectingProgressBar.setVisibility(GONE);
            }

            @Override
            public void onConnectFailure(Room room, TwilioException e) {
                //videoStatusTextView.setText("Failed to connect");
                configureAudio(false);
                enableVolumeControl(false);
                intializeUI();
            }

            @Override
            public void onDisconnected(Room room, TwilioException e) {
                localParticipant = null;
                reconnectingProgressBar.setVisibility(GONE);
                SingleVideoCalling.this.room = null;
                configureAudio(false);
                enableVolumeControl(false);
                if (!disconnectedFromOnDestroy) {
                    initializeParticipantContainers();
                    intializeUI();
                }
            }

            @Override
            public void onDominantSpeakerChanged(@NonNull Room room, @Nullable RemoteParticipant remoteParticipant) {
                if (remoteParticipant == null) {
                    if (currentDominantSpeakerImg != null) {
                        currentDominantSpeakerImg.setVisibility(View.GONE);
                    }
                    return;
                }
                ParticipantView participantContainer = participantViewGroupMap.get(remoteParticipant.getSid());
                if (participantContainer != null) {
                    if (currentDominantSpeakerImg != null) {
                        currentDominantSpeakerImg.setVisibility(View.GONE);
                    }
                    currentDominantSpeakerImg = participantContainer.getDominantSpeakerImg();
                    currentDominantSpeakerImg.setVisibility(GONE);
                }
            }

            @Override
            public void onParticipantConnected(Room room, RemoteParticipant remoteParticipant) {
                addRemoteParticipant(remoteParticipant);

            }

            @Override
            public void onParticipantDisconnected(Room room, RemoteParticipant remoteParticipant) {
                removeRemoteParticipant(remoteParticipant);
            }

            @Override
            public void onRecordingStarted(Room room) {

            }

            @Override
            public void onRecordingStopped(Room room) {

            }
        };
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

                //videoStatusTextView.setText("onVideoTrackSubscribed");
                addRemoteParticipantVideo(remoteParticipant, remoteVideoTrack);
            }

            @Override
            public void onVideoTrackUnsubscribed(RemoteParticipant remoteParticipant,
                                                 RemoteVideoTrackPublication remoteVideoTrackPublication,
                                                 RemoteVideoTrack remoteVideoTrack) {
                Log.i(TAG, String.format("onVideoTrackUnsubscribed: " +
                                "[RemoteParticipant: identity=%s], " +
                                "[RemoteVideoTrack: enabled=%b, name=%s]",
                        remoteParticipant.getIdentity(),
                        remoteVideoTrack.isEnabled(),
                        remoteVideoTrack.getName()));
                //videoStatusTextView.setText("onVideoTrackUnsubscribed");
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

    private DialogInterface.OnClickListener connectClickListener(final EditText roomEditText) {
        return (dialog, which) -> {
            connectToRoom(roomEditText.getText().toString());
        };
    }

    private View.OnClickListener disconnectClickListener() {
        return v -> {
            if (room != null) {
                room.disconnect();
            }
            String userToken = HRpreference.getInstance(getApplicationContext()).getUserInfo().getAuthToken();
            diclineCallRequest(userToken, callId);
            finish();
        };
    }

    private View.OnClickListener connectActionClickListener() {
        return v -> showConnectDialog();
    }

    private DialogInterface.OnClickListener cancelConnectDialogClickListener() {
        return (dialog, which) -> {
            intializeUI();
            connectDialog.dismiss();
        };
    }

    private View.OnClickListener localVideoClickListener() {
        return v -> {
            if (localVideoTrack != null) {
                boolean enable = !localVideoTrack.isEnabled();
                localVideoTrack.enable(enable);
                int icon;
                if (enable) {
                    icon = R.drawable.video_call_enable;
                } else {
                    icon = R.drawable.no_video;
                }
                localVideoActionFab.setImageDrawable(
                        ContextCompat.getDrawable(SingleVideoCalling.this, icon));
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
                muteActionFab.setImageDrawable(ContextCompat.getDrawable(
                        SingleVideoCalling.this, icon));
            }
        };
    }

    private View.OnClickListener startChatListener() {
        return v -> {
            Intent intent = new Intent(SingleVideoCalling.this, ChatScreen.class);
            intent.putExtra("CallingUserID", eventUser.getId());
            intent.putExtra("UserDetials", eventUser);
            startActivity(intent);
        };
    }

    private void configureAudio(boolean enable) {
        if (enable) {
            previousAudioMode = audioManager.getMode();
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

    private View.OnClickListener switchCameraClickListener() {
        return v -> {
            if (cameraCapturerCompat != null) {
                CameraCapturer.CameraSource cameraSource = cameraCapturerCompat.getCameraSource();
                cameraCapturerCompat.switchCamera();
                if (localVideoTextureView.getVisibility() == View.VISIBLE) {
                    localVideoTextureView.setMirror(cameraSource == CameraCapturer.CameraSource.BACK_CAMERA);
                }
            }
        };
    }

    private void getAuthToken(String token) {

        TwillioRequest twillioRequest = new TwillioRequest();
        twillioRequest.getTwillioAuthToken(token, true, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                int statusCode = response.code();
                if (statusCode == 200) {
                    SingleVideoCalling.this.accessToken = response.body().string();
                    runOnUiThread(() -> connectToRoom(roomName));
                }

            }

        });
    }

    @Override
    public void onBackPressed() {
        if (BaseActivity.getNumberOfTasks() > 2) {
            Intent intent = new Intent(SingleVideoCalling.this, HomeScreen.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        } else {
            moveTaskToBack(true);
        }

    }

    private void stopScreenCapture() {
        unPublishedLocalTrack();
        if (localVideoTrack == null && checkPermissionForCameraAndMicrophone()) {
            localVideoTrack = LocalVideoTrack.create(this,
                    true,
                    cameraCapturerCompat,
                    LOCAL_VIDEO_TRACK_NAME);
            localVideoTrack.addRenderer(localVideoTextureView);
            if (localParticipant != null) {
                localParticipant.publishTrack(localVideoTrack);
            }

        }
        startCallRecording.setImageResource(R.drawable.screenshare_disable);
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

    private void startScreenCapture() {
        unPublishedLocalTrack();
        if (localVideoTrack == null && checkPermissionForCameraAndMicrophone()) {
            localVideoTrack = LocalVideoTrack.create(this, true, screenCapturer);
            localVideoTrack.addRenderer(localVideoTextureView);
            if (localParticipant != null) {
                localParticipant.publishTrack(localVideoTrack);
                startCallRecording.setImageResource(R.drawable.screen_share_enable);
            }
        }
    }

    @Override
    public void onClick(View view) {
        if (view == startCallRecording) {
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
        }
    }


    private void diclineCallRequest(String token, String callID) {
        CallApiRequest callApiRequest = new CallApiRequest();
        callApiRequest.requestFoEndCall(callID, token, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                String error = e.getLocalizedMessage();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String myResonse = response.toString();
                finish();
            }
        });

    }

    private void registerReceiver() {
        if (!isReceiverRegistered) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(DISCONNECT_CALL_ACTION);
            LocalBroadcastManager.getInstance(this).registerReceiver(
                    videoCallReciever, intentFilter);
            isReceiverRegistered = true;
        }
    }

    private void unregisterReceiver() {
        if (isReceiverRegistered) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(videoCallReciever);
            isReceiverRegistered = false;
        }
    }

    private class VideoCallReciever extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null && action.equals(DISCONNECT_CALL_ACTION)) {
                finish();
            }
        }
    }
}

