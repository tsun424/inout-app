package com.tsun.inout.ui;

import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tsun.inout.R;
import com.tsun.inout.model.ActivityBean;

/**
 *	Main Activity
 ************************************************************************
 *	@Author Xiaoming Yang
 *	@Date	11-04-2016 12:07
 ************************************************************************
 *	update time			editor				updated information
 */

public class BrowseActivity extends AppCompatActivity implements View.OnTouchListener {

    TextView actDurationTv;
    private GestureDetectorCompat mDetector;
    public static final int HORIZON_MIN_DISTANCE = 30;

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

        LinearLayout actDetailsLayout = (LinearLayout)findViewById(R.id.act_details_layout);
        actDetailsLayout.setOnTouchListener(this);
        mDetector = new GestureDetectorCompat(this,new MyGestureListener());

        actDurationTv = (TextView)findViewById(R.id.act_duration);

        Bundle data = getIntent().getExtras();
        ActivityBean activityBean = (ActivityBean) data.getParcelable("activityBean");
        String actDuration = activityBean.getStartTime()+"\nTo\n"+activityBean.getEndTime();
        actDurationTv.setText(actDuration);
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return this.mDetector.onTouchEvent(event);
    }

    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent event) {
            return true;
        }
        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2,
                               float velocityX, float velocityY) {

            if (event1.getX() < 80 && event2.getX() - event1.getX() > HORIZON_MIN_DISTANCE && Math.abs(velocityX) > 0) {
                closeMe();
            }

            return true;
        }
    }

    private void closeMe(){
        finish();
    }
}
