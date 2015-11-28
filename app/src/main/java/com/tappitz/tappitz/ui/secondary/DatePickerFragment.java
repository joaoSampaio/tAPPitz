package com.tappitz.tappitz.ui.secondary;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    private DatePicked datePicked;
    private int year, month, day;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker

        if(year == 0) {
            final Calendar c = Calendar.getInstance();
            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);
        }
        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        // Do something with the date chosen by the user
        if(getDatePicked() != null){
            datePicked.onDatePicked(year, month, day);
        }
    }


    public DatePicked getDatePicked() {
        return datePicked;
    }

    public void setDatePicked(DatePicked datePicked) {
        this.datePicked = datePicked;
    }

    public interface DatePicked{
        public void onDatePicked(int year, int month, int day);
    }

    public void setDate(int year, int month, int day){
        this.year = year;
        this.month = month;
        this.day = day;
    }

}