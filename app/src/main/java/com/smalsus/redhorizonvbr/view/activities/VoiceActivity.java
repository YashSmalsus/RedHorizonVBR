package com.smalsus.redhorizonvbr.view.activities;



import androidx.appcompat.app.AppCompatActivity;




public class VoiceActivity extends AppCompatActivity {
//
//    public static final String INCOMING_CALL_INVITE = "INCOMING_CALL_INVITE";
//    public static final String CANCELLED_CALL_INVITE = "CANCELLED_CALL_INVITE";
//    public static final String INCOMING_CALL_NOTIFICATION_ID = "INCOMING_CALL_NOTIFICATION_ID";
//    public static final String ACTION_INCOMING_CALL = "ACTION_INCOMING_CALL";
//    public static final String ACTION_CANCEL_CALL = "ACTION_CANCEL_CALL";
//    public static final String ACTION_FCM_TOKEN = "ACTION_FCM_TOKEN";
//    private static final String TAG = "VoiceActivity";
//    private static final String TWILIO_ACCESS_TOKEN_SERVER_URL = "TWILIO_ACCESS_TOKEN_SERVER_URL";
//    private static final int MIC_PERMISSION_REQUEST_CODE = 1;
//    private static final int SNACKBAR_DURATION = 4000;
//    private static String identity = "alice";
//    // Empty HashMap, never populated for the Quickstart
//    HashMap<String, String> params = new HashMap<>();
//    private String accessToken;
//    private AudioManager audioManager;
//    private int savedAudioMode = AudioManager.MODE_INVALID;
//    private boolean isReceiverRegistered = false;
//    private VoiceBroadcastReceiver voiceBroadcastReceiver;
//    private CoordinatorLayout coordinatorLayout;
//    RegistrationListener registrationListener = registrationListener();
//    private FloatingActionButton callActionFab;
//    private FloatingActionButton hangupActionFab;
//    private FloatingActionButton holdActionFab;
//    private FloatingActionButton muteActionFab;
//    private Chronometer chronometer;
//    private SoundPoolManager soundPoolManager;
//    private NotificationManager notificationManager;
//    private AlertDialog alertDialog;
//    private CallInvite activeCallInvite;
//    private Call activeCall;
//    Call.Listener callListener = callListener();
//    private int activeCallNotificationId;
//    private String userID;
//    private EventUser userDetails;
//    private RingtonePlayer ringtonePlayer;
//    private boolean isIncomingCall;
//    private ImageView userProfilePicture;
//    private TextView callerName;
//
//    public static AlertDialog createIncomingCallDialog(
//            Context context,
//            CallInvite callInvite,
//            DialogInterface.OnClickListener answerCallClickListener,
//            DialogInterface.OnClickListener cancelClickListener) {
//        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
//        alertDialogBuilder.setIcon(R.drawable.ic_call_black_24dp);
//        alertDialogBuilder.setTitle("Incoming Call");
//        alertDialogBuilder.setPositiveButton("Accept", answerCallClickListener);
//        alertDialogBuilder.setNegativeButton("Reject", cancelClickListener);
//        alertDialogBuilder.setMessage(callInvite.getFrom() + " is calling.");
//        return alertDialogBuilder.create();
//    }
//
//    public static AlertDialog createCallDialog(final DialogInterface.OnClickListener callClickListener,
//                                               final DialogInterface.OnClickListener cancelClickListener,
//                                               final Context context) {
//        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
//
//        alertDialogBuilder.setIcon(R.drawable.call_button_active);
//        alertDialogBuilder.setTitle("Call");
//        alertDialogBuilder.setPositiveButton("Call", callClickListener);
//        alertDialogBuilder.setNegativeButton("Cancel", cancelClickListener);
//        alertDialogBuilder.setCancelable(false);
//
//        LayoutInflater li = LayoutInflater.from(context);
//        View dialogView = li.inflate(R.layout.dialog_call, null);
//        final EditText contact = dialogView.findViewById(R.id.contact);
//        contact.setHint(R.string.callee);
//        alertDialogBuilder.setView(dialogView);
//
//        return alertDialogBuilder.create();
//
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_voice);
//        Window window = getWindow();
//        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
//                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
//                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//
//        userProfilePicture = findViewById(R.id.userProfilePicture);
//        callerName = findViewById(R.id.callerName);
//
//
//        Intent intent = getIntent();
//        userID = intent.getStringExtra("CallingUserID");
//        userDetails = (EventUser) intent.getSerializableExtra("UserDetials");
//        isIncomingCall = intent.getBooleanExtra("IsIncomingCall", false);
//        coordinatorLayout = findViewById(R.id.coordinator_layout);
//        callActionFab = findViewById(R.id.call_action_fab);
//        hangupActionFab = findViewById(R.id.hangup_action_fab);
//        holdActionFab = findViewById(R.id.hold_action_fab);
//        muteActionFab = findViewById(R.id.mute_action_fab);
//        chronometer = findViewById(R.id.chronometer);
//
//        callActionFab.setOnClickListener(callActionFabClickListener());
//        hangupActionFab.setOnClickListener(hangupActionFabClickListener());
//        holdActionFab.setOnClickListener(holdActionFabClickListener());
//        muteActionFab.setOnClickListener(muteActionFabClickListener());
//
//        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        soundPoolManager = SoundPoolManager.getInstance(this);
//
//        /*
//         * Setup the broadcast receiver to be notified of FCM Token updates
//         * or incoming call invite in this Activity.
//         */
//        voiceBroadcastReceiver = new VoiceBroadcastReceiver();
//        registerReceiver();
//
//        /*
//         * Needed for setting/abandoning audio focus during a call
//         */
//        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
//        audioManager.setSpeakerphoneOn(true);
//
//        /*
//         * Enable changing the volume using the up/down keys during a conversation
//         */
//        setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
//        ringtonePlayer = new RingtonePlayer(this, R.raw.beep);
//
//        /*
//         * Setup the UI
//         */
//        resetUI();
//
//        /*
//         * Displays a call dialog if the intent contains a call invite
//         */
//        handleIncomingCallIntent(getIntent());
//
//        /*
//         * Ensure the microphone permission is enabled
//         */
//        if (!checkPermissionForMicrophone()) {
//            requestPermissionForMicrophone();
//        }
//
////        if (!isIncomingCall) {
////            ringtonePlayer.play(true);
////        }
//
//    }
//
//    @Override
//    protected void onNewIntent(Intent intent) {
//        super.onNewIntent(intent);
//        handleIncomingCallIntent(intent);
//    }
//
//    private RegistrationListener registrationListener() {
//        return new RegistrationListener() {
//            @Override
//            public void onRegistered(String accessToken, String fcmToken) {
//                Log.d(TAG, "Successfully registered FCM " + fcmToken);
//            }
//
//            @Override
//            public void onError(RegistrationException error, String accessToken, String fcmToken) {
//                String message = String.format("Registration Error: %d, %s", error.getErrorCode(), error.getMessage());
//                Log.e(TAG, message);
//                Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_LONG).show();
//            }
//        };
//    }
//
//    private Call.Listener callListener() {
//        return new Call.Listener() {
//            /*
//             * This callback is emitted once before the Call.Listener.onConnected() callback when
//             * the callee is being alerted of a Call. The behavior of this callback is determined by
//             * the answerOnBridge flag provided in the Dial verb of your TwiML application
//             * associated with this client. If the answerOnBridge flag is false, which is the
//             * default, the Call.Listener.onConnected() callback will be emitted immediately after
//             * Call.Listener.onRinging(). If the answerOnBridge flag is true, this will cause the
//             * call to emit the onConnected callback only after the call is answered.
//             * See answeronbridge for more details on how to use it with the Dial TwiML verb. If the
//             * twiML response contains a Say verb, then the call will emit the
//             * Call.Listener.onConnected callback immediately after Call.Listener.onRinging() is
//             * raised, irrespective of the value of answerOnBridge being set to true or false
//             */
//            @Override
//            public void onRinging(Call call) {
//                Log.d(TAG, "Ringing");
//            }
//
//            @Override
//            public void onConnectFailure(Call call, CallException error) {
//                setAudioFocus(false);
//                Log.d(TAG, "Connect failure");
//                String message = String.format("Call Error: %d, %s", error.getErrorCode(), error.getMessage());
//                Log.e(TAG, message);
//                Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_SHORT).show();
//                resetUI();
//            }
//
//            @Override
//            public void onConnected(Call call) {
//                setAudioFocus(true);
//                Log.d(TAG, "Connected");
//                activeCall = call;
//            }
//
//            @Override
//            public void onReconnecting(@NonNull Call call, @NonNull CallException callException) {
//                Log.d(TAG, "onReconnecting");
//            }
//
//            @Override
//            public void onReconnected(@NonNull Call call) {
//                Log.d(TAG, "onReconnected");
//            }
//
//            @Override
//            public void onDisconnected(Call call, CallException error) {
//                setAudioFocus(false);
//                Log.d(TAG, "Disconnected");
//                if (error != null) {
//                    String message = String.format("Call Error: %d, %s", error.getErrorCode(), error.getMessage());
//                    Log.e(TAG, message);
//                    Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_SHORT).show();
//                }
//                resetUI();
//            }
//        };
//    }
//
//    /*
//     * The UI state when there is an active call
//     */
//    private void setCallUI() {
//        callActionFab.hide();
//        hangupActionFab.show();
//        holdActionFab.show();
//        muteActionFab.show();
//        chronometer.setVisibility(View.VISIBLE);
//        chronometer.setBase(SystemClock.elapsedRealtime());
//        chronometer.start();
//    }
//
//    /*
//     * Reset UI elements
//     */
//    private void resetUI() {
//        callActionFab.show();
//        muteActionFab.setImageDrawable(ContextCompat.getDrawable(VoiceActivity.this, R.drawable.ic_mic_white_24dp));
//        holdActionFab.hide();
//        holdActionFab.setBackgroundTintList(ColorStateList
//                .valueOf(ContextCompat.getColor(this, R.color.color_accent)));
//        muteActionFab.hide();
//        hangupActionFab.hide();
//        chronometer.setVisibility(View.INVISIBLE);
//        chronometer.stop();
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        registerReceiver();
//        callToUser();
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//
//    }
//
//    @Override
//    public void onDestroy() {
//
//        soundPoolManager.release();
//        super.onDestroy();
//    }
//
//    private void handleIncomingCallIntent(Intent intent) {
//        if (intent != null && intent.getAction() != null) {
//            if (intent.getAction().equals(ACTION_INCOMING_CALL)) {
//                activeCallInvite = intent.getParcelableExtra(INCOMING_CALL_INVITE);
//                if (activeCallInvite != null) {
//                    soundPoolManager.playRinging();
//                    alertDialog = createIncomingCallDialog(VoiceActivity.this,
//                            activeCallInvite,
//                            answerCallClickListener(),
//                            cancelCallClickListener());
//                    alertDialog.show();
//                    activeCallNotificationId = intent.getIntExtra(INCOMING_CALL_NOTIFICATION_ID, 0);
//                }
//            } else if (intent.getAction().equals(ACTION_CANCEL_CALL)) {
//                if (alertDialog != null && alertDialog.isShowing()) {
//                    soundPoolManager.stopRinging();
//                    alertDialog.cancel();
//                }
//            } else if (intent.getAction().equals(ACTION_FCM_TOKEN)) {
//
//            }
//        }
//    }
//
//    private void registerReceiver() {
//        if (!isReceiverRegistered) {
//            IntentFilter intentFilter = new IntentFilter();
//            intentFilter.addAction(ACTION_INCOMING_CALL);
//            intentFilter.addAction(ACTION_CANCEL_CALL);
//            intentFilter.addAction(ACTION_FCM_TOKEN);
//            LocalBroadcastManager.getInstance(this).registerReceiver(
//                    voiceBroadcastReceiver, intentFilter);
//            isReceiverRegistered = true;
//        }
//    }
//
//    private void unregisterReceiver() {
//        if (isReceiverRegistered) {
//            LocalBroadcastManager.getInstance(this).unregisterReceiver(voiceBroadcastReceiver);
//            isReceiverRegistered = false;
//        }
//    }
//
//    private DialogInterface.OnClickListener answerCallClickListener() {
//        return new DialogInterface.OnClickListener() {
//
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                soundPoolManager.stopRinging();
//                answer();
//                setCallUI();
//                alertDialog.dismiss();
//            }
//        };
//    }
//
//    private void callToUser() {
//        String token = HRpreference.getInstance(getApplicationContext()).getTwiilioToken();
//        this.accessToken = token;
//        params.put("to", userID);
//        params.put("to", userID);
//        ConnectOptions connectOptions = new ConnectOptions.Builder(token)
//                .params(params)
//                .build();
//        activeCall = Voice.connect(VoiceActivity.this, connectOptions, callListener);
//        setCallUI();
//
//
//    }
//
//    private DialogInterface.OnClickListener callClickListener() {
//        return new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                // Place a call
//                EditText contact = ((AlertDialog) dialog).findViewById(R.id.contact);
//                params.put("to", contact.getText().toString());
//                ConnectOptions connectOptions = new ConnectOptions.Builder(accessToken)
//                        .params(params)
//                        .build();
//                activeCall = Voice.connect(VoiceActivity.this, connectOptions, callListener);
//                setCallUI();
//                alertDialog.dismiss();
//            }
//        };
//    }
//
//
//    private DialogInterface.OnClickListener cancelCallClickListener() {
//        return new DialogInterface.OnClickListener() {
//
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                soundPoolManager.stopRinging();
//                if (activeCallInvite != null) {
//                    activeCallInvite.reject(VoiceActivity.this);
//                    notificationManager.cancel(activeCallNotificationId);
//                }
//                alertDialog.dismiss();
//            }
//        };
//    }
//
//    /*
//     * Register your FCM token with Twilio to receive incoming call invites
//     *
//     * If a valid google-services.json has not been provided or the FirebaseInstanceId has not been
//     * initialized the fcmToken will be null.
//     *
//     * In the case where the FirebaseInstanceId has not yet been initialized the
//     * VoiceFirebaseInstanceIDService.onTokenRefresh should result in a LocalBroadcast to this
//     * activity which will attempt registerForCallInvites again.
//     *
//     */
//    private void registerForCallInvites() {
//        final String fcmToken = FirebaseInstanceId.getInstance().getToken();
//        if (fcmToken != null) {
//            Log.i(TAG, "Registering with FCM");
//            Voice.register(accessToken, Voice.RegistrationChannel.FCM, fcmToken, registrationListener);
//        }
//    }
//
//    private View.OnClickListener callActionFabClickListener() {
//        return new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                alertDialog = createCallDialog(callClickListener(), cancelCallClickListener(), VoiceActivity.this);
//                alertDialog.show();
//            }
//        };
//    }
//
//    private View.OnClickListener hangupActionFabClickListener() {
//        return new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                soundPoolManager.playDisconnect();
//                resetUI();
//                disconnect();
//            }
//        };
//    }
//
//    private View.OnClickListener holdActionFabClickListener() {
//        return new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                hold();
//            }
//        };
//    }
//
//    private View.OnClickListener muteActionFabClickListener() {
//        return new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mute();
//            }
//        };
//    }
//
//    /*
//     * Accept an incoming Call
//     */
//    private void answer() {
//        activeCallInvite.accept(this, callListener);
//        notificationManager.cancel(activeCallNotificationId);
//    }
//
//    /*
//     * Disconnect from Call
//     */
//    private void disconnect() {
//        if (activeCall != null) {
//            activeCall.disconnect();
//            activeCall = null;
//        }
//    }
//
//    private void hold() {
//        if (activeCall != null) {
//            boolean hold = !activeCall.isOnHold();
//            activeCall.hold(hold);
//
//            // Set fab as pressed when call is on hold
//            ColorStateList holdActionBarCsl = hold ?
//                    ColorStateList.valueOf(ContextCompat.getColor(this,
//                            R.color.colorPrimaryDark)) :
//                    ColorStateList.valueOf(ContextCompat.getColor(this,
//                            R.color.color_accent));
//            holdActionFab.setBackgroundTintList(holdActionBarCsl);
//        }
//    }
//
//    private void mute() {
//        if (activeCall != null) {
//            boolean mute = !activeCall.isMuted();
//            activeCall.mute(mute);
//            if (mute) {
//                muteActionFab.setImageDrawable(ContextCompat.getDrawable(VoiceActivity.this, R.drawable.ic_mic_white_off_24dp));
//            } else {
//                muteActionFab.setImageDrawable(ContextCompat.getDrawable(VoiceActivity.this, R.drawable.ic_mic_white_24dp));
//            }
//        }
//    }
//
//    private void setAudioFocus(boolean setFocus) {
//        if (audioManager != null) {
//            if (setFocus) {
//                savedAudioMode = audioManager.getMode();
//                // Request audio focus before making any device switch.
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                    AudioAttributes playbackAttributes = new AudioAttributes.Builder()
//                            .setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION)
//                            .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
//                            .build();
//                    AudioFocusRequest focusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)
//                            .setAudioAttributes(playbackAttributes)
//                            .setAcceptsDelayedFocusGain(true)
//                            .setOnAudioFocusChangeListener(new AudioManager.OnAudioFocusChangeListener() {
//                                @Override
//                                public void onAudioFocusChange(int i) {
//                                }
//                            })
//                            .build();
//                    audioManager.requestAudioFocus(focusRequest);
//                } else {
//                    int focusRequestResult = audioManager.requestAudioFocus(new AudioManager.OnAudioFocusChangeListener() {
//
//                                                                                @Override
//                                                                                public void onAudioFocusChange(int focusChange) {
//                                                                                }
//                                                                            }, AudioManager.STREAM_VOICE_CALL,
//                            AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
//                }
//                /*
//                 * Start by setting MODE_IN_COMMUNICATION as default audio mode. It is
//                 * required to be in this mode when playout and/or recording starts for
//                 * best possible VoIP performance. Some devices have difficulties with speaker mode
//                 * if this is not set.
//                 */
//                audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
//            } else {
//                audioManager.setMode(savedAudioMode);
//                audioManager.abandonAudioFocus(null);
//            }
//        }
//    }
//
//    private boolean checkPermissionForMicrophone() {
//        int resultMic = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
//        return resultMic == PackageManager.PERMISSION_GRANTED;
//    }
//
//    private void requestPermissionForMicrophone() {
//        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)) {
//            Snackbar.make(coordinatorLayout,
//                    "Microphone permissions needed. Please allow in your application settings.",
//                    Snackbar.LENGTH_SHORT).show();
//        } else {
//            ActivityCompat.requestPermissions(
//                    this,
//                    new String[]{Manifest.permission.RECORD_AUDIO},
//                    MIC_PERMISSION_REQUEST_CODE);
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        /*
//         * Check if microphone permissions is granted
//         */
//        if (requestCode == MIC_PERMISSION_REQUEST_CODE && permissions.length > 0) {
//            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
//                Snackbar.make(coordinatorLayout,
//                        "Microphone permissions needed. Please allow in your application settings.",
//                        Snackbar.LENGTH_SHORT).show();
//            } else {
//                // retrieveAccessToken();
//            }
//        }
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.menu, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.speaker_menu_item:
//                if (audioManager.isSpeakerphoneOn()) {
//                    audioManager.setSpeakerphoneOn(false);
//                    item.setIcon(R.drawable.ic_phonelink_ring_white_24dp);
//                } else {
//                    audioManager.setSpeakerphoneOn(true);
//                    item.setIcon(R.drawable.ic_volume_up_white_24dp);
//                }
//                break;
//        }
//        return true;
//    }
//
//    private class VoiceBroadcastReceiver extends BroadcastReceiver {
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            if (action.equals(ACTION_INCOMING_CALL) || action.equals(ACTION_CANCEL_CALL)) {
//                handleIncomingCallIntent(intent);
//            }
//        }
//    }
//
//    /*
//     * Get an access token from your Twilio access token server
//     */

}
