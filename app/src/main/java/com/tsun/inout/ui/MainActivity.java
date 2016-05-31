package com.tsun.inout.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.microsoft.aad.adal.AuthenticationCallback;
import com.microsoft.aad.adal.AuthenticationConstants;
import com.microsoft.aad.adal.AuthenticationContext;
import com.microsoft.aad.adal.AuthenticationResult;
import com.tsun.inout.R;
import com.tsun.inout.model.ActivityBean;
import com.tsun.inout.util.Constants;
import com.tsun.inout.util.PublicUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.LENGTH_SHORT;


/**
 *	Main Activity
 ************************************************************************
 *	@Author Xiaoming Yang
 *	@Date	08-04-2016 11:40
 ************************************************************************
 *	update time			editor				updated information
 */

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ActivityListFragment.OnActivityListSelectedListener {

    private long lastClickTime = 0;
    private RequestQueue queue;

    static final int PICK_NEW_RESULT = 1;
    static final int OPEN_DETAILS = 2;
    public static final String TAG = "jsRequest";
    public static final int HTTP_TIMEOUT_MS = 10000;
    /**
     * Extra query parameter nux=1 uses new login page at AAD. This is optional.
     */
    static final String EXTRA_QUERY_PARAM = "nux=1";

    private ActivityListFragment activityListFragment;
    private AuthenticationContext mAuthContext;
    private ProgressDialog mLoginProgressDialog;

    private TextView tvLoginName;
    private TextView tvLoginEmail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerLayout = navigationView.getHeaderView(0);
        tvLoginName = (TextView)headerLayout.findViewById(R.id.tv_login_user);
        tvLoginEmail = (TextView)headerLayout.findViewById(R.id.tv_login_email);

        // do authentication start
        // Provide key info for Encryption
        if (Build.VERSION.SDK_INT < 18) {
            try {
                PublicUtil.setupKeyForSample();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Encryption failed:"+e, LENGTH_LONG).show();
            }
        }
        mAuthContext = new AuthenticationContext(MainActivity.this, Constants.AUTHORITY_URL, true);
        // mLoginProgressDialog.show();
        mAuthContext.acquireToken(MainActivity.this, Constants.RESOURCE_ID, Constants.CLIENT_ID,
                Constants.REDIRECT_URL, "", EXTRA_QUERY_PARAM, getCallback());
        // do authentication end

        queue = Volley.newRequestQueue(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // To test session cookie behavior
        mLoginProgressDialog = new ProgressDialog(this);
        mLoginProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mLoginProgressDialog.setMessage("Login in progress...");

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mLoginProgressDialog != null) {
            // to test session cookie behavior
            mLoginProgressDialog.dismiss();
            mLoginProgressDialog = null;
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //super.onBackPressed();
            if(lastClickTime <= 0){
                Toast.makeText(this,"Press again to exit",Toast.LENGTH_SHORT).show();
                lastClickTime = System.currentTimeMillis();
            }else{
                long currentClickTime = System.currentTimeMillis();
                if(currentClickTime-lastClickTime < 2000){
                    super.onBackPressed();
                }else{
                    Toast.makeText(this,"Press again to exit",Toast.LENGTH_SHORT).show();
                    lastClickTime = currentClickTime;
                }
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // noinspection SimplifiableIfStatement
        if (id == R.id.menu_activity_new) {
            Intent intent = new Intent(getBaseContext(), NewActivity.class);
            startActivityForResult(intent, PICK_NEW_RESULT);
            return true;
        }

         return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_list_act) {
            activityListFragment = new ActivityListFragment();
            android.support.v4.app.FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, activityListFragment);
            fragmentTransaction.commit();

        } else if (id == R.id.nav_new_act) {
            Intent intent = new Intent(getBaseContext(), NewActivity.class);
            startActivityForResult(intent, PICK_NEW_RESULT);
        } else if (id == R.id.nav_exit) {
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AuthenticationConstants.UIRequest.BROWSER_FLOW) {
            if (mAuthContext != null) {
                mAuthContext.onActivityResult(requestCode, resultCode, data);
            }
        }else if(requestCode == PICK_NEW_RESULT){
            if(resultCode == RESULT_OK){
                /*
                activityListFragment = new ActivityListFragment();
                android.support.v4.app.FragmentTransaction fragmentTransaction =
                        getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, activityListFragment).commit();
                */
                activityListFragment = (ActivityListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                activityListFragment.refreshPage();
            }
        }else if (requestCode == OPEN_DETAILS){
            activityListFragment = (ActivityListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            activityListFragment.refreshPage();
        }

    }

    @Override
    public void onActivitySelected(ActivityBean activityBean) {
        Intent intent = new Intent(getBaseContext(), BrowseActivityPager.class);
        intent.putExtra("activityBean", activityBean);
        startActivityForResult(intent, OPEN_DETAILS);
    }

    // authentication start
    private AuthenticationCallback<AuthenticationResult> getCallback() {
        return new AuthenticationCallback<AuthenticationResult>() {

            @Override
            public void onError(Exception exc) {
                if (mLoginProgressDialog != null && mLoginProgressDialog.isShowing()) {
                    mLoginProgressDialog.dismiss();
                }

                Toast.makeText(getApplicationContext(), "Login error, get token error:" + exc.getMessage(),
                        LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(AuthenticationResult result) {
                if (mLoginProgressDialog != null && mLoginProgressDialog.isShowing()) {
                    mLoginProgressDialog.dismiss();
                }
                if (result != null && !result.getAccessToken().isEmpty()) {
                    setLocalToken(result);
                    ActivityListFragment fragment = new ActivityListFragment();
                    android.support.v4.app.FragmentTransaction fragmentTransaction =
                            getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_container, fragment);
                    fragmentTransaction.commit();
                    if (result.getUserInfo() != null) {
                        tvLoginName.setText(result.getUserInfo().getGivenName());
                        tvLoginEmail.setText(result.getUserInfo().getDisplayableId());
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Error: didn't get AuthenticationResult from Azure AD",
                            LENGTH_LONG).show();
                }


            }

        };
    }

    private void setLocalToken(AuthenticationResult newToken) {
        Constants.CURRENT_RESULT = newToken;
        PublicUtil.putStringToSP(this,"accessToken",newToken.getAccessToken());
        // handle get user id and store it into SharedPreferences
        String getUserUri = "http://ec2-54-149-243-26.us-west-2.compute.amazonaws.com/inout/public/index.php/user/checkUser/"+Constants.CURRENT_RESULT.getUserInfo().getUserId()
                +"/"+Constants.CURRENT_RESULT.getUserInfo().getDisplayableId();

        JsonObjectRequest jsObjectRequest = new JsonObjectRequest(Request.Method.GET, getUserUri, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String currentUserId = Long.toString(response.getLong("id"));

                            PublicUtil.putStringToSP(MainActivity.this, "userId", currentUserId);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                handleError(error.toString());
            }
        }){//here before semicolon ; and use { }.
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                // return super.getHeaders();
                HashMap<String, String> header = new HashMap<String, String>(super.getHeaders());

                header.put(Constants.HEADER_AUTHORIZATION, Constants.HEADER_AUTHORIZATION_VALUE_PREFIX + Constants.CURRENT_RESULT.getAccessToken());
                return header;
            }

            @Override
            public String getBodyContentType() {
                return super.getBodyContentType();
            }
        };
        jsObjectRequest.setTag(TAG);
        jsObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                HTTP_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsObjectRequest);
    }


    // authentication end
    private void handleError(String errInfo){
        if(errInfo == null || errInfo.equals("")){
            errInfo = "Something wrong...";
        }
        Toast.makeText(this,
                errInfo, Toast.LENGTH_LONG).show();
    }

}
