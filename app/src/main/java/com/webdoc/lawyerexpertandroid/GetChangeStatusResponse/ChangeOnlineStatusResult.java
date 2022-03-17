
package com.webdoc.lawyerexpertandroid.GetChangeStatusResponse;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class ChangeOnlineStatusResult {

    @SerializedName("Message")
    @Expose
    private String message;
    @SerializedName("ResponseCode")
    @Expose
    private String responseCode;

    @SerializedName("Status")
    @Expose
    private String Status;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }
}
