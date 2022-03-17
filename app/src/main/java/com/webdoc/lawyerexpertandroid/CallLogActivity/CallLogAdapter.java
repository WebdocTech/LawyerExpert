package com.webdoc.lawyerexpertandroid.CallLogActivity;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.squareup.picasso.Picasso;
import com.webdoc.lawyerexpertandroid.CallLogActivity.CallLogResponse.CallLogHistory;
import com.webdoc.lawyerexpertandroid.R;

import java.util.ArrayList;
import java.util.List;


public class CallLogAdapter extends RecyclerView.Adapter<CallLogAdapter.ViewHolder> {

    Context context;
    // ArrayList<CallLogReqModell> datalist;
    List<CallLogHistory> datalistt = new ArrayList<>();


    public CallLogAdapter(Context context, List<CallLogHistory> callLogResponse) {

        this.datalistt = callLogResponse;
        this.context = context;


    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.call_log_item, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {


        //otherUserExpertiesModelClass otherUserExpertiesModelClass = Global.list1.get(position);

        if (datalistt.get(position).getCallStatus().equals("Connected")) {
            Picasso.get().load(R.drawable.connectedcall).into(holder.iv_call_status_image);
            holder.tv_callStatus.setTextColor(context.getResources().getColor(R.color.green));
            holder.tv_callDurration.setVisibility(View.VISIBLE);
            holder.tv_call_duration_heading.setVisibility(View.VISIBLE);
            holder.tv_callDurration.setText(datalistt.get(position).getCallDuration());

        }
        holder.tv_CallerName.setText(datalistt.get(position).getCallerEmail());
        holder.tv_callStatus.setText(datalistt.get(position).getCallStatus());
        holder.tv_IncommingCallTime.setText(datalistt.get(position).getIncomingCallTime());

        String test = datalistt.get(position).getCallStatus();
        char first = test.charAt(0);
        if (datalistt.get(position).getCallStatus().equals("Missed")) {
            TextDrawable drawable1 = TextDrawable.builder()
                    .buildRound(String.valueOf(first), Color.RED);
            holder.iv_call_status.setImageDrawable(drawable1);
        } else {
            TextDrawable drawable1 = TextDrawable.builder()
                    .buildRound(String.valueOf(first), context.getResources().getColor(R.color.green));
            holder.iv_call_status.setImageDrawable(drawable1);
        }
        // radius in px


    }

    @Override
    public int getItemCount() {

        return datalistt.size();

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_CallerName, tv_IncommingCallTime, tv_callStatus, tv_callDurration, tv_call_duration_heading;
        ImageView iv_call_status_image, iv_call_status;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_CallerName = itemView.findViewById(R.id.tv_CallerName);
            tv_IncommingCallTime = itemView.findViewById(R.id.tv_IncommingCallTime);
            tv_callStatus = itemView.findViewById(R.id.tv_callStatus);
            tv_callDurration = itemView.findViewById(R.id.tv_callDurration);
            iv_call_status_image = itemView.findViewById(R.id.iv_call_status_image);
            iv_call_status = itemView.findViewById(R.id.iv_call_status);
            tv_call_duration_heading = itemView.findViewById(R.id.tv_call_duration_heading);

        }
    }

}
