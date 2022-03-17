
package com.webdoc.lawyerexpertandroid.CallLogActivity.CallLogResponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class CallLogResponse {

    @SerializedName("CallLogResult")
    @Expose
    private CallLogResult callLogResult;

    public CallLogResult getCallLogResult() {
        return callLogResult;
    }

    public void setCallLogResult(CallLogResult callLogResult) {
        this.callLogResult = callLogResult;
    }

}
