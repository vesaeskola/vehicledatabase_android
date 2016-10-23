/*++

Module Name:

VehicleListActivity.java

Abstract:

This module is the main activity page of the application. Main page contain ListView which
contain vehicle basic data read from database.

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
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;

import database.DBEngine;
import database.VehicleContract;
import utilities.EnginePool;

public class VehicleListActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {
    private static final String TAG = "VehicleListActivity";
    public static String PACKAGE_NAME;
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

        PACKAGE_NAME = getApplicationContext().getPackageName();

        // Example of a call to a native method
        //TextView tv = (TextView) findViewById(R.id.sample_text);
        //tv.setText(stringFromJNI());
        mVehicleListView = (ListView) findViewById(R.id.list_vehicle);
        mVehicleListView.setClickable(true);

        mVehicleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
                VehicleListItem vehicle = (VehicleListItem) mVehicleListView.getItemAtPosition(position);
                Log.d(TAG, "vehicle clicked ID: " + vehicle.mVehicleId);
            }
        });

        // SQLiteOpenHelper based helper to open or create database.
        // Note: Will delete existing data if new database structure is used.
        mDatabaseEngine = (DBEngine)EnginePool.getEngine("DBEngine");

        updateUI();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    public void OnOpenMenu(View view) {
        View parent = (View) view.getParent();
        Log.d(TAG, "onOpenMenu");

        PopupMenu popup = new PopupMenu(this, view);
        popup.setOnMenuItemClickListener(VehicleListActivity.this);
        MenuInflater inflater = popup.getMenuInflater();

        if (mVehicleListView.getAdapter().isEmpty() || mVehicleListView.getSelectedItem() == null) {
            inflater.inflate(R.menu.menu_01, popup.getMenu());
        }
        else {
            inflater.inflate(R.menu.menu_02, popup.getMenu());
        }

        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuitem_edit_vehicle: {
                Log.d(TAG, "Menu item selected: Edit vehicle");

                VehicleListItem vehicle = (VehicleListItem)mVehicleListView.getSelectedItem();
                if (vehicle != null) {
                    // Open VehicleEntryBasicActivity page
                    Intent intent = new Intent(this, VehicleEntryBasicActivity.class);

                    intent.putExtra("vehicle_Id", vehicle.mVehicleId);
                    startActivityForResult(intent, Constants.RequestCode.REQUEST_EDIT_VEHICLE);

                    Log.d(TAG, "open vehicle editor with vehicle ID: " + vehicle.mVehicleId);
                }
                return true;
            }
            case R.id.menuitem_delete_vehicle: {
                Log.d(TAG, "Menu item selected: Delete vehicle");

                VehicleListItem vehicle = (VehicleListItem)mVehicleListView.getSelectedItem();
                if (vehicle != null) {
                mDatabaseEngine.deleteVehicle (vehicle.mVehicleId);
                }
                /*
                new AlertDialog.Builder(this)
                        .setTitle(R.string.vehicle_info_delete_vehicle_title)
                        .setMessage(R.string.vehicle_info_delete_vehicle_confirmation)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                Log.d(TAG, "Vehicle delete selected");
                            }
                        })
                        .setNegativeButton(android.R.string.no, null).show();
                */
                return true;
            }
            case R.id.menuitem_show_privacy_policy: {
                Log.d(TAG, "Menu item selected: Privacy policy");
                /*
                new AlertDialog.Builder(this)
                        .setTitle(R.string.privacy_policy_title)
                        .setMessage(R.string.privacy_policy_content)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                Log.d(TAG, "Vehicle delete selected");
                            }
                        })
                        .setNegativeButton(android.R.string.no, null).show();
                        */
                return true;
            }

            default: {
                return false;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                updateUI();
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.d(TAG, "New vechile entering cancelled");
            }
        }
    }//onActivityResult

    private void updateUI() {
        ArrayList<VehicleListItem> vehicleList = new ArrayList<VehicleListItem>();
        // Open existing database file
        SQLiteDatabase db = mDatabaseEngine.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + VehicleContract.VehicleEntry.TABLE;
        Log.d(TAG, "selectQuery: " + selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);

        while (cursor.moveToNext()) {
            int vehicleID = cursor.getInt(cursor.getColumnIndex(VehicleContract.VehicleEntry._ID));
            VehicleListItem vehicle = new VehicleListItem(vehicleID,
                    cursor.getString(cursor.getColumnIndex(VehicleContract.VehicleEntry.COL_MAKE)),
                    cursor.getString(cursor.getColumnIndex(VehicleContract.VehicleEntry.COL_MODEL)),
                    cursor.getString(cursor.getColumnIndex(VehicleContract.VehicleEntry.COL_REGPLATE)),
                    cursor.getString(cursor.getColumnIndex(VehicleContract.VehicleEntry.COL_VINCODE))
            );

            vehicleList.add(vehicle);
            Log.d(TAG, "Vehicle read from database: " + vehicle.mVehicleId + " " + vehicle.make + "  " + vehicle.model + "  " + vehicle.regplate + "  " + vehicle.vincode);
        }

        TextView tvWellcomeMessage = (TextView) findViewById(R.id.add_vehicle_tip_text);
        ImageView ivWellcomeMessage = (ImageView) findViewById(R.id.add_vehicle_tip);

        if (vehicleList.isEmpty())
        {
            tvWellcomeMessage.setVisibility(View.VISIBLE);
            ivWellcomeMessage.setVisibility(View.VISIBLE);
        }
        else
        {
            tvWellcomeMessage.setVisibility(View.GONE);
            ivWellcomeMessage.setVisibility(View.GONE);
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
        Log.d(TAG, "Selected vehicle: " + vehicleOpenBtn.getId() + " " + vehicleOpenBtn.getTag());

        // Open VehicleEntryBasicActivity page
        Intent intent = new Intent(this, VehicleInfoActivity.class);
        intent.putExtra("vehicle_Id", (int) vehicleOpenBtn.getTag());
        startActivityForResult(intent, 1);
    }


    public void onNewVehicle (View view) {
        Log.d(TAG, "onNewVehicle");

        // Open VehicleEntryBasicActivity page
        Intent intent = new Intent(this, VehicleEntryBasicActivity.class);
        startActivityForResult(intent, 1);
    }
    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}