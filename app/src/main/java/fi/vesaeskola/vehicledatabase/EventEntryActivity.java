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
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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

        if (mVehicleId != -1) {
            SimpleDateFormat simpleDataFormat = new SimpleDateFormat("dd MMM yyyy");
            Date now = new Date();
            now.setTime(mDateLong);
            btnDate.setText(simpleDataFormat.format(now));
        }
        else if (mActionId != -1) {
            String selectQuery = "SELECT  * FROM " + VehicleContract.EventEntry.TABLE + " WHERE " + VehicleContract.EventEntry._ID + " = " + mActionId;
            Log.d(TAG, "edit existing action: selectQuery: " + selectQuery);

            // Open or create database file
            SQLiteDatabase db = mDatabaseEngine.getWritableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);

            if (cursor.moveToNext()) {
                //mAmount.setText(cursor.getString(cursor.getColumnIndex(VehicleContract.EventEntry.COL_EVENTID)));
                mExpense.setText(cursor.getString(cursor.getColumnIndex(VehicleContract.EventEntry.COL_EXPENSE)));
                mMileage.setText(cursor.getString(cursor.getColumnIndex(VehicleContract.EventEntry.COL_MILEAGE)));
                mDescription.setText(cursor.getString(cursor.getColumnIndex(VehicleContract.EventEntry.COL_DESCRIPTION)));
                mDateLong = cursor.getLong(cursor.getColumnIndex(VehicleContract.EventEntry.COL_DATE));
                SimpleDateFormat simpleDataFormat = new SimpleDateFormat("dd MMM yyyy");
                Date now = new Date();
                now.setTime(mDateLong);
                btnDate.setText(simpleDataFormat.format(now));
            }
        }
    }

    public void OnEventEntered(View view) {
        View parent = (View) view.getParent();
        Log.d(TAG, "OnEventEntered, selected vehicle: " + mVehicleId);

        // Open or create database file
        SQLiteDatabase db = mDatabaseEngine.getWritableDatabase();

        ContentValues values = new ContentValues();
        if (mVehicleId != -1) {
            values.put(VehicleContract.EventEntry.COL_VEHICLEID, mVehicleId);
            values.put(VehicleContract.EventEntry.COL_DATE, mDateLong);
            values.put(VehicleContract.EventEntry.COL_MILEAGE, mMileage.getText().toString());
            values.put(VehicleContract.EventEntry.COL_EXPENSE, mExpense.getText().toString());
            values.put(VehicleContract.EventEntry.COL_DESCRIPTION, mDescription.getText().toString());

            db.insertWithOnConflict(VehicleContract.EventEntry.TABLE,
                    null,
                    values,
                    SQLiteDatabase.CONFLICT_REPLACE);
            db.close();

            Log.d(TAG, "New event entered to database");
        }
        else if (mActionId != -1)
        {
            values.put(VehicleContract.EventEntry.COL_DATE, mDateLong);
            values.put(VehicleContract.EventEntry.COL_MILEAGE, mMileage.getText().toString());
            values.put(VehicleContract.EventEntry.COL_EXPENSE, mExpense.getText().toString());
            values.put(VehicleContract.EventEntry.COL_DESCRIPTION, mDescription.getText().toString());

            db.update(VehicleContract.EventEntry.TABLE , values, VehicleContract.EventEntry._ID + " = " + mActionId, null);
            Log.d(TAG, "Existing event updated from database");
        }

        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }
}
