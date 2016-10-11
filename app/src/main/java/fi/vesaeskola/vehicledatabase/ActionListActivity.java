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

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import database.DBEngine;
import database.VehicleContract;

public class ActionListActivity extends AppCompatActivity {
    private static final String TAG = "VehicleListActivity";
    private DBEngine mDatabaseEngine;
    private ListView mActionListView;
    private ActionListAdapter mActionAdapter;
    private int mVehicleId;
    private int mActionListType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action_list);

        // SQLiteOpenHelper based helper to open or create database.
        // Note: Will delete existing data if new database structure is used.
        mDatabaseEngine = new DBEngine(this);

        mActionListView = (ListView) findViewById(R.id.list_actions);

        Bundle bundle = getIntent().getExtras();
        mVehicleId = bundle.getInt("vehicle_Id");
        Log.d(TAG, "Vehicle info to show: " + mVehicleId);
        mActionListType = bundle.getInt("listType");

        updateUI();

    }

    private void updateUI() {
        Log.d(TAG, "updateUI: ActionListType: " +  mActionListType + ", VehicleId: " + mVehicleId);

        ArrayList<ActionListItem> actionList = new ArrayList<ActionListItem>();
        // Open existing database file
        SQLiteDatabase db = mDatabaseEngine.getReadableDatabase();

        String selectQuery = "SELECT  * FROM ";

        switch(mActionListType)
        {
            case 1: {
                selectQuery = selectQuery + VehicleContract.EventEntry.TABLE;
                break;
            }
            case 2: {
                selectQuery = selectQuery + VehicleContract.ServiceEntry.TABLE;
                break;
            }
            case 3: {
                selectQuery = selectQuery + VehicleContract.FuelingEntry.TABLE;
                break;
            }
        }

        selectQuery = selectQuery + " WHERE _id = " + mVehicleId;

        Log.d(TAG, "selectQuery: " + selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);

        while (cursor.moveToNext()) {
            ActionListItem action = new ActionListItem("r1c1", "r1c2", "r2c1", "r2c2");

            String description = "";

            switch(mActionListType)
            {
                case 1: {
                    action.row1_col1 = cursor.getString(cursor.getColumnIndex(VehicleContract.EventEntry.COL_MILEAGE)) + " mi.";

                    Date actionDate = new Date();
                    actionDate.setTime(cursor.getLong(cursor.getColumnIndex(VehicleContract.EventEntry.COL_DATE)));
                    SimpleDateFormat simpleDataFormat = new SimpleDateFormat("dd MMM yyyy");
                    action.row1_col2 = simpleDataFormat.format(actionDate);

                    action.row2_col1 = cursor.getString(cursor.getColumnIndex(VehicleContract.EventEntry.COL_PRISE)) + "$";
                    description = cursor.getString(cursor.getColumnIndex(VehicleContract.EventEntry.COL_DESCRIPTION));
                    break;
                }
                case 2: {
                    action.row1_col1 = cursor.getString(cursor.getColumnIndex(VehicleContract.ServiceEntry.COL_MILEAGE)) + " mi.";

                    Date actionDate = new Date();
                    actionDate.setTime(cursor.getLong(cursor.getColumnIndex(VehicleContract.ServiceEntry.COL_DATE)));
                    SimpleDateFormat simpleDataFormat = new SimpleDateFormat("dd MMM yyyy");
                    action.row1_col2 = simpleDataFormat.format(actionDate);

                    action.row2_col1 = cursor.getString(cursor.getColumnIndex(VehicleContract.EventEntry.COL_PRISE)) + "$";
                    description = cursor.getString(cursor.getColumnIndex(VehicleContract.ServiceEntry.COL_DESCRIPTION));
                    break;
                }
                case 3: {
                    action.row1_col1 = cursor.getString(cursor.getColumnIndex(VehicleContract.FuelingEntry.COL_MILEAGE)) + " mi.";

                    Date actionDate = new Date();
                    actionDate.setTime(cursor.getLong(cursor.getColumnIndex(VehicleContract.FuelingEntry.COL_DATE)));
                    SimpleDateFormat simpleDataFormat = new SimpleDateFormat("dd MMM yyyy");
                    action.row1_col2 = simpleDataFormat.format(actionDate);

                    action.row2_col1 = cursor.getString(cursor.getColumnIndex(VehicleContract.FuelingEntry.COL_AMOUNT)) + " ga. (" + cursor.getString(cursor.getColumnIndex(VehicleContract.FuelingEntry.COL_PRISE)) + "$)";

                    description = cursor.getString(cursor.getColumnIndex(VehicleContract.FuelingEntry.COL_DESCRIPTION));
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
