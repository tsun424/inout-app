package com.tsun.inout.ui;

import android.app.ProgressDialog;
import android.content.Context;
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

    public static final String TAG = "jsRequest";
    public static final int HTTP_TIMEOUT_MS = 10000;

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
                queue.add(jsArrayRequest);
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
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.list_long_click, menu);
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
                return true;
            case R.id.delete:
                // delete the activity
                doDelete(activityBean.getId(), info.position);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void getActivityList(){

        String apiUrl = "http://benwk.azurewebsites.net/public/index.php/activity";

        jsArrayRequest = new JsonArrayRequest
                (Request.Method.GET, apiUrl, null, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        renderList(response);
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

        String apiUrl = "http://benwk.azurewebsites.net/public/index.php/activity/checkIn/"+activityId;

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


        String apiUrl = "http://benwk.azurewebsites.net/public/index.php/activity/"+activityId;

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
            // Parsing json array response
            // loop through each json object
            for (int i = 0; i < response.length(); i++) {

                JSONObject activity = (JSONObject) response.get(i);
                // JSONObject phone = activity.getJSONObject("phone");
                ActivityBean activityBean = new ActivityBean();
                activityBean.setId(activity.getString("id"));
                activityBean.setStartTime(activity.getString("start_time"));
                activityBean.setEndTime(activity.getString("end_time"));
                activityBean.setActivityType(activity.getString("type"));
                activityBean.setStatus(activity.getString("status"));
                activityBean.setContact(activity.getString("contact"));
                activityBean.setGroupName(activity.getString("groupName"));
                activityBean.setComments(activity.getString("comments"));
                activityBean.setActivityTypeId(activity.getString("activity_type_id"));
                activityBean.setRepeatUnit(activity.getString("repeat_unit"));
                activityBean.setIsWorkingAlone(activity.getInt("is_working_alone"));
                activityBean.setIsRepeat(activity.getInt("is_repeat"));
                adapter.add(activityBean);
                adapter.notifyDataSetChanged();
            }
            dismissEffect();

        } catch (JSONException e) {
            e.printStackTrace();
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
        if(swipeRefresh.isRefreshing()){
            swipeRefresh.setRefreshing(false);
        }
    }

    public interface OnActivityListSelectedListener{
        public void onActivitySelected(ActivityBean activityBean);
    }

}
