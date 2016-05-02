package com.tsun.inout.ui;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.DatePicker;

import java.util.Calendar;

/**
 *	Date picker fragment
 ************************************************************************
 *	@Author Xiaoming Yang
 *	@Date	21-04-2016 09:00
 ************************************************************************
 *	update time			editor				updated information
 */

public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    private int viewId;      // the view which call it, to recognise which view in the callback method onDateTimeSetListener.onDateTimeSet
    private onDateSelectedListener mCallback;

    public DatePickerFragment(){}

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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (onDateSelectedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        // Do something with the date chosen by the user
        mCallback.onDateSelected(this.viewId, year, month, day);
    }

    /**
     * this method must be called after the object is instantiated
     *
     * @param viewId the view which call it, to recognise which view in the callback method
     *             onDateTimeSetListener.onDateTimeSet
     *
     * @return void
     */
    public void setViewId(int viewId){
        this.viewId = viewId;
    }

    public interface onDateSelectedListener{
        public void onDateSelected(int viewId, int year, int month, int day);
    }
}