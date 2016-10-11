/*++

Module Name:

ActionEntryActivity.java

Abstract:

This class is collect data related to basic activity.

Environment:

Android

Copyright (C) 2016 Vesa Eskola.

--*/
package fi.vesaeskola.vehicledatabase;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import android.app.Activity;
import android.view.View;
import android.widget.EditText;

import java.util.Calendar;
import java.util.Date;

import database.DBEngine;

import static android.content.ContentValues.TAG;

/**
 * Created by vesae on 11.10.2016.
 */

public class ActionEntryActivity extends AppCompatActivity {
    protected DBEngine mDatabaseEngine;
    protected int mVehicleId;
    public long mDateLong;
    public EditText mMileage, mPrice, mDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // SQLiteOpenHelper based helper to open or create database.
        // Note: Will delete existing data if new database structure is used.
        mDatabaseEngine = new DBEngine(this);

        Bundle bundle = getIntent().getExtras();
        mVehicleId = bundle.getInt("vehicle_Id");
        Log.d(TAG, "VehicleId: " + mVehicleId);

        final Calendar calendar = Calendar.getInstance();
        Date now = calendar.getTime();
        mDateLong = now.getTime();

        mMileage = (EditText) findViewById(R.id.mileage);
        mPrice = (EditText) findViewById(R.id.price);
        mDescription = (EditText) findViewById(R.id.description);
    }

    public void showDatePickerDialog(View view) {
        DialogFragment newFragment = new DatePickerFragment();
        android.app.FragmentManager fragMan = getFragmentManager();
        newFragment.show(fragMan, "datePicker");
    }
}
