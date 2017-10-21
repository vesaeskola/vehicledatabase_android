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
import android.app.FragmentManager;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import database.DBEngine;
import database.VehicleContract;
import utilities.EnginePool;

import static android.content.ContentValues.TAG;

public class VehicleEntryBasicActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {
    private static final String TAG = "VehicleEntryBasicAct.";
    private DBEngine mDatabaseEngine;
    public EditText mMake, mModel, mRegplate;
    private FragmentManager mFm = getFragmentManager();
    private int mVehicleId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_entry_basic);

        //mDatabaseEngine = new DBEngine(this);
        mDatabaseEngine = (DBEngine) EnginePool.getEngine("DBEngine");

        TextView title = (TextView)findViewById(R.id.title);

        // Connect UI widgets to member variables
        mMake = (EditText) findViewById(R.id.vehicle_make);
        mModel = (EditText) findViewById(R.id.vehicle_model);
        mRegplate = (EditText) findViewById(R.id.vehicle_regplate);

        Bundle bundle = getIntent().getExtras();
        if (getIntent().hasExtra("vehicle_Id")) {
            mVehicleId = bundle.getInt("vehicle_Id");

            title.setText(R.string.vehicle_entry_basic_edit_vehicle);

            String selectQuery = "SELECT  * FROM " + VehicleContract.VehicleEntry.TABLE + " WHERE " + VehicleContract.VehicleEntry._ID + " = " + mVehicleId;
            Log.d(TAG, "Edit existing vehicle, VehicleId: " + mVehicleId + ", selectQuery: " + selectQuery);

            // Open or create database file
            SQLiteDatabase db = mDatabaseEngine.getWritableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);

            if (cursor.moveToNext()) {
                mMake.setText(cursor.getString(cursor.getColumnIndex(VehicleContract.VehicleEntry.COL_MAKE)));
                mModel.setText(cursor.getString(cursor.getColumnIndex(VehicleContract.VehicleEntry.COL_MODEL)));
                mRegplate.setText(cursor.getString(cursor.getColumnIndex(VehicleContract.VehicleEntry.COL_REGPLATE)));
                //mVehicleImagePath = cursor.getString(cursor.getColumnIndex(VehicleContract.VehicleEntry.COL_IMAGEPATH));
            }
        } else {
            mVehicleId = -1;
            title.setText(R.string.vehicle_entry_basic_new_vehicle);
            Log.d(TAG, "Create a new vehicle");
        }

        /*
        if (mVehicleImagePath == null) {
            ImageView imageView = (ImageView) findViewById(R.id.klemmari_icon);
            imageView.setVisibility(View.INVISIBLE);
        }
        */
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
        startActivityForResult(intent, Constants.RequestCode.REQUEST_VEHICLE_INFO);
    }

    public void OnOpenMenu(View view) {
        View parent = (View) view.getParent();
        Log.d(TAG, "onOpenMenu");

        PopupMenu popup = new PopupMenu(this, view);
        popup.setOnMenuItemClickListener(VehicleEntryBasicActivity.this);
        MenuInflater inflater = popup.getMenuInflater();

        inflater.inflate(R.menu.menu_02, popup.getMenu());

        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuitem_test: {
                Log.d(TAG, "Menu item selected: Test");

                ConfirmationDialogFragment alertFragment = new ConfirmationDialogFragment();
                alertFragment.configure (Constants.ConfirmationDialogReason.CONF_REASON_TEST, R.drawable.expense_icon3, "Test", "Testing the dialog");
                alertFragment.show(mFm, "");
            }
            case R.id.menuitem_show_privacy_policy: {
                Log.d(TAG, "Menu item selected: Privacy policy");
                return true;
            }

            default: {
                return false;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {

            Log.d(TAG, "OnActivityResult: RESULT_OK");

            /*
            if (requestCode == Constants.RequestCode.REQUEST_IMAGE_CAPTURE) {
                mVehicleImagePath = mCurrentPhotoPath;
                ImageView imageView = (ImageView) findViewById(R.id.attachment_icon);
                imageView.setVisibility(View.VISIBLE);
                super.imageCaptured ();
            }
            else*/ if (requestCode == Constants.RequestCode.REQUEST_VEHICLE_INFO) {
                // Open or create database file
                SQLiteDatabase db = mDatabaseEngine.getWritableDatabase();

                int year = data.getIntExtra("ret_year", 0);
                String vincode = data.getStringExtra("ret_vincode");
                String description = data.getStringExtra("ret_description");
                int fuelUnit = data.getIntExtra("ret_fuel_unit", -1);
                int odometerUnit = data.getIntExtra("ret_odometer_unit", -1);
                /*
                String detImagePath = data.getStringExtra("ret_imagePath");
                if (detImagePath != null) {
                    mVehicleImagePath = detImagePath;
                }
                */


                ContentValues values = new ContentValues();
                values.put(VehicleContract.VehicleEntry.COL_VINCODE, vincode);
                values.put(VehicleContract.VehicleEntry.COL_MAKE, mMake.getText().toString());
                values.put(VehicleContract.VehicleEntry.COL_MODEL, mModel.getText().toString());
                values.put(VehicleContract.VehicleEntry.COL_YEAR, year);
                values.put(VehicleContract.VehicleEntry.COL_REGPLATE, mRegplate.getText().toString());
                values.put(VehicleContract.VehicleEntry.COL_DESCRIPTION, description);
                values.put(VehicleContract.VehicleEntry.COL_FUEL_UNIT_ID, fuelUnit);
                values.put(VehicleContract.VehicleEntry.COL_ODOMETER_UNIT_ID, odometerUnit);
                //values.put(VehicleContract.VehicleEntry.COL_IMAGEPATH, mVehicleImagePath);

                // TBD: This will be entered later
                //values.put(VehicleContract.VehicleEntry.COL_IMAGEPATH, "null");

                if (mVehicleId == -1) {
                    db.insertWithOnConflict(VehicleContract.VehicleEntry.TABLE,
                            null,
                            values,
                            SQLiteDatabase.CONFLICT_REPLACE);
                    db.close();
                    Log.d(TAG, "New vechile entered to database");
                } else {
                    db.update(VehicleContract.VehicleEntry.TABLE, values, VehicleContract.VehicleEntry._ID + " = " + mVehicleId, null);
                    Log.d(TAG, "Existing vehicle updated from database");
                }

                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Log.d(TAG, "New vechile detail entering cancelled");

            Intent returnIntent = new Intent();
            setResult(Activity.RESULT_CANCELED, returnIntent);
            finish();
        }
    }//onActivityResult
}

