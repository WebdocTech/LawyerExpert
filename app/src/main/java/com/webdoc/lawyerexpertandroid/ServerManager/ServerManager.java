package com.webdoc.lawyerexpertandroid.ServerManager;

import android.app.Activity;
import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.webdoc.lawyerexpertandroid.CallLogActivity.CallLogResponse.CallLogResponse;
import com.webdoc.lawyerexpertandroid.Essentials.Constants;
import com.webdoc.lawyerexpertandroid.Essentials.Global;
import com.webdoc.lawyerexpertandroid.GetChangeStatusResponse.GetChangeStatusResponse;
import com.webdoc.lawyerexpertandroid.GetDetailsResponse.GetDetailsResponse;
import com.webdoc.lawyerexpertandroid.LoginResponseModel.LoginResponseModel;
import com.webdoc.lawyerexpertandroid.LogoutResponseModel.LogoutResponseModel;
import com.webdoc.lawyerexpertandroid.Preferences.Preferences;
import com.webdoc.lawyerexpertandroid.SaveCallLogResponse.SaveCallLogsResponse;
import com.webdoc.lawyerexpertandroid.SaveConsultationResponse.SaveConsultationResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class ServerManager {

    public static Context ctx;
    Preferences preferences;

    public ServerManager(Context ctx) {
        this.ctx = ctx;
        preferences = new Preferences(ctx);
    }

    public void jsonParse(final String calledAPI, Map<String, String> postParams) {
        /* Callback interface to wait for response */
        final VolleyListener volleyListener = (VolleyListener) ctx;
        /* SSL Verification Certificate */
        SSLVerification sslVerification = new SSLVerification(ctx);
        final RequestQueue requestQueue = Volley.newRequestQueue(ctx, sslVerification.Certificate());
        String url = Constants.BaseURL + calledAPI;
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, new JSONObject(postParams),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            preferences = new Preferences(ctx);
                            Gson gson = new Gson();
                            switch (calledAPI) {
                                case Constants.LoginAPI: {

                                    LoginResponseModel loginResponse = gson.fromJson(response.toString(), LoginResponseModel.class);
                                    if (loginResponse.getLawyerProfileData().getMessage().equalsIgnoreCase("Successfully Logedin")) {
                                        Global.LoginResponse = loginResponse;
                                        volleyListener.apiCallResponse(Constants.LoginAPI, String.valueOf(Global.LoginResponse));
                                    } else {
                                        volleyListener.apiCallResponse(Constants.LoginAPI, loginResponse.getLawyerProfileData().getMessage());
                                    }
                                    break;
                                }
                                case Constants.GetDetails: {

                                    GetDetailsResponse getDetailsResponse = gson.fromJson(response.toString(), GetDetailsResponse.class);

                                    if (getDetailsResponse.getGetDetailsResult().getMessage().equalsIgnoreCase(Constants.STR_SUCCESS)) {

                                        Global.getDetailsResponse = getDetailsResponse;

                                        volleyListener.apiCallResponse(Constants.SaveConsultationAPI, String.valueOf(getDetailsResponse.getGetDetailsResult()));
                                    } else {
                                        volleyListener.apiCallResponse(Constants.SaveConsultationAPI, getDetailsResponse.getGetDetailsResult().getMessage());
                                    }
                                    break;
                                }

                                case Constants.ChangeOnlineStatus: {

                                    GetChangeStatusResponse changeStatusResponse = gson.fromJson(response.toString(), GetChangeStatusResponse.class);
                                    if (changeStatusResponse.getChangeOnlineStatusResult().getResponseCode().equalsIgnoreCase(Constants.RESPONSE_CODE_SUCCESS)) {
                                        Global.changeStatusResponse = changeStatusResponse;
                                        volleyListener.apiCallResponse(Constants.ChangeOnlineStatus, changeStatusResponse.getChangeOnlineStatusResult().getResponseCode());
                                    } else {
                                        volleyListener.apiCallResponse(Constants.ChangeOnlineStatus, changeStatusResponse.getChangeOnlineStatusResult().getMessage());
                                    }
                                    break;
                                }

                                case Constants.LogoutAPI: {

                                    LogoutResponseModel logoutResponseModel = gson.fromJson(response.toString(), LogoutResponseModel.class);
                                    if (logoutResponseModel.getLogoutResult().getResponseCode().equalsIgnoreCase(Constants.RESPONSE_CODE_SUCCESS)) {
                                        Global.logoutResponse = logoutResponseModel;
                                        volleyListener.apiCallResponse(Constants.LogoutAPI, logoutResponseModel.getLogoutResult().getResponseCode());
                                    } else {
                                        volleyListener.apiCallResponse(Constants.LogoutAPI, logoutResponseModel.getLogoutResult().getMessage());
                                    }
                                    break;
                                }

                                case Constants.CallLogDetails: {


                                    CallLogResponse callLogResponse = gson.fromJson(response.toString(), CallLogResponse.class);
                                    if (callLogResponse.getCallLogResult().getResponseCode().equalsIgnoreCase(Constants.RESPONSE_CODE_SUCCESS)) {
                                        Global.callLogResponse = callLogResponse;
                                        volleyListener.apiCallResponse(Constants.CallLogDetails, callLogResponse.getCallLogResult().getResponseCode());
                                    } else {
                                        volleyListener.apiCallResponse(Constants.CallLogDetails, callLogResponse.getCallLogResult().getMessage());
                                    }
                                    break;
                                }

                                case Constants.SaveCallLog: {

                                    SaveCallLogsResponse saveCallLogsResponse = gson.fromJson(response.toString(), SaveCallLogsResponse.class);
                                    if (saveCallLogsResponse.getSaveCallLogResult().getResponseCode().equalsIgnoreCase(Constants.RESPONSE_CODE_SUCCESS)) {
                                        Global.saveCallLogsResponse = saveCallLogsResponse;
                                        volleyListener.apiCallResponse(Constants.SaveCallLog, saveCallLogsResponse.getSaveCallLogResult().getResponseCode());
                                    } else {
                                        volleyListener.apiCallResponse(Constants.SaveCallLog, saveCallLogsResponse.getSaveCallLogResult().getMessage());
                                    }
                                    break;
                                }

                                case Constants.SaveConsultationAPI: {


                                    SaveConsultationResponse saveConsultationResponse = gson.fromJson(response.toString(), SaveConsultationResponse.class);
                                    if (saveConsultationResponse.getResult().getResponseCode().equalsIgnoreCase(Constants.RESPONSE_CODE_SUCCESS)) {
                                        Global.saveConsultationResponse = saveConsultationResponse;
                                        volleyListener.apiCallResponse(Constants.SaveConsultationAPI, saveConsultationResponse.getResult().getResponseCode());
                                    } else {
                                        volleyListener.apiCallResponse(Constants.SaveConsultationAPI, saveConsultationResponse.getResult().getMessage());
                                    }
                                    break;
                                }

                                default:
                                    break;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }
                , new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.toString().contains("No address associated with hostname")) {
                    if (Global.utils.isProgressBarShowing) {
                        Global.utils.dismissProgressBar();
                    }

                    //Toast.makeText(ctx, "Oops! No Internet Connection", Toast.LENGTH_LONG).show();
                    Global.utils.showErrorSnakeBar((Activity) ctx, "Oops! No Internet Connection");
                } else if (error.toString().contains("ENETUNREACH")) {

                } else {
                    if (Global.utils.isProgressBarShowing) {
                        Global.utils.dismissProgressBar();
                    }

                    //Toast.makeText(ctx, "Something Went Wrong", Toast.LENGTH_LONG).show();
                    Global.utils.showErrorSnakeBar((Activity) ctx, "Something Went Wrong");
                }


            }
        }) {
            /* Passing some request headers */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        requestQueue.add(jsonObjReq);
    }

}
