package com.tsun.inout.ui;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.tsun.inout.R;

/**
 * Created by Ben on 17/04/2016.
 */
public class TransparentFragment extends Fragment {

    public TransparentFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_transparent, container, false);
        return rootView;
    }
}
