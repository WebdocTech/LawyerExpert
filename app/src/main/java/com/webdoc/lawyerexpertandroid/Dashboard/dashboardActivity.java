package com.webdoc.lawyerexpertandroid.Dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.webdoc.lawyerexpertandroid.Essentials.Constants;
import com.webdoc.lawyerexpertandroid.Essentials.Global;
import com.webdoc.lawyerexpertandroid.Essentials.jsonPlaceHolderApi;
import com.webdoc.lawyerexpertandroid.GetDetailsResponse.GetDetailsResponse;
import com.webdoc.lawyerexpertandroid.Login.LoginActivity;
import com.webdoc.lawyerexpertandroid.LogoutResponseModel.LogoutResponseModel;
import com.webdoc.lawyerexpertandroid.MyProfile.MyProfileFrag;
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

public class dashboardActivity extends AppCompatActivity implements VolleyListener {

    private DrawerLayout mDrawer;
    private ActionBarDrawerToggle mToggle;
    NavigationView nvDrawer;
    Preferences preferences;
    ServerManager serverManager;
    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        requestQueue = Volley.newRequestQueue(this);
        preferences = new Preferences(this);
        serverManager = new ServerManager(dashboardActivity.this);

        //  getDetails();
        Map<String, String> postParams = new HashMap<String, String>();
        serverManager.jsonParse(Constants.GetDetails, postParams);
        updateToken();
        // DoctorStatus();

        FragmentTransaction fm = getSupportFragmentManager().beginTransaction();
        fm.replace(R.id.fragContainer_DashboardActivity, new MyProfileFrag());
        fm.commit();

        mDrawer = findViewById(R.id.drawer_layout);
        mToggle = new ActionBarDrawerToggle(this, mDrawer, R.string.drawer_open, R.string.drawer_close);
        mDrawer.addDrawerListener(mToggle);
        mToggle.syncState();

     /*   getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setLogo(R.drawable.logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);*/
        nvDrawer = findViewById(R.id.nav_view);

        setupDrawerContent(nvDrawer);
    }

    public void updateToken() {
        //TODO: DEVICE TOKENS
        String token = FirebaseInstanceId.getInstance().getToken();
        String email = Global.LoginResponse.getLawyerProfileData().getAgriExpertProfile().getUsername().replace(".", "");

        HashMap hashMap = new HashMap();
        hashMap.put("token", token);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("Tokens").child("Doctors").child(email).setValue(hashMap);
    }

/*
    public void DoctorStatus() {

        Map<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", "online");

        FirebaseDatabase.getInstance().getReference().child("Doctors").child(preferences.getKeyEmail().replace(".", ""))
                .updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(dashboardActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
*/

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    private void selectDrawerItem(MenuItem menuItem) {

        int itemId = menuItem.getItemId();
        if (itemId == R.id.action_logout) {

            Global.utils.startProgressBar(this, "Logging Out...");

            HashMap<String, String> logoutParams = new HashMap<>();
            logoutParams.put("AgriExpertProfilesId", preferences.getKeyApplicationUserId());
            callLogoutApi(logoutParams);

        }
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
                    Global.utils.showErrorSnakeBar(dashboardActivity.this, response.message());
                    return;
                }
                preferences.clearSharedPreferences();
                Global.utils.dismissProgressBar();
                startActivity(new Intent(dashboardActivity.this, LoginActivity.class));
                finish();
            }

            @Override
            public void onFailure(Call<LogoutResponseModel> call, Throwable t) {
                Toast.makeText(dashboardActivity.this, t + "", Toast.LENGTH_SHORT).show();
                call.cancel();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (mToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void callGetDetailsApi() {
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
        Call<GetDetailsResponse> call1 = jsonPlaceHolderApi.GetDetails();

        call1.enqueue(new Callback<GetDetailsResponse>() {
            @Override
            public void onResponse(Call<GetDetailsResponse> call, Response<GetDetailsResponse> response) {

                GetDetailsResponse getDetailsResponse = response.body();
                Global.getDetailsResponse = getDetailsResponse;
                if (!response.isSuccessful()) {
                    Global.utils.showErrorSnakeBar(dashboardActivity.this, response.message());
                    return;
                }

               /* Intent intent = new Intent(ConsultationActivity.this, dashboardActivity.class);
                startActivity(intent);
                finish();*/
            }

            @Override
            public void onFailure(Call<GetDetailsResponse> call, Throwable t) {
                Toast.makeText(dashboardActivity.this, t + "", Toast.LENGTH_SHORT).show();
                call.cancel();
            }
        });

    }

    @Override
    public void apiCallResponse(String api, String response) throws JSONException {

        if (api.equalsIgnoreCase(Constants.GetDetails)) {
        }
        else if (api.equalsIgnoreCase(Constants.ChangeOnlineStatus)) {

            if (Global.changeStatusResponse.getChangeOnlineStatusResult()!=null)
            {
                if (Global.changeStatusResponse.getChangeOnlineStatusResult().getStatus().equals("1")) {
                    MyProfileFrag.switch2.setText("Online");
                } else if (Global.changeStatusResponse.getChangeOnlineStatusResult().getStatus().equals("0")) {
                    MyProfileFrag.switch2.setText("Ofline");
                } else {
                    MyProfileFrag.switch2.setText("Busy");
                }
            }

        }

        if (api.equalsIgnoreCase(Constants.LogoutAPI)) {

            preferences.clearSharedPreferences();
            Global.utils.dismissProgressBar();
            startActivity(new Intent(dashboardActivity.this, LoginActivity.class));
            finish();
        }
    }

}
