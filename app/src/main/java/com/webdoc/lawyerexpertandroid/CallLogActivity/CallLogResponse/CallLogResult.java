
package com.webdoc.lawyerexpertandroid.CallLogActivity.CallLogResponse;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class CallLogResult {

    @SerializedName("CallLogHistory")
    @Expose
    private List<CallLogHistory> callLogHistory = null;
    @SerializedName("Connected")
    @Expose
    private String connected;
    @SerializedName("Message")
    @Expose
    private String message;
    @SerializedName("Missed")
    @Expose
    private String missed;
    @SerializedName("ResponseCode")
    @Expose
    private String responseCode;

    public List<CallLogHistory> getCallLogHistory() {
        return callLogHistory;
    }

    public void setCallLogHistory(List<CallLogHistory> callLogHistory) {
        this.callLogHistory = callLogHistory;
    }

    public String getConnected() {
        return connected;
    }

    public void setConnected(String connected) {
        this.connected = connected;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMissed() {
        return missed;
    }

    public void setMissed(String missed) {
        this.missed = missed;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

}
