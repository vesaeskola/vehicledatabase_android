/*++

Module Name:

EventEntryActivity.java

Abstract:

This class is collect data related to common event.

Environment:

Android

Copyright (C) 2016 Vesa Eskola.

--*/

package fi.vesaeskola.vehicledatabase;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.text.SimpleDateFormat;
import java.util.Date;

import database.VehicleContract;

public class EventEntryActivity extends ActionEntryActivity {
    private static final String TAG = "ActionEntryActivity";
    // TBD: Somekind of radiobutton widget---public EditText mAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_event_entry);
        super.onCreate(savedInstanceState);

        // Convert long mDateLong of super class to human readable format into button label
        Button btnDate = (Button) findViewById(R.id.btn_pick_date);
        SimpleDateFormat simpleDataFormat = new SimpleDateFormat("dd MMM yyyy");
        Date now = new Date();
        now.setTime(mDateLong);
        btnDate.setText(simpleDataFormat.format(now));
    }

    public void OnEventEntered(View view) {
        View parent = (View) view.getParent();
        Log.d(TAG, "OnEventEntered, selected vehicle: " + mVehicleId);

        // Connect UI widgets to member variables
        //mAmount = (EditText) findViewById(R.id.fueling_amount);

        // Open or create database file
        SQLiteDatabase db = mDatabaseEngine.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(VehicleContract.EventEntry.COL_VEHICLEID, mVehicleId);
        values.put(VehicleContract.EventEntry.COL_DATE, mDateLong);
        values.put(VehicleContract.EventEntry.COL_MILEAGE, mMileage.getText().toString());
        //values.put(VehicleContract.ServiceEntry.COL_AMOUNT, mAmount.getText().toString());
        values.put(VehicleContract.EventEntry.COL_PRISE, mPrice.getText().toString());
        values.put(VehicleContract.EventEntry.COL_DESCRIPTION, mDescription.getText().toString());

        db.insertWithOnConflict(VehicleContract.EventEntry.TABLE,
                null,
                values,
                SQLiteDatabase.CONFLICT_REPLACE);
        db.close();

        Log.d(TAG, "New event entered to database");

        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

}
