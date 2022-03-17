
package com.webdoc.lawyerexpertandroid.SaveCallLogResponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class SaveCallLogResult {

    @SerializedName("Message")
    @Expose
    private String message;
    @SerializedName("ResponseCode")
    @Expose
    private String responseCode;
    @SerializedName("SaveCallogId")
    @Expose
    private Integer saveCallogId;

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

    public Integer getSaveCallogId() {
        return saveCallogId;
    }

    public void setSaveCallogId(Integer saveCallogId) {
        this.saveCallogId = saveCallogId;
    }

}
