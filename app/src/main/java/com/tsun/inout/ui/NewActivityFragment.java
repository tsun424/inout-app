package com.tsun.inout.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.tsun.inout.R;

/**
 *	New activity creation fragment
 ************************************************************************
 *	@Author Xiaoming Yang
 *	@Date	08-04-2016 11:40
 ************************************************************************
 *	update time			editor				updated information
 */

public class NewActivityFragment extends Fragment{

    public NewActivityFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_act_new, container, false);

        final Button mainButton = (Button) rootView.findViewById(R.id.main_button);
        mainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mainButton.setBackgroundColor(Color.YELLOW);
            }
        });
        // Inflate the layout for this fragment
        return rootView;
    }
}
