/*++

Module Name:

VehicleInfoActivity.java

Abstract:

This class is used to show all vehicle information to user.

Environment:

Android

Copyright (C) 2016 Vesa Eskola.

--*/

package fi.vesaeskola.vehicledatabase;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.PopupMenu.OnMenuItemClickListener;

import java.text.SimpleDateFormat;
import java.util.Date;

import database.DBEngine;
import database.VehicleContract;

public class VehicleInfoActivity extends AppCompatActivity implements OnMenuItemClickListener {
    private static final String TAG = "VehicleInfoActivity";
    private DBEngine mDatabaseEngine;
    private int mVehicleId;

    static final int FUELING_ENTRY_ACTION_REQUEST = 1;
    static final int SERVICE_ENTRY_ACTION_REQUEST = 2;
    static final int EVENT_ENTRY_ACTION_REQUEST = 3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_info);

        Bundle bundle = getIntent().getExtras();
        mVehicleId = bundle.getInt("vehicle_Id");
        mVehicleId = mVehicleId + 1;
        Log.d(TAG, "Vehicle info to show: " + mVehicleId);

        // SQLiteOpenHelper based helper to open or create database.
        // Note: Will delete existing data if new database structure is used.
        mDatabaseEngine = new DBEngine(this);

        SQLiteDatabase db = mDatabaseEngine.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + VehicleContract.VehicleEntry.TABLE + " WHERE _id = " + mVehicleId;
        Log.d(TAG, "selectQuery: " + selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);

        TextView tvMake = (TextView) findViewById(R.id.vehicle_make);
        TextView tvModel = (TextView) findViewById(R.id.vehicle_model);
        TextView tvPlate = (TextView) findViewById(R.id.vehicle_regplate);
        //TextView tvYear = (TextView) findViewById(R.id.vehicle_year);
        TextView tvVinCode = (TextView) findViewById(R.id.vehicle_vincode);
        TextView tvDescription = (TextView) findViewById(R.id.vehicle_description);

        while (cursor.moveToNext()) {
            tvMake.setText(cursor.getString(cursor.getColumnIndex(VehicleContract.VehicleEntry.COL_MAKE)));
            tvModel.setText(cursor.getString(cursor.getColumnIndex(VehicleContract.VehicleEntry.COL_MODEL)));
            tvPlate.setText(cursor.getString(cursor.getColumnIndex(VehicleContract.VehicleEntry.COL_REGPLATE)));
            //tvYear.setText(cursor.getString(cursor.getColumnIndex(VehicleContract.VehicleEntry.COL_YEAR)));
            tvVinCode.setText(cursor.getString(cursor.getColumnIndex(VehicleContract.VehicleEntry.COL_VINCODE)));
            tvDescription.setText(cursor.getString(cursor.getColumnIndex(VehicleContract.VehicleEntry.COL_DESCRIPTION)));
        }

        readLastFuelingInfoRow();
        readLastServiceInfoRow();
        readLastEventInfoRow();
    }

    private void readLastFuelingInfoRow()
    {
        if (mDatabaseEngine == null) {
            return;
        }

        SQLiteDatabase db = mDatabaseEngine.getReadableDatabase();

        // char* sql = "SELECT * FROM FUELING WHERE VehicleID = ? ORDER by Mileage DESC LIMIT 1";
        String selectQuery = "SELECT  * FROM " + VehicleContract.FuelingEntry.TABLE + " WHERE " + VehicleContract.FuelingEntry.COL_VEHICLEID + " = " + mVehicleId + " ORDER by " + VehicleContract.FuelingEntry.COL_MILEAGE + " DESC LIMIT 1";
        Log.d(TAG, "selectQuery: " + selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);

        TextView tvFuelingCol1 = (TextView) findViewById(R.id.recent_fueling_col1);
        TextView tvFuelingCol2 = (TextView) findViewById(R.id.recent_fueling_col2);
        TextView tvFuelingRow2 = (TextView) findViewById(R.id.recent_fueling_row2);

        if (cursor.moveToNext()) {
            // Refueling, column 1
            tvFuelingCol1.setText(cursor.getString(cursor.getColumnIndex(VehicleContract.FuelingEntry.COL_MILEAGE)) + " mi.");

            // Refueling, column 2
            long fuelingDate = cursor.getLong(cursor.getColumnIndex(VehicleContract.FuelingEntry.COL_DATE));
            SimpleDateFormat simpleDataFormat = new SimpleDateFormat("dd MMM yyyy");
            Date now = new Date();
            now.setTime(fuelingDate);
            tvFuelingCol2.setText(simpleDataFormat.format(now));

            // Refueling, row 2
            String description = cursor.getString(cursor.getColumnIndex(VehicleContract.FuelingEntry.COL_DESCRIPTION));
            if (description.length() < 1 ) {
                tvFuelingRow2.setText(cursor.getString(cursor.getColumnIndex(VehicleContract.FuelingEntry.COL_AMOUNT)) + " ga.");
            }
            else if (description.length() > 26 ) {
                String shortDescription = description.substring(0,26);
                tvFuelingRow2.setText(cursor.getString(cursor.getColumnIndex(VehicleContract.FuelingEntry.COL_AMOUNT)) + " ga. " + shortDescription + "..");
            }
            else
            {
                tvFuelingRow2.setText(cursor.getString(cursor.getColumnIndex(VehicleContract.FuelingEntry.COL_AMOUNT)) + " ga. " + description);
            }
        }
        else
        {
            tvFuelingCol1.setText(R.string.vehicle_info_no_refuelings);
            tvFuelingCol2.setText("");
            tvFuelingRow2.setText("");
        }
    }

    private void readLastServiceInfoRow()
    {
        if (mDatabaseEngine == null) {
            return;
        }

        SQLiteDatabase db = mDatabaseEngine.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + VehicleContract.ServiceEntry.TABLE + " WHERE " + VehicleContract.ServiceEntry.COL_VEHICLEID + " = " + mVehicleId + " ORDER by " + VehicleContract.ServiceEntry.COL_MILEAGE + " DESC LIMIT 1";
        Log.d(TAG, "selectQuery: " + selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);

        TextView tvServiceCol1 = (TextView) findViewById(R.id.recent_service_col1);
        TextView tvServiceCol2 = (TextView) findViewById(R.id.recent_service_col2);
        TextView tvServiceRow2 = (TextView) findViewById(R.id.recent_service_row2);

        if (cursor.moveToNext()) {
            // Service, column 1
            tvServiceCol1.setText(cursor.getString(cursor.getColumnIndex(VehicleContract.ServiceEntry.COL_MILEAGE)) + " mi.");

            // Service, column 2
            long serviceDate = cursor.getLong(cursor.getColumnIndex(VehicleContract.ServiceEntry.COL_DATE));
            SimpleDateFormat simpleDataFormat = new SimpleDateFormat("dd MMM yyyy");
            Date now = new Date();
            now.setTime(serviceDate);
            tvServiceCol2.setText(simpleDataFormat.format(now));

            // Service, row 2
            String description = cursor.getString(cursor.getColumnIndex(VehicleContract.ServiceEntry.COL_DESCRIPTION));
            if (description.length() < 1 ) {
                tvServiceRow2.setText(cursor.getString(cursor.getColumnIndex(VehicleContract.ServiceEntry.COL_PRISE)) + " $");
            }
            else if (description.length() > 32 ) {
                String shortDescription = description.substring(0,32);
                //tvServiceRow2.setText(cursor.getString(cursor.getColumnIndex(VehicleContract.ServiceEntry.COL_PRISE)) + " $ @" + shortDescription);
                tvServiceRow2.setText(shortDescription + "..");

            }
            else
            {
                //tvServiceRow2.setText(cursor.getString(cursor.getColumnIndex(VehicleContract.ServiceEntry.COL_PRISE)) + " $ @" + description);
                tvServiceRow2.setText(description);
            }
        }
        else
        {
            tvServiceCol1.setText(R.string.vehicle_info_no_services);
            tvServiceCol2.setText("");
            tvServiceRow2.setText("");
        }
    }

    private void readLastEventInfoRow()
    {
        if (mDatabaseEngine == null) {
            return;
        }

        SQLiteDatabase db = mDatabaseEngine.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + VehicleContract.EventEntry.TABLE + " WHERE " + VehicleContract.EventEntry.COL_VEHICLEID + " = " + mVehicleId + " ORDER by " + VehicleContract.EventEntry.COL_MILEAGE + " DESC LIMIT 1";
        Log.d(TAG, "selectQuery: " + selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);

        TextView tvEventCol1 = (TextView) findViewById(R.id.recent_event_col1);
        TextView tvEventCol2 = (TextView) findViewById(R.id.recent_event_col2);
        TextView tvEventRow2 = (TextView) findViewById(R.id.recent_event_row2);

        if (cursor.moveToNext()) {
            // Event, column 1
            tvEventCol1.setText(cursor.getString(cursor.getColumnIndex(VehicleContract.EventEntry.COL_MILEAGE)) + " mi.");

            // Event, column 2
            long eventDate = cursor.getLong(cursor.getColumnIndex(VehicleContract.EventEntry.COL_DATE));
            SimpleDateFormat simpleDataFormat = new SimpleDateFormat("dd MMM yyyy");
            Date now = new Date();
            now.setTime(eventDate);
            tvEventCol2.setText(simpleDataFormat.format(now));

            // Event, row 2
            String description = cursor.getString(cursor.getColumnIndex(VehicleContract.EventEntry.COL_DESCRIPTION));
            if (description.length() < 1 ) {
                ;//tvEventRow2.setText(cursor.getString(cursor.getColumnIndex(VehicleContract.EventEntry.COL_PRISE)) + " $");
            }
            else if (description.length() > 32 ) {
                String shortDescription = description.substring(0,32);
                //tvEventRow2.setText(cursor.getString(cursor.getColumnIndex(VehicleContract.EventEntry.COL_PRISE)) + " $ @" + shortDescription);
                tvEventRow2.setText(shortDescription + "..");
            }
            else
            {
                //tvEventRow2.setText(cursor.getString(cursor.getColumnIndex(VehicleContract.EventEntry.COL_PRISE)) + " $ @" + description);
                tvEventRow2.setText(description);

            }
        }
        else
        {
            tvEventCol1.setText(R.string.vehicle_info_no_events );
            tvEventCol2.setText("");
            tvEventRow2.setText("");
        }
    }

    public void onNewFueling(View view) {
        View parent = (View) view.getParent();
        Log.d(TAG, "onNewFueling, selected vehicle: " + mVehicleId);

        // Open FuelingEntryActivity page
        //
        // TBD: There is problem: If user delete one vehicle list id and sql id are not anymore iin sync. Need some
        // to store the VEHICLES table _id column into list view
        Intent intent = new Intent(this, FuelingEntryActivity.class);
        intent.putExtra("vehicle_Id", (int)mVehicleId);
        startActivityForResult(intent, FUELING_ENTRY_ACTION_REQUEST);
    }

    public void onNewService(View view) {
        View parent = (View) view.getParent();
        Log.d(TAG, "onNewService, selected vehicle: " + mVehicleId);

        // Open ServiceEntryActivity page
        //
        // TBD: There is problem: If user delete one vehicle list id and sql id are not anymore iin sync. Need some
        // to store the VEHICLES table _id column into list view
        Intent intent = new Intent(this, ServiceEntryActivity.class);
        intent.putExtra("vehicle_Id", (int)mVehicleId);
        startActivityForResult(intent, SERVICE_ENTRY_ACTION_REQUEST);
    }

    public void onNewEvent(View view) {
        View parent = (View) view.getParent();
        Log.d(TAG, "onNewEvent, selected vehicle: " + mVehicleId);

        // Open EventEntryActivity page
        //
        // TBD: There is problem: If user delete one vehicle list id and sql id are not anymore iin sync. Need some
        // to store the VEHICLES table _id column into list view
        Intent intent = new Intent(this, EventEntryActivity.class);
        intent.putExtra("vehicle_Id", (int)mVehicleId);
        startActivityForResult(intent, EVENT_ENTRY_ACTION_REQUEST);

    }

    public void openMenu(View view) {
        View parent = (View) view.getParent();
        Log.d(TAG, "openMenu, selected vehicle: " + mVehicleId);

        PopupMenu popup = new PopupMenu(this, view);
        popup.setOnMenuItemClickListener(VehicleInfoActivity.this);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.vehicle_info, popup.getMenu());

        popup.show();


    }


    public void openEventList(View view) {
        View parent = (View) view.getParent();
        Log.d(TAG, "openEventList, selected vehicle: " + mVehicleId);

        // Open ActionListActivity page
        //
        // TBD: There is problem: If user delete one vehicle list id and sql id are not anymore iin sync. Need some
        // to store the VEHICLES table _id column into list view
        Intent intent = new Intent(this, ActionListActivity.class);
        intent.putExtra("vehicle_Id", (int)mVehicleId);
        // TBD: Create enumerations for: 1=eventList, 2=serviceList, 3=refuelingList
        intent.putExtra("listType", FUELING_ENTRY_ACTION_REQUEST);
        startActivityForResult(intent, 1);
    }

    public void openServiceList(View view) {
        View parent = (View) view.getParent();
        Log.d(TAG, "openEventList");

        // Open ActionListActivity page
        //
        // TBD: There is problem: If user delete one vehicle list id and sql id are not anymore iin sync. Need some
        // to store the VEHICLES table _id column into list view
        Intent intent = new Intent(this, ActionListActivity.class);
        intent.putExtra("vehicle_Id", (int)mVehicleId);
        // TBD: Create enumerations for: 1=eventList, 2=serviceList, 3=refuelingList
        intent.putExtra("listType", SERVICE_ENTRY_ACTION_REQUEST);
        startActivityForResult(intent, 1);
    }

    public void openFuelingList(View view) {
        View parent = (View) view.getParent();
        Log.d(TAG, "openEventList");

        // Open ActionListActivity page
        //
        // TBD: There is problem: If user delete one vehicle list id and sql id are not anymore iin sync. Need some
        // to store the VEHICLES table _id column into list view
        Intent intent = new Intent(this, ActionListActivity.class);
        intent.putExtra("vehicle_Id", (int)mVehicleId);
        // TBD: Create enumerations for: 1=eventList, 2=serviceList, 3=refuelingList
        intent.putExtra("listType", EVENT_ENTRY_ACTION_REQUEST);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_CANCELED) {
            Log.d(TAG, "Activity result: RESULT_CANCELED");
        }
        else if (resultCode == Activity.RESULT_OK) {
            Log.d(TAG, "Activity result: RESULT_OK");

            switch (requestCode) {
                case FUELING_ENTRY_ACTION_REQUEST: {
                    readLastFuelingInfoRow();
                    break;
                }
                case SERVICE_ENTRY_ACTION_REQUEST: {
                    readLastServiceInfoRow();
                    break;
                }
                case EVENT_ENTRY_ACTION_REQUEST: {
                    readLastEventInfoRow();
                    break;
                }

            }
        }

    }//onActivityResult

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.menuitem_show_privacy_policy: {
                Log.d(TAG, "Menu item selected: Privacy policy");

                new AlertDialog.Builder(this)
                        .setTitle(R.string.privacy_policy_title)
                        .setMessage(R.string.privacy_policy_content)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                Log.d(TAG, "Vehicle delete selected");
                            }})
                        .setNegativeButton(android.R.string.no, null).show();
                return true;
            }

            case R.id.menuitem_delete_vehicle: {
                Log.d(TAG, "Menu item selected: Delete vehicle");

                new AlertDialog.Builder(this)
                        .setTitle(R.string.vehicle_info_delete_vehicle_title)
                        .setMessage(R.string.vehicle_info_delete_vehicle_confirmation)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                Log.d(TAG, "Vehicle delete selected");
                            }})
                        .setNegativeButton(android.R.string.no, null).show();

                return true;
            }

            default: {
                return false;
            }
        }
    }
}
