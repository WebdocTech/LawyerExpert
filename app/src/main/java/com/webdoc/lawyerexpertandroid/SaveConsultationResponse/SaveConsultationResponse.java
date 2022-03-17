
package com.webdoc.lawyerexpertandroid.SaveConsultationResponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class SaveConsultationResponse {

    @SerializedName("Result")
    @Expose
    private Result result;

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

}
