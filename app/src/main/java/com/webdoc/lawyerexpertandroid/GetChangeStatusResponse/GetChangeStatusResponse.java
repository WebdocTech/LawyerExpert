
package com.webdoc.lawyerexpertandroid.GetChangeStatusResponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class GetChangeStatusResponse {

    @SerializedName("ChangeOnlineStatusResult")
    @Expose
    private ChangeOnlineStatusResult changeOnlineStatusResult;

    public ChangeOnlineStatusResult getChangeOnlineStatusResult() {
        return changeOnlineStatusResult;
    }

    public void setChangeOnlineStatusResult(ChangeOnlineStatusResult changeOnlineStatusResult) {
        this.changeOnlineStatusResult = changeOnlineStatusResult;
    }

}
