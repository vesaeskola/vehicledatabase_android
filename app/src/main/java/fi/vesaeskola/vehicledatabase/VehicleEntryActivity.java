/*++

Module Name:

VehicleEntryActivity.java

Abstract:

This module is user to capture vehicle basic information (make, model, regplate, vincode) from user.

Environment:

Android

Copyright � 2016 Vesa Eskola.

--*/
package fi.vesaeskola.vehicledatabase;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class VehicleEntryActivity extends AppCompatActivity {

    public EditText make, model, regplate, vincode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_entry);
    }


    public void OnVehicleCanceled(View view) {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED,returnIntent);
        finish();
    }

    public void OnVehicleEntered(View view) {
        make = (EditText) findViewById(R.id.vehicle_make);
        model = (EditText) findViewById(R.id.vehicle_model);
        regplate = (EditText) findViewById(R.id.vehicle_regplate);
        vincode = (EditText) findViewById(R.id.vehicle_vincode);

        Intent returnIntent = new Intent();
        returnIntent.putExtra("ret_make", make.getText().toString() );
        returnIntent.putExtra("ret_model", model.getText().toString() );
        returnIntent.putExtra("ret_regplate", regplate.getText().toString() );
        returnIntent.putExtra("ret_vincode", vincode.getText().toString() );
        setResult(Activity.RESULT_OK,returnIntent);
        finish();
    }
}

