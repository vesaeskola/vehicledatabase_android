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
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

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

        // Convert long mDateLong of super class to human readable format into button label
        Button btnDate = (Button) findViewById(R.id.btn_pick_date);
        SimpleDateFormat simpleDataFormat = new SimpleDateFormat("dd MMM yyyy");
        Date now = new Date();
        now.setTime(mDateLong);
        btnDate.setText(simpleDataFormat.format(now));

    }

    public void OnServiceEntered(View view) {
        View parent = (View) view.getParent();
        Log.d(TAG, "OnServiceEntered, selected vehicle: " + mVehicleId);

        // Connect UI widgets to member variables
        //mAmount = (EditText) findViewById(R.id.fueling_amount);

        // Open or create database file
        SQLiteDatabase db = mDatabaseEngine.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(VehicleContract.ServiceEntry.COL_VEHICLEID, mVehicleId);
        values.put(VehicleContract.ServiceEntry.COL_DATE, mDateLong);
        values.put(VehicleContract.ServiceEntry.COL_MILEAGE, mMileage.getText().toString());
        //values.put(VehicleContract.ServiceEntry.COL_AMOUNT, mAmount.getText().toString());
        values.put(VehicleContract.ServiceEntry.COL_PRISE, mPrice.getText().toString());
        values.put(VehicleContract.ServiceEntry.COL_DESCRIPTION, mDescription.getText().toString());

        db.insertWithOnConflict(VehicleContract.ServiceEntry.TABLE,
                null,
                values,
                SQLiteDatabase.CONFLICT_REPLACE);
        db.close();

        Log.d(TAG, "New service entered to database");

        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }
}
