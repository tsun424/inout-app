package com.tsun.inout.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
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
import com.tsun.inout.service.ActivityAdapter;
import com.tsun.inout.model.ActivityBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 *	List all my current activities
 ************************************************************************
 *	@Author Xiaoming Yang
 *	@Date	08-04-2016 11:43
 ************************************************************************
 *	update time			editor				updated information
 */

public class ActivityListFragment extends Fragment {
    private ListView actLv;
    private RequestQueue queue;
    private ActivityAdapter adapter;
    private ProgressDialog ringProgressDialog;
    private SwipeRefreshLayout swipeRefresh;
    private JsonArrayRequest jsArrayRequest;
    private OnActivityListSelectedListener mCallback;

    private int currentEditPosition;        // current edit position

    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final String NZ_DATE_FORMAT = "dd/MM/yyyy";
    private static final String NZ_DATE_TIME_FORMAT = "h:mm a dd/MM/yyyy";
    public static final String TAG = "jsRequest";
    public static final int HTTP_TIMEOUT_MS = 10000;
    public static final int PICK_EDIT_RESULT = 2;

    public ActivityListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_act_list, container, false);

        actLv = (ListView) rootView.findViewById(R.id.act_list);
        registerForContextMenu(actLv);
        actLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ActivityBean data = (ActivityBean) parent.getItemAtPosition(position);
                mCallback.onActivitySelected(data);

            }
        });

        ringProgressDialog = ProgressDialog.show(getActivity(), "Please wait ...", "Loading data ...", true);
        ringProgressDialog.setCancelable(true);

        queue = Volley.newRequestQueue(this.getContext());
        getActivityList();

        swipeRefresh = (SwipeRefreshLayout)rootView.findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorAccent);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // This method performs the actual data-refresh operation.
                // The method calls setRefreshing(false) when it's finished.
                if(jsArrayRequest != null){
                    queue.add(jsArrayRequest);
                }else{
                    getActivityList();
                }
            }
        });

        return rootView;
    }

    @Override
    public void onStop() {
        super.onStop();
        // cancel current network requests
        if (queue != null) {
            queue.cancelAll(TAG);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnActivityListSelectedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        ActivityBean activityBean = (ActivityBean)adapter.getItem(((AdapterView.AdapterContextMenuInfo)menuInfo).position);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.list_long_click, menu);
        if(activityBean.getIsRepeat() != 1){
            MenuItem deleteAllItem = menu.findItem(R.id.delete_all);
            deleteAllItem.setVisible(false);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        ActivityBean activityBean = (ActivityBean)adapter.getItem(info.position);
        switch (item.getItemId()) {
            case R.id.check_in:
                // do check in
                doCheckIn(activityBean.getId(), info.position);
                return true;
            case R.id.edit:
                // do edit
                doEdit(info.position);
                return true;
            case R.id.delete:
                // delete the activity
                doDelete(activityBean.getId(), info.position);
                return true;
            case R.id.delete_all:
                // delete the activity
                doDeleteAll(activityBean.getId(), info.position);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void getActivityList(){

        if(!checkConnectivity()){
            return;
        }

        // TODO change user id
        // String apiUrl = "http://benwk.azurewebsites.net/public/index.php/activity/getMyOwnActivity/"+"1";
        // String apiUrl = "http://10.0.2.2/inout/public/index.php/activity/getMyOwnActivity/"+"1";
        String apiUrl = "http://ec2-54-149-243-26.us-west-2.compute.amazonaws.com/inout/public/index.php/activity/getMyOwnActivity/"+"1";

        jsArrayRequest = new JsonArrayRequest
                (Request.Method.GET, apiUrl, null, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        renderList(response);
                        cacheData();
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

    private void doCheckIn(String activityId, final int position){

        if(!checkConnectivity()){
            return;
        }

        String apiUrl = "http://ec2-54-149-243-26.us-west-2.compute.amazonaws.com/inout/public/index.php/activity/checkIn/"+activityId;

        doJsonObjectRequest
                (Request.Method.GET, apiUrl, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int result = response.getInt("result");
                            if(result == 1){
                                adapter.removeItem(position);
                                adapter.notifyDataSetChanged();
                            }
                            dismissEffect();
                            Toast.makeText(getContext(),
                                    "Check in successfully.", Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        handleError(error.toString());
                    }
                }, 1);

    }

    private void doDelete(String activityId, final int position){

        if(!checkConnectivity()){
            return;
        }

        String apiUrl = "http://ec2-54-149-243-26.us-west-2.compute.amazonaws.com/inout/public/index.php/activity/"+activityId;

        doJsonObjectRequest
                (Request.Method.DELETE, apiUrl, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int result = response.getInt("result");
                            if(result == 1){
                                adapter.removeItem(position);
                                adapter.notifyDataSetChanged();
                            }
                            dismissEffect();
                            Toast.makeText(getContext(),
                                    "Delete the activity successfully.", Toast.LENGTH_LONG).show();
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

    private void doDeleteAll(String activityId, final int position){
        if(!checkConnectivity()){
            return;
        }

        String apiUrl = "http://ec2-54-149-243-26.us-west-2.compute.amazonaws.com/inout/public/index.php/activity/delAll/"+activityId;

        doJsonObjectRequest
                (Request.Method.GET, apiUrl, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int result = response.getInt("result");
                            if(result == 1){
                                adapter.removeItem(position);
                                adapter.notifyDataSetChanged();
                            }
                            dismissEffect();
                            Toast.makeText(getContext(),
                                    "Delete the activity successfully.", Toast.LENGTH_LONG).show();
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

    private void doEdit(final int position){

        if(!checkConnectivity()){
            return;
        }

        Intent intent = new Intent(getActivity().getBaseContext(), EditActivity.class);
        intent.putExtra("activityBean", (ActivityBean)adapter.getItem(position));
        currentEditPosition = position;
        startActivityForResult(intent, PICK_EDIT_RESULT);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_EDIT_RESULT){
            if(resultCode == getActivity().RESULT_OK){
                ActivityBean activityBean = (ActivityBean)data.getExtras().getParcelable("updatedBean");
                adapter.updateItem(currentEditPosition, activityBean);
                adapter.notifyDataSetChanged();
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
     * @param action 1: do check in, 2: do delete
     *
     * @return void
     */
    private void doJsonObjectRequest( int method, String url, JSONObject jsonRequest, Response.Listener<JSONObject> response, Response.ErrorListener errorListener, int action){

        String dialogMsg = "";

        switch(action){
            case 1:
                dialogMsg = "Checking in ...";
                break;
            case 2:
                dialogMsg = "Deleting ...";
                break;
        }
        ringProgressDialog = ProgressDialog.show(getActivity(), "Please wait ...", dialogMsg, true);
        ringProgressDialog.setCancelable(false);

        JsonObjectRequest jsObjectRequest = new JsonObjectRequest(method, url, jsonRequest, response, errorListener);
        jsObjectRequest.setTag(TAG);
        jsObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                HTTP_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Add the request to the RequestQueue.
        queue.add(jsObjectRequest);
    }

    private void renderList(JSONArray response){

        try {
            adapter = new ActivityAdapter(getContext());
            actLv.setAdapter(adapter);
            SimpleDateFormat dateTimeSdf = new SimpleDateFormat(DATE_TIME_FORMAT);
            SimpleDateFormat nzDateTimeSdf = new SimpleDateFormat(NZ_DATE_TIME_FORMAT);
            SimpleDateFormat nzDateSdf = new SimpleDateFormat(NZ_DATE_FORMAT);
            SimpleDateFormat DateSdf = new SimpleDateFormat(DATE_FORMAT);
            // Parsing json array response
            // loop through each json object
            for (int i = 0; i < response.length(); i++) {

                JSONObject activity = (JSONObject) response.get(i);
                ActivityBean activityBean = new ActivityBean();
                activityBean.setId(activity.getString("id"));
                try {
                    activityBean.setStartDateTime(nzDateTimeSdf.format(dateTimeSdf.parse(activity.getString("start_time"))));
                    String tempEndDateTime = activity.getString("end_time");
                    if(!"".equals(tempEndDateTime) && tempEndDateTime != null && !"null".equals(tempEndDateTime)){
                        activityBean.setEndDateTime(nzDateTimeSdf.format(dateTimeSdf.parse(activity.getString("end_time"))));
                    }
                } catch (ParseException e) {
                    dismissEffect();
                    e.printStackTrace();
                }
                activityBean.setActivityType(activity.getString("activity_type"));
                activityBean.setStatus(activity.getString("status"));
                activityBean.setContact(activity.getString("contact"));
                activityBean.setGroupName(activity.getString("group_name"));
                activityBean.setComments(activity.getString("comments"));
                activityBean.setActivityTypeId(activity.getString("activity_type_id"));
                activityBean.setIsWorkingAlone(activity.getInt("is_working_alone"));
                activityBean.setIsRepeat(activity.getInt("is_repeat"));
                if(activityBean.getIsRepeat() == 1){
                    activityBean.setRepeatFrequency(activity.getInt("repeat_frequency"));
                    activityBean.setRepeatUnitId(activity.getString("repeat_unit"));
                    try {
                        activityBean.setRepeatStartDate(nzDateSdf.format(DateSdf.parse(activity.getString("repeat_start_date"))));
                        activityBean.setRepeatEndDate(nzDateSdf.format(DateSdf.parse(activity.getString("repeat_end_date"))));
                    } catch (ParseException e) {
                        dismissEffect();
                        e.printStackTrace();
                    }
                }
                JSONArray selectedGroups = activity.getJSONArray("groups");
                for(int j = 0; j < selectedGroups.length(); j++){
                    JSONObject group = (JSONObject)selectedGroups.get(j);
                    activityBean.getSelectedGroups().add(group.getInt("id"));
                }
                adapter.add(activityBean);
                adapter.notifyDataSetChanged();
            }
            dismissEffect();

        } catch (JSONException e) {
            e.printStackTrace();
            dismissEffect();
            Toast.makeText(getContext(),
                    "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void handleError(String errInfo){
        if(errInfo == null || errInfo.equals("")){
            errInfo = "Something wrong...";
        }
        dismissEffect();
        Toast.makeText(getContext(),
                errInfo, Toast.LENGTH_LONG).show();
    }

    private void dismissEffect(){
        if(ringProgressDialog.isShowing()){
            ringProgressDialog.dismiss();
        }
        if(swipeRefresh != null && swipeRefresh.isRefreshing()){
            swipeRefresh.setRefreshing(false);
        }
    }

    public interface OnActivityListSelectedListener{
        public void onActivitySelected(ActivityBean activityBean);
    }

    public boolean checkConnectivity() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if(!(networkInfo != null && networkInfo.isConnected())){
            dismissEffect();
            Toast.makeText(getContext(), "Your device is not connected to Internet, please check network connectivity.", Toast.LENGTH_LONG).show();
            return false;
        }else{
            return true;
        }
    }

    private void cacheData(){
        String apiUrl = "http://ec2-54-149-243-26.us-west-2.compute.amazonaws.com/inout/public/index.php/lookup";
        // String apiUrl = "http://10.0.2.2/inout/public/index.php/lookup";

        JsonObjectRequest jsObjectRequest = new JsonObjectRequest(Request.Method.GET, apiUrl, null,
            new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    SharedPreferences sharedPref = getActivity().getSharedPreferences(
                            getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("selectData", response.toString());
                    editor.commit();
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    handleError(error.toString());
                }
            });
        jsObjectRequest.setTag(TAG);
        jsObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                HTTP_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsObjectRequest);

        String getGroupsUrl = "http://ec2-54-149-243-26.us-west-2.compute.amazonaws.com/inout/public/index.php/user/getGroups/"+"1";
        // String apiUrl = "http://10.0.2.2/inout/public/index.php/user/getGroups/"+"1";

        JsonArrayRequest jsArrayRequest = new JsonArrayRequest
                (Request.Method.GET, getGroupsUrl, null, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        SharedPreferences sharedPref = getActivity().getSharedPreferences(
                                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString("myGroups", response.toString());
                        editor.commit();
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

    public void refreshPage(){
        if(jsArrayRequest != null){
            queue.add(jsArrayRequest);
        }else{
            getActivityList();
        }
    }
}
