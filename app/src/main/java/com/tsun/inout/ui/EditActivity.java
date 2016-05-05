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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

/**
 *	Edit activity
 ************************************************************************
 *	@Author Xiaoming Yang
 *	@Date	24-04-2016 12:40
 ************************************************************************
 *	update time			editor				updated information
 */

public class EditActivity extends EditableActActivity implements View.OnTouchListener{

    private ProgressDialog ringProgressDialog;
    private GestureDetectorCompat mDetector;
    private TextView tvStartTime;
    private TextView tvEndTime;
    private TextView tvRepeatStartDate;
    private TextView tvRepeatEndDate;
    private EditText etContact;
    private EditText etComments;
    private EditText etRepeatFrequency;
    private Switch swWorkingAlone;
    private Switch swEditRepeat;
    private Switch swUnknownTime;
    private ImageButton btnEndTime;

    private String updRepeat;
    private ArrayList<String> groupNameArrayList;

    private static final String TIME_FORMAT = "HH:mm";
    private static final String DATE_FORMAT = "dd-MM-yyyy";
    private static final String NZ_DATE_TIME_FORMAT = "h:mm a dd/MM/yyyy";
    public static final int HORIZON_MIN_DISTANCE = 30;
    public static final String TAG = "jsRequest";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setViewId(R.layout.activity_act_edit);
        super.onCreate(savedInstanceState);
        Toolbar toolbar = (Toolbar) findViewById(R.id.act_new_toolbar);
        toolbar.setTitle(R.string.edit_activity);
        toolbar.setNavigationIcon(R.drawable.ic_navigate_back);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                finish();
            }
        });


        setActivityBean((ActivityBean) getIntent().getExtras().getParcelable("activityBean"));
        updRepeat = "false";        // string type is for being compatible with website updRepeat
        if(activityBean.getGroupName() != null){
            groupNameArrayList = new ArrayList<String>(Arrays.asList(activityBean.getGroupName().split(",")));
        }else{
            groupNameArrayList = new ArrayList<String>();
        }

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
        swWorkingAlone = (Switch)findViewById(R.id.sw_working_alone);
        swWorkingAlone.setOnCheckedChangeListener(new SwitchChangeListener());
        swEditRepeat = (Switch)findViewById(R.id.sw_edit_repeat);
        swEditRepeat.setOnCheckedChangeListener(new SwitchChangeListener());
        swUnknownTime = (Switch)findViewById(R.id.sw_unknown_time);
        swUnknownTime.setOnCheckedChangeListener(new SwitchChangeListener());
        btnEndTime = (ImageButton)findViewById(R.id.btn_end_time);

        LinearLayout actDetailsLayout = (LinearLayout)findViewById(R.id.act_edit_linear_layout);
        actDetailsLayout.setOnTouchListener(this);
        mDetector = new GestureDetectorCompat(this,new MyGestureListener());
        getSelectData();
        renderPage();
    }

    @Override
    public void renderPage(){
        if(activityBean.getIsWorkingAlone() == 1){
            swWorkingAlone.setChecked(true);
        }
        SimpleDateFormat nzDateTimeSdf = new SimpleDateFormat(NZ_DATE_TIME_FORMAT);
        SimpleDateFormat dateSdf = new SimpleDateFormat(DATE_FORMAT);
        SimpleDateFormat timeSdf = new SimpleDateFormat(TIME_FORMAT);

        String startDateTimeStr = activityBean.getStartDateTime();
        String endDateTimeStr = activityBean.getEndDateTime();
        tvStartTime.setText(startDateTimeStr);
        tvEndTime.setText(endDateTimeStr);
        try {
            Date startDateTime = nzDateTimeSdf.parse(startDateTimeStr);
            if(!"".equals(endDateTimeStr) && endDateTimeStr != null){
                Date endDateTime = nzDateTimeSdf.parse(endDateTimeStr);
                activityBean.setEndDate(dateSdf.format(endDateTime));
                activityBean.setEndTime(timeSdf.format(endDateTime));
            }else{
                tvEndTime.setText(R.string.unknown_end_time);
                tvEndTime.setOnClickListener(null);
                swUnknownTime.setChecked(true);
                btnEndTime.setEnabled(false);
            }
            activityBean.setStartDate(dateSdf.format(startDateTime));
            activityBean.setStartTime(timeSdf.format(startDateTime));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        etContact.setText(activityBean.getContact());
        etComments.setText(activityBean.getComments());
        if(activityBean.getIsRepeat() == 1){
            etRepeatFrequency.setText(activityBean.getRepeatFrequency()+"");
            tvRepeatStartDate.setText(activityBean.getRepeatStartDate());
            tvRepeatEndDate.setText(activityBean.getRepeatEndDate());
        }else {
            LinearLayout linear_repeat = (LinearLayout)findViewById(R.id.linear_repeat);
            linear_repeat.setVisibility(View.GONE);
        }
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

    @Override
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
                if(activityBean.getSelectedGroups() != null && !activityBean.getSelectedGroups().isEmpty()
                        && activityBean.getSelectedGroups().indexOf(id) > -1){
                    chb.setChecked(true);
                }
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
            if(chb.isChecked() && activityBean.getSelectedGroups().indexOf(chb.getId()) == -1){
                activityBean.getSelectedGroups().add(chb.getId());
                if(groupNameArrayList.indexOf(chb.getText().toString()) == -1){
                    groupNameArrayList.add(chb.getText().toString());
                }
            }else{
                int idx = activityBean.getSelectedGroups().indexOf(chb.getId());
                if(idx > -1){
                    activityBean.getSelectedGroups().remove(idx);
                }
                int nameIdx = groupNameArrayList.indexOf(chb.getText().toString());
                if(nameIdx > -1){
                    groupNameArrayList.remove(nameIdx);
                }
            }
        }
    }


    @Override
    public void renderSpinner(JSONArray jsonArray, Spinner spinner){
        ArrayAdapter<LookupBean> adapter = new ArrayAdapter<LookupBean>(this,android.R.layout.simple_spinner_dropdown_item);
        int selectedPosition = -1;
        for (int i = 0; i< jsonArray.length(); i++) {
            try {
                JSONObject lookup = (JSONObject) jsonArray.get(i);
                String id = lookup.getString("id");
                String name = lookup.getString("name");
                LookupBean lookupBean = new LookupBean(id, name);
                adapter.add(lookupBean);
                if(activityBean.getActivityTypeId().equals(id)){
                    selectedPosition = adapter.getPosition(lookupBean);
                }else if(activityBean.getRepeatUnitId() != null && activityBean.getRepeatUnitId().equals(id)){
                    selectedPosition = adapter.getPosition(lookupBean);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        spinner.setAdapter(adapter);
        spinner.setSelection(selectedPosition);
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
                case R.id.sw_edit_repeat:
                    if(isChecked){
                        updRepeat = "true";
                    }else{
                        updRepeat = "false";
                    }
                    break;
                case R.id.sw_unknown_time:
                    if(isChecked){
                        activityBean.setEndDateTime(null);
                        activityBean.setEndDate(null);
                        activityBean.setEndTime(null);
                        tvEndTime.setText(R.string.unknown_end_time);
                        tvEndTime.setOnClickListener(null);
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
            activityBean.setIsRepeat(1);
        }

        ringProgressDialog = ProgressDialog.show(this, "Please wait ...", "Saving data ...", true);
        ringProgressDialog.setCancelable(false);
        JSONObject activityJsonObject = activityBean.toJSONObject();
        try {
            activityJsonObject.put("updRepeat", updRepeat);
        } catch (JSONException e) {
            ringProgressDialog.dismiss();
            e.printStackTrace();
        }
        String apiUrl = "http://ec2-54-149-243-26.us-west-2.compute.amazonaws.com/inout/public/index.php/activity/"+activityBean.getId();
        // String apiUrl = "http://10.0.2.2/inout/public/index.php/activity/"+activityBean.getId();

        doJsonObjectRequest
                (Request.Method.PUT, apiUrl, activityJsonObject, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        ringProgressDialog.dismiss();
                        try {
                            int result = response.getInt("result");
                            if(result == 1){
                                Toast.makeText(getBaseContext(), "Edit activity successfully.", Toast.LENGTH_LONG).show();
                                if(!groupNameArrayList.isEmpty()){
                                    activityBean.setGroupName(android.text.TextUtils.join(",", groupNameArrayList));
                                }
                                getIntent().putExtra("updatedBean", activityBean);
                                setResult(RESULT_OK, getIntent());
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
