package com.tsun.inout.ui;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.DatePicker;

import java.util.Calendar;

/**
 *	This is a date picker dialog, after date is set, the DateTimePickerFragment will be instantiated
 *  to get the final date-time data
 ************************************************************************
 *	@Author Xiaoming Yang
 *	@Date	08-04-2016 11:40
 ************************************************************************
 *	update time			editor				updated information
 */

public class DateToTimeFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    private View view;      // the view which call it, to recognise which view in the callback method onDateTimeSetListener.onDateTimeSet
    private boolean mFirst = true;              // work around solution for solving the bug about timepicker open twice

    public DateToTimeFragment(){}

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        // Do something with the date chosen by the user
        if (mFirst) {
            mFirst = false;
            DateTimePickerFragment timePickerFragment = new DateTimePickerFragment();
            timePickerFragment.showTime(getFragmentManager(), this.getTag(), this.view, year, month, day);
        }
    }

    /**
     * this method must be called after the object is instantiated
     *
     * @param view the view which call it, to recognise which view in the callback method
     *             onDateTimeSetListener.onDateTimeSet
     *
     * @return void
     */
    public void setView(View view){
        this.view = view;
    }
}