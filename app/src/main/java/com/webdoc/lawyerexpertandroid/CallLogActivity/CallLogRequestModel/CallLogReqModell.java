package com.webdoc.lawyerexpertandroid.CallLogActivity.CallLogRequestModel;

public class CallLogReqModell {
    public String callDuration,callStatus,callType,callerEmail,incomingCallTime,pickCallTime,webdocAgentEmail;

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
