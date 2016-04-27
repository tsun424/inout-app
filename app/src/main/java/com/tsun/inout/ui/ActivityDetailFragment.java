package com.tsun.inout.ui;

import android.content.Intent;
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

import java.util.ArrayList;

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
    private TextView tvTimeOut;
    private TextView tvTimeIn;
    private TextView tvActivityType;
    private TextView tvStatus;
    private TextView tvContact;
    private TextView tvGroups;
    private TextView tvComments;
    private TextView tvWorkingAlone;
    private TextView tvIsRepeat;

    public static final String YES = "YES";
    public static final String NO = "NO";
    public static final int PICK_EDIT_RESULT = 2;

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
        tvTimeOut = (TextView)rootView.findViewById(R.id.tv_time_out);
        tvTimeIn = (TextView)rootView.findViewById(R.id.tv_time_in);
        tvActivityType = (TextView)rootView.findViewById(R.id.tv_activity_type);
        tvStatus = (TextView)rootView.findViewById(R.id.tv_status);
        tvContact = (TextView)rootView.findViewById(R.id.tv_contact);
        tvGroups = (TextView)rootView.findViewById(R.id.tv_groups);
        tvComments = (TextView)rootView.findViewById(R.id.tv_comments);
        tvWorkingAlone = (TextView)rootView.findViewById(R.id.tv_working_alone);
        tvIsRepeat = (TextView)rootView.findViewById(R.id.tv_is_repeat);

        renderFragment();
        return rootView;
    }

    private void renderFragment(){

        tvTimeOut.setText(activityBean.getStartDateTime());
        tvTimeIn.setText(activityBean.getEndDateTime());
        tvActivityType.setText(activityBean.getActivityType());
        tvStatus.setText(activityBean.getStatus());
        tvContact.setText(activityBean.getContact());
        tvGroups.setText(activityBean.getGroupName());
        tvComments.setText(activityBean.getComments());
        if(activityBean.getIsWorkingAlone() == 1){
            tvWorkingAlone.setText(YES);
        }else{
            tvWorkingAlone.setText(NO);
        }
        if(activityBean.getIsRepeat() == 1){
            tvIsRepeat.setText(YES);
        }else{
            tvIsRepeat.setText(NO);
        }
    }

    private class OnMenuIemClick implements Toolbar.OnMenuItemClickListener{

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()){
                case R.id.menu_act_edit:
                    Intent intent = new Intent(getActivity().getBaseContext(), EditActivity.class);
                    intent.putExtra("activityBean", activityBean);
                    startActivityForResult(intent, PICK_EDIT_RESULT);
                    break;
            }
            return true;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_EDIT_RESULT){
            if(resultCode == getActivity().RESULT_OK){
                activityBean = (ActivityBean)data.getExtras().getParcelable("updatedBean");
                renderFragment();
            }
        }

    }

}
