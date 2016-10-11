/*++

Module Name:

VehicleEntryDetailActivity.java

Abstract:

This class is used to capture vehicle detail information (vincode, description, units) from user.

Environment:

Android

Copyright (C) 2016 Vesa Eskola.

--*/

package fi.vesaeskola.vehicledatabase;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class VehicleEntryDetailActivity extends AppCompatActivity {
    public EditText year, vincode, description;
    private static final String TAG = "VehicleEntryDetailAct.";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_entry_detail);
    }

    public void OnVehicleDetailEntered(View view) {

        Log.d(TAG, "New vechile detail entered");

        // Connect UI widgets to member variables
        description = (EditText) findViewById(R.id.vehicle_description);
        year = (EditText) findViewById(R.id.vehicle_year);
        vincode = (EditText) findViewById(R.id.vehicle_vincode);

        Intent returnIntent = new Intent();

        returnIntent.putExtra("ret_year", Integer.valueOf(year.getText().toString()));
        returnIntent.putExtra("ret_vincode", vincode.getText().toString());
        returnIntent.putExtra("ret_description", description.getText().toString());
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    public void OnVehicleCanceled(View view) {
        Log.d(TAG, "New vechile detail entering cancelled");
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED,returnIntent);
        finish();
    }

}
