
package com.webdoc.lawyerexpertandroid.GetDetailsResponse;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
public class SubCategoryDetail {

    @SerializedName("Remarks")
    @Expose
    private String remarks;
    @SerializedName("SubCategoryName")
    @Expose
    private String subCategoryName;

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getSubCategoryName() {
        return subCategoryName;
    }

    public void setSubCategoryName(String subCategoryName) {
        this.subCategoryName = subCategoryName;
    }

}
