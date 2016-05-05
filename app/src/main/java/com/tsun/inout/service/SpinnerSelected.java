package com.tsun.inout.service;


import android.app.Activity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.tsun.inout.R;
import com.tsun.inout.model.ActivityBean;
import com.tsun.inout.model.LookupBean;

/**
 *	the method is fired when spinner is selected
 ************************************************************************
 *	@Author Xiaoming Yang
 *	@Date	019-04-2016 15:00
 ************************************************************************
 *	update time			editor				updated information
 */

public class SpinnerSelected implements AdapterView.OnItemSelectedListener {

    private ActivityBean activityBean;
    private OnWorkingAloneSelectedListener activity;
    // the activity or fragment which call SpinnerSelected must implement this interface
    public interface OnWorkingAloneSelectedListener{
        void displayLayout(boolean display);
    }

    public SpinnerSelected(ActivityBean activityBean, OnWorkingAloneSelectedListener activity){
        this.activityBean = activityBean;
        this.activity = activity;
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        Spinner spinner = (Spinner)parent;
        LookupBean lookupBean = (LookupBean)parent.getItemAtPosition(position);
        String lookUpId = lookupBean.getId();
        String lookUpName = lookupBean.getName();
        if(spinner.getId() == R.id.sp_act_type){
            activityBean.setActivityTypeId(lookUpId);
            activityBean.setActivityType(lookUpName);
            if("30".equals(lookUpId) || "33".equals(lookUpId)){
                activity.displayLayout(true);
            }else{
                activity.displayLayout(false);
            }
        }else if(spinner.getId() == R.id.sp_repeat_unit){
            activityBean.setRepeatUnitId(lookUpId);
            activityBean.setRepeatUnitName(lookUpName);
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
