package com.tsun.inout.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tsun.inout.R;
import com.tsun.inout.model.ActivityBean;

/**
 *	browse details of an activity
 ************************************************************************
 *	@Author Xiaoming Yang
 *	@Date	16-04-2016 11:43
 ************************************************************************
 *	update time			editor				updated information
 */

public class ActivityDetailFragment extends Fragment {

    private ActivityBean activityBean;
    private TextView tv_time_out;
    private TextView tv_time_in;
    private TextView tv_activity_type;
    private TextView tv_status;
    private TextView tv_contact;
    private TextView tv_groups;
    private TextView tv_comments;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.activity_act_details, container, false);
        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.act_details_toolbar);
        toolbar.setTitle(R.string.activity_details);
        toolbar.setNavigationIcon(R.drawable.btn_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        toolbar.inflateMenu(R.menu.activity_detials);
        toolbar.setOnMenuItemClickListener(new OnMenuIemClick());

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            activityBean = (ActivityBean) bundle.getParcelable("activityBean");
        }
        tv_time_out = (TextView)rootView.findViewById(R.id.tv_time_out);
        tv_time_in = (TextView)rootView.findViewById(R.id.tv_time_in);
        tv_activity_type = (TextView)rootView.findViewById(R.id.tv_activity_type);
        tv_status = (TextView)rootView.findViewById(R.id.tv_status);
        tv_contact = (TextView)rootView.findViewById(R.id.tv_contact);
        tv_groups = (TextView)rootView.findViewById(R.id.tv_groups);
        tv_comments = (TextView)rootView.findViewById(R.id.tv_comments);

        renderFragment();
        return rootView;
    }

    private void renderFragment(){
        tv_time_out.setText(activityBean.getStartTime());
        tv_time_in.setText(activityBean.getEndTime());
        tv_activity_type.setText(activityBean.getActivityType());
        tv_status.setText(activityBean.getStatus());
        tv_contact.setText(activityBean.getContact());
        tv_groups.setText(activityBean.getGroupName());
        tv_comments.setText(activityBean.getComments());
    }

    private class OnMenuIemClick implements Toolbar.OnMenuItemClickListener{

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()){
                case R.id.menu_act_edit:
                    System.out.println("Edit clicked");
                    break;
            }
            return true;
        }
    }

}
