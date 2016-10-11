/*++

Module Name:

VehicleEntryActivity.java

Abstract:

This class is used to capture vehicle basic information (make, model, regplate) from user.

Environment:

Android

Copyright (C) 2016 Vesa Eskola.

--*/

package fi.vesaeskola.vehicledatabase;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class VehicleEntryBasicActivity extends AppCompatActivity {
    private static final String TAG = "VehicleEntryBasicActivity";
    public EditText make, model,regplate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_entry_basic);
    }


    public void OnVehicleCanceled(View view) {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();
    }

    public void OnVehicleEntered(View view) {

        // Connect UI widgets to member variables
        make = (EditText) findViewById(R.id.vehicle_make);
        model = (EditText) findViewById(R.id.vehicle_model);
        regplate = (EditText) findViewById(R.id.vehicle_regplate);

        // Open VehicleEntryDetailActivity page
        Intent intent = new Intent(this, VehicleEntryDetailActivity.class);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                Log.d(TAG, "New vechile detail entered");

                Intent returnIntent = new Intent();

                int year = data.getIntExtra("ret_year", 0);
                String vincode = data.getStringExtra("ret_vincode");
                String description = data.getStringExtra("ret_description");

                returnIntent.putExtra("ret_make", make.getText().toString());
                returnIntent.putExtra("ret_model", model.getText().toString());
                returnIntent.putExtra("ret_regplate", regplate.getText().toString());
                returnIntent.putExtra("ret_year", year);
                returnIntent.putExtra("ret_vincode", vincode);
                returnIntent.putExtra("ret_description", description);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                Log.d(TAG, "New vechile detail entering cancelled");

                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_CANCELED, returnIntent);
                finish();

            }
        }
    }//onActivityResult

}

