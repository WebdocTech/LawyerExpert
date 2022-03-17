
package com.webdoc.lawyerexpertandroid.LoginResponseModel;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class AgriExpertProfile {

    @SerializedName("Address")
    @Expose
    private String address;
    @SerializedName("ApplicationUserId")
    @Expose
    private String applicationUserId;
    @SerializedName("FirstName")
    @Expose
    private String firstName;
    @SerializedName("LastName")
    @Expose
    private String lastName;
    @SerializedName("MobileNumber")
    @Expose
    private String mobileNumber;
    @SerializedName("OnlineStatus")
    @Expose
    private String onlineStatus;
    @SerializedName("Status")
    @Expose
    private Object status;
    @SerializedName("Username")
    @Expose
    private String username;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getApplicationUserId() {
        return applicationUserId;
    }

    public void setApplicationUserId(String applicationUserId) {
        this.applicationUserId = applicationUserId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getOnlineStatus() {
        return onlineStatus;
    }

    public void setOnlineStatus(String onlineStatus) {
        this.onlineStatus = onlineStatus;
    }

    public Object getStatus() {
        return status;
    }

    public void setStatus(Object status) {
        this.status = status;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}
