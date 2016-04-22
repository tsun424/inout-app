package com.tsun.inout.ui;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 *	will be instantiated by DateToTimeFragment, user choose time from this widget
 ************************************************************************
 *	@Author Xiaoming Yang
 *	@Date	21-04-2016 09:00
 ************************************************************************
 *	update time			editor				updated information
 */
public class DateTimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    // get date data from last DateToTimeFragment
    private int year;
    private int month;
    private int day;
    // the view which call it, to recognise which view in the callback method onDateTimeSetListener.onDateTimeSet
    private View view;

    private onDateTimeSetListener mCallback;

    public DateTimePickerFragment(){}

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        mCallback.onDateTimeSet(this.view, this.year, this.month, this.day, hourOfDay, minute);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (onDateTimeSetListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    public void showTime(FragmentManager manager, String tag, View view, int year, int month, int day){

            this.year = year;
            this.month = month;
            this.day = day;
            this.view = view;
            this.show(manager, tag);
    }

    public interface onDateTimeSetListener{
        public void onDateTimeSet(View view, int year, int month, int day, int hourOfDay, int minute);
    }
}
