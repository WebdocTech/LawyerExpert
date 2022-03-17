
package com.webdoc.lawyerexpertandroid.LoginResponseModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LawyerProfileData {

    @SerializedName("AgriExpertProfile")
    @Expose
    private AgriExpertProfile agriExpertProfile;
    @SerializedName("Message")
    @Expose
    private String message;
    @SerializedName("ResponseCode")
    @Expose
    private String responseCode;

    public AgriExpertProfile getAgriExpertProfile() {
        return agriExpertProfile;
    }

    public void setAgriExpertProfile(AgriExpertProfile agriExpertProfile) {
        this.agriExpertProfile = agriExpertProfile;
    }

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

}
