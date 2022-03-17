package com.webdoc.lawyerexpertandroid.CallLogActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.webdoc.lawyerexpertandroid.CallLogActivity.CallLogRequestModel.CallLogReqModell;
import com.webdoc.lawyerexpertandroid.CallLogActivity.CallLogResponse.CallLogHistory;
import com.webdoc.lawyerexpertandroid.CallLogActivity.CallLogResponse.CallLogResponse;
import com.webdoc.lawyerexpertandroid.Essentials.Constants;
import com.webdoc.lawyerexpertandroid.Essentials.Global;
import com.webdoc.lawyerexpertandroid.Essentials.jsonPlaceHolderApi;
import com.webdoc.lawyerexpertandroid.Preferences.Preferences;
import com.webdoc.lawyerexpertandroid.R;
import com.webdoc.lawyerexpertandroid.ServerManager.ServerManager;
import com.webdoc.lawyerexpertandroid.ServerManager.VolleyListener;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CallLogsActivity extends AppCompatActivity implements VolleyListener {

    CallLogAdapter adapter;
    RecyclerView rv_callLogs;
    Preferences preferences;
    ArrayList<CallLogReqModell> arrayList;
    TextView tv_all_calls, tv_missed_calls, tv_connected_calls;
    LottieAnimationView loader;
    ServerManager serverManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_logs);

        serverManager = new ServerManager(CallLogsActivity.this);
        arrayList = new ArrayList<>();
        preferences = new Preferences(CallLogsActivity.this);
        rv_callLogs = findViewById(R.id.rv_callLogs);
        rv_callLogs.setHasFixedSize(true);

        tv_all_calls = findViewById(R.id.tv_all_calls);
        tv_missed_calls = findViewById(R.id.tv_missed_calls);
        tv_connected_calls = findViewById(R.id.tv_connected_calls);

        loader = findViewById(R.id.loader);

        tv_all_calls.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                tv_all_calls.setBackground(CallLogsActivity.this.getResources().getDrawable(R.drawable.border_rectangle_blue));
                tv_all_calls.setTextColor(CallLogsActivity.this.getResources().getColor(R.color.white));
                tv_missed_calls.setBackground(getResources().getDrawable(R.drawable.border_rectangle));
                tv_connected_calls.setBackground(getResources().getDrawable(R.drawable.border_rectangle));
                tv_missed_calls.setTextColor(CallLogsActivity.this.getResources().getColor(R.color.black));
                tv_connected_calls.setTextColor(CallLogsActivity.this.getResources().getColor(R.color.black));

                adapter = new CallLogAdapter(CallLogsActivity.this, Global.calllogList);
                rv_callLogs.setAdapter(adapter);


            }
        });

        tv_connected_calls.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                tv_connected_calls.setBackground(CallLogsActivity.this.getResources().getDrawable(R.drawable.border_rectangle_blue));
                tv_connected_calls.setTextColor(CallLogsActivity.this.getResources().getColor(R.color.white));
                tv_missed_calls.setBackground(getResources().getDrawable(R.drawable.border_rectangle));
                tv_all_calls.setBackground(getResources().getDrawable(R.drawable.border_rectangle));
                tv_missed_calls.setTextColor(CallLogsActivity.this.getResources().getColor(R.color.black));
                tv_all_calls.setTextColor(CallLogsActivity.this.getResources().getColor(R.color.black));


                ArrayList<CallLogHistory> connectedCallsList = new ArrayList<>();
                for (int i = 0; i < Global.calllogList.size(); i++) {

                    if (Global.calllogList.get(i).getCallStatus().equals("Connected")) {
                        connectedCallsList.add(Global.calllogList.get(i));
                    }

                }

                adapter = new CallLogAdapter(CallLogsActivity.this, connectedCallsList);
                rv_callLogs.setAdapter(adapter);


            }
        });

        tv_missed_calls.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_missed_calls.setBackground(CallLogsActivity.this.getResources().getDrawable(R.drawable.border_rectangle_blue));
                tv_missed_calls.setTextColor(CallLogsActivity.this.getResources().getColor(R.color.white));
                tv_all_calls.setBackground(getResources().getDrawable(R.drawable.border_rectangle));
                tv_connected_calls.setBackground(getResources().getDrawable(R.drawable.border_rectangle));
                tv_all_calls.setTextColor(CallLogsActivity.this.getResources().getColor(R.color.black));
                tv_connected_calls.setTextColor(CallLogsActivity.this.getResources().getColor(R.color.black));


                ArrayList<CallLogHistory> missedCallsList = new ArrayList<>();
                for (int i = 0; i < Global.calllogList.size(); i++) {

                    if (Global.calllogList.get(i).getCallStatus().equals("Missed")) {
                        missedCallsList.add(Global.calllogList.get(i));
                    }

                }

                adapter = new CallLogAdapter(CallLogsActivity.this, missedCallsList);
                rv_callLogs.setAdapter(adapter);


            }
        });

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        rv_callLogs.setLayoutManager(mLayoutManager);

        HashMap<String, String> statusParams = new HashMap<>();
        statusParams.put("LawyerEmail", preferences.getKeyEmail()); //preferences.getKeyApplicationUserId());

      //  callLogsApi(statusParams);

        serverManager.jsonParse(Constants.CallLogDetails, statusParams);


    }

    private void callLogsApi(HashMap<String, String> statusParams) {

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
        Call<CallLogResponse> call1 = jsonPlaceHolderApi.CallLogResponse(statusParams);

        call1.enqueue(new Callback<CallLogResponse>() {
            @Override
            public void onResponse(Call<CallLogResponse> call, Response<CallLogResponse> response) {

                if (!response.isSuccessful()) {
                    Global.utils.showErrorSnakeBar(CallLogsActivity.this, response.message());
                    return;
                }


                CallLogResponse callLogResponse = response.body();
                Global.callLogResponse = callLogResponse;
                Global.calllogList = callLogResponse.getCallLogResult().getCallLogHistory();


                adapter = new CallLogAdapter(CallLogsActivity.this, Global.calllogList);
                rv_callLogs.setAdapter(adapter);
                loader.setVisibility(View.GONE);


            }

            @Override
            public void onFailure(Call<CallLogResponse> call, Throwable t) {
                Toast.makeText(CallLogsActivity.this, t + "", Toast.LENGTH_SHORT).show();
                call.cancel();
            }
        });


    }

    @Override
    public void apiCallResponse(String api, String response) throws JSONException {


        if (api.equalsIgnoreCase(Constants.CallLogDetails)) {

            Global.calllogList = Global.callLogResponse.getCallLogResult().getCallLogHistory();
            adapter = new CallLogAdapter(CallLogsActivity.this, Global.calllogList);
            rv_callLogs.setAdapter(adapter);
            loader.setVisibility(View.GONE);

        }
    }


}
