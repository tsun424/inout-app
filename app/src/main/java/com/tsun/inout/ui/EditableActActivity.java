package com.tsun.inout.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.CheckBox;
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

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 *	super activity of NewActivity and EditActivity, which can reduce
 *  duplicate code
 ************************************************************************
 *	@Author Xiaoming Yang
 *	@Date	08-05-2016 10:40
 ************************************************************************
 *	update time			editor				updated information
 */

public class EditableActActivity extends AppCompatActivity implements
        DateTimePickerFragment.onDateTimeSetListener,
        DatePickerFragment.onDateSelectedListener,
        SpinnerSelected.OnWorkingAloneSelectedListener {

    protected Spinner spType;
    protected Spinner spRepeatUnit;

    protected ActivityBean activityBean;
    protected RequestQueue queue;
    protected int viewId;

    public static final String TIME_FORMAT = "HH:mm";
    public static final String DATE_FORMAT = "dd-MM-yyyy";
    public static final String NZ_DATE_FORMAT = "dd/MM/yyyy";
    public static final String NZ_DATE_TIME_FORMAT = "h:mm a dd/MM/yyyy";
    public static final String TAG = "jsRequest";
    public static final int HTTP_TIMEOUT_MS = 10000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(this.viewId);
        queue = Volley.newRequestQueue(this);
        spType = (Spinner)findViewById(R.id.sp_act_type);
        spRepeatUnit = (Spinner)findViewById(R.id.sp_repeat_unit);
    }

    // this is an empty method, child classes must implement this method
    public void renderPage(){}

    /**
     * set the content view id, child class should set this id before call onCreate method,
     * otherwise, the super class cannot find the views
     */
    public void setViewId(int viewId){
        this.viewId = viewId;
    }

    @Override
    public void displayLayout(boolean display) {
        LinearLayout linearWorkingAlone = (LinearLayout)findViewById(R.id.linear_working_alone);
        View vWorkingAlone = (View)findViewById(R.id.v_working_alone);
        LinearLayout linearGroups = (LinearLayout)findViewById(R.id.linear_layout_groups);
        View vGroups = (View)findViewById(R.id.v_working_alone);
        Switch swWorkingAlone = (Switch)findViewById(R.id.sw_working_alone);
        if(display){
            linearWorkingAlone.setVisibility(View.VISIBLE);
            vWorkingAlone.setVisibility(View.VISIBLE);
            linearGroups.setVisibility(View.VISIBLE);
            vGroups.setVisibility(View.VISIBLE);
        }else{
            linearWorkingAlone.setVisibility(View.GONE);
            vWorkingAlone.setVisibility(View.GONE);
            linearGroups.setVisibility(View.GONE);
            vGroups.setVisibility(View.GONE);
            swWorkingAlone.setChecked(false);
            uncheckGroups();
        }
    }
    private void uncheckGroups(){
        LinearLayout linearGroups = (LinearLayout)findViewById(R.id.linear_groups);
        int childCount = linearGroups.getChildCount();
        for (int i=0; i < childCount; i++){
            View v = linearGroups.getChildAt(i);
            if(v instanceof CheckBox){
                ((CheckBox) v).setChecked(false);
                int idx = activityBean.getSelectedGroups().indexOf(((CheckBox) v).getId());
                if(idx > -1){
                    activityBean.getSelectedGroups().remove(idx);
                }
            }
        }
    }

    @Override
    public void onDateSelected(int viewId, int year, int month, int day) {
        GregorianCalendar calendar = new GregorianCalendar(year, month, day);
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.UK);
        String selectedDate = sdf.format(calendar.getTime());
        SimpleDateFormat nzSdf = new SimpleDateFormat(NZ_DATE_FORMAT, Locale.UK);
        String nzSelectedDate = nzSdf.format(calendar.getTime());
        TextView tvRepeatStartDate = (TextView)findViewById(R.id.tv_repeat_start_date);
        TextView tvRepeatEndDate = (TextView)findViewById(R.id.tv_repeat_end_date);

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

    @Override
    public void onDateTimeSet(int viewId, int year, int month, int day, int hourOfDay, int minute) {
        GregorianCalendar calendar = new GregorianCalendar(year, month, day, hourOfDay, minute);
        SimpleDateFormat dateSdf = new SimpleDateFormat(DATE_FORMAT, Locale.UK);
        SimpleDateFormat timeSdf = new SimpleDateFormat(TIME_FORMAT, Locale.UK);
        SimpleDateFormat nzDateTimeSdf = new SimpleDateFormat(NZ_DATE_TIME_FORMAT, Locale.UK);
        String selectedDate = dateSdf.format(calendar.getTime());
        String selectedTime = timeSdf.format(calendar.getTime());
        String selectedDateTime = nzDateTimeSdf.format(calendar.getTime());
        TextView tvStartTime = (TextView)findViewById(R.id.tv_start_time);
        TextView tvEndTime = (TextView)findViewById(R.id.tv_end_time);

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

    public void setActivityBean(ActivityBean activityBean){
        this.activityBean = activityBean;
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

    protected void closeMe(){
        finish();
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
    protected void doJsonObjectRequest(int method, String url, JSONObject jsonRequest, Response.Listener<JSONObject> response, Response.ErrorListener errorListener){

        JsonObjectRequest jsObjectRequest = new JsonObjectRequest(method, url, jsonRequest, response, errorListener);
        jsObjectRequest.setTag(TAG);
        jsObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                HTTP_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Add the request to the RequestQueue.
        queue.add(jsObjectRequest);
    }

    protected void handleError(String errInfo){
        if(errInfo == null || errInfo.equals("")){
            errInfo = "Something wrong...";
        }
        Toast.makeText(this,
                errInfo, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // cancel current network requests
        if (queue != null) {
            queue.cancelAll(TAG);
        }
    }

    protected void getSelectData(){

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

    protected void getGroups(){
        SharedPreferences sharedPref = getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        JSONArray jsonArray;
        try {
            jsonArray = new JSONArray(sharedPref.getString("myGroups","[]"));
            if(jsonArray.length() > 0){
                renderGroups(jsonArray);
                renderPage();
            }else{
                // TODO change to real userId
                String apiUrl = "http://ec2-54-149-243-26.us-west-2.compute.amazonaws.com/inout/public/index.php/user/getGroups/"+"1";
                // String apiUrl = "http://10.0.2.2/inout/public/index.php/user/getGroups/"+"1";

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
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // this is an empty method, child classes must implement this method
    public void renderGroups(JSONArray response){}

    // this is an empty method, child classes must implement this method
    public void renderSpinner(JSONArray jsonArray, Spinner spinner){}

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
    public void closeKeyboard(){
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    class SpinnerSelected implements AdapterView.OnItemSelectedListener {

        public SpinnerSelected(){}


        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            Spinner spinner = (Spinner)parent;
            LookupBean lookupBean = (LookupBean)parent.getItemAtPosition(position);
            String lookUpId = lookupBean.getId();
            String lookUpName = lookupBean.getName();
            if(spinner.getId() == R.id.sp_act_type){
                activityBean.setActivityTypeId(lookUpId);
                activityBean.setActivityType(lookUpName);
                if("30".equals(lookUpId) || "33".equals(lookUpId)){
                    displayLayout(true);
                }else{
                    displayLayout(false);
                }
            }else if(spinner.getId() == R.id.sp_repeat_unit){
                activityBean.setRepeatUnitId(lookUpId);
                activityBean.setRepeatUnitName(lookUpName);
            }

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }
}
