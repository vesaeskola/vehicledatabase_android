/*++

Module Name:

ActionListActivity.java

Abstract:

This module is the action list activity page. Action list page contain ListView which
contain action information. In this context action could be service, re-fueling or common event.

Environment:

Android

Copyright (C) 2016 Vesa Eskola.

--*/
package fi.vesaeskola.vehicledatabase;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Date;
import java.util.Locale;

import database.DBEngine;
import database.VehicleContract;

public class ActionListActivity extends AppCompatActivity {
    private static final String TAG = "VehicleListActivity";
    private DBEngine mDatabaseEngine;
    private ListView mActionListView;
    private ActionListAdapter mActionAdapter;
    private int mVehicleId;
    private int mActionListType;
    private int mFuelUnitId;        // TBD: Create a class to collect these unit id's etc.
    private int mOdometerUnitId;
    private boolean mVehicleModified = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action_list);

        // SQLiteOpenHelper based helper to open or create database.
        // Note: Will delete existing data if new database structure is used.
        mDatabaseEngine = new DBEngine(this);

        mActionListView = (ListView) findViewById(R.id.list_actions);
        mActionListView.setClickable(true);
        mActionListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
            ActionListItem actionItem = (ActionListItem)mActionListView.getItemAtPosition(position);
            Intent intent = null;
            switch (mActionListType)
            {
                case Constants.ActionType.ACTION_TYPE_FUELINGS: {
                    intent = new  Intent(ActionListActivity.this, FuelingEntryActivity.class);
                    break;
                }
                case Constants.ActionType.ACTION_TYPE_SERVICES: {
                    intent = new  Intent(ActionListActivity.this, ServiceEntryActivity.class);
                    break;
                }
                case Constants.ActionType.ACTION_TYPE_EVENTS: {
                    intent = new  Intent(ActionListActivity.this, EventEntryActivity.class);
                    break;
                }
            }
            intent.putExtra("action_Id", actionItem.mActionId);
            startActivityForResult(intent, 1);
            Log.d(TAG, "open action editor with action ID: " + actionItem.mActionId);
            }
        });

        Bundle bundle = getIntent().getExtras();
        mVehicleId = bundle.getInt("vehicle_Id");
        Log.d(TAG, "Vehicle info to show: " + mVehicleId);
        mActionListType = bundle.getInt("listType");
        mFuelUnitId = bundle.getInt("fuelUnitId");
        mOdometerUnitId = bundle.getInt("odometerUnitId");

        updateUI();
    }

    @Override
    public void onBackPressed() {
        // This is the trick to force VehicleInfoActivity UI to be updated
        Intent returnIntent = new Intent();
        if (mVehicleModified) {
            Log.d(TAG, "onBackPressed: vehicle modified");
            setResult(Activity.RESULT_OK, returnIntent);
        }
        else {
            Log.d(TAG, "onBackPressed: vehicle not modified");
            setResult(Activity.RESULT_CANCELED, returnIntent);
        }

        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                Log.d(TAG, "Vechile actions modified, TBD: VehicleListActivity need to refreshed");
                mVehicleModified = true;
                updateUI();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                Log.d(TAG, "Vechile actions not modified");
            }
        }
    }//onActivityResult

    private void updateUI() {
        Log.d(TAG, "updateUI: ActionListType: " +  mActionListType + ", VehicleId: " + mVehicleId);

        ArrayList<ActionListItem> actionList = new ArrayList<ActionListItem>();
        // Open existing database file
        SQLiteDatabase db = mDatabaseEngine.getReadableDatabase();

        String selectQuery = "SELECT * FROM ";

        switch(mActionListType)
        {
            case Constants.ActionType.ACTION_TYPE_FUELINGS: {
                selectQuery = selectQuery + VehicleContract.FuelingEntry.TABLE + " WHERE " + VehicleContract.FuelingEntry.COL_VEHICLEID + " = " + mVehicleId;
                break;
            }
            case Constants.ActionType.ACTION_TYPE_SERVICES: {
                selectQuery = selectQuery + VehicleContract.ServiceEntry.TABLE + " WHERE " + VehicleContract.ServiceEntry.COL_VEHICLEID + " = " + mVehicleId;
                break;
            }
            case Constants.ActionType.ACTION_TYPE_EVENTS: {
                selectQuery = selectQuery + VehicleContract.EventEntry.TABLE + " WHERE " + VehicleContract.EventEntry.COL_VEHICLEID + " = " + mVehicleId;
                break;
            }
        }

        Log.d(TAG, "selectQuery: " + selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);

        while (cursor.moveToNext()) {
            ActionListItem action = new ActionListItem(-1, "r1c1", "r1c2", "r2c1", "r2c2");

            String description = "";

            switch(mActionListType)
            {
                case Constants.ActionType.ACTION_TYPE_FUELINGS: {
                    action.mActionId =  cursor.getInt(cursor.getColumnIndex(VehicleContract.FuelingEntry._ID));

                    if (mOdometerUnitId == Constants.OdometerUnitId.ODOMETER_UNIT_MILES) {
                        action.row1_col1 = cursor.getString(cursor.getColumnIndex(VehicleContract.FuelingEntry.COL_MILEAGE)) + " " + getResources().getString(R.string.vehicle_entry_detail_short_mail);
                    } else {
                        action.row1_col1 = cursor.getString(cursor.getColumnIndex(VehicleContract.FuelingEntry.COL_MILEAGE)) + " " + getResources().getString(R.string.vehicle_entry_detail_short_km);
                    }

                    Date actionDate = new Date();
                    actionDate.setTime(cursor.getLong(cursor.getColumnIndex(VehicleContract.FuelingEntry.COL_DATE)));
                    SimpleDateFormat simpleDataFormat = new SimpleDateFormat("dd MMM yyyy");
                    action.row1_col2 = simpleDataFormat.format(actionDate);

                    String amount = cursor.getString(cursor.getColumnIndex(VehicleContract.FuelingEntry.COL_AMOUNT));
                    String expense = cursor.getString(cursor.getColumnIndex(VehicleContract.FuelingEntry.COL_EXPENSE));

                    // TBD: Check this: http://stackoverflow.com/questions/12694192/locale-currency-symbol
                    Currency currency = Currency.getInstance(Locale.getDefault());
                    Log.v("TAG",currency.getSymbol());

                    if (mFuelUnitId == Constants.FuelUnitId.FUEL_UNIT_GALLON) {
                        action.row2_col1 = amount + " " + getResources().getString(R.string.vehicle_entry_detail_short_gallon) + " (" + expense + currency.getSymbol() + ")";
                    } else {
                        action.row2_col1 = amount + " " + getResources().getString(R.string.vehicle_entry_detail_short_liter) + " (" + expense + currency.getSymbol() + ")";
                    }


                    //action.row2_col1 =  amount + " ga (" + expense + "$)" ;

                    description = cursor.getString(cursor.getColumnIndex(VehicleContract.FuelingEntry.COL_DESCRIPTION));
                    break;
                }
                case Constants.ActionType.ACTION_TYPE_SERVICES: {
                    action.mActionId =  cursor.getInt(cursor.getColumnIndex(VehicleContract.FuelingEntry._ID));

                    if (mOdometerUnitId == Constants.OdometerUnitId.ODOMETER_UNIT_MILES) {
                        action.row1_col1 = cursor.getString(cursor.getColumnIndex(VehicleContract.FuelingEntry.COL_MILEAGE)) + getResources().getString(R.string.vehicle_entry_detail_short_mail);
                    } else {
                        action.row1_col1 = cursor.getString(cursor.getColumnIndex(VehicleContract.FuelingEntry.COL_MILEAGE)) + getResources().getString(R.string.vehicle_entry_detail_short_km);
                    }

                    Date actionDate = new Date();
                    actionDate.setTime(cursor.getLong(cursor.getColumnIndex(VehicleContract.ServiceEntry.COL_DATE)));
                    SimpleDateFormat simpleDataFormat = new SimpleDateFormat("dd MMM yyyy");
                    action.row1_col2 = simpleDataFormat.format(actionDate);

                    // TBD: Check this: http://stackoverflow.com/questions/12694192/locale-currency-symbol
                    Currency currency = Currency.getInstance(Locale.getDefault());
                    Log.v("TAG",currency.getSymbol());

                    action.row2_col1 = cursor.getString(cursor.getColumnIndex(VehicleContract.ServiceEntry.COL_EXPENSE)) + currency.getSymbol() ;
                    description = cursor.getString(cursor.getColumnIndex(VehicleContract.ServiceEntry.COL_DESCRIPTION));
                    break;
                }
                case Constants.ActionType.ACTION_TYPE_EVENTS: {
                    action.mActionId =  cursor.getInt(cursor.getColumnIndex(VehicleContract.FuelingEntry._ID));

                    if (mOdometerUnitId == Constants.OdometerUnitId.ODOMETER_UNIT_MILES) {
                        action.row1_col1 = cursor.getString(cursor.getColumnIndex(VehicleContract.FuelingEntry.COL_MILEAGE)) + getResources().getString(R.string.vehicle_entry_detail_short_mail);
                    } else {
                        action.row1_col1 = cursor.getString(cursor.getColumnIndex(VehicleContract.FuelingEntry.COL_MILEAGE)) + getResources().getString(R.string.vehicle_entry_detail_short_km);
                    }

                    Date actionDate = new Date();
                    actionDate.setTime(cursor.getLong(cursor.getColumnIndex(VehicleContract.EventEntry.COL_DATE)));
                    SimpleDateFormat simpleDataFormat = new SimpleDateFormat("dd MMM yyyy");
                    action.row1_col2 = simpleDataFormat.format(actionDate);

                    // TBD: Check this: http://stackoverflow.com/questions/12694192/locale-currency-symbol
                    Currency currency = Currency.getInstance(Locale.getDefault());
                    Log.v("TAG",currency.getSymbol());

                    action.row2_col1 = cursor.getString(cursor.getColumnIndex(VehicleContract.EventEntry.COL_EXPENSE)) + currency.getSymbol();

                    description = cursor.getString(cursor.getColumnIndex(VehicleContract.EventEntry.COL_DESCRIPTION));
                    break;
                }
            }

            if (description.length() < 1 ) {
                action.row2_col2 = "";
            }
            else if (description.length() > 30 ) {
                String shortDescription = description.substring(0,30);
                action.row2_col2 = shortDescription +"..";
            }
            else {
                action.row2_col2 = description;
            }

            actionList.add(action);
            Log.d(TAG, "Action read from database: " + action.row1_col1 + "  " + action.row1_col2 + " " + action.row2_col1 + "  " + action.row2_col2);
        }

        if (mActionListView != null) {
            if (mActionAdapter == null) {
                mActionAdapter = new ActionListAdapter(this, actionList);
                mActionListView.setAdapter(mActionAdapter);
            } else {
                mActionAdapter.clear();
                mActionAdapter.addAll(actionList);
                mActionAdapter.notifyDataSetChanged();
            }
        }
        cursor.close();
        db.close();
    }

}
