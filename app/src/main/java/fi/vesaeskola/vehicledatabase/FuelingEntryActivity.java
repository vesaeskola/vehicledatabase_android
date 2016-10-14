/*++

Module Name:

FuelingEntryActivity.java

Abstract:

This class is collect data related to re-fueling.

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

import database.DBEngine;
import database.VehicleContract;

public class FuelingEntryActivity extends ActionEntryActivity {
    private static final String TAG = "FuelingEntryActivity";
    public EditText mAmount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_fueling_entry);
        super.onCreate(savedInstanceState);

        Button btnDate = (Button) findViewById(R.id.btn_pick_date);

        if (mVehicleId != -1) {
            // Convert long mDateLong of super class to human readable format into button label
            SimpleDateFormat simpleDataFormat = new SimpleDateFormat("dd MMM yyyy");
            Date now = new Date();
            now.setTime(mDateLong);
            btnDate.setText(simpleDataFormat.format(now));
        }
        else if (mActionId != -1) {
            String selectQuery = "SELECT  * FROM " + VehicleContract.FuelingEntry.TABLE + " WHERE " + VehicleContract.FuelingEntry._ID + " = " + mActionId;
            Log.d(TAG, "edit existing action: selectQuery: " + selectQuery);

            // Open or create database file
            SQLiteDatabase db = mDatabaseEngine.getWritableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);

            // Connect UI widgets to member variables
            mAmount = (EditText) findViewById(R.id.fueling_amount);

            // TBD: Change to be if clause
            if (cursor.moveToNext()) {
                mAmount.setText(cursor.getString(cursor.getColumnIndex(VehicleContract.FuelingEntry.COL_AMOUNT)));
                mExpense.setText(cursor.getString(cursor.getColumnIndex(VehicleContract.FuelingEntry.COL_EXPENSE)));
                mMileage.setText(cursor.getString(cursor.getColumnIndex(VehicleContract.FuelingEntry.COL_MILEAGE)));
                mDescription.setText(cursor.getString(cursor.getColumnIndex(VehicleContract.FuelingEntry.COL_DESCRIPTION)));
                mDateLong = cursor.getLong(cursor.getColumnIndex(VehicleContract.FuelingEntry.COL_DATE));
                SimpleDateFormat simpleDataFormat = new SimpleDateFormat("dd MMM yyyy");
                Date now = new Date();
                now.setTime(mDateLong);
                btnDate.setText(simpleDataFormat.format(now));
            }
        }
    }

    public void OnFuelingEntered(View view) {
        View parent = (View) view.getParent();
        Log.d(TAG, "OnFuelingEntered, selected vehicle: " + mVehicleId);

        // Connect UI widgets to member variables
        mAmount = (EditText) findViewById(R.id.fueling_amount);

        // Open or create database file
        SQLiteDatabase db = mDatabaseEngine.getWritableDatabase();

        ContentValues values = new ContentValues();

        if (mVehicleId != -1) {
            values.put(VehicleContract.FuelingEntry.COL_VEHICLEID, mVehicleId);
            values.put(VehicleContract.FuelingEntry.COL_DATE, mDateLong);
            values.put(VehicleContract.FuelingEntry.COL_MILEAGE, mMileage.getText().toString());
            values.put(VehicleContract.FuelingEntry.COL_AMOUNT, mAmount.getText().toString());
            values.put(VehicleContract.FuelingEntry.COL_EXPENSE, mExpense.getText().toString());
            values.put(VehicleContract.FuelingEntry.COL_DESCRIPTION, mDescription.getText().toString());

            db.insertWithOnConflict(VehicleContract.FuelingEntry.TABLE,
                    null,
                    values,
                    SQLiteDatabase.CONFLICT_REPLACE);
            db.close();

            Log.d(TAG, "New fueling entered to database");
        }
        else if (mActionId != -1)
        {
            values.put(VehicleContract.FuelingEntry.COL_DATE, mDateLong);
            values.put(VehicleContract.FuelingEntry.COL_MILEAGE, mMileage.getText().toString());
            values.put(VehicleContract.FuelingEntry.COL_AMOUNT, mAmount.getText().toString());
            values.put(VehicleContract.FuelingEntry.COL_EXPENSE, mExpense.getText().toString());
            values.put(VehicleContract.FuelingEntry.COL_DESCRIPTION, mDescription.getText().toString());

            db.update(VehicleContract.FuelingEntry.TABLE , values, VehicleContract.FuelingEntry._ID + " = " + mActionId, null);

            Log.d(TAG, "Existing fueling updated from database");
        }

        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }
}
