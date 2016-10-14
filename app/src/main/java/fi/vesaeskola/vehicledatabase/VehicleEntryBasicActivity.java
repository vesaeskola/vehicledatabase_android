/*++

Module Name:

VehicleEntryActivity.java

Abstract:

This class is used to collect vehicle basic information from the user (make, model, regplate).

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
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Date;

import database.DBEngine;
import database.VehicleContract;

import static android.content.ContentValues.TAG;

public class VehicleEntryBasicActivity extends AppCompatActivity {
    private static final String TAG = "VehicleEntryBasicAct.";
    private DBEngine mDatabaseEngine;
    public EditText mMake, mModel,mRegplate;
    private int mVehicleId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_entry_basic);

        mDatabaseEngine = new DBEngine(this);

        // Connect UI widgets to member variables
        mMake = (EditText) findViewById(R.id.vehicle_make);
        mModel = (EditText) findViewById(R.id.vehicle_model);
        mRegplate = (EditText) findViewById(R.id.vehicle_regplate);

        Bundle bundle = getIntent().getExtras();
        if (getIntent().hasExtra("vehicle_Id")) {
            mVehicleId = bundle.getInt("vehicle_Id");

            String selectQuery = "SELECT  * FROM " + VehicleContract.VehicleEntry.TABLE + " WHERE " + VehicleContract.VehicleEntry._ID + " = " + mVehicleId;
            Log.d(TAG, "Edit existing vehicle, VehicleId: " + mVehicleId + ", selectQuery: " + selectQuery);

            // Open or create database file
            SQLiteDatabase db = mDatabaseEngine.getWritableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);

            if (cursor.moveToNext()) {
                mMake.setText(cursor.getString(cursor.getColumnIndex(VehicleContract.VehicleEntry.COL_MAKE)));
                mModel.setText(cursor.getString(cursor.getColumnIndex(VehicleContract.VehicleEntry.COL_MODEL)));
                mRegplate.setText(cursor.getString(cursor.getColumnIndex(VehicleContract.VehicleEntry.COL_REGPLATE)));
            }
        }
        else
        {
            mVehicleId = -1;
            Log.d(TAG, "Create a new vehicle");
        }
    }


    public void OnVehicleCanceled(View view) {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();
    }

    public void OnVehicleEntered(View view) {

        // Open VehicleEntryDetailActivity page
        Intent intent = new Intent(this, VehicleEntryDetailActivity.class);
        if (mVehicleId != -1) {
            // Open in edit existing mode
            intent.putExtra("vehicle_Id", mVehicleId);
        }
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {

                Log.d(TAG, "Vechile detail data entered");

                // Open or create database file
                SQLiteDatabase db = mDatabaseEngine.getWritableDatabase();

                int year = data.getIntExtra("ret_year", 0);
                String vincode = data.getStringExtra("ret_vincode");
                String description = data.getStringExtra("ret_description");
                int fuelUnit = data.getIntExtra("ret_fuel_unit", -1);
                int odometerUnit = data.getIntExtra("ret_odometer_unit", -1);

                ContentValues values = new ContentValues();
                values.put(VehicleContract.VehicleEntry.COL_VINCODE, vincode);
                values.put(VehicleContract.VehicleEntry.COL_MAKE, mMake.getText().toString());
                values.put(VehicleContract.VehicleEntry.COL_MODEL, mModel.getText().toString());
                values.put(VehicleContract.VehicleEntry.COL_YEAR, year);
                values.put(VehicleContract.VehicleEntry.COL_REGPLATE, mRegplate.getText().toString());
                values.put(VehicleContract.VehicleEntry.COL_DESCRIPTION, description);
                values.put(VehicleContract.VehicleEntry.COL_FUEL_UNIT_ID, fuelUnit);
                values.put(VehicleContract.VehicleEntry.COL_ODOMETER_UNIT_ID, odometerUnit);

                // TBD: This will be entered later
                //values.put(VehicleContract.VehicleEntry.COL_IMAGEPATH, "null");

                if (mVehicleId == -1) {
                    db.insertWithOnConflict(VehicleContract.VehicleEntry.TABLE,
                            null,
                            values,
                            SQLiteDatabase.CONFLICT_REPLACE);
                    db.close();
                    Log.d(TAG, "New vechile entered to database");
                }
                else {
                    db.update(VehicleContract.VehicleEntry.TABLE , values, VehicleContract.VehicleEntry._ID + " = " + mVehicleId, null);
                    Log.d(TAG, "Existing vehicle updated from database");
                }

              Intent returnIntent = new Intent();
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
            else if (resultCode == Activity.RESULT_CANCELED) {
                Log.d(TAG, "New vechile detail entering cancelled");

                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_CANCELED, returnIntent);
                finish();

            }
        }
    }//onActivityResult

}

