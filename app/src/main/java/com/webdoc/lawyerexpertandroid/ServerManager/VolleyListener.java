package com.webdoc.lawyerexpertandroid.ServerManager;

import org.json.JSONException;

/**
 * Created by Admin on 7/3/2019.
 */

public interface VolleyListener {

    /* These methods will be called when you implement interface on activity at top */

    public void apiCallResponse(String api, String response) throws JSONException;
}
