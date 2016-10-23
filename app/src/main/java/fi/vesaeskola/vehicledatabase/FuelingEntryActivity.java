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
import android.widget.EditText;
import android.widget.TextView;

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

        TextView title = (TextView)findViewById(R.id.title);
        TextView textDate = (TextView) findViewById(R.id.date_text);
        TextView textRefuelingSymbol = (TextView) findViewById(R.id.refueling_unit);
        mAmount = (EditText) findViewById(R.id.refueling_amount);

        // TBD: Select correct string according user preferencies
        textRefuelingSymbol.setText(R.string.vehicle_entry_detail_liter);


        if (mVehicleId != -1) {
            title.setText(R.string.fueling_entry_new_refueling);

            SimpleDateFormat simpleDataFormat = new SimpleDateFormat("dd MMM yy");
            Date now = new Date();
            now.setTime(mDateLong);
            textDate.setText(simpleDataFormat.format(now));
        } else if (mActionId != -1) {
            // Edit old action
            title.setText(R.string.fueling_entry_edit_refueling);

            String selectQuery = "SELECT  * FROM " + VehicleContract.FuelingEntry.TABLE + " WHERE " + VehicleContract.FuelingEntry._ID + " = " + mActionId;
            Log.d(TAG, "edit existing action: selectQuery: " + selectQuery);

            // Open or create database file
            SQLiteDatabase db = mDatabaseEngine.getWritableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);

            // TBD: Change to be if clause
            if (cursor.moveToNext()) {

                String sAmount = VehileDatabaseApplication.ConvertIntToPlatformString(cursor.getInt(cursor.getColumnIndex(VehicleContract.FuelingEntry.COL_AMOUNT)));
                mAmount.setText(sAmount);
                String sExpense = VehileDatabaseApplication.ConvertIntToPlatformString(cursor.getInt(cursor.getColumnIndex(VehicleContract.FuelingEntry.COL_EXPENSE)));
                mExpense.setText(sExpense);
                int iMileage = cursor.getInt(cursor.getColumnIndex(VehicleContract.FuelingEntry.COL_MILEAGE));
                mMileage.setText(String.valueOf(iMileage));
                mDescription.setText(cursor.getString(cursor.getColumnIndex(VehicleContract.FuelingEntry.COL_DESCRIPTION)));
                mDateLong = cursor.getLong(cursor.getColumnIndex(VehicleContract.FuelingEntry.COL_DATE));
                SimpleDateFormat simpleDataFormat = new SimpleDateFormat("dd MMM yy");
                Date now = new Date();
                now.setTime(mDateLong);
                textDate.setText(simpleDataFormat.format(now));
            }
        }
    }

    public void OnFuelingEntered(View view) {
        View parent = (View) view.getParent();
        Log.d(TAG, "OnFuelingEntered, selected vehicle: " + mVehicleId);

        // Open or create database file
        SQLiteDatabase db = mDatabaseEngine.getWritableDatabase();

        ContentValues values = new ContentValues();

        if (mVehicleId != -1) {
            values.put(VehicleContract.FuelingEntry.COL_VEHICLEID, mVehicleId);
            values.put(VehicleContract.FuelingEntry.COL_DATE, mDateLong);

            int iMileage = VehileDatabaseApplication.ConvertStringToIntNoRound(mMileage.getText().toString());
            values.put(VehicleContract.FuelingEntry.COL_MILEAGE, iMileage);

            int amount = VehileDatabaseApplication.ConvertStringToInt(mAmount.getText().toString());
            values.put(VehicleContract.FuelingEntry.COL_AMOUNT, amount);

            int expense = VehileDatabaseApplication.ConvertStringToInt(mExpense.getText().toString());
            values.put(VehicleContract.FuelingEntry.COL_EXPENSE, expense);

            values.put(VehicleContract.FuelingEntry.COL_DESCRIPTION, mDescription.getText().toString());

            db.insertWithOnConflict(VehicleContract.FuelingEntry.TABLE,
                    null,
                    values,
                    SQLiteDatabase.CONFLICT_REPLACE);
            db.close();

            Log.d(TAG, "New fueling entered to database");
        } else if (mActionId != -1) {
            values.put(VehicleContract.FuelingEntry.COL_DATE, mDateLong);
            values.put(VehicleContract.FuelingEntry.COL_MILEAGE, mMileage.getText().toString());
            int amount = VehileDatabaseApplication.ConvertStringToInt(mAmount.getText().toString());
            values.put(VehicleContract.FuelingEntry.COL_AMOUNT, amount);
            int expense = VehileDatabaseApplication.ConvertStringToInt(mExpense.getText().toString());
            values.put(VehicleContract.FuelingEntry.COL_EXPENSE, expense);
            values.put(VehicleContract.FuelingEntry.COL_DESCRIPTION, mDescription.getText().toString());

            db.update(VehicleContract.FuelingEntry.TABLE, values, VehicleContract.FuelingEntry._ID + " = " + mActionId, null);

            Log.d(TAG, "Existing fueling updated from database");
        }

        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }
}
