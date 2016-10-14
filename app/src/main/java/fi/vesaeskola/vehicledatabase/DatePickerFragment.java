/*++

Module Name:

DatePickerFragment.java

Abstract:

This class is used to show date picker dialog for the user

Environment:

Android

Copyright (C) 2016 Vesa Eskola.

--*/
package fi.vesaeskola.vehicledatabase;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class DatePickerFragment extends DialogFragment
    implements DatePickerDialog.OnDateSetListener {
    public int mYear = -1, mMonth = -1, mDay = -1;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Calendar calendar = Calendar.getInstance();

        if (mYear == -1 || mMonth == -1 || mDay == -1) {
            // Use the current date as the default date in the picker if caller have
            // not provided other date
            //
            mYear = calendar.get(Calendar.YEAR);
            mMonth = calendar.get(Calendar.MONTH);
            mDay = calendar.get(Calendar.DAY_OF_MONTH);
        }
        //SimpleDateFormat month_date = new SimpleDateFormat("MMM");
        //String month_name = month_date.format(calendar.getTime());

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, mYear, mMonth, mDay);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
    // Do something with the date chosen by the user
        Button btnDate = (Button)getActivity().findViewById(R.id.btn_pick_date);


        final Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);

        // Set entered date into button label
        SimpleDateFormat simpleDataFormat = new SimpleDateFormat("dd MMM yyyy");
        String strDate = simpleDataFormat.format(calendar.getTime());
        btnDate.setText(strDate);

        // Store entered date into member variable of caller activity
        ActionEntryActivity parentActivity = (ActionEntryActivity)getActivity();
        parentActivity.mDateLong = calendar.getTime().getTime();
    }

    public void setDate (long longDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(longDate));
        mYear = cal.get(Calendar.YEAR);
        mMonth = cal.get(Calendar.MONTH);
        mDay = cal.get(Calendar.DAY_OF_MONTH);
    }

}
