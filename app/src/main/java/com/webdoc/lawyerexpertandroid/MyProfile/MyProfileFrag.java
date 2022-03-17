package com.webdoc.lawyerexpertandroid.MyProfile;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.ncorti.slidetoact.SlideToActView;
import com.webdoc.lawyerexpertandroid.CallLogActivity.CallLogsActivity;
import com.webdoc.lawyerexpertandroid.Essentials.Constants;
import com.webdoc.lawyerexpertandroid.Essentials.Global;
import com.webdoc.lawyerexpertandroid.Essentials.jsonPlaceHolderApi;
import com.webdoc.lawyerexpertandroid.GetChangeStatusResponse.GetChangeStatusResponse;
import com.webdoc.lawyerexpertandroid.Login.LoginActivity;
import com.webdoc.lawyerexpertandroid.LogoutResponseModel.LogoutResponseModel;
import com.webdoc.lawyerexpertandroid.Preferences.Preferences;
import com.webdoc.lawyerexpertandroid.R;
import com.webdoc.lawyerexpertandroid.ServerManager.ServerManager;
import com.webdoc.lawyerexpertandroid.ServerManager.VolleyListener;

import org.json.JSONException;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MyProfileFrag extends Fragment implements VolleyListener {
    TextView tvName, tvEmail;
    Preferences preferences;
    ImageView iv_logout;
    public static Switch switch2;
    SlideToActView slide;
    ServerManager serverManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_profile, container, false);

        serverManager = new ServerManager(getActivity());

        tvName = view.findViewById(R.id.tvName_MyProfile);
        tvEmail = view.findViewById(R.id.tvEmail_MyProfile);
        iv_logout = view.findViewById(R.id.iv_logout);
        switch2 = view.findViewById(R.id.switch2);
        slide = view.findViewById(R.id.example);
        slide.getOnSlideResetListener();
        slide.setOnSlideCompleteListener(new SlideToActView.OnSlideCompleteListener() {
            @Override
            public void onSlideComplete(SlideToActView slideToActView) {

                Intent intent = new Intent(getActivity(), CallLogsActivity.class);
                startActivity(intent);
            }
        });
        preferences = new Preferences(getActivity());
        tvName.setText("Welcome " + Global.LoginResponse.getLawyerProfileData().getAgriExpertProfile().getFirstName() + " " + Global.LoginResponse.getLawyerProfileData().getAgriExpertProfile().getLastName());
        tvEmail.setText(preferences.getKeyEmail());

        HashMap<String, String> statusParams = new HashMap<>();
        switch2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {


                    statusParams.put("AgriExpertProfilesId", preferences.getKeyApplicationUserId());
                    statusParams.put("Status", "1");

                    serverManager.jsonParse(Constants.ChangeOnlineStatus, statusParams);
                    //CallchangeOnlineStatusApi(statusParams);


                } else {


                    statusParams.put("AgriExpertProfilesId", preferences.getKeyApplicationUserId());
                    statusParams.put("Status", "0");
                    serverManager.jsonParse(Constants.ChangeOnlineStatus, statusParams);
                    //    CallchangeOnlineStatusApi(statusParams);
                }
            }


        });
        iv_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Global.utils.startProgressBar(getActivity(), "Logging Out...");

                HashMap<String, String> logoutParams = new HashMap<>();
                logoutParams.put("AgriExpertProfilesId", preferences.getKeyApplicationUserId());
                serverManager.jsonParse(Constants.CallLogoutApi, logoutParams);
                //  callLogoutApi(logoutParams);

            }
        });

        return view;
    }

    private void callLogoutApi(HashMap<String, String> logoutParams) {


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
        Call<LogoutResponseModel> call1 = jsonPlaceHolderApi.LogoutApi(logoutParams);

        call1.enqueue(new Callback<LogoutResponseModel>() {
            @Override
            public void onResponse(Call<LogoutResponseModel> call, Response<LogoutResponseModel> response) {


                LogoutResponseModel logoutResponse = response.body();
                Global.logoutResponse = logoutResponse;
                if (!response.isSuccessful()) {
                    Global.utils.dismissProgressBar();
                    Global.utils.showErrorSnakeBar(getActivity(), response.message());
                    return;
                }
                preferences.clearSharedPreferences();
                Global.utils.dismissProgressBar();
                startActivity(new Intent(getActivity(), LoginActivity.class));
                getActivity().finish();
            }

            @Override
            public void onFailure(Call<LogoutResponseModel> call, Throwable t) {
                Toast.makeText(getActivity(), t + "", Toast.LENGTH_SHORT).show();
                call.cancel();
            }
        });
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
                    Global.utils.showErrorSnakeBar(getActivity(), response.message());
                    return;
                }

                if (statusResponse.getChangeOnlineStatusResult().getStatus().equals("1")) {
                    switch2.setText("Online");
                } else if (statusResponse.getChangeOnlineStatusResult().getStatus().equals("0")) {
                    switch2.setText("Ofline");
                } else {
                    switch2.setText("Busy");
                }

            }

            @Override
            public void onFailure(Call<GetChangeStatusResponse> call, Throwable t) {
                Toast.makeText(getActivity(), t + "", Toast.LENGTH_SHORT).show();
                call.cancel();
            }
        });


    }

    @Override
    public void onResume() {
        slide.resetSlider();
        super.onResume();
    }

    @Override
    public void apiCallResponse(String api, String response) throws JSONException {

        if (api.equalsIgnoreCase(Constants.ChangeOnlineStatus)) {

            if (Global.changeStatusResponse.getChangeOnlineStatusResult().getStatus().equals("1")) {
                switch2.setText("Online");
            } else if (Global.changeStatusResponse.getChangeOnlineStatusResult().getStatus().equals("0")) {
                switch2.setText("Ofline");
            } else {
                switch2.setText("Busy");
            }
        }
        else if (api.equalsIgnoreCase(Constants.LogoutAPI)) {

            preferences.clearSharedPreferences();
            Global.utils.dismissProgressBar();
            startActivity(new Intent(getActivity(), LoginActivity.class));
            getActivity().finish();
        }
    }
}
