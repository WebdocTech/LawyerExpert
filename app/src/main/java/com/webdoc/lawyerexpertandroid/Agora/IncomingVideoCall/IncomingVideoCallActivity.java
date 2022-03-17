package com.webdoc.lawyerexpertandroid.Agora.IncomingVideoCall;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.ncorti.slidetoact.SlideToActView;
import com.webdoc.lawyerexpertandroid.Agora.VideoCall.VideoCall;
import com.webdoc.lawyerexpertandroid.Essentials.Constants;
import com.webdoc.lawyerexpertandroid.Essentials.Global;
import com.webdoc.lawyerexpertandroid.Essentials.jsonPlaceHolderApi;
import com.webdoc.lawyerexpertandroid.FirebaseService.FcmListenerService;
import com.webdoc.lawyerexpertandroid.GetChangeStatusResponse.GetChangeStatusResponse;
import com.webdoc.lawyerexpertandroid.MyProfile.MyProfileFrag;
import com.webdoc.lawyerexpertandroid.Preferences.Preferences;
import com.webdoc.lawyerexpertandroid.R;
import com.webdoc.lawyerexpertandroid.SaveCallLogResponse.SaveCallLogsResponse;
import com.webdoc.lawyerexpertandroid.ServerManager.ServerManager;
import com.webdoc.lawyerexpertandroid.ServerManager.VolleyListener;

import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class IncomingVideoCallActivity extends AppCompatActivity implements VolleyListener {
    TextView tv_callerID;
    LottieAnimationView Animation_RejectCall, Animation_AcceptCall;
    Preferences preferences;
    public static Handler ringing_handler = new Handler();
    public static Runnable ringing_runnable;
    int ringing_seconds = 0;
    SlideToActView sliderAcceptCall, sliderRejectCall;
    Vibrator vibe;
    ServerManager serverManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_incoming_call);

        serverManager = new ServerManager(IncomingVideoCallActivity.this);
        sliderAcceptCall = findViewById(R.id.sliderAcceptCall);
        sliderRejectCall = findViewById(R.id.sliderRejectCall);
        vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        YoYo.with(Techniques.Shake)
                .duration(1500)
                .repeat(1000)
                .playOn(sliderAcceptCall);

        ringing_handler.postDelayed(ringing_runnable = new Runnable() {
            public void run() {
                ringing_seconds++;
                vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                vibe.vibrate(500);
                String call_duration = convertSeconds(ringing_seconds);

                if (call_duration.equalsIgnoreCase("00:00:25")) {
                    Global.call_not_answered = true;
                    endCall();
                } else {
                    ringing_handler.postDelayed(ringing_runnable, 1000);
                }
            }
        }, 1000);

        preferences = new Preferences(this);
        //DoctorStatus("busy");

        tv_callerID = (TextView) findViewById(R.id.tv_callerID);
        Animation_RejectCall = findViewById(R.id.Animation_RejectCall);
        Animation_AcceptCall = findViewById(R.id.Animation_AcceptCall);
        tv_callerID.setText(Global.callerID);

        sliderAcceptCall.setOnSlideCompleteListener(new SlideToActView.OnSlideCompleteListener() {
            @Override
            public void onSlideComplete(SlideToActView slideToActView) {
                vibe.cancel();
                ringing_handler.removeCallbacks(ringing_runnable);
                FcmListenerService.mNotificationManager.cancel(2);
                Intent intent = new Intent(IncomingVideoCallActivity.this, VideoCall.class);
                finish();
                startActivity(intent);
            }
        });

        sliderAcceptCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vibe.cancel();
                ringing_handler.removeCallbacks(ringing_runnable);
                FcmListenerService.mNotificationManager.cancel(2);
                Intent intent = new Intent(IncomingVideoCallActivity.this, VideoCall.class);
                finish();
                startActivity(intent);

            }
        });

        sliderRejectCall.setOnSlideCompleteListener(new SlideToActView.OnSlideCompleteListener() {
            @Override
            public void onSlideComplete(SlideToActView slideToActView) {
                ringing_handler.removeCallbacks(ringing_runnable);
                FcmListenerService.mNotificationManager.cancel(2);
                endCall();
                finish();
            }
        });

        sliderRejectCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ringing_handler.removeCallbacks(ringing_runnable);
                FcmListenerService.mNotificationManager.cancel(2);
                endCall();
                finish();

            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
        } else {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                    | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                    | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                    | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                    | WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);
        }

        // to release screen lock
        KeyguardManager keyguardManager = (KeyguardManager) getApplicationContext().getSystemService(Context.KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock keyguardLock = keyguardManager.newKeyguardLock("SCREEN LOCK");
        keyguardLock.disableKeyguard();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (Global.utils.mediaPlayer != null) {
            Global.utils.mediaPlayer.stop();
        }
    }

    private void endCall() {
        vibe.cancel();
        if (Global.utils.mediaPlayer != null) {
            Global.utils.stopMediaPlayer();
        }
        FcmListenerService.mNotificationManager.cancel(2);
        ringing_handler.removeCallbacks(null);

        HashMap<String, String> SaveCallLogPArams = new HashMap<>();

        SaveCallLogPArams.put("callDuration", "00:00");
        SaveCallLogPArams.put("webdocAgentEmail", preferences.getKeyEmail());
        SaveCallLogPArams.put("callerEmail", Global.callerID);
        SaveCallLogPArams.put("callStatus", "Missed");
        SaveCallLogPArams.put("callType", "Video Call");
        SaveCallLogPArams.put("incomingCallTime", getDate());
        SaveCallLogPArams.put("pickCallTime", getDate());
        //preferences.getKeyApplicationUserId());
        // callSaveCallLogsApi(SaveCallLogPArams);

        serverManager.jsonParse(Constants.SaveCallLog, SaveCallLogPArams);
        finish();
    }

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
                    Global.utils.showErrorSnakeBar(IncomingVideoCallActivity.this, response.message());
                    return;
                }


                SaveCallLogsResponse saveCallLogsResponse = response.body();
                Global.saveCallLogsResponse = saveCallLogsResponse;
                HashMap<String, String> statusParams = new HashMap<>();
                statusParams.put("AgriExpertProfilesId", preferences.getKeyApplicationUserId());
                statusParams.put("Status", "1");
                serverManager.jsonParse(Constants.ChangeOnlineStatus, statusParams);
                //       CallchangeOnlineStatusApi(statusParams);

            }

            @Override
            public void onFailure(Call<SaveCallLogsResponse> call, Throwable t) {
                Toast.makeText(IncomingVideoCallActivity.this, t + "", Toast.LENGTH_SHORT).show();
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

    private void CallchangeOnlineStatusApi(HashMap<String, String> statusParams) {

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
        Call<GetChangeStatusResponse> call1 = jsonPlaceHolderApi.ChangeOnlineStatus(statusParams);

        call1.enqueue(new Callback<GetChangeStatusResponse>() {
            @Override
            public void onResponse(Call<GetChangeStatusResponse> call, Response<GetChangeStatusResponse> response) {

                GetChangeStatusResponse statusResponse = response.body();
                // Global.getDetailsResponse = getDetailsResponse;
                if (!response.isSuccessful()) {
                    Global.utils.showErrorSnakeBar(IncomingVideoCallActivity.this, response.message());
                    return;
                }

                if (statusResponse.getChangeOnlineStatusResult().getStatus().equals("1")) {
                    MyProfileFrag.switch2.setText("Online");
                    MyProfileFrag.switch2.setChecked(true);
                } else if (statusResponse.getChangeOnlineStatusResult().getStatus().equals("0")) {
                    MyProfileFrag.switch2.setText("Ofline");
                    MyProfileFrag.switch2.setChecked(false);
                } else {
                    MyProfileFrag.switch2.setText("Busy");
                }

            }

            @Override
            public void onFailure(Call<GetChangeStatusResponse> call, Throwable t) {
                Toast.makeText(IncomingVideoCallActivity.this, t + "", Toast.LENGTH_SHORT).show();
                call.cancel();
            }
        });


    }

    @Override
    public void apiCallResponse(String api, String response) throws JSONException {

        if (api.equalsIgnoreCase(Constants.SaveCallLog)) {


            HashMap<String, String> statusParams = new HashMap<>();
            statusParams.put("AgriExpertProfilesId", preferences.getKeyApplicationUserId());
            statusParams.put("Status", "1");
            //    CallchangeOnlineStatusApi(statusParams);
            serverManager.jsonParse(Constants.ChangeOnlineStatus, statusParams);
        } else if (api.equalsIgnoreCase(Constants.ChangeOnlineStatus)) {
            if (Global.changeStatusResponse.getChangeOnlineStatusResult().getStatus().equals("1")) {
                MyProfileFrag.switch2.setText("Online");
                MyProfileFrag.switch2.setChecked(true);
            } else if (Global.changeStatusResponse.getChangeOnlineStatusResult().getStatus().equals("0")) {
                MyProfileFrag.switch2.setText("Ofline");
                MyProfileFrag.switch2.setChecked(false);
            } else {
                MyProfileFrag.switch2.setText("Busy");
            }
        }
    }
}