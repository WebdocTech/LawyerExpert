package com.webdoc.lawyerexpertandroid.Splash;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.iid.FirebaseInstanceId;
import com.webdoc.lawyerexpertandroid.Dashboard.dashboardActivity;
import com.webdoc.lawyerexpertandroid.Essentials.Constants;
import com.webdoc.lawyerexpertandroid.Essentials.Global;
import com.webdoc.lawyerexpertandroid.Essentials.jsonPlaceHolderApi;
import com.webdoc.lawyerexpertandroid.Login.LoginActivity;
import com.webdoc.lawyerexpertandroid.LoginResponseModel.LoginResponseModel;
import com.webdoc.lawyerexpertandroid.Preferences.Preferences;
import com.webdoc.lawyerexpertandroid.R;
import com.webdoc.lawyerexpertandroid.ServerManager.ServerManager;
import com.webdoc.lawyerexpertandroid.ServerManager.VolleyListener;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SplashActivity extends AppCompatActivity implements VolleyListener {

    ImageView ivLogo;
    Preferences preferences;
    ServerManager serverManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        serverManager = new ServerManager(SplashActivity.this);


        preferences = new Preferences(this);
        ivLogo = findViewById(R.id.ivLogo);

        Animation animBlink = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.blink_animation);
        animBlink.setDuration(3000);
        ivLogo.startAnimation(animBlink);


        animBlink.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                checkInfo();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void checkInfo() {
        if (preferences.getKeyLogin()) {

            Global.utils.startProgressBar(SplashActivity.this, "Please Wait....");
            Map<String, String> postParams = new HashMap<String, String>();
            postParams.put("Email", preferences.getKeyEmail());
            postParams.put("Password", preferences.getKeyPassword());
            postParams.put("DeviceToken", FirebaseInstanceId.getInstance().getToken());
            postParams.put("OS", "Android");

            //  callLoginApi(postParams);
            serverManager.jsonParse(Constants.LoginAPI, postParams);

        } else {
            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void callLoginApi(Map<String, String> postParams) {

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
        Call<LoginResponseModel> call1 = jsonPlaceHolderApi.LoginApi(postParams);

        call1.enqueue(new Callback<LoginResponseModel>() {
            @Override
            public void onResponse(Call<LoginResponseModel> call, Response<LoginResponseModel> response) {

                LoginResponseModel loginResponse = response.body();
                Global.LoginResponse = loginResponse;
                if (!response.isSuccessful()) {
                    Global.utils.dismissProgressBar();
                    Global.utils.showErrorSnakeBar(SplashActivity.this, response.message());
                    return;
                }
                Intent intent = new Intent(SplashActivity.this, dashboardActivity.class);
                Global.utils.dismissProgressBar();
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(Call<LoginResponseModel> call, Throwable t) {
                Toast.makeText(SplashActivity.this, t + "", Toast.LENGTH_SHORT).show();
                call.cancel();
            }
        });
    }

    @Override
    public void apiCallResponse(String api, String response) throws JSONException {

        if (api.equalsIgnoreCase(Constants.LoginAPI)) {
            Intent intent = new Intent(SplashActivity.this, dashboardActivity.class);
            Global.utils.dismissProgressBar();
            startActivity(intent);
            finish();
        }
    }
}
