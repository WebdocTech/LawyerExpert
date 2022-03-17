
package com.webdoc.lawyerexpertandroid.GetDetailsResponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CategoryDetail {

    @SerializedName("CategoryName")
    @Expose
    private String categoryName;
    @SerializedName("SubCategoryDetails")
    @Expose
    private List<SubCategoryDetail> subCategoryDetails = null;

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public List<SubCategoryDetail> getSubCategoryDetails() {
        return subCategoryDetails;
    }

    public void setSubCategoryDetails(List<SubCategoryDetail> subCategoryDetails) {
        this.subCategoryDetails = subCategoryDetails;
    }

}
