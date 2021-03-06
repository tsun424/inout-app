package com.tsun.inout.service;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tsun.inout.R;
import com.tsun.inout.model.ActivityBean;

import java.util.ArrayList;
import java.util.List;

/**
 *	ActivityAdapter, make list for activities
 ************************************************************************
 *	@Author Xiaoming Yang
 *	@Date	08-04-2016 11:35
 ************************************************************************
 *	update time			editor				updated information
 */

public class ActivityAdapter extends BaseAdapter {

    private Context context;
    private List<ActivityBean> actList;

    public ActivityAdapter(Context context){
        this.actList = new ArrayList<ActivityBean>();
        this.context = context;
    }

    public void add(ActivityBean activityBean){
        this.actList.add(activityBean);
    }

    @Override
    public int getCount() {
        return actList.size();
    }

    @Override
    public Object getItem(int position) {
        return actList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout ll = null;

        if(convertView!=null){
            ll = (LinearLayout)convertView;
        }else{
            ll = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.act_list_cell,null);
        }
        ActivityBean data = (ActivityBean) getItem(position);

        TextView tvStartTime = (TextView)ll.findViewById(R.id.act_start_time);
        TextView tvEndTime = (TextView)ll.findViewById(R.id.act_end_time);
        TextView tvActivityType = (TextView)ll.findViewById(R.id.act_type);
        ImageView ivStatus = (ImageView)ll.findViewById(R.id.iv_status);
        ImageView ivRepeat = (ImageView)ll.findViewById(R.id.iv_is_repeat);

        String endDateTime = data.getEndDateTime();
        if(data.getEndDateTime() == null || "null".equals(data.getEndDateTime())){
            endDateTime = "Unknown End Time";
        }
        tvStartTime.setText(data.getStartDateTime() + " To ");
        tvEndTime.setText(endDateTime);
        tvActivityType.setText(data.getActivityType());
        String status = data.getStatus();
        if("Active".equals(status)){
            ivStatus.setImageResource(R.drawable.ic_active);
        }else if("Completed".equals(status)){
            ivStatus.setImageResource(R.drawable.ic_done);
        }else if("Overdue".equals(status)){
            ivStatus.setImageResource(R.drawable.ic_alert);
        }
        if(data.getIsRepeat() == 1){
            ivRepeat.setImageResource(R.drawable.ic_repeat);
        }else{
            ivRepeat.setImageDrawable(null);
        }


        return ll;
    }

    public void removeItem(int position){
        actList.remove(position);
    }

    public void emptyAdapter(){
        this.actList = new ArrayList<ActivityBean>();
    }

    public void updateItem(int position, ActivityBean activityBean){
        if(actList.get(position) != null){
            actList.set(position, activityBean);
        }
    }
}
