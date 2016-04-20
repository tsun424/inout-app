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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.tsun.inout.R;
import com.tsun.inout.model.ActivityBean;
import com.tsun.inout.model.LookupBean;
import com.tsun.inout.service.SpinnerSelected;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *	New activity creation fragment
 ************************************************************************
 *	@Author Xiaoming Yang
 *	@Date	08-04-2016 11:40
 ************************************************************************
 *	update time			editor				updated information
 */

public class NewActivity extends AppCompatActivity implements View.OnTouchListener{

    private RequestQueue queue;
    private ProgressDialog ringProgressDialog;
    private GestureDetectorCompat mDetector;
    private Spinner spType;
    private Spinner spRepeatUnit;
    private ActivityBean activityBean;                  // new activity data
    private Button btnStartTime;
    private Button btnEndTime;

    private boolean isWorkingAloneClicked = false;
    private boolean isUnknownClicked = false;

    public static final int HORIZON_MIN_DISTANCE = 30;
    public static final String TAG = "jsRequest";
    public static final int HTTP_TIMEOUT_MS = 10000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_new);
        Toolbar toolbar = (Toolbar) findViewById(R.id.act_new_toolbar);
        toolbar.setTitle(R.string.new_activity);
        toolbar.setNavigationIcon(R.drawable.ic_menu_back);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                finish();
            }
        });
        queue = Volley.newRequestQueue(this);
        getSelectData();

        activityBean = new ActivityBean();

        // get view components
        SpinnerSelected spinnerSelect = new SpinnerSelected(activityBean);
        spType = (Spinner)findViewById(R.id.sp_act_type);
        spType.setOnItemSelectedListener(spinnerSelect);
        spRepeatUnit = (Spinner)findViewById(R.id.sp_repeat_unit);
        spRepeatUnit.setOnItemSelectedListener(spinnerSelect);

        btnStartTime = (Button)findViewById(R.id.btn_start_time);



        LinearLayout actDetailsLayout = (LinearLayout)findViewById(R.id.act_new_linear_layout);
        actDetailsLayout.setOnTouchListener(this);
        mDetector = new GestureDetectorCompat(this,new MyGestureListener());

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
            Toast.makeText(this, activityBean.getType()+" === "+activityBean.getRepeatUnit(), Toast.LENGTH_LONG).show();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }


    private void getSelectData(){

        String apiUrl = "http://benwk.azurewebsites.net/public/index.php/lookup";

        doJsonObjectRequest
                (Request.Method.GET, apiUrl, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray activityType = (JSONArray)response.getJSONArray("activityType");
                            renderSpinner(activityType, spType);
                            JSONArray repeatUnit = (JSONArray)response.getJSONArray("repeatUnit");
                            renderSpinner(repeatUnit, spRepeatUnit);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        handleError(error.toString());
                    }
                }, 2);
    }

    /**
     * encapsulate JsonObjectRequest into this method
     *
     * @param method refer to Request.Method.GET, Request.Method.POST...
     * @param url URL of http request
     * @param response successful callback method
     * @param errorListener error callback method
     * @param action 1: do save
     *
     * @return void
     */
    private void doJsonObjectRequest(int method, String url, JSONObject jsonRequest, Response.Listener<JSONObject> response, Response.ErrorListener errorListener, int action){

        String dialogMsg = "";

        switch(action){
            case 1:
                dialogMsg = "Saving the activity ...";
                break;
        }
        if(ringProgressDialog != null && dialogMsg != ""){
            ringProgressDialog = ProgressDialog.show(this, "Please wait ...", dialogMsg, true);
            ringProgressDialog.setCancelable(false);
        }

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
        }
    }


}
