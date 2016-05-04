package com.tsun.inout.service;


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

    public SpinnerSelected(ActivityBean activityBean){
        this.activityBean = activityBean;
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
        }else if(spinner.getId() == R.id.sp_repeat_unit){
            activityBean.setRepeatUnitId(lookUpId);
            activityBean.setRepeatUnitName(lookUpName);
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
