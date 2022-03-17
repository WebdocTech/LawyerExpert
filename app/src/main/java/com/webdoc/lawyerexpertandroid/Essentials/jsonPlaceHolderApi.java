package com.webdoc.lawyerexpertandroid.Essentials;

import com.webdoc.lawyerexpertandroid.CallLogActivity.CallLogResponse.CallLogResponse;
import com.webdoc.lawyerexpertandroid.GetChangeStatusResponse.GetChangeStatusResponse;
import com.webdoc.lawyerexpertandroid.GetDetailsResponse.GetDetailsResponse;
import com.webdoc.lawyerexpertandroid.LoginResponseModel.LoginResponseModel;
import com.webdoc.lawyerexpertandroid.LogoutResponseModel.LogoutResponseModel;
import com.webdoc.lawyerexpertandroid.SaveCallLogResponse.SaveCallLogsResponse;
import com.webdoc.lawyerexpertandroid.SaveConsultationResponse.SaveConsultationResponse;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface jsonPlaceHolderApi {

    @POST("Login")
    Call<LoginResponseModel> LoginApi(@Body Map<String, String> pAymentResponse);

    @POST("Logout")
    Call<LogoutResponseModel> LogoutApi(@Body Map<String, String> pAymentResponse);

    @POST("Consultation")
    Call<SaveConsultationResponse> SaveConsultation(@Body Map<String, String> pAymentResponse);

    @POST("GetDetails")
    Call<GetDetailsResponse> GetDetails();

    @POST("ChangeOnlineStatus")
    Call<GetChangeStatusResponse> ChangeOnlineStatus(@Body Map<String, String> pAymentResponse);


    @POST("CallLogDetails")
    Call<CallLogResponse> CallLogResponse(@Body Map<String, String> pAymentResponse);


    @POST("SaveCallLog")
    Call<SaveCallLogsResponse> SaveCallLog(@Body Map<String, String> pAymentResponse);

}
