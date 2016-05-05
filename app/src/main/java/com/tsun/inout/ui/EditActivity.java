package com.tsun.inout.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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
    private Switch swWorkingAlone;
    private Switch swEditRepeat;
    private Switch swUnknownTime;
    private ImageButton btnEndTime;

    private ActivityBean activityBean;                  // new activity data
    private String updRepeat;
    private ArrayList<String> groupNameArrayList;

    private static final String TIME_FORMAT = "HH:mm";
    private static final String DATE_FORMAT = "dd-MM-yyyy";
    private static final String NZ_DATE_TIME_FORMAT = "h:mm a dd/MM/yyyy";
    private static final String NZ_DATE_FORMAT = "dd/MM/yyyy";
    public static final int HORIZON_MIN_DISTANCE = 30;
    public static final String TAG = "jsRequest";
    public static final int HTTP_TIMEOUT_MS = 10000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_edit);
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
        queue = Volley.newRequestQueue(this);

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

    private void renderPage(){
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
                tvEndTime.setText("Unknown End Time");
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
        }
    }

    class DateTimeOnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            DateToTimeFragment dateTimeFragment = new DateToTimeFragment();
            dateTimeFragment.setViewId(v.getId());
            String tag = "";

            switch (v.getId()){
                case R.id.tv_start_time:
                    tag = "startTimePicker";
                    break;
                case R.id.tv_end_time:
                    tag = "endTimePicker";
                    break;
            }

            dateTimeFragment.show(getSupportFragmentManager(), tag);
        }
    }

    class DateOnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            DatePickerFragment datePickerFragment = new DatePickerFragment();
            datePickerFragment.setViewId(v.getId());
            String tag = "";

            switch (v.getId()){
                case R.id.tv_repeat_start_date:
                    tag = "repeatStartPicker";
                    break;
                case R.id.tv_repeat_end_date:
                    tag = "repeatEndPicker";
                    break;
            }

            datePickerFragment.show(getSupportFragmentManager(), tag);
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

        SharedPreferences sharedPref = getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(sharedPref.getString("selectData","{}"));
            if(jsonObject.length() > 0){
                JSONArray activityType = (JSONArray)jsonObject.getJSONArray("activityType");
                renderSpinner(activityType, spType);
                JSONArray repeatUnit = (JSONArray)jsonObject.getJSONArray("repeatUnit");
                renderSpinner(repeatUnit, spRepeatUnit);
                getGroups();
            }else{
                String apiUrl = "http://ec2-54-149-243-26.us-west-2.compute.amazonaws.com/inout/public/index.php/lookup";
                // String apiUrl = "http://10.0.2.2/inout/public/index.php/lookup";

                doJsonObjectRequest
                        (Request.Method.GET, apiUrl, null, new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    JSONArray activityType = (JSONArray)response.getJSONArray("activityType");
                                    renderSpinner(activityType, spType);
                                    JSONArray repeatUnit = (JSONArray)response.getJSONArray("repeatUnit");
                                    renderSpinner(repeatUnit, spRepeatUnit);
                                    getGroups();
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
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getGroups(){
        SharedPreferences sharedPref = getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        JSONArray jsonArray;
        try {
            jsonArray = new JSONArray("[]");
            jsonArray = new JSONArray(sharedPref.getString("myGroups","[]"));
            if(jsonArray.length() > 0){
                renderGroups(jsonArray);
            }else{
                // TODO change to real userId
                String apiUrl = "http://ec2-54-149-243-26.us-west-2.compute.amazonaws.com/inout/public/index.php/user/getGroups/"+"1";
                // String apiUrl = "http://10.0.2.2/inout/public/index.php/user/getGroups/"+"1";

                JsonArrayRequest jsArrayRequest = new JsonArrayRequest
                        (Request.Method.GET, apiUrl, null, new Response.Listener<JSONArray>() {

                            @Override
                            public void onResponse(JSONArray response) {
                                renderGroups(response);
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
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void renderGroups(JSONArray response){
        linearGroups = (LinearLayout)findViewById(R.id.linear_groups);
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
                        tvEndTime.setText("Unknown End Time");
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

    public void setTime(View v){

        DateToTimeFragment dateTimeFragment = new DateToTimeFragment();
        dateTimeFragment.setViewId(v.getId());
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
    public void onDateTimeSet(int viewId, int year, int month, int day, int hourOfDay, int minute) {
        GregorianCalendar calendar = new GregorianCalendar(year, month, day, hourOfDay, minute);
        SimpleDateFormat dateSdf = new SimpleDateFormat(DATE_FORMAT, Locale.UK);
        SimpleDateFormat timeSdf = new SimpleDateFormat(TIME_FORMAT, Locale.UK);
        SimpleDateFormat nzDateTimeSdf = new SimpleDateFormat(NZ_DATE_TIME_FORMAT, Locale.UK);
        String selectedDate = dateSdf.format(calendar.getTime());
        String selectedTime = timeSdf.format(calendar.getTime());
        String selectedDateTime = nzDateTimeSdf.format(calendar.getTime());
        switch (viewId){
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
            case R.id.tv_start_time:
                activityBean.setStartDate(selectedDate);
                activityBean.setStartTime(selectedTime);
                activityBean.setStartDateTime(selectedDateTime);
                tvStartTime.setText(selectedDateTime);
                break;
            case R.id.tv_end_time:
                activityBean.setEndDate(selectedDate);
                activityBean.setEndTime(selectedTime);
                activityBean.setEndDateTime(selectedDateTime);
                tvEndTime.setText(selectedDateTime);
                break;
        }
    }

    public void setDate(View v){

        DatePickerFragment datePickerFragment = new DatePickerFragment();
        datePickerFragment.setViewId(v.getId());
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
    public void onDateSelected(int viewId, int year, int month, int day) {

        GregorianCalendar calendar = new GregorianCalendar(year, month, day);
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.UK);
        String selectedDate = sdf.format(calendar.getTime());
        SimpleDateFormat nzSdf = new SimpleDateFormat(NZ_DATE_FORMAT, Locale.UK);
        String nzSelectedDate = nzSdf.format(calendar.getTime());
        switch (viewId){
            case R.id.btn_repeat_start_date:
                activityBean.setRepeatStartDate(selectedDate);
                tvRepeatStartDate.setText(nzSelectedDate);
                break;
            case R.id.btn_repeat_end_date:
                activityBean.setRepeatEndDate(selectedDate);
                tvRepeatEndDate.setText(nzSelectedDate);
                break;
            case R.id.tv_repeat_start_date:
                activityBean.setRepeatStartDate(selectedDate);
                tvRepeatStartDate.setText(nzSelectedDate);
                break;
            case R.id.tv_repeat_end_date:
                activityBean.setRepeatEndDate(selectedDate);
                tvRepeatEndDate.setText(nzSelectedDate);
                break;
        }
    }

    public void save(){

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

    public boolean checkConnectivity() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if(!(networkInfo != null && networkInfo.isConnected())){
            Toast.makeText(getBaseContext(), "Your device is not connected to Internet, please check network connectivity.", Toast.LENGTH_LONG).show();
            return false;
        }else{
            return true;
        }
    }
    private void closeKeyboard(){
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
