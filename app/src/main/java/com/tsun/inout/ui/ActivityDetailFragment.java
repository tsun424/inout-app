package com.tsun.inout.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.tsun.inout.R;

/**
 *	browse details of an activity
 ************************************************************************
 *	@Author Xiaoming Yang
 *	@Date	16-04-2016 11:43
 ************************************************************************
 *	update time			editor				updated information
 */

public class ActivityDetailFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.activity_act_details, container, false);;
        return rootView;
    }
}
