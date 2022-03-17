package com.webdoc.lawyerexpertandroid.Agora.VideoCall;

import android.Manifest;
import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.webdoc.lawyerexpertandroid.Essentials.Constants;
import com.webdoc.lawyerexpertandroid.Essentials.Global;
import com.webdoc.lawyerexpertandroid.Essentials.jsonPlaceHolderApi;
import com.webdoc.lawyerexpertandroid.Preferences.Preferences;
import com.webdoc.lawyerexpertandroid.R;
import com.webdoc.lawyerexpertandroid.SaveCallLogResponse.SaveCallLogsResponse;
import com.webdoc.lawyerexpertandroid.SaveConsultation.ConsultationActivity;
import com.webdoc.lawyerexpertandroid.ServerManager.ServerManager;
import com.webdoc.lawyerexpertandroid.ServerManager.VolleyListener;

import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;
import io.agora.rtc.video.VideoEncoderConfiguration;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class VideoCall extends AppCompatActivity implements VolleyListener {
    private static final String TAG = VideoCall.class.getSimpleName();
    String call_duration;
    ServerManager serverManager;

    private static final int PERMISSION_REQ_ID = 22;

    // Permission WRITE_EXTERNAL_STORAGE is not mandatory
    // for Agora RTC SDK, just in case if you wanna save
    // logs to external sdcard.
    private static final String[] REQUESTED_PERMISSIONS = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private RtcEngine mRtcEngine;
    private boolean mCallEnd;
    private boolean mMuted;

    private FrameLayout mLocalContainer;
    private RelativeLayout mRemoteContainer;
    private SurfaceView mLocalView;
    private SurfaceView mRemoteView;

    private ImageView mCallBtn;
    private ImageView mMuteBtn;
    private ImageView mSwitchCameraBtn;

    Preferences preferences;

    TextView tv_call_status, tv_call_time;
    int call_seconds, ringing_seconds = 0;

    Handler call_time_handler = new Handler();
    Runnable runnable;
    public static Handler ringing_handler = new Handler();
    public static Runnable ringing_runnable;
    boolean callEndClicked = false;

    ImageView btn_stopVideo;
    boolean stopVideo;

    public static Activity activity;


    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {

        @Override
        public void onJoinChannelSuccess(String channel, final int uid, int elapsed) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //mLogView.logI("Join channel success, uid: " + (uid & 0xFFFFFFFFL));
                }
            });
        }


        @Override
        public void onFirstRemoteVideoDecoded(final int uid, int width, int height, int elapsed) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //mLogView.logI("First remote video decoded, uid: " + (uid & 0xFFFFFFFFL));
                    setupRemoteVideo(uid);
                }
            });
        }


        @Override
        public void onUserOffline(final int uid, int reason) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //mLogView.logI("User offline, uid: " + (uid & 0xFFFFFFFFL));
                    onRemoteUserLeft();
                }
            });
        }

        @Override
        public void onUserJoined(int uid, int elapsed) {
            super.onUserJoined(uid, elapsed);

            ringing_handler.removeCallbacks(ringing_runnable);

            call_time_handler.postDelayed(runnable = new Runnable() {
                public void run() {
                    //do something

                    call_seconds++;

                    call_duration = convertSeconds(call_seconds);

                    tv_call_time.setText(call_duration);

                    call_time_handler.postDelayed(runnable, 1000);
                }
            }, 1000);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tv_call_status.setText("Connected");
                    //Global.feedbackDialog = true;
                }
            });

            Global.utils.stopMediaPlayer();
        }
    };

    private void setupRemoteVideo(int uid) {
        // Only one remote video view is available for this
        // tutorial. Here we check if there exists a surface
        // view tagged as this uid.
        int count = mRemoteContainer.getChildCount();
        View view = null;
        for (int i = 0; i < count; i++) {
            View v = mRemoteContainer.getChildAt(i);
            if (v.getTag() instanceof Integer && ((int) v.getTag()) == uid) {
                view = v;
            }
        }

        if (view != null) {
            return;
        }

        /*
          Creates the video renderer view.
          CreateRendererView returns the SurfaceView type. The operation and layout of the view
          are managed by the app, and the Agora SDK renders the view provided by the app.
          The video display view must be created using this method instead of directly
          calling SurfaceView.
         */
        mRemoteView = RtcEngine.CreateRendererView(getBaseContext());
        mRemoteContainer.addView(mRemoteView);
        // Initializes the video view of a remote user.
        mRtcEngine.setupRemoteVideo(new VideoCanvas(mRemoteView, VideoCanvas.RENDER_MODE_HIDDEN, uid));
        mRemoteView.setTag(uid);
    }

    private void onRemoteUserLeft() {
        removeRemoteVideo();
    }

    private void removeRemoteVideo() {
        if (mRemoteView != null) {
            mRemoteContainer.removeView(mRemoteView);
        }
        // Destroys remote view
        mRemoteView = null;

        ringing_handler.removeCallbacks(ringing_runnable);
        call_time_handler.removeCallbacks(runnable);

        //SaveCallLog();
        Intent intent = new Intent(this, ConsultationActivity.class);
        finish();
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = this;
        serverManager = new ServerManager(VideoCall.this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);

        // to release screen lock
        KeyguardManager keyguardManager = (KeyguardManager) getApplicationContext().getSystemService(Context.KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock keyguardLock = keyguardManager.newKeyguardLock("SCREEN LOCK");
        keyguardLock.disableKeyguard();

        setContentView(R.layout.activity_video_call);

        Bundle args = getIntent().getExtras();

        preferences = new Preferences(this);

        tv_call_status = (TextView) findViewById(R.id.tv_call_status);
        tv_call_time = (TextView) findViewById(R.id.tv_call_time);


        /**/

        initUI();

        // Ask for permissions at runtime.
        // This is just an example set of permissions. Other permissions
        // may be needed, and please refer to our online documents.
        if (checkSelfPermission(REQUESTED_PERMISSIONS[0], PERMISSION_REQ_ID) &&
                checkSelfPermission(REQUESTED_PERMISSIONS[1], PERMISSION_REQ_ID) &&
                checkSelfPermission(REQUESTED_PERMISSIONS[2], PERMISSION_REQ_ID)) {
            initEngineAndJoinChannel();
        }
    }

    private void initUI() {
        mLocalContainer = findViewById(R.id.local_video_view_container);
        mRemoteContainer = findViewById(R.id.remote_video_view_container);

        //btn_stopVideo = findViewById(R.id.btn_stopVideo);
        mCallBtn = findViewById(R.id.btn_call);
        mMuteBtn = findViewById(R.id.btn_mute);
        mSwitchCameraBtn = findViewById(R.id.btn_switch_camera);

        mCallBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callEndClicked = true;
                endCall();
            }
        });

        /*btn_stopVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!stopVideo) {
                    btn_stopVideo.setImageResource(R.drawable.video_disable);
                    mRtcEngine.muteLocalVideoStream(true);
                    stopVideo = true;
                } else {
                    btn_stopVideo.setImageResource(R.drawable.video_enable);
                    mRtcEngine.muteLocalVideoStream(false);
                    stopVideo = false;
                }
            }
        });*/

        //mLogView = findViewById(R.id.log_recycler_view);

        // Sample logs are optional.
        showSampleLogs();
    }

    private void showSampleLogs() {
        /*mLogView.logI("Welcome to Agora 1v1 video call");
        mLogView.logW("You will see custom logs here");
        mLogView.logE("You can also use this to show errors");*/
    }

    private boolean checkSelfPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(this, permission) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, REQUESTED_PERMISSIONS, requestCode);
            return false;
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQ_ID) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED ||
                    grantResults[1] != PackageManager.PERMISSION_GRANTED ||
                    grantResults[2] != PackageManager.PERMISSION_GRANTED) {
                showLongToast("Need permissions " + Manifest.permission.RECORD_AUDIO +
                        "/" + Manifest.permission.CAMERA + "/" + Manifest.permission.WRITE_EXTERNAL_STORAGE);
                finish();
                return;
            }

            // Here we continue only if all permissions are granted.
            // The permissions can also be granted in the system settings manually.
            initEngineAndJoinChannel();
        }
    }

    private void showLongToast(final String msg) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void initEngineAndJoinChannel() {
        // This is our usual steps for joining
        // a channel and starting a call.
        initializeEngine();
        setupVideoConfig();
        setupLocalVideo();
        joinChannel();
    }

    private void initializeEngine() {
        try {
            mRtcEngine = RtcEngine.create(getBaseContext(), getString(R.string.agora_app_id), mRtcEventHandler);
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
            throw new RuntimeException("NEED TO check rtc sdk init fatal error\n" + Log.getStackTraceString(e));
        }
    }

    private void setupVideoConfig() {
        // In simple use cases, we only need to enable video capturing
        // and rendering once at the initialization step.
        // Note: audio recording and playing is enabled by default.
        mRtcEngine.enableVideo();

        // Please go to this page for detailed explanation
        // https://docs.agora.io/en/Video/API%20Reference/java/classio_1_1agora_1_1rtc_1_1_rtc_engine.html#af5f4de754e2c1f493096641c5c5c1d8f
        mRtcEngine.setVideoEncoderConfiguration(new VideoEncoderConfiguration(
                VideoEncoderConfiguration.VD_640x360,
                VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15,
                VideoEncoderConfiguration.STANDARD_BITRATE,
                VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT));
    }

    private void setupLocalVideo() {
        // This is used to set a local preview.
        // The steps setting local and remote view are very similar.
        // But note that if the local user do not have a uid or do
        // not care what the uid is, he can set his uid as ZERO.
        // Our server will assign one and return the uid via the event
        // handler callback function (onJoinChannelSuccess) after
        // joining the channel successfully.
        mLocalView = RtcEngine.CreateRendererView(getBaseContext());
        mLocalView.setZOrderMediaOverlay(true);
        mLocalContainer.addView(mLocalView);
        // Initializes the local video view.
        // RENDER_MODE_HIDDEN: Uniformly scale the video until it fills the visible boundaries. One dimension of the video may have clipped contents.
        mRtcEngine.setupLocalVideo(new VideoCanvas(mLocalView, VideoCanvas.RENDER_MODE_HIDDEN, 0));
    }

    private void joinChannel() {
        // 1. Users can only see each other after they join the
        // same channel successfully using the same app id.
        // 2. One token is only valid for the channel name that
        // you use to generate this token.
        String token = "";
        if (TextUtils.isEmpty(token) || TextUtils.equals(token, "")) {
            token = null; // default, no token
        }
        mRtcEngine.joinChannel(token, Global.channel, "Extra Optional Data", 0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!mCallEnd) {
            leaveChannel();
        }
        /*
          Destroys the RtcEngine instance and releases all resources used by the Agora SDK.

          This method is useful for apps that occasionally make voice or video calls,
          to free up resources for other operations when not making calls.
         */
        RtcEngine.destroy();
    }

    private void leaveChannel() {
        mRtcEngine.leaveChannel();
    }

    public void onLocalAudioMuteClicked(View view) {
        mMuted = !mMuted;
        // Stops/Resumes sending the local audio stream.
        mRtcEngine.muteLocalAudioStream(mMuted);
        int res = mMuted ? R.drawable.btn_mute : R.drawable.btn_unmute;
        mMuteBtn.setImageResource(res);
    }

    public void onSwitchCameraClicked(View view) {
        // Switches between front and rear cameras.
        mRtcEngine.switchCamera();
    }

    /*public void onCallClicked(View view) {
        if (mCallEnd) {
            startCall();
            mCallEnd = false;
            mCallBtn.setImageResource(R.drawable.btn_endcall);
        } else {
            endCall();
            mCallEnd = true;
            mCallBtn.setImageResource(R.drawable.btn_startcall);
        }

        showButtons(!mCallEnd);
    }*/

    private void startCall() {
        setupLocalVideo();
        joinChannel();
    }

    private void endCall() {

        HashMap<String, String> SaveCallLogPArams = new HashMap<>();

        SaveCallLogPArams.put("callDuration", call_duration);
        SaveCallLogPArams.put("webdocAgentEmail", preferences.getKeyEmail());
        SaveCallLogPArams.put("callerEmail", Global.callerID);
        SaveCallLogPArams.put("callStatus", "Connected");
        SaveCallLogPArams.put("callType", "Video Call");
        SaveCallLogPArams.put("incomingCallTime", getDate());
        SaveCallLogPArams.put("pickCallTime", getDate());//preferences.getKeyApplicationUserId());
        serverManager.jsonParse(Constants.SaveCallLog, SaveCallLogPArams);
        //    callSaveCallLogsApi(SaveCallLogPArams);

        removeLocalVideo();
        removeRemoteVideo();
        leaveChannel();

        if (Global.utils.mediaPlayer != null) {
            Global.utils.stopMediaPlayer();
        }

        /*if(tv_call_status.getText().equals("Ringing"))
        {
            if(!callEndClicked) {
                Global.call_not_answered = true;
            } else {
                Global.call_not_answered = false;
            }
            missedCallNotification();
        }*/
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        endCall();
        finish();
    }

    private void removeLocalVideo() {
        if (mLocalView != null) {
            mLocalContainer.removeView(mLocalView);
        }
        mLocalView = null;
    }

    private void showButtons(boolean show) {
        int visibility = show ? View.VISIBLE : View.GONE;
        mMuteBtn.setVisibility(visibility);
        mSwitchCameraBtn.setVisibility(visibility);
    }

    /*private void missedCallNotification()
    {
        JSONObject params = new JSONObject();
        try {
            params.put("to", Global.selectedPatientDeviceToken);
            params.put("data", new JSONObject()
                    .put("title", "Missed Video Call")
                    .put("body", preferences.getKeyDoctorEmail())
                    .put("channel", Global.channel));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Global.utils.sendNotification(this, params);
        MissedCallLog();
    }

    private void MissedCallLog()
    {
        DatabaseReference firebaseDatabaseReferenceDoctors;
        firebaseDatabaseReferenceDoctors = FirebaseDatabase.getInstance().getReference().child("CallLog").child("Users").child(Global.patientIDCalling.replace(".", ""));
        String logKeyDoctors = firebaseDatabaseReferenceDoctors.push().getKey().toString();
        Map<String, Object> hashMap = new HashMap<>();
        hashMap.put("patientName", Global.patientNameCalling);
        hashMap.put("doctorName", preferences.getKeyDoctorEmail().replace(Constants.APP_EXTENSION,""));
        hashMap.put("patientNumber", Global.patientIDCalling.replace(Constants.APP_EXTENSION, ""));
        hashMap.put("patientEmail", Global.patientIDCalling);
        hashMap.put("doctorEmail", Global.channel);
        hashMap.put("callDuration", convertSeconds(call_seconds).toString());
        hashMap.put("incomingCallTime", Calendar.getInstance().getTime().toString());
        hashMap.put("pickCallTime", Calendar.getInstance().getTime().toString());
        hashMap.put("dateTime", Calendar.getInstance().getTime().toString());
        hashMap.put("callType", "video");
        hashMap.put("callStatus", "missed");
        hashMap.put("caller", Global.callerID.replace(Constants.APP_EXTENSION, ""));
        hashMap.put("callee", Global.calleeID.replace(Constants.APP_EXTENSION,""));
        firebaseDatabaseReferenceDoctors.child(logKeyDoctors).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) { }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(VideoCall.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void SaveCallLog() {
        DatabaseReference firebaseDatabaseReference;
        firebaseDatabaseReference = FirebaseDatabase.getInstance().getReference().child("CallLog").child("Doctors").child(preferences.getKeyDoctorEmail().replace(".", ""));
        String logKey = firebaseDatabaseReference.push().getKey().toString();
        Map<String, Object> hashMap = new HashMap<>();
        hashMap.put("patientName", Global.patientNameCalling);
        hashMap.put("doctorName", preferences.getKeyDoctorEmail().replace(Constants.APP_EXTENSION,""));
        hashMap.put("patientNumber", Global.patientIDCalling.replace(Constants.APP_EXTENSION, ""));
        hashMap.put("patientEmail", Global.patientIDCalling);
        hashMap.put("doctorEmail", preferences.getKeyDoctorEmail().toString());
        hashMap.put("callDuration", convertSeconds(call_seconds).toString());
        hashMap.put("incomingCallTime", incomingCallTime);
        hashMap.put("pickCallTime", pickCallTime);
        hashMap.put("dateTime", Calendar.getInstance().getTime().toString());
        hashMap.put("callType", "video");
        hashMap.put("callStatus", Global.callStatus);
        hashMap.put("caller", Global.callerID.replace(Constants.APP_EXTENSION, ""));
        hashMap.put("callee", Global.calleeID.replace(Constants.APP_EXTENSION,""));
        firebaseDatabaseReference.child(logKey).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Intent intent = new Intent(VideoCall.this, GiveConsultationActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("callType", "VideoCall");
                intent.putExtras(bundle);
                startActivity(intent);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                SaveCallLog();
            }
        });
    }*/

    String convertSeconds(int sec) {
        int seconds = sec % 60;
        int minutes = sec / 60;
        if (minutes >= 60) {
            int hours = minutes / 60;
            minutes %= 60;
            if (hours >= 24) {
                int days = hours / 24;
                return String.format("%d days %02d:%02d:%02d", days, hours % 24, minutes, seconds);
            }
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        }
        return String.format("00:%02d:%02d", minutes, seconds);
    }

    public static void closeActivity() {
        if (activity != null)
            activity.finish();
    }

    public void callSaveCallLogsApi(HashMap<String, String> saveCallLogPArams) {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient();
        client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.MINUTES)
                .addInterceptor(interceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()

                .baseUrl("https://qmsservice.webddocsystems.com/Service1.svc/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client) // Set HttpClient to be used by Retrofit
                .build();

        jsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(jsonPlaceHolderApi.class);
        Call<SaveCallLogsResponse> call1 = jsonPlaceHolderApi.SaveCallLog(saveCallLogPArams);

        call1.enqueue(new Callback<SaveCallLogsResponse>() {
            @Override
            public void onResponse(Call<SaveCallLogsResponse> call, Response<SaveCallLogsResponse> response) {

                if (!response.isSuccessful()) {
                    Global.utils.showErrorSnakeBar(VideoCall.this, response.message());
                    return;
                }


                SaveCallLogsResponse saveCallLogsResponse = response.body();
                Global.saveCallLogsResponse = saveCallLogsResponse;

            }

            @Override
            public void onFailure(Call<SaveCallLogsResponse> call, Throwable t) {
                Toast.makeText(VideoCall.this, t + "", Toast.LENGTH_SHORT).show();
                call.cancel();
            }
        });

    }

    public String getDate() {
        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault());
        String formattedDate = df.format(c);

        return formattedDate;
    }

    @Override
    public void apiCallResponse(String api, String response) throws JSONException {

        if (api.equalsIgnoreCase(Constants.SaveCallLog)) {


        }


    }
}

