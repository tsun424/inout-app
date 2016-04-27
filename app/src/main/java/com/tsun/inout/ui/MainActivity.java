package com.tsun.inout.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.tsun.inout.R;
import com.tsun.inout.model.ActivityBean;

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

    static final int PICK_NEW_RESULT = 1;

    private ActivityListFragment activityListFragment;


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

        //Set the fragment initially
        ActivityListFragment fragment = new ActivityListFragment();
        android.support.v4.app.FragmentTransaction fragmentTransaction =
                getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_NEW_RESULT){
            if(resultCode == RESULT_OK){
                activityListFragment = new ActivityListFragment();
                android.support.v4.app.FragmentTransaction fragmentTransaction =
                        getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, activityListFragment).commit();
            }
        }

    }

    @Override
    public void onActivitySelected(ActivityBean activityBean) {
        Intent intent = new Intent(getBaseContext(), BrowseActivityPager.class);
        intent.putExtra("activityBean", activityBean);
        startActivity(intent);
    }
}
