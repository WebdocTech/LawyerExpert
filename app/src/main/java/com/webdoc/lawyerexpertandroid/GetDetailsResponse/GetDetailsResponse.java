
package com.webdoc.lawyerexpertandroid.GetDetailsResponse;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
public class GetDetailsResponse {

    @SerializedName("GetDetailsResult")
    @Expose
    private GetDetailsResult getDetailsResult;

    public GetDetailsResult getGetDetailsResult() {
        return getDetailsResult;
    }

    public void setGetDetailsResult(GetDetailsResult getDetailsResult) {
        this.getDetailsResult = getDetailsResult;
    }

}
