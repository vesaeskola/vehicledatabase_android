/*++

Module Name:

MainActivity.java

Abstract:

This module is the main activity page of the application. Main page contain ListView which
contain vehicle basic data read from database.

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
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import java.util.ArrayList;

import database.DBEngine;
import database.VehicleContract;

public class VehicleListActivity extends AppCompatActivity {
    private static final String TAG = "VehicleListActivity";
    private DBEngine mDatabaseEngine;
    private ListView mVehicleListView;
    private VehicleListAdapter mVehicleAdapter;

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_list);

        // Example of a call to a native method
        //TextView tv = (TextView) findViewById(R.id.sample_text);
        //tv.setText(stringFromJNI());
        mVehicleListView = (ListView) findViewById(R.id.list_vehicle);

        // SQLiteOpenHelper based helper to open or create database.
        // Note: Will delete existing data if new database structure is used.
        mDatabaseEngine = new DBEngine(this);

        updateUI();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menuitem_add_vehicle:
                Log.d(TAG, "menu item clicked: Add new Vechile");

                // Open VehicleEntryBasicActivity page
                Intent intent = new Intent(this, VehicleEntryBasicActivity.class);
                startActivityForResult(intent, 1);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                String make=data.getStringExtra("ret_make");
                String model=data.getStringExtra("ret_model");
                String regplate=data.getStringExtra("ret_regplate");
                int year = data.getIntExtra("ret_year", 0);
                String vincode=data.getStringExtra("ret_vincode");
                String description=data.getStringExtra("ret_description");

                Log.d(TAG, "New vechile entered by user: " + make + " " + model + " " + regplate + " " + year + " " + vincode + " " + description);

                // Open or create database file
                SQLiteDatabase db = mDatabaseEngine.getWritableDatabase();

                ContentValues values = new ContentValues();
                values.put(VehicleContract.VehicleEntry.COL_VINCODE, vincode);
                values.put(VehicleContract.VehicleEntry.COL_MAKE, make);
                values.put(VehicleContract.VehicleEntry.COL_MODEL, model);
                values.put(VehicleContract.VehicleEntry.COL_YEAR, year);
                values.put(VehicleContract.VehicleEntry.COL_REGPLATE, regplate);
                values.put(VehicleContract.VehicleEntry.COL_DESCRIPTION, description);
                values.put(VehicleContract.VehicleEntry.COL_FUEL_UNIT_ID, 1);
                values.put(VehicleContract.VehicleEntry.COL_ODOMETER_UNIT_ID, 1);
                values.put(VehicleContract.VehicleEntry.COL_IMAGEPATH, "null");

                db.insertWithOnConflict(VehicleContract.VehicleEntry.TABLE,
                        null,
                        values,
                        SQLiteDatabase.CONFLICT_REPLACE);
                db.close();

                Log.d(TAG, "New vechile entered to database");

                updateUI();

            }
            if (resultCode == Activity.RESULT_CANCELED) {
                Log.d(TAG, "New vechile entering cancelled");
            }
        }
    }//onActivityResult

    private void updateUI() {
        Log.d(TAG, "updateUI: Select * from VEHICLES table");
        ArrayList<VehicleListItem> vehicleList = new ArrayList<VehicleListItem>();
        // Open existing database file
        SQLiteDatabase db = mDatabaseEngine.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + VehicleContract.VehicleEntry.TABLE;
        Log.d(TAG, "selectQuery: " + selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);

        while (cursor.moveToNext()) {
            VehicleListItem vehicle = new VehicleListItem("make", "model", "regplate", "vincode");
            vehicle.make = cursor.getString(cursor.getColumnIndex(VehicleContract.VehicleEntry.COL_MAKE));
            vehicle.model = cursor.getString(cursor.getColumnIndex(VehicleContract.VehicleEntry.COL_MODEL));
            vehicle.regplate = cursor.getString(cursor.getColumnIndex(VehicleContract.VehicleEntry.COL_REGPLATE));
            vehicle.vincode = cursor.getString(cursor.getColumnIndex(VehicleContract.VehicleEntry.COL_VINCODE));

            vehicleList.add(vehicle);
            Log.d(TAG, "Vehicle read from database: " + vehicle.make + "  " + vehicle.model + "  " + vehicle.regplate + "  " + vehicle.vincode);
        }

        if (mVehicleListView != null) {
            if (mVehicleAdapter == null) {
                mVehicleAdapter = new VehicleListAdapter(this, vehicleList);
                mVehicleListView.setAdapter(mVehicleAdapter);
            } else {
                mVehicleAdapter.clear();
                mVehicleAdapter.addAll(vehicleList);
                mVehicleAdapter.notifyDataSetChanged();
            }
        }
        cursor.close();
        db.close();
    }


    public void openVehicle(View view) {
        View parent = (View) view.getParent();
        Button vehicleOpenBtn = (Button) parent.findViewById(R.id.vehicle_open);
        Log.d(TAG, "Selected vehicle: " + vehicleOpenBtn.getId() + " " + vehicleOpenBtn.getTag() );

        // TBD: There is problem: If user delete one vehicle list id and sql id are not anymore iin sync. Need some
        // to store the VEHICLES table _id column into list view
        // Open VehicleEntryBasicActivity page
        Intent intent = new Intent(this, VehicleInfoActivity.class);
        intent.putExtra("vehicle_Id", (int)vehicleOpenBtn.getTag());
        startActivityForResult(intent, 1);
    }


    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}