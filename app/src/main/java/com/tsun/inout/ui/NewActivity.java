package com.tsun.inout.ui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.tsun.inout.R;
import com.tsun.inout.model.ActivityBean;
import com.tsun.inout.model.LookupBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *	New activity creation
 ************************************************************************
 *	@Author Xiaoming Yang
 *	@Date	08-04-2016 11:40
 ************************************************************************
 *	update time			editor				updated information
 */

public class NewActivity extends EditableActActivity implements View.OnTouchListener{

    private ProgressDialog ringProgressDialog;
    private GestureDetectorCompat mDetector;
    private TextView tvStartTime;
    private TextView tvEndTime;
    private TextView tvRepeatStartDate;
    private TextView tvRepeatEndDate;
    private EditText etContact;
    private EditText etComments;
    private EditText etRepeatFrequency;
    private ImageButton btnEndTime;


    private static final String TIME_FORMAT = "HH:mm";
    private static final String DATE_FORMAT = "dd-MM-yyyy";
    private static final String NZ_DATE_TIME_FORMAT = "h:mm a dd/MM/yyyy";
    public static final int HORIZON_MIN_DISTANCE = 30;
    public static final String TAG = "jsRequest";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setViewId(R.layout.activity_act_new);
        super.onCreate(savedInstanceState);
        Toolbar toolbar = (Toolbar) findViewById(R.id.act_new_toolbar);
        toolbar.setTitle(R.string.new_activity);
        toolbar.setNavigationIcon(R.drawable.ic_navigate_back);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                finish();
            }
        });
        // queue = Volley.newRequestQueue(this);

        // initial activityBean
        setActivityBean(new ActivityBean());

        // get view components
        SpinnerSelected spinnerSelect = new SpinnerSelected();
        spType.setOnItemSelectedListener(spinnerSelect);
        spRepeatUnit.setOnItemSelectedListener(spinnerSelect);

        tvStartTime = (TextView)findViewById(R.id.tv_start_time);
        tvStartTime.setOnClickListener(new DateTimeOnClickListener());
        tvEndTime = (TextView)findViewById(R.id.tv_end_time);
        tvEndTime.setOnClickListener(new DateTimeOnClickListener());
        tvRepeatStartDate = (TextView)findViewById(R.id.tv_repeat_start_date);
        tvRepeatStartDate.setOnClickListener(new DateOnClickListener());
        tvRepeatEndDate = (TextView)findViewById(R.id.tv_repeat_end_date);
        tvRepeatEndDate.setOnClickListener(new DateOnClickListener());
        etContact = (EditText)findViewById(R.id.et_contact);
        etComments = (EditText)findViewById(R.id.et_comments);
        etRepeatFrequency = (EditText)findViewById(R.id.et_repeat_frequency);
        Switch swWorkingAlone = (Switch) findViewById(R.id.sw_working_alone);
        swWorkingAlone.setOnCheckedChangeListener(new SwitchChangeListener());
        Switch swUnknownEndTime = (Switch) findViewById(R.id.sw_unknown_time);
        swUnknownEndTime.setOnCheckedChangeListener(new SwitchChangeListener());

        LinearLayout actDetailsLayout = (LinearLayout)findViewById(R.id.act_new_linear_layout);
        actDetailsLayout.setOnTouchListener(this);
        mDetector = new GestureDetectorCompat(this,new MyGestureListener());
        getSelectData();
    }

    @Override
    public void renderPage(){
        Date currentDate = new Date();
        SimpleDateFormat nzDateTimeFormat = new SimpleDateFormat(NZ_DATE_TIME_FORMAT);
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        SimpleDateFormat timeFormat = new SimpleDateFormat(TIME_FORMAT);
        String startDateTimeStr = nzDateTimeFormat.format(currentDate);
        tvStartTime.setText(startDateTimeStr);
        activityBean.setStartDateTime(startDateTimeStr);
        activityBean.setStartDate(dateFormat.format(currentDate));
        activityBean.setStartTime(timeFormat.format(currentDate));
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_new, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // noinspection SimplifiableIfStatement
        if (id == R.id.menu_act_save) {
            save();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // cancel current network requests
        if (queue != null) {
            queue.cancelAll(TAG);
        }
    }


    public void renderGroups(JSONArray response){
        LinearLayout linearGroups = (LinearLayout) findViewById(R.id.linear_groups);
        for(int i = 0; i < response.length(); i++){
            try {
                JSONObject group = (JSONObject) response.get(i);
                int id = group.getInt("id");
                String groupName = group.getString("group_name");
                CheckBox chb = new CheckBox(this);
                chb.setId(id);
                chb.setText(groupName);
                chb.setOnClickListener(new ChbOnClickListener());
                linearGroups.addView(chb);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    class ChbOnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            CheckBox chb = (CheckBox)v;
            if(chb.isChecked()){
                activityBean.getSelectedGroups().add(chb.getId());
            }else{
                int idx = activityBean.getSelectedGroups().indexOf(chb.getId());
                if(idx > -1){
                    activityBean.getSelectedGroups().remove(idx);
                }
            }
        }
    }

    @Override
    public void renderSpinner(JSONArray jsonArray, Spinner spinner){
        ArrayAdapter<LookupBean> adapter = new ArrayAdapter<LookupBean>(this,android.R.layout.simple_spinner_dropdown_item);

        for (int i = 0; i< jsonArray.length(); i++) {
            try {
                JSONObject lookup = (JSONObject) jsonArray.get(i);
                String id = lookup.getString("id");
                String name = lookup.getString("name");
                LookupBean lookupBean = new LookupBean(id, name);
                adapter.add(lookupBean);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        spinner.setAdapter(adapter);
    }

    class SwitchChangeListener implements CompoundButton.OnCheckedChangeListener{

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            switch(buttonView.getId()){
                case R.id.sw_working_alone:
                    if(isChecked){
                        activityBean.setIsWorkingAlone(1);
                    }else{
                        activityBean.setIsWorkingAlone(0);
                    }
                    break;
                case R.id.sw_unknown_time:
                    if(isChecked){
                        activityBean.setEndDateTime(null);
                        activityBean.setEndDate(null);
                        activityBean.setEndTime(null);
                        tvEndTime.setText(R.string.unknown_end_time);
                        tvEndTime.setOnClickListener(null);
                        btnEndTime = (ImageButton)findViewById(R.id.btn_end_time);
                        btnEndTime.setEnabled(false);
                    }else{
                        tvEndTime.setText(R.string.hint_end_time);
                        tvEndTime.setOnClickListener(new DateTimeOnClickListener());
                        btnEndTime.setEnabled(true);
                    }
                    break;
            }
        }
    }

    private void save(){

        if(!checkConnectivity()){
            return;
        }

        activityBean.setContact(etContact.getText().toString());
        activityBean.setComments(etComments.getText().toString());
        if(!("".equals(etRepeatFrequency.getText().toString()))){
            activityBean.setRepeatFrequency(Integer.parseInt(etRepeatFrequency.getText().toString()));
        }

        ringProgressDialog = ProgressDialog.show(this, "Please wait ...", "Saving data ...", true);
        ringProgressDialog.setCancelable(false);
        JSONObject activityJsonObject = activityBean.toJSONObject();

        String apiUrl = "http://ec2-54-149-243-26.us-west-2.compute.amazonaws.com/inout/public/index.php/activity";
        // String apiUrl = "http://10.0.2.2/inout/public/index.php/activity";

        doJsonObjectRequest
                (Request.Method.POST, apiUrl, activityJsonObject, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        ringProgressDialog.dismiss();
                        try {
                            int result = response.getInt("result");
                            if(result == 1){
                                Toast.makeText(getBaseContext(), "Save activity successfully.", Toast.LENGTH_LONG).show();
                                setResult(RESULT_OK);
                                closeKeyboard();
                                finish();
                            }else{
                                String errorMsg = response.getString("errorMsg");
                                Toast.makeText(getBaseContext(), "Error: "+errorMsg, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ringProgressDialog.dismiss();
                        handleError(error.toString());
                    }
                });
    }
}
