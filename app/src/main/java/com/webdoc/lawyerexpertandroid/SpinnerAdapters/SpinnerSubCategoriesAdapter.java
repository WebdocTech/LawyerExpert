package com.webdoc.lawyerexpertandroid.SpinnerAdapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.webdoc.lawyerexpertandroid.GetDetailsResponse.SubCategoryDetail;
import com.webdoc.lawyerexpertandroid.R;

import java.util.List;

public class SpinnerSubCategoriesAdapter extends ArrayAdapter<SubCategoryDetail> {
    private Activity context;
    List<SubCategoryDetail> data = null;

    public SpinnerSubCategoriesAdapter(Activity context, int resource, List<SubCategoryDetail> data) {
        super(context, resource, data);
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) { // Ordinary view in Spinner, we use android.R.layout.simple_spinner_item
        return initView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) { // This view starts when we click the spinner.
        return initView(position, convertView, parent);
    }

    private View initView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        if (row == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            row = inflater.inflate(R.layout.spinner_item, parent, false);
        }

        SubCategoryDetail item = data.get(position);
        String name = item.getSubCategoryName();

        if (item != null) {
            TextView text = (TextView) row.findViewById(R.id.item_value);
            if (text != null) {
                text.setText(name);
            }
        }

        return row;
    }//init

}
