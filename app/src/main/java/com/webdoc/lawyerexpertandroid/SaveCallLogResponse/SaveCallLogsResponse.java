
package com.webdoc.lawyerexpertandroid.SaveCallLogResponse;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SaveCallLogsResponse {

    @SerializedName("SaveCallLogResult")
    @Expose
    private SaveCallLogResult saveCallLogResult;

    public SaveCallLogResult getSaveCallLogResult() {
        return saveCallLogResult;
    }

    public void setSaveCallLogResult(SaveCallLogResult saveCallLogResult) {
        this.saveCallLogResult = saveCallLogResult;
    }

}
