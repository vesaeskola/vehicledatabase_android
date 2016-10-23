/*++

Module Name:

ServiceEntryActivity.java

Abstract:

This class is collect data related to service.

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
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import database.VehicleContract;

public class ServiceEntryActivity extends ActionEntryActivity {
    private static final String TAG = "ServiceEntryActivity";
    // TBD: Somekind of checkbox widget---public EditText mAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_service_entry);
        super.onCreate(savedInstanceState);

        TextView title = (TextView)findViewById(R.id.title);
        TextView textDate = (TextView) findViewById(R.id.date_text);

        if (mVehicleId != -1) {
            title.setText(R.string.service_entry_new_service);

            SimpleDateFormat simpleDataFormat = new SimpleDateFormat("dd MMM yy");
            Date now = new Date();
            now.setTime(mDateLong);
            textDate.setText(simpleDataFormat.format(now));
        } else if (mActionId != -1) {
            title.setText(R.string.service_entry_edit_service);

            String selectQuery = "SELECT  * FROM " + VehicleContract.ServiceEntry.TABLE + " WHERE " + VehicleContract.ServiceEntry._ID + " = " + mActionId;
            Log.d(TAG, "edit existing service: selectQuery: " + selectQuery);

            // Open or create database file
            SQLiteDatabase db = mDatabaseEngine.getWritableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);

            if (cursor.moveToNext()) {
                String sExpense = VehileDatabaseApplication.ConvertIntToPlatformString(cursor.getInt(cursor.getColumnIndex(VehicleContract.FuelingEntry.COL_EXPENSE)));
                mExpense.setText(sExpense);

                int iMileage = cursor.getInt(cursor.getColumnIndex(VehicleContract.ServiceEntry.COL_MILEAGE));
                mMileage.setText(String.valueOf(iMileage));

                mDescription.setText(cursor.getString(cursor.getColumnIndex(VehicleContract.ServiceEntry.COL_DESCRIPTION)));
                mDateLong = cursor.getLong(cursor.getColumnIndex(VehicleContract.ServiceEntry.COL_DATE));
                SimpleDateFormat simpleDataFormat = new SimpleDateFormat("dd MMM yy");
                Date now = new Date();
                now.setTime(mDateLong);
                textDate.setText(simpleDataFormat.format(now));
            }
        }
    }

    public void OnServiceEntered(View view) {
        View parent = (View) view.getParent();
        Log.d(TAG, "OnServiceEntered, selected vehicle: " + mVehicleId);

        // Open or create database file
        SQLiteDatabase db = mDatabaseEngine.getWritableDatabase();

        ContentValues values = new ContentValues();

        if (mVehicleId != -1) {
            values.put(VehicleContract.ServiceEntry.COL_VEHICLEID, mVehicleId);
            values.put(VehicleContract.ServiceEntry.COL_DATE, mDateLong);
            int iMileage = VehileDatabaseApplication.ConvertStringToIntNoRound(mMileage.getText().toString());
            values.put(VehicleContract.ServiceEntry.COL_MILEAGE, iMileage);
            int expense = VehileDatabaseApplication.ConvertStringToInt(mExpense.getText().toString());
            values.put(VehicleContract.ServiceEntry.COL_EXPENSE, expense);
            values.put(VehicleContract.ServiceEntry.COL_DESCRIPTION, mDescription.getText().toString());

            db.insertWithOnConflict(VehicleContract.ServiceEntry.TABLE,
                    null,
                    values,
                    SQLiteDatabase.CONFLICT_REPLACE);
            db.close();

            Log.d(TAG, "New service entered to database");
        } else if (mActionId != -1) {
            values.put(VehicleContract.ServiceEntry.COL_DATE, mDateLong);
            int iMileage = VehileDatabaseApplication.ConvertStringToIntNoRound(mMileage.getText().toString());
            values.put(VehicleContract.ServiceEntry.COL_MILEAGE, iMileage);
            int expense = VehileDatabaseApplication.ConvertStringToInt(mExpense.getText().toString());
            values.put(VehicleContract.ServiceEntry.COL_EXPENSE, expense);
            values.put(VehicleContract.ServiceEntry.COL_DESCRIPTION, mDescription.getText().toString());

            db.update(VehicleContract.ServiceEntry.TABLE, values, VehicleContract.ServiceEntry._ID + " = " + mActionId, null);
            Log.d(TAG, "Existing service updated from database");
        }

        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }
}
