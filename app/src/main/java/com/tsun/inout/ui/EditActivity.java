package com.tsun.inout.ui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.tsun.inout.R;
import com.tsun.inout.model.ActivityBean;
import com.tsun.inout.model.LookupBean;
import com.tsun.inout.service.SpinnerSelected;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 *	Edit activity
 ************************************************************************
 *	@Author Xiaoming Yang
 *	@Date	24-04-2016 12:40
 ************************************************************************
 *	update time			editor				updated information
 */

public class EditActivity extends AppCompatActivity implements View.OnTouchListener,
        DateTimePickerFragment.onDateTimeSetListener,
        DatePickerFragment.onDateSelectedListener {

    private RequestQueue queue;

    private ProgressDialog ringProgressDialog;
    private GestureDetectorCompat mDetector;
    private Spinner spType;
    private Spinner spRepeatUnit;
    private TextView tvStartTime;
    private TextView tvEndTime;
    private TextView tvRepeatStartDate;
    private TextView tvRepeatEndDate;
    private EditText etContact;
    private EditText etComments;
    private EditText etRepeatFrequency;
    private LinearLayout linearGroups;
    private CheckBox chbWorkingAlone;

    private ActivityBean activityBean;                  // new activity data
    private String updRepeat;
    private ArrayList<String> groupNameArrayList;

    private static final String TIME_FORMAT = "HH:mm:ss";
    private static final String NZ_DATE_FORMAT = "dd-MM-yyyy";
    private static final String NZ_DATE_TIME_FORMAT = "dd-MM-yyyy HH:mm:ss";
    public static final int HORIZON_MIN_DISTANCE = 30;
    public static final String TAG = "jsRequest";
    public static final int HTTP_TIMEOUT_MS = 10000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_edit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.act_new_toolbar);
        toolbar.setTitle(R.string.edit_activity);
        toolbar.setNavigationIcon(R.drawable.btn_back);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                finish();
            }
        });
        queue = Volley.newRequestQueue(this);
        getSelectData();

        activityBean = (ActivityBean) getIntent().getExtras().getParcelable("activityBean");
        updRepeat = "false";        // string type is for being compatible with website updRepeat
        if(activityBean.getGroupName() != null){
            groupNameArrayList = new ArrayList<String>(Arrays.asList(activityBean.getGroupName().split(",")));
        }else{
            groupNameArrayList = new ArrayList<String>();
        }


        // get view components
        SpinnerSelected spinnerSelect = new SpinnerSelected(activityBean);
        spType = (Spinner)findViewById(R.id.sp_act_type);
        spType.setOnItemSelectedListener(spinnerSelect);
        spRepeatUnit = (Spinner)findViewById(R.id.sp_repeat_unit);
        spRepeatUnit.setOnItemSelectedListener(spinnerSelect);

        tvStartTime = (TextView)findViewById(R.id.tv_start_time);
        tvEndTime = (TextView)findViewById(R.id.tv_end_time);
        tvRepeatStartDate = (TextView)findViewById(R.id.tv_repeat_start_date);
        tvRepeatEndDate = (TextView)findViewById(R.id.tv_repeat_end_date);
        etContact = (EditText)findViewById(R.id.et_contact);
        etComments = (EditText)findViewById(R.id.et_comments);
        etRepeatFrequency = (EditText)findViewById(R.id.et_repeat_frequency);
        chbWorkingAlone = (CheckBox)findViewById(R.id.chb_working_alone);

        LinearLayout actDetailsLayout = (LinearLayout)findViewById(R.id.act_new_linear_layout);
        actDetailsLayout.setOnTouchListener(this);
        mDetector = new GestureDetectorCompat(this,new MyGestureListener());

    }

    private void renderPage(){
        if(activityBean.getIsWorkingAlone() == 1){
            chbWorkingAlone.setChecked(true);
        }
        SimpleDateFormat nzDateTimeSdf = new SimpleDateFormat(NZ_DATE_TIME_FORMAT);
        SimpleDateFormat dateSdf = new SimpleDateFormat(NZ_DATE_FORMAT);
        SimpleDateFormat timeSdf = new SimpleDateFormat(TIME_FORMAT);

        String startDateTimeStr = activityBean.getStartDateTime();
        String endDateTimeStr = activityBean.getEndDateTime();
        tvStartTime.setText(startDateTimeStr);
        tvEndTime.setText(endDateTimeStr);
        try {
            Date startDateTime = nzDateTimeSdf.parse(startDateTimeStr);
            Date endDateTime = nzDateTimeSdf.parse(endDateTimeStr);
            activityBean.setStartDate(dateSdf.format(startDateTime));
            activityBean.setEndDate(dateSdf.format(endDateTime));
            activityBean.setStartTime(timeSdf.format(startDateTime));
            activityBean.setEndTime(timeSdf.format(endDateTime));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        etContact.setText(activityBean.getContact());
        etComments.setText(activityBean.getComments());
        if(activityBean.getIsRepeat() == 1){
            etRepeatFrequency.setText(activityBean.getRepeatFrequency()+"");
            tvRepeatStartDate.setText(activityBean.getRepeatStartDate());
            tvRepeatEndDate.setText(activityBean.getRepeatEndDate());
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

    private void closeMe(){
        finish();
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


    private void getSelectData(){

        // String apiUrl = "http://benwk.azurewebsites.net/public/index.php/lookup";
        String apiUrl = "http://10.0.2.2/inout/public/index.php/lookup";

        doJsonObjectRequest
                (Request.Method.GET, apiUrl, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray activityType = (JSONArray)response.getJSONArray("activityType");
                            renderSpinner(activityType, spType);
                            JSONArray repeatUnit = (JSONArray)response.getJSONArray("repeatUnit");
                            renderSpinner(repeatUnit, spRepeatUnit);
                            addGroups();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        handleError(error.toString());
                    }
                });
    }

    private void addGroups(){
        // TODO change to real userId
        // String apiUrl = "http://benwk.azurewebsites.net/public/index.php/user/getGroups/"+"1";
        String apiUrl = "http://10.0.2.2/inout/public/index.php/user/getGroups/"+"1";
        linearGroups = (LinearLayout)findViewById(R.id.linear_groups);

        JsonArrayRequest jsArrayRequest = new JsonArrayRequest
                (Request.Method.GET, apiUrl, null, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        renderGroups(response);
                        renderPage();
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        handleError("Getting data error...");
                    }
                });
        jsArrayRequest.setTag(TAG);
        jsArrayRequest.setRetryPolicy(new DefaultRetryPolicy(
                HTTP_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Add the request to the RequestQueue.
        queue.add(jsArrayRequest);
    }

    private void renderGroups(JSONArray response){
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

    /**
     * encapsulate JsonObjectRequest into this method
     *
     * @param method refer to Request.Method.GET, Request.Method.POST...
     * @param url URL of http request
     * @param response successful callback method
     * @param errorListener error callback method
     *
     * @return void
     */
    private void doJsonObjectRequest(int method, String url, JSONObject jsonRequest, Response.Listener<JSONObject> response, Response.ErrorListener errorListener){

        JsonObjectRequest jsObjectRequest = new JsonObjectRequest(method, url, jsonRequest, response, errorListener);
        jsObjectRequest.setTag(TAG);
        jsObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                HTTP_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Add the request to the RequestQueue.
        queue.add(jsObjectRequest);
    }

    private void handleError(String errInfo){
        if(errInfo == null || errInfo.equals("")){
            errInfo = "Something wrong...";
        }
        Toast.makeText(this,
                errInfo, Toast.LENGTH_LONG).show();
    }

    private void renderSpinner(JSONArray jsonArray, Spinner spinner){
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
                }else if(activityBean.getRepeatUnitId().equals(id)){
                    selectedPosition = adapter.getPosition(lookupBean);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        spinner.setAdapter(adapter);
        spinner.setSelection(selectedPosition);
    }

    public void onCheckboxClicked(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked
        switch(view.getId()) {
            case R.id.chb_working_alone:
                if (checked)
                    activityBean.setIsWorkingAlone(1);
                else
                    activityBean.setIsWorkingAlone(0);
                break;
            case R.id.chb_unknown_time:
                if (checked){
                    //set null to related fields
                }
                break;
            case R.id.chb_edit_repeat:
                if (checked){
                    updRepeat = "true";
                }else{
                    updRepeat = "false";
                }
                break;
        }
    }

    public void setTime(View v){

        DateToTimeFragment dateTimeFragment = new DateToTimeFragment();
        dateTimeFragment.setView(v);
        String tag = "";

        switch (v.getId()){
            case R.id.btn_start_time:
                tag = "startTimePicker";
                break;
            case R.id.btn_end_time:
                tag = "endTimePicker";
                break;
        }

        dateTimeFragment.show(getSupportFragmentManager(), tag);
    }

    @Override
    public void onDateTimeSet(View view, int year, int month, int day, int hourOfDay, int minute) {
        GregorianCalendar calendar = new GregorianCalendar(year, month, day, hourOfDay, minute);
        SimpleDateFormat dateSdf = new SimpleDateFormat(NZ_DATE_FORMAT, Locale.UK);
        dateSdf.setCalendar(calendar);
        SimpleDateFormat timeSdf = new SimpleDateFormat(TIME_FORMAT, Locale.UK);
        timeSdf.setCalendar(calendar);
        String selectedDate = dateSdf.format(calendar.getTime());
        String selectedTime = timeSdf.format(calendar.getTime());
        String selectedDateTime = selectedDate+" "+selectedTime;
        switch (view.getId()){
            case R.id.btn_start_time:
                activityBean.setStartDate(selectedDate);
                activityBean.setStartTime(selectedTime);
                activityBean.setStartDateTime(selectedDateTime);
                tvStartTime.setText(selectedDateTime);
                break;
            case R.id.btn_end_time:
                activityBean.setEndDate(selectedDate);
                activityBean.setEndTime(selectedTime);
                activityBean.setEndDateTime(selectedDateTime);
                tvEndTime.setText(selectedDateTime);
                break;
        }
    }

    public void setDate(View v){

        DatePickerFragment datePickerFragment = new DatePickerFragment();
        datePickerFragment.setView(v);
        String tag = "";

        switch (v.getId()){
            case R.id.btn_repeat_start_date:
                tag = "repeatStartPicker";
                break;
            case R.id.btn_repeat_end_date:
                tag = "repeatEndPicker";
                break;
        }

        datePickerFragment.show(getSupportFragmentManager(), tag);
    }

    @Override
    public void onDateSelected(View view, int year, int month, int day) {

        GregorianCalendar calendar = new GregorianCalendar(year, month, day);
        SimpleDateFormat sdf = new SimpleDateFormat(NZ_DATE_FORMAT, Locale.UK);
        sdf.setCalendar(calendar);
        String selectedDate = sdf.format(calendar.getTime());
        switch (view.getId()){
            case R.id.btn_repeat_start_date:
                activityBean.setRepeatStartDate(selectedDate);
                tvRepeatStartDate.setText(selectedDate);
                break;
            case R.id.btn_repeat_end_date:
                activityBean.setRepeatEndDate(selectedDate);
                tvRepeatEndDate.setText(selectedDate);
                break;
        }
    }

    public void save(){

        activityBean.setContact(etContact.getText().toString());
        activityBean.setComments(etComments.getText().toString());
        if(!("".equals(etRepeatFrequency.getText().toString()))){
            activityBean.setRepeatFrequency(Integer.parseInt(etRepeatFrequency.getText().toString()));
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
        // String apiUrl = "http://benwk.azurewebsites.net/public/index.php/activity/"+activityBean.getId();
        String apiUrl = "http://10.0.2.2/inout/public/index.php/activity/"+activityBean.getId();

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