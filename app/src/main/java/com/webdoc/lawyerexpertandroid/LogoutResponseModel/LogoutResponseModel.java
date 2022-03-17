
package com.webdoc.lawyerexpertandroid.LogoutResponseModel;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LogoutResponseModel {

    @SerializedName("LogoutResult")
    @Expose
    private LogoutResult logoutResult;

    public LogoutResult getLogoutResult() {
        return logoutResult;
    }

    public void setLogoutResult(LogoutResult logoutResult) {
        this.logoutResult = logoutResult;
    }

}
