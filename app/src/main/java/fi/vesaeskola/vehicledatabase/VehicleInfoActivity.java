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
import android.widget.Button;
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
    private int mFuelUnitId;        // TBD: Create a class to collect these unit id's etc.
    private int mOdometerUnitId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_info);

        Bundle bundle = getIntent().getExtras();
        mVehicleId = bundle.getInt("vehicle_Id");
        Log.d(TAG, "Vehicle info to show: " + mVehicleId);

        // SQLiteOpenHelper based helper to open or create database.
        // Note: Will delete existing data if new database structure is used.
        mDatabaseEngine = new DBEngine(this);

        updateUI (Constants.ActionType.ACTION_TYPE_VEHICLE);
        updateUI (Constants.ActionType.ACTION_TYPE_FUELINGS);
        updateUI (Constants.ActionType.ACTION_TYPE_SERVICES);
        updateUI (Constants.ActionType.ACTION_TYPE_EVENTS);
    }

    private void updateUI (int requestCode) {

        switch (requestCode) {
            case Constants.ActionType.ACTION_TYPE_FUELINGS: {
                readLastFuelingInfoRow();
                break;
            }
            case Constants.ActionType.ACTION_TYPE_SERVICES: {
                readLastServiceInfoRow();
                break;
            }
            case Constants.ActionType.ACTION_TYPE_EVENTS: {
                readLastEventInfoRow();
                break;
            }
            case Constants.ActionType.ACTION_TYPE_VEHICLE: {
                readVehicleInfo();
                break;
            }
        }
    }

    private void readVehicleInfo () {
        if (mDatabaseEngine == null) {
            Log.d(TAG, "readVehicleInfo: mDatabaseEngine==null");
            return;
        }

        SQLiteDatabase db = mDatabaseEngine.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + VehicleContract.VehicleEntry.TABLE + " WHERE _id = " + mVehicleId;
        Log.d(TAG, "selectQuery: " + selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);

        TextView tvMake = (TextView) findViewById(R.id.vehicle_make);
        TextView tvModel = (TextView) findViewById(R.id.vehicle_model);
        TextView tvPlate = (TextView) findViewById(R.id.vehicle_regplate);
        TextView tvVinCode = (TextView) findViewById(R.id.vehicle_vincode);
        TextView tvDescription = (TextView) findViewById(R.id.vehicle_description);

        if (cursor.moveToNext()) {
            tvMake.setText(cursor.getString(cursor.getColumnIndex(VehicleContract.VehicleEntry.COL_MAKE)));
            tvModel.setText(cursor.getString(cursor.getColumnIndex(VehicleContract.VehicleEntry.COL_MODEL)));
            tvPlate.setText(cursor.getString(cursor.getColumnIndex(VehicleContract.VehicleEntry.COL_REGPLATE)));
            tvVinCode.setText(cursor.getString(cursor.getColumnIndex(VehicleContract.VehicleEntry.COL_VINCODE)));
            String description = cursor.getString(cursor.getColumnIndex(VehicleContract.VehicleEntry.COL_DESCRIPTION));
            mFuelUnitId = cursor.getInt(cursor.getColumnIndex(VehicleContract.VehicleEntry.COL_FUEL_UNIT_ID));
            mOdometerUnitId = cursor.getInt(cursor.getColumnIndex(VehicleContract.VehicleEntry.COL_ODOMETER_UNIT_ID));

            if (description.length() < 1 ) {
                tvDescription.setText("");
            }
            else if (description.length() > 32 ) {
                String shortDescription = description.substring(0,32);
                tvDescription.setText(shortDescription + "..");
            }
            else
            {
                tvDescription.setText(description);
            }
            mFuelUnitId = cursor.getInt(cursor.getColumnIndex(VehicleContract.VehicleEntry.COL_FUEL_UNIT_ID));
            mOdometerUnitId = cursor.getInt(cursor.getColumnIndex(VehicleContract.VehicleEntry.COL_ODOMETER_UNIT_ID));
        }
    }

    private void readLastFuelingInfoRow()
    {
        if (mDatabaseEngine == null) {
            Log.d(TAG, "readVehicleInfo: mDatabaseEngine==null");
            return;
        }

        SQLiteDatabase db = mDatabaseEngine.getReadableDatabase();

        Button btnFuelingMore = (Button) findViewById(R.id.btn_fueling_more);
        btnFuelingMore.setEnabled(false);

        // char* sql = "SELECT * FROM FUELING WHERE VehicleID = ? ORDER by Mileage DESC LIMIT 1";
        String selectQuery = "SELECT  * FROM " + VehicleContract.FuelingEntry.TABLE + " WHERE " + VehicleContract.FuelingEntry.COL_VEHICLEID + " = " + mVehicleId + " ORDER by " + VehicleContract.FuelingEntry.COL_MILEAGE + " DESC LIMIT 1";
        Log.d(TAG, "selectQuery: " + selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);

        TextView tvFuelingCol1 = (TextView) findViewById(R.id.recent_fueling_col1);
        TextView tvFuelingCol2 = (TextView) findViewById(R.id.recent_fueling_col2);
        TextView tvFuelingRow2 = (TextView) findViewById(R.id.recent_fueling_row2);

        if (cursor.moveToNext()) {
            btnFuelingMore.setEnabled(true);

            // Refueling, column 1
            tvFuelingCol1.setText(cursor.getString(cursor.getColumnIndex(VehicleContract.FuelingEntry.COL_MILEAGE))  + " ");
            if (mOdometerUnitId == Constants.OdometerUnitId.ODOMETER_UNIT_MILES) {
                tvFuelingCol1.append(getResources().getString(R.string.vehicle_entry_detail_short_mail));
            } else {
                tvFuelingCol1.append(getResources().getString(R.string.vehicle_entry_detail_short_km));
            }

            // Refueling, column 2
            long fuelingDate = cursor.getLong(cursor.getColumnIndex(VehicleContract.FuelingEntry.COL_DATE));
            SimpleDateFormat simpleDataFormat = new SimpleDateFormat("dd MMM yyyy");
            Date now = new Date();
            now.setTime(fuelingDate);
            tvFuelingCol2.setText(simpleDataFormat.format(now));
            /*
            tvFuelingCol1.setText(cursor.getString(cursor.getColumnIndex(VehicleContract.FuelingEntry.COL_MILEAGE)));
            if (mFuelUnitId == Constants.FuelUnitId.FUEL_UNIT_GALLON) {
                tvFuelingCol1.append(getResources().getString(R.string.vehicle_entry_detail_short_gallon));
            } else {
                tvFuelingCol1.append(getResources().getString(R.string.vehicle_entry_detail_short_liter));
            }
            */

            // Refueling, row 2
            String description = cursor.getString(cursor.getColumnIndex(VehicleContract.FuelingEntry.COL_DESCRIPTION));
            tvFuelingRow2.setText(cursor.getString(cursor.getColumnIndex(VehicleContract.FuelingEntry.COL_AMOUNT)) + " ");
            if (mFuelUnitId == Constants.FuelUnitId.FUEL_UNIT_GALLON) {
                tvFuelingRow2.append(getResources().getString(R.string.vehicle_entry_detail_short_gallon));
            } else {
                tvFuelingRow2.append(getResources().getString(R.string.vehicle_entry_detail_short_liter));
            }

            if (description.length() < 1 ) {
                ;
            }
            else if (description.length() > 32 ) {
                String shortDescription = description.substring(0,32);
                //tvFuelingRow2.setText(cursor.getString(cursor.getColumnIndex(VehicleContract.FuelingEntry.COL_AMOUNT)) + " ga. " + shortDescription + "..");
                tvFuelingRow2.append(" " + shortDescription + "..");
            }
            else
            {
                //tvFuelingRow2.setText(cursor.getString(cursor.getColumnIndex(VehicleContract.FuelingEntry.COL_AMOUNT)) + " ga. " + description);
                tvFuelingRow2.append(" " + description);
            }
        }
        else
        {
            tvFuelingCol1.setText(R.string.vehicle_info_no_fuelings);
            tvFuelingCol2.setText("");
            tvFuelingRow2.setText("");
        }
    }

    private void readLastServiceInfoRow()
    {
        if (mDatabaseEngine == null) {
            Log.d(TAG, "readVehicleInfo: mDatabaseEngine==null");
            return;
        }

        SQLiteDatabase db = mDatabaseEngine.getReadableDatabase();

        Button btnServiceMore = (Button) findViewById(R.id.btn_service_more);
        btnServiceMore.setEnabled(false);

        String selectQuery = "SELECT  * FROM " + VehicleContract.ServiceEntry.TABLE + " WHERE " + VehicleContract.ServiceEntry.COL_VEHICLEID + " = " + mVehicleId + " ORDER by " + VehicleContract.ServiceEntry.COL_MILEAGE + " DESC LIMIT 1";
        Log.d(TAG, "selectQuery: " + selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);

        TextView tvServiceCol1 = (TextView) findViewById(R.id.recent_service_col1);
        TextView tvServiceCol2 = (TextView) findViewById(R.id.recent_service_col2);
        TextView tvServiceRow2 = (TextView) findViewById(R.id.recent_service_row2);

        if (cursor.moveToNext()) {
            btnServiceMore.setEnabled(true);

            // Service, column 1
            //tvServiceCol1.setText(cursor.getString(cursor.getColumnIndex(VehicleContract.ServiceEntry.COL_MILEAGE)) + " mi.");
            tvServiceCol1.setText(cursor.getString(cursor.getColumnIndex(VehicleContract.FuelingEntry.COL_MILEAGE)) + " ");
            if (mOdometerUnitId == Constants.OdometerUnitId.ODOMETER_UNIT_MILES) {
                tvServiceCol1.append(getResources().getString(R.string.vehicle_entry_detail_short_mail));
            } else {
                tvServiceCol1.append(getResources().getString(R.string.vehicle_entry_detail_short_km));
            }

            // Service, column 2
            long serviceDate = cursor.getLong(cursor.getColumnIndex(VehicleContract.ServiceEntry.COL_DATE));
            SimpleDateFormat simpleDataFormat = new SimpleDateFormat("dd MMM yyyy");
            Date now = new Date();
            now.setTime(serviceDate);
            tvServiceCol2.setText(simpleDataFormat.format(now));

            // Service, row 2
            // TBD: Handle currency unit ($, £ €) somehow
            String description = cursor.getString(cursor.getColumnIndex(VehicleContract.ServiceEntry.COL_DESCRIPTION));
            if (description.length() < 1 ) {
                ;//tvServiceRow2.setText(cursor.getString(cursor.getColumnIndex(VehicleContract.ServiceEntry.COL_EXPENSE)) + " $");
            }
            else if (description.length() > 34 ) {
                String shortDescription = description.substring(0,34);
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
            Log.d(TAG, "readVehicleInfo: mDatabaseEngine==null");
            return;
        }

        SQLiteDatabase db = mDatabaseEngine.getReadableDatabase();

        Button btnEventMore = (Button) findViewById(R.id.btn_events_more);
        btnEventMore.setEnabled(false);

        String selectQuery = "SELECT  * FROM " + VehicleContract.EventEntry.TABLE + " WHERE " + VehicleContract.EventEntry.COL_VEHICLEID + " = " + mVehicleId + " ORDER by " + VehicleContract.EventEntry.COL_MILEAGE + " DESC LIMIT 1";
        Log.d(TAG, "selectQuery: " + selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);

        TextView tvEventCol1 = (TextView) findViewById(R.id.recent_event_col1);
        TextView tvEventCol2 = (TextView) findViewById(R.id.recent_event_col2);
        TextView tvEventRow2 = (TextView) findViewById(R.id.recent_event_row2);

        if (cursor.moveToNext()) {
            btnEventMore.setEnabled(true);

            // Event, column 1
            //tvEventCol1.setText(cursor.getString(cursor.getColumnIndex(VehicleContract.EventEntry.COL_MILEAGE)) + " mi.");
            tvEventCol1.setText(cursor.getString(cursor.getColumnIndex(VehicleContract.FuelingEntry.COL_MILEAGE)) + " ");
            if (mOdometerUnitId == Constants.OdometerUnitId.ODOMETER_UNIT_MILES) {
                tvEventCol1.append(getResources().getString(R.string.vehicle_entry_detail_short_mail));
            } else {
                tvEventCol1.append(getResources().getString(R.string.vehicle_entry_detail_short_km));
            }

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
        Intent intent = new Intent(this, FuelingEntryActivity.class);
        intent.putExtra("vehicle_Id", mVehicleId);
        startActivityForResult(intent, Constants.ActionType.ACTION_TYPE_FUELINGS);
    }

    public void onNewService(View view) {
        View parent = (View) view.getParent();
        Log.d(TAG, "onNewService, selected vehicle: " + mVehicleId);

        // Open ServiceEntryActivity page
        //
        Intent intent = new Intent(this, ServiceEntryActivity.class);
        intent.putExtra("vehicle_Id", mVehicleId);
        startActivityForResult(intent, Constants.ActionType.ACTION_TYPE_SERVICES );
    }

    public void onNewEvent(View view) {
        View parent = (View) view.getParent();
        Log.d(TAG, "onNewEvent, selected vehicle: " + mVehicleId);

        // Open EventEntryActivity page
        //
        Intent intent = new Intent(this, EventEntryActivity.class);
        intent.putExtra("vehicle_Id", mVehicleId);
        startActivityForResult(intent, Constants.ActionType.ACTION_TYPE_EVENTS);

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
        Log.d(TAG, "openActionList of events, selected vehicle: " + mVehicleId);

        // Open ActionListActivity page to show all events
        //
        Intent intent = new Intent(this, ActionListActivity.class);
        intent.putExtra("vehicle_Id", mVehicleId);
        intent.putExtra("listType", Constants.ActionType.ACTION_TYPE_EVENTS);
        intent.putExtra("fuelUnitId", mFuelUnitId);
        intent.putExtra("odometerUnitId", mOdometerUnitId);
        startActivityForResult(intent, Constants.ActionType.ACTION_TYPE_EVENTS);
    }

    public void openServiceList(View view) {
        View parent = (View) view.getParent();
        Log.d(TAG, "openActionList of services, selected vehicle: " + mVehicleId);

        // Open ActionListActivity page to show all services
        //
        Intent intent = new Intent(this, ActionListActivity.class);
        intent.putExtra("vehicle_Id", mVehicleId);
        intent.putExtra("listType", Constants.ActionType.ACTION_TYPE_SERVICES);
        startActivityForResult(intent, Constants.ActionType.ACTION_TYPE_SERVICES);
    }

    public void openFuelingList(View view) {
        View parent = (View) view.getParent();
        Log.d(TAG, "openActionList of fuelings, selected vehicle: " + mVehicleId);

        // Open ActionListActivity page to show all refuelings
        //
        Intent intent = new Intent(this, ActionListActivity.class);
        intent.putExtra("vehicle_Id", mVehicleId);
        intent.putExtra("listType", Constants.ActionType.ACTION_TYPE_FUELINGS);
        startActivityForResult(intent, Constants.ActionType.ACTION_TYPE_FUELINGS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_CANCELED) {
            Log.d(TAG, "Activity result: RESULT_CANCELED");
        }
        else if (resultCode == Activity.RESULT_OK) {
            Log.d(TAG, "Activity result: RESULT_OK");
            updateUI (requestCode);
        }

    }//onActivityResult

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.menuitem_edit_vehicle: {
                Log.d(TAG, "Menu item selected: Edit vehicle");

                // Open VehicleEntryBasicActivity page
                Intent intent = new  Intent(this, VehicleEntryBasicActivity.class);

                intent.putExtra("vehicle_Id", mVehicleId);
                startActivityForResult(intent, Constants.ActionType.ACTION_TYPE_VEHICLE);

                Log.d(TAG, "open vehicle editor with vehicle ID: " + mVehicleId);

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

            default: {
                return false;
            }
        }
    }
}
