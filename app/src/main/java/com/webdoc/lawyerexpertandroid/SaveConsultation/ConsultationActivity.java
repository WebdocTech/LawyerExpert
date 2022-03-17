package com.webdoc.lawyerexpertandroid.SaveConsultation;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.webdoc.lawyerexpertandroid.Essentials.Constants;
import com.webdoc.lawyerexpertandroid.Essentials.Global;
import com.webdoc.lawyerexpertandroid.Essentials.jsonPlaceHolderApi;
import com.webdoc.lawyerexpertandroid.GetDetailsResponse.CategoryDetail;
import com.webdoc.lawyerexpertandroid.GetDetailsResponse.GetDetailsResponse;
import com.webdoc.lawyerexpertandroid.GetDetailsResponse.SubCategoryDetail;
import com.webdoc.lawyerexpertandroid.Preferences.Preferences;
import com.webdoc.lawyerexpertandroid.R;
import com.webdoc.lawyerexpertandroid.SaveConsultationResponse.SaveConsultationResponse;
import com.webdoc.lawyerexpertandroid.ServerManager.ServerManager;
import com.webdoc.lawyerexpertandroid.ServerManager.VolleyListener;
import com.webdoc.lawyerexpertandroid.SpinnerAdapters.SpinnerCategoriesAdapter;
import com.webdoc.lawyerexpertandroid.SpinnerAdapters.SpinnerSubCategoriesAdapter;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ConsultationActivity extends AppCompatActivity implements VolleyListener {

    Button btnSaveConsultation;
    Preferences preferences;
    Spinner spinner1, spinner2;
    EditText tv_remarks;
    List<CategoryDetail> categoryList;
    List<SubCategoryDetail> subCategoryDetailslist;
    String remarks;
    ServerManager serverManager;

    CategoryDetail categoryDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consultation);


        serverManager = new ServerManager(ConsultationActivity.this);
        spinner1 = findViewById(R.id.spinner);
        spinner2 = findViewById(R.id.spinner2);
        tv_remarks = findViewById(R.id.tv_remarks);

        categoryList = new ArrayList();
        ArrayList remarkslist = new ArrayList();
        for (int i = 0; i < Global.getDetailsResponse.getGetDetailsResult().getCategoryDetails().size(); i++) {
            categoryList.add(Global.getDetailsResponse.getGetDetailsResult().getCategoryDetails().get(i));
        }


        SpinnerCategoriesAdapter spinnerCountriesAdapter = new SpinnerCategoriesAdapter(this, R.layout.spinner_item, categoryList);
        spinner1.setAdapter(spinnerCountriesAdapter);

        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
                categoryDetail = categoryList.get(position);

                //EXAMINING BODY
                subCategoryDetailslist = new ArrayList();
                for (int i = 0; i < categoryDetail.getSubCategoryDetails().size(); i++) {
                    subCategoryDetailslist.add(categoryDetail.getSubCategoryDetails().get(i));
                    if (categoryDetail.getSubCategoryDetails().get(i).getRemarks() != null) {
                        remarks = categoryDetail.getSubCategoryDetails().get(i).getRemarks();

                    }
                }
                SpinnerSubCategoriesAdapter subCategoriesAdapter = new SpinnerSubCategoriesAdapter(ConsultationActivity.this, R.layout.spinner_item, subCategoryDetailslist);
                spinner2.setAdapter(subCategoriesAdapter);

                tv_remarks.setText(remarks);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub;
            }
        });

        /*  initialization   */
        preferences = new Preferences(ConsultationActivity.this);
        //      callGetDetailsApi();
        Map<String, String> postParams = new HashMap<String, String>();
        //serverManager.jsonParse(Constants.GetDetails, postParams);
        /*   Set Screen Size   */
        Window window = this.getWindow();
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width * .9), (int) (height * .8));

        /*  Bind Views  */

        btnSaveConsultation = findViewById(R.id.btnSaveConsultation_ConsultationActivity);

        btnSaveConsultation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveConsultation();
            }
        });
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
                    Global.utils.showErrorSnakeBar(ConsultationActivity.this, response.message());
                    return;
                }

               /* Intent intent = new Intent(ConsultationActivity.this, dashboardActivity.class);
                startActivity(intent);
                finish();*/
            }

            @Override
            public void onFailure(Call<GetDetailsResponse> call, Throwable t) {
                Toast.makeText(ConsultationActivity.this, t + "", Toast.LENGTH_SHORT).show();
                call.cancel();
            }
        });

    }

    @Override
    public void onBackPressed() {
        Global.utils.showErrorSnakeBar(ConsultationActivity.this, "Save Consultation First!");
    }

    private void saveConsultation() {
        String spinner1Text = spinner1.getSelectedItem().toString();
        String spinner2Text = spinner2.getSelectedItem().toString();

        if (TextUtils.isEmpty(spinner1Text) || TextUtils.isEmpty(spinner2Text) || TextUtils.isEmpty(remarks)) {
            Global.utils.showErrorSnakeBar(ConsultationActivity.this, "Please fill all fields");
            return;
        } else {
            Global.utils.startProgressBar(ConsultationActivity.this, "Authenticating....");

            Map<String, String> postParams = new HashMap<String, String>();

            postParams.put("LawyerProfilesId", preferences.getKeyEmail());
            postParams.put("CustomerProfilesId", Global.callerID);
            postParams.put("Remarks", remarks);

            //   callSaveConsultationApi(postParams);
            serverManager.jsonParse(Constants.SaveConsultationAPI, postParams);
        }
    }

    private void callSaveConsultationApi(Map<String, String> postParams) {


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
        Call<SaveConsultationResponse> call1 = jsonPlaceHolderApi.SaveConsultation(postParams);

        call1.enqueue(new Callback<SaveConsultationResponse>() {
            @Override
            public void onResponse(Call<SaveConsultationResponse> call, Response<SaveConsultationResponse> response) {
                Global.utils.dismissProgressBar();
                SaveConsultationResponse saveConsultationResponse = response.body();
                Global.saveConsultationResponse = saveConsultationResponse;
                if (!response.isSuccessful()) {
                    Global.utils.dismissProgressBar();
                    Global.utils.showErrorSnakeBar(ConsultationActivity.this, response.message());
                    return;
                } else {
                    if (saveConsultationResponse.getResult().getResponseCode().equals("0001")) {
                        Global.utils.showErrorSnakeBar(ConsultationActivity.this, "Lawyer not found");
                    } else {
                        Toast.makeText(ConsultationActivity.this, "Consultation Successfully Sent!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }

            }

            @Override
            public void onFailure(Call<SaveConsultationResponse> call, Throwable t) {
                Toast.makeText(ConsultationActivity.this, t + "", Toast.LENGTH_SHORT).show();
                call.cancel();
            }
        });


       /* if (response.equals(Constants.RESPONSE_CODE_SUCCESS)) {

        } else {

        }*/
    }

    @Override
    public void apiCallResponse(String api, String response) throws JSONException {

        if (api.equalsIgnoreCase(Constants.SaveConsultationAPI)) {
            if (Global.saveConsultationResponse!=null)
            {
                if (Global.saveConsultationResponse.getResult().getResponseCode().equals("0001")) {
                    Global.utils.showErrorSnakeBar(ConsultationActivity.this, "Lawyer not found");
                } else {
                    Toast.makeText(ConsultationActivity.this, "Consultation Successfully Sent!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }


        } else if (api.equalsIgnoreCase(Constants.GetDetails)) {

        }

    }
}


