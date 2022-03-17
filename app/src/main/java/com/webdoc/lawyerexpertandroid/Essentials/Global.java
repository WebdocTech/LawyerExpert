package com.webdoc.lawyerexpertandroid.Essentials;

import android.content.Context;

import com.webdoc.lawyerexpertandroid.CallLogActivity.CallLogResponse.CallLogHistory;
import com.webdoc.lawyerexpertandroid.CallLogActivity.CallLogResponse.CallLogResponse;
import com.webdoc.lawyerexpertandroid.GetChangeStatusResponse.GetChangeStatusResponse;
import com.webdoc.lawyerexpertandroid.GetDetailsResponse.GetDetailsResponse;
import com.webdoc.lawyerexpertandroid.LoginResponseModel.LoginResponseModel;
import com.webdoc.lawyerexpertandroid.LogoutResponseModel.LogoutResponseModel;
import com.webdoc.lawyerexpertandroid.SaveCallLogResponse.SaveCallLogsResponse;
import com.webdoc.lawyerexpertandroid.SaveConsultationResponse.SaveConsultationResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by WaleedPCC on 4/29/2019.
 */

public class Global {
    public static Context myContext;
    public static Utils utils = new Utils();
    public static Context context;
    public static boolean call_not_answered;

    /*TODO: Agora*/
    public static String channel, callerID;


    public static LoginResponseModel LoginResponse = new LoginResponseModel();
    public static GetDetailsResponse getDetailsResponse = new GetDetailsResponse();
    public static LogoutResponseModel logoutResponse = new LogoutResponseModel();
    public static SaveConsultationResponse saveConsultationResponse = new SaveConsultationResponse();
    public static CallLogResponse callLogResponse = new CallLogResponse();
    public static SaveCallLogsResponse saveCallLogsResponse = new SaveCallLogsResponse();
    public static List<CallLogHistory> calllogList = new ArrayList();
    public static GetChangeStatusResponse changeStatusResponse = new GetChangeStatusResponse();

}
