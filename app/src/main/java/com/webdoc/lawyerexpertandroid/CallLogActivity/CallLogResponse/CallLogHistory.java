
package com.webdoc.lawyerexpertandroid.CallLogActivity.CallLogResponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class CallLogHistory {

    @SerializedName("callDuration")
    @Expose
    private String callDuration;
    @SerializedName("callStatus")
    @Expose
    private String callStatus;
    @SerializedName("callType")
    @Expose
    private String callType;
        @SerializedName("callerEmail")
    @Expose
    private String callerEmail;
    @SerializedName("incomingCallTime")
    @Expose
    private String incomingCallTime;
    @SerializedName("pickCallTime")
    @Expose
    private String pickCallTime;
    @SerializedName("webdocAgentEmail")
    @Expose
    private String webdocAgentEmail;

    public String getCallDuration() {
        return callDuration;
    }

    public void setCallDuration(String callDuration) {
        this.callDuration = callDuration;
    }

    public String getCallStatus() {
        return callStatus;
    }

    public void setCallStatus(String callStatus) {
        this.callStatus = callStatus;
    }

    public String getCallType() {
        return callType;
    }

    public void setCallType(String callType) {
        this.callType = callType;
    }

    public String getCallerEmail() {
        return callerEmail;
    }

    public void setCallerEmail(String callerEmail) {
        this.callerEmail = callerEmail;
    }

    public String getIncomingCallTime() {
        return incomingCallTime;
    }

    public void setIncomingCallTime(String incomingCallTime) {
        this.incomingCallTime = incomingCallTime;
    }

    public String getPickCallTime() {
        return pickCallTime;
    }

    public void setPickCallTime(String pickCallTime) {
        this.pickCallTime = pickCallTime;
    }

    public String getWebdocAgentEmail() {
        return webdocAgentEmail;
    }

    public void setWebdocAgentEmail(String webdocAgentEmail) {
        this.webdocAgentEmail = webdocAgentEmail;
    }

}
