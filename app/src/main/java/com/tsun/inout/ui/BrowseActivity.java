package com.tsun.inout.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.tsun.inout.R;
import com.tsun.inout.service.ActivityBean;

/**
 *	Main Activity
 ************************************************************************
 *	@Author Xiaoming Yang
 *	@Date	11-04-2016 12:07
 ************************************************************************
 *	update time			editor				updated information
 */

public class BrowseActivity extends AppCompatActivity {

    TextView actDurationTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.activity_details);
        toolbar.setNavigationIcon(R.drawable.ic_menu_back);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                finish();
            }
        });

        actDurationTv = (TextView)findViewById(R.id.act_duration);

        Bundle data = getIntent().getExtras();
        ActivityBean activityBean = (ActivityBean) data.getParcelable("activityBean");
        String actDuration = activityBean.getStartTime()+"\nTo\n"+activityBean.getEndTime();
        actDurationTv.setText(actDuration);
    }
}
