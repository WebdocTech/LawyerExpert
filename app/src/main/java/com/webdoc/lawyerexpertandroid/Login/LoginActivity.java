package com.webdoc.lawyerexpertandroid.Login;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.agriexpertchat.RegisterUserForChatInterface;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.iid.FirebaseInstanceId;
import com.webdoc.lawyerexpertandroid.Dashboard.dashboardActivity;
import com.webdoc.lawyerexpertandroid.Essentials.Constants;
import com.webdoc.lawyerexpertandroid.Essentials.Global;
import com.webdoc.lawyerexpertandroid.Essentials.jsonPlaceHolderApi;
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

    public class LoginActivity extends AppCompatActivity implements RegisterUserForChatInterface, VolleyListener {

    Button btnLogin;
    TextInputEditText etUsername, etPassword;
    Preferences preferences;
    ProgressBar progressBar;
    String uname;
    ServerManager serverManager;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        preferences = new Preferences(this);
        Global.context = LoginActivity.this;

        serverManager = new ServerManager(LoginActivity.this);

        Window window = this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.dullBlue));

        progressBar = findViewById(R.id.progressBar_loginActivity);
        btnLogin = (Button) findViewById(R.id.btnLogin_LoginActivity);
        etUsername = findViewById(R.id.etUsername_LoginActivity);
        etPassword = findViewById(R.id.etPassword_LoginActivity);


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startProgressBar();
                btnLogin.setClickable(false);
                etUsername.setEnabled(false);
                etPassword.setEnabled(false);
                uname = etUsername.getText().toString();
                String pass = etPassword.getText().toString();
                if (uname.equalsIgnoreCase("") || uname == null || uname.length() < 5) {
                    stopProgressBar();
                    etUsername.requestFocus();
                    etUsername.setError("Enter Username");
                } else if (pass.equalsIgnoreCase("") || pass == null) {
                    stopProgressBar();
                    etPassword.requestFocus();
                    etPassword.setError("Enter Password");
                } else {
                    preferences.setKeyPassword(pass);
                    Map<String, String> postParams = new HashMap<String, String>();
                    postParams.put("Email", uname);
                    postParams.put("Password", pass);
                    postParams.put("DeviceToken", FirebaseInstanceId.getInstance().getToken());
                    postParams.put("OS", "Android");
                    // callLoginApi(postParams);
                    serverManager.jsonParse(Constants.LoginAPI, postParams);
                }
            }
        });
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


                    Global.utils.showErrorSnakeBar(LoginActivity.this, response.message());
                    stopProgressBar();
                    return;
                }

                preferences.setKeyLogin(true);
                preferences.setKeyEmail(uname);
                preferences.setKeyApplicationUserId(Global.LoginResponse.getLawyerProfileData().getAgriExpertProfile().getApplicationUserId());
                Intent intent = new Intent(LoginActivity.this, dashboardActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(Call<LoginResponseModel> call, Throwable t) {
                Toast.makeText(LoginActivity.this, t + "", Toast.LENGTH_SHORT).show();
                call.cancel();
            }
        });
    }

    private void startProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setIndeterminate(true);

    }

    private void stopProgressBar() {
        progressBar.setVisibility(View.GONE);
        btnLogin.setClickable(true);
        etUsername.setEnabled(true);
        etPassword.setEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void apiCallResponse(String api, String response) throws JSONException {

        if (api.equalsIgnoreCase(Constants.LoginAPI)) {

            preferences.setKeyLogin(true);
            preferences.setKeyEmail(uname);
            preferences.setKeyApplicationUserId(Global.LoginResponse.getLawyerProfileData().getAgriExpertProfile().getApplicationUserId());
            Intent intent = new Intent(LoginActivity.this, dashboardActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void RegisterUserResponse(String response) {

    }
}
