
package com.webdoc.lawyerexpertandroid.LoginResponseModel;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LoginResponseModel {

    @SerializedName("LawyerProfileData")
    @Expose
    private LawyerProfileData lawyerProfileData;

    public LawyerProfileData getLawyerProfileData() {
        return lawyerProfileData;
    }

    public void setLawyerProfileData(LawyerProfileData lawyerProfileData) {
        this.lawyerProfileData = lawyerProfileData;
    }

}
