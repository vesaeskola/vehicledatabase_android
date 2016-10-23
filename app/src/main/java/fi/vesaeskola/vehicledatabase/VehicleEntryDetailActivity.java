/*++

Module Name:

VehicleEntryDetailActivity.java

Abstract:

This class is used to collect vehicle detail information from the user (vincode, description,
units) from user.

Environment:

Android

Copyright (C) 2016 Vesa Eskola.

--*/

package fi.vesaeskola.vehicledatabase;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import database.DBEngine;
import database.VehicleContract;
import utilities.EnginePool;

public class VehicleEntryDetailActivity extends AppCompatActivity {
    private static final String TAG = "VehicleEntryDetailAct.";
    private DBEngine mDatabaseEngine;
    public EditText mYear, mVincode, mDescription;
    private int mVehicleId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_entry_detail);

        TextView title = (TextView)findViewById(R.id.title);

        //mDatabaseEngine = new DBEngine(this);
        mDatabaseEngine = (DBEngine) EnginePool.getEngine("DBEngine");

        // Connect UI widgets to member variables
        mYear = (EditText) findViewById(R.id.year);
        mVincode = (EditText) findViewById(R.id.vincode);
        mDescription = (EditText) findViewById(R.id.description);

        Bundle bundle = getIntent().getExtras();
        if (getIntent().hasExtra("vehicle_Id")) {
            mVehicleId = bundle.getInt("vehicle_Id");

            title.setText(R.string.vehicle_entry_basic_edit_vehicle);

            String selectQuery = "SELECT  * FROM " + VehicleContract.VehicleEntry.TABLE + " WHERE " + VehicleContract.VehicleEntry._ID + " = " + mVehicleId;
            Log.d(TAG, "Edit existing vehicle, VehicleId: " + mVehicleId + ", selectQuery: " + selectQuery);

            // Open or create database file
            SQLiteDatabase db = mDatabaseEngine.getWritableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);

            // TBD: Change to be if clause
            if (cursor.moveToNext()) {
                mYear.setText(cursor.getString(cursor.getColumnIndex(VehicleContract.VehicleEntry.COL_YEAR)));
                mVincode.setText(cursor.getString(cursor.getColumnIndex(VehicleContract.VehicleEntry.COL_VINCODE)));
                mDescription.setText(cursor.getString(cursor.getColumnIndex(VehicleContract.VehicleEntry.COL_DESCRIPTION)));

                RadioButton toBeChecked =  (cursor.getInt(cursor.getColumnIndex(VehicleContract.VehicleEntry.COL_FUEL_UNIT_ID)) == Constants.FuelUnitId.FUEL_UNIT_GALLON) ?  (RadioButton)findViewById(R.id.radioButton1) : (RadioButton)findViewById(R.id.radioButton2);
                toBeChecked.setChecked(true);
                toBeChecked =  (cursor.getInt(cursor.getColumnIndex(VehicleContract.VehicleEntry.COL_ODOMETER_UNIT_ID)) == Constants.OdometerUnitId.ODOMETER_UNIT_MILES) ?  (RadioButton)findViewById(R.id.radioButton3) : (RadioButton)findViewById(R.id.radioButton4);
                toBeChecked.setChecked(true);
            }
        }
        else {
            title.setText(R.string.vehicle_entry_basic_new_vehicle);
        }

    }

    public void OnVehicleDetailEntered(View view) {

        Log.d(TAG, "New vechile detail entered");

        // Connect UI widgets to member variables
        //mDescription = (EditText) findViewById(R.id.vehicle_description);
        //mYear = (EditText) findViewById(R.id.vehicle_year);
        //mVincode = (EditText) findViewById(R.id.vehicle_vincode);

        Intent retIntent = new Intent();

        retIntent.putExtra("ret_year", Integer.valueOf(mYear.getText().toString()));
        retIntent.putExtra("ret_vincode", mVincode.getText().toString());
        retIntent.putExtra("ret_description", mDescription.getText().toString());

        int fuelUnit = (((RadioButton) findViewById(R.id.radioButton1)).isChecked()) ? Constants.FuelUnitId.FUEL_UNIT_GALLON : Constants.FuelUnitId.FUEL_UNIT_LITER;
        int odometerUnit = (((RadioButton) findViewById(R.id.radioButton3)).isChecked()) ? Constants.OdometerUnitId.ODOMETER_UNIT_MILES : Constants.OdometerUnitId.ODOMETER_UNIT_KM;
        retIntent.putExtra("ret_fuel_unit", fuelUnit);
        retIntent.putExtra("ret_odometer_unit", odometerUnit);

        setResult(Activity.RESULT_OK, retIntent);
        finish();
    }

    public void OnVehicleCanceled(View view) {
        Log.d(TAG, "New vechile detail entering cancelled");
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();
    }

}
