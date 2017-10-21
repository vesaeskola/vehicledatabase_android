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
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Date;
import java.util.Locale;

import database.DBEngine;
import database.VehicleContract;
import utilities.EnginePool;
import utilities.Utilities;

public class ActionListActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {
    private static final String TAG = "VehicleListActivity";
    private DBEngine mDatabaseEngine;
    private ListView mActionListView;
    private ActionListAdapter mActionAdapter;
    private int mVehicleId;
    private int mActionListType;
    private int mFuelUnitId;        // TBD: Create a class to collect these unit id's etc.
    private int mOdometerUnitId;
    private boolean mVehicleDataModified = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action_list);

        // SQLiteOpenHelper based helper to open or create database.
        // Note: Will delete existing data if new database structure is used.
        mDatabaseEngine = (DBEngine) EnginePool.getEngine("DBEngine");
        mActionListView = (ListView) findViewById(R.id.list_actions);
        mActionListView.setClickable(true);

        mActionListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
            ActionListItem actionItem = (ActionListItem) mActionListView.getItemAtPosition(position);
            Intent intent = null;
            switch (mActionListType) {
                case Constants.RequestCode.REQUEST_FUELING: {
                    intent = new Intent(ActionListActivity.this, FuelingEntryActivity.class);
                    break;
                }
                case Constants.RequestCode.REQUEST_SERVICE: {
                    intent = new Intent(ActionListActivity.this, ServiceEntryActivity.class);
                    break;
                }
                case Constants.RequestCode.REQUEST_EVENT: {
                    intent = new Intent(ActionListActivity.this, EventEntryActivity.class);
                    break;
                }
            }
            intent.putExtra("action_Id", actionItem.mActionId);
            startActivityForResult(intent, mActionListType);
            Log.d(TAG, "open action editor with action ID: " + actionItem.mActionId);
            }
        });

        Bundle bundle = getIntent().getExtras();
        mVehicleId = bundle.getInt("vehicle_Id");
        mActionListType = bundle.getInt("listType");
        mFuelUnitId = bundle.getInt("fuelUnitId");
        mOdometerUnitId = bundle.getInt("odometerUnitId");
        Log.d(TAG, "Vehicle info to show: " + mVehicleId + " listType: " + mActionListType);

        // Update UI and the list view
        updateUI(Constants.RequestCode.REQUEST_VEHICLE_INFO);
    }

    public void OnOpenMenu(View view) {
        View parent = (View) view.getParent();
        Log.d(TAG, "onOpenMenu");

        PopupMenu popup = new PopupMenu(this, view);
        popup.setOnMenuItemClickListener(ActionListActivity.this);
        MenuInflater inflater = popup.getMenuInflater();

        inflater.inflate(R.menu.menu_01, popup.getMenu());

        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
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
    public void onBackPressed() {
        // This is the trick to force VehicleInfoActivity UI to be updated
        Intent returnIntent = new Intent();
        if (mVehicleDataModified) {
            Log.d(TAG, "onBackPressed: vehicle modified");
            setResult(Activity.RESULT_OK, returnIntent);
        } else {
            Log.d(TAG, "onBackPressed: vehicle not modified");
            setResult(Activity.RESULT_CANCELED, returnIntent);
        }

        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK) {
            mVehicleDataModified = true;
            updateUI(requestCode);
        }
        else if (resultCode == Activity.RESULT_CANCELED) {
            Log.d(TAG, "Vechile actions not modified");
        }

    }//onActivityResult

    public void openAttachments(View view) {
        View parent = (View) view.getParent();
        Log.d(TAG, "openAttachments");

        /*
        ActionListItem action = (ActionListItem)mActionListView.getSelectedItem();
        if (action != null) {
            if (action.mAttachmentCount == 0) {
                return;
            }
        }
        */


        ImageView ivOpenAttachment = (ImageView) parent.findViewById(R.id.attachment_icon);
        TextView tvOdometer = (TextView)parent.findViewById(R.id.row1_col1);
        TextView tvDate = (TextView)parent.findViewById(R.id.row1_col2);
        TextView tvMake = (TextView) findViewById(R.id.vehicle_make);
        TextView tvModel = (TextView) findViewById(R.id.vehicle_model);
        TextView tvRegPlate = (TextView) findViewById(R.id.vehicle_regplate);


        Log.d(TAG, "Selected action: " + ivOpenAttachment.getId() + " " + ivOpenAttachment.getTag());

        // Open AttachmentViewerActivity page
        Intent intent = new Intent(this, AttachmentViewerActivity.class);

        intent.putExtra("action_Id", (int) ivOpenAttachment.getTag());
        intent.putExtra("action_Type", mActionListType);
        intent.putExtra("action_date", tvDate.getText().toString());
        intent.putExtra("action_odometer", tvOdometer.getText().toString());
        intent.putExtra("vehicle_make", tvMake.getText().toString());
        intent.putExtra("vehicle_model", tvModel.getText().toString());
        intent.putExtra("vehicle_regplate", tvRegPlate.getText().toString());

        // Here we set request code REQUEST_EDIT_VEHICLE to indicate user could edit vehicle while viewing the info of it
        startActivityForResult(intent, Constants.RequestCode.REQUEST_VEHICLE_INFO);
    }

    private void updateUI(int requestCode) {

        Log.d(TAG, "updateUI: requestCode: " + requestCode + "ActionListType: " + mActionListType + ", VehicleId: " + mVehicleId);


        SQLiteDatabase db = mDatabaseEngine.getReadableDatabase();

        // Vehicle info was edited, update the title only
        if (requestCode == Constants.RequestCode.REQUEST_VEHICLE_INFO) {
            String selectQuery = "SELECT " + VehicleContract.VehicleEntry.COL_MAKE + ", "
                                           + VehicleContract.VehicleEntry.COL_MODEL + ", "
                                           + VehicleContract.VehicleEntry.COL_REGPLATE +
                                 " FROM "  + VehicleContract.VehicleEntry.TABLE +
                                 " WHERE " + VehicleContract.VehicleEntry._ID + " = " + mVehicleId;

            Log.d(TAG, "selectQuery: " + selectQuery);
            Cursor cursor = db.rawQuery(selectQuery, null);

            if (cursor.moveToNext()) {
                TextView tvMake = (TextView) findViewById(R.id.vehicle_make);
                TextView tvModel = (TextView) findViewById(R.id.vehicle_model);
                TextView tvPlate = (TextView) findViewById(R.id.vehicle_regplate);

                tvMake.setText(cursor.getString(cursor.getColumnIndex(VehicleContract.VehicleEntry.COL_MAKE)));
                tvModel.setText(cursor.getString(cursor.getColumnIndex(VehicleContract.VehicleEntry.COL_MODEL)));
                tvPlate.setText(cursor.getString(cursor.getColumnIndex(VehicleContract.VehicleEntry.COL_REGPLATE)));
            }
        }

        TextView tvTitle = (TextView) findViewById(R.id.title);

        // Update entire list view. This must be also done if vehicle has been updated to get units updated
        ArrayList<ActionListItem> actionList = new ArrayList<ActionListItem>();

        String selectQuery = "SELECT * FROM ";
        ArrayList<String> actionTypes = null;

        switch (mActionListType) {
            case Constants.RequestCode.REQUEST_FUELING: {
                tvTitle.setText(R.string.title_fuelings);
                selectQuery = selectQuery + VehicleContract.FuelingEntry.TABLE + " WHERE " + VehicleContract.FuelingEntry.COL_VEHICLEID + " = " + mVehicleId;
                break;
            }
            case Constants.RequestCode.REQUEST_SERVICE: {
                tvTitle.setText(R.string.title_services);
                selectQuery = selectQuery + VehicleContract.ServiceEntry.TABLE + " WHERE " + VehicleContract.ServiceEntry.COL_VEHICLEID + " = " + mVehicleId;
                actionTypes = mDatabaseEngine.getServiceTypeList ();

                break;
            }
            case Constants.RequestCode.REQUEST_EVENT: {
                tvTitle.setText(R.string.title_events);
                selectQuery = selectQuery + VehicleContract.EventEntry.TABLE + " WHERE " + VehicleContract.EventEntry.COL_VEHICLEID + " = " + mVehicleId;
                actionTypes = mDatabaseEngine.getEventTypeList ();
                break;
            }
        }

        Log.d(TAG, "selectQuery: " + selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);

        while (cursor.moveToNext()) {
            ActionListItem action = new ActionListItem(-1, "r1c1", "r1c2", "r2c1", "r2c2", "row3", 0);

            String description = "";

            switch (mActionListType) {
                case Constants.RequestCode.REQUEST_FUELING: {
                    action.mActionId = cursor.getInt(cursor.getColumnIndex(VehicleContract.FuelingEntry._ID));

                    String mileage = cursor.getString(cursor.getColumnIndex(VehicleContract.FuelingEntry.COL_MILEAGE));
                    if (mileage != null) {
                        if (mOdometerUnitId == Constants.OdometerUnitId.ODOMETER_UNIT_MILES) {
                            action.row1_col1 = mileage + getResources().getString(R.string.vehicle_entry_detail_short_mail);  // TBD: Localization
                        } else {
                            action.row1_col1 = mileage + getResources().getString(R.string.vehicle_entry_detail_short_km);
                        }
                    } else {
                        action.row1_col1 = "---";
                    }

                    Date actionDate = new Date();
                    actionDate.setTime(cursor.getLong(cursor.getColumnIndex(VehicleContract.FuelingEntry.COL_DATE)));
                    SimpleDateFormat simpleDataFormat = new SimpleDateFormat(getResources().getString(R.string.general_short_date));
                    action.row1_col2 = simpleDataFormat.format(actionDate);

                    // TBD: Calculate € total from €/l * amount
                    int amount = cursor.getInt(cursor.getColumnIndex(VehicleContract.FuelingEntry.COL_AMOUNT));
                    int expense = cursor.getInt(cursor.getColumnIndex(VehicleContract.FuelingEntry.COL_EXPENSE));
                    int total = (amount * expense) / 100;

                    String sAmount = VehileDatabaseApplication.ConvertIntToPlatformString(amount);
                    String sExpense = VehileDatabaseApplication.ConvertIntToPlatformString(expense);
                    String sTotal = VehileDatabaseApplication.ConvertIntToPlatformString(total);


                    // TBD: Check this: http://stackoverflow.com/questions/12694192/locale-currency-symbol
                    Currency currency = Currency.getInstance(Locale.getDefault());

                    Log.v("TAG", currency.getSymbol());

                    if (mFuelUnitId == Constants.FuelUnitId.FUEL_UNIT_GALLON) {
                        action.row2_col1 = sAmount + " " + getResources().getString(R.string.vehicle_entry_detail_short_gallon) + " (" + sExpense + currency.getSymbol() + ")";           // TBD: Localization
                    } else {
                        action.row2_col1 = sAmount + " " +
                                          getResources().getString(R.string.vehicle_entry_detail_short_liter) +
                                          " (" + sExpense + currency.getSymbol() + "/" + getResources().getString(R.string.vehicle_entry_detail_short_liter) + ", Tot:" +
                                          sTotal + currency.getSymbol() + ")";
                    }

                    action.row2_col2 = "";

                    description = cursor.getString(cursor.getColumnIndex(VehicleContract.FuelingEntry.COL_DESCRIPTION));
                    break;
                }
                case Constants.RequestCode.REQUEST_SERVICE: {
                    action.mActionId = cursor.getInt(cursor.getColumnIndex(VehicleContract.ServiceEntry._ID));

                    String mileage = cursor.getString(cursor.getColumnIndex(VehicleContract.ServiceEntry.COL_MILEAGE));
                    if (mileage != null) {
                        if (mOdometerUnitId == Constants.OdometerUnitId.ODOMETER_UNIT_MILES) {
                            action.row1_col1 = mileage + getResources().getString(R.string.vehicle_entry_detail_short_mail);  // TBD: Localization
                        } else {
                            action.row1_col1 = mileage + getResources().getString(R.string.vehicle_entry_detail_short_km);
                        }
                    } else {
                            action.row1_col1 = "---";
                    }

                    Date actionDate = new Date();
                    actionDate.setTime(cursor.getLong(cursor.getColumnIndex(VehicleContract.ServiceEntry.COL_DATE)));
                    SimpleDateFormat simpleDataFormat = new SimpleDateFormat(getResources().getString(R.string.general_short_date));
                    action.row1_col2 = simpleDataFormat.format(actionDate);

                    // TBD: Check this: http://stackoverflow.com/questions/12694192/locale-currency-symbol
                    Currency currency = Currency.getInstance(Locale.getDefault());
                    Log.v("TAG", currency.getSymbol());

                    String sExpense = VehileDatabaseApplication.ConvertIntToPlatformString(cursor.getInt(cursor.getColumnIndex(VehicleContract.ServiceEntry.COL_EXPENSE)));
                    action.row2_col2 = sExpense + currency.getSymbol();

                    // tbd: Read service type table and convert to string.

                    action.row2_col1 = "";
                    if (actionTypes != null) {
                        String serviceTypeDescription = "";

                        int serviceType = cursor.getInt(cursor.getColumnIndex(VehicleContract.ServiceEntry.COL_SERVICETYPE));
                        for (int i = 0; i < actionTypes.size(); i++) {
                            if ((serviceType % 2) > 0) {
                                if (serviceTypeDescription.length() > 0) {
                                    serviceTypeDescription += " ";
                                }
                                serviceTypeDescription += actionTypes.get(i);
                            }
                            serviceType = serviceType >> 1;
                        }

                        action.row2_col1 = Utilities.abbreviateLongString (serviceTypeDescription, 46 - sExpense.length());
                    }

                    description = cursor.getString(cursor.getColumnIndex(VehicleContract.ServiceEntry.COL_DESCRIPTION));
                    break;
                }
                case Constants.RequestCode.REQUEST_EVENT: {
                    action.mActionId = cursor.getInt(cursor.getColumnIndex(VehicleContract.EventEntry._ID));

                    String mileage = cursor.getString(cursor.getColumnIndex(VehicleContract.EventEntry.COL_MILEAGE));
                    if (mileage != null) {
                        if (mOdometerUnitId == Constants.OdometerUnitId.ODOMETER_UNIT_MILES) {
                            action.row1_col1 = mileage + getResources().getString(R.string.vehicle_entry_detail_short_mail);  // TBD: Localization
                        } else {
                            action.row1_col1 = mileage + getResources().getString(R.string.vehicle_entry_detail_short_km);
                        }
                    } else {
                        action.row1_col1 = "---";
                    }


                    Date actionDate = new Date();
                    actionDate.setTime(cursor.getLong(cursor.getColumnIndex(VehicleContract.EventEntry.COL_DATE)));
                    SimpleDateFormat simpleDataFormat = new SimpleDateFormat(getResources().getString(R.string.general_short_date));
                    action.row1_col2 = simpleDataFormat.format(actionDate);

                    // TBD: Check this: http://stackoverflow.com/questions/12694192/locale-currency-symbol
                    Currency currency = Currency.getInstance(Locale.getDefault());
                    Log.v("TAG", currency.getSymbol());

                    String sExpense = VehileDatabaseApplication.ConvertIntToPlatformString(cursor.getInt(cursor.getColumnIndex(VehicleContract.EventEntry.COL_EXPENSE)));
                    action.row2_col2 = sExpense + currency.getSymbol();

                    // tbd: Read service type table and convert to string.
                    if (actionTypes != null) {
                        int eventType = cursor.getInt(cursor.getColumnIndex(VehicleContract.EventEntry.COL_EVENTTYPE));
                        action.row2_col1 = actionTypes.get(eventType);
                    }

                    description = cursor.getString(cursor.getColumnIndex(VehicleContract.EventEntry.COL_DESCRIPTION));
                    break;
                }
            }

            if (description.length() < 1) {
                //action.row2_col2 = "";
                action.row3 = "";
            } else if (description.length() > 46) {
                String shortDescription = description.substring(0, 46);
                //action.row2_col2 = shortDescription + "..";
                action.row3 = shortDescription + "..";
            } else {
                //action.row2_col2 = description;
                action.row3 = description;
            }

            // TBD: Move this code into common DBEngine module
            String selectQuery2 = "SELECT  * FROM " + VehicleContract.ImageLinkEntry.TABLE +
                                  " WHERE " + VehicleContract.ImageLinkEntry.COL_ACTIONID + " = " + action.mActionId +
                                  " AND " + VehicleContract.ImageLinkEntry.COL_ACTIONTYPE + " = " + mActionListType;
            Cursor cursor2 = db.rawQuery(selectQuery2, null);
            action.mAttachmentCount = 0;
            while (cursor2.moveToNext()) {
                String imagePath = cursor2.getString(cursor2.getColumnIndex(VehicleContract.ImageLinkEntry.COL_IMAGEPATH));
                if (imagePath != null) {
                    action.mAttachmentCount++;
                }
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

    public void onEditVehicle (View view)
    {
        Log.d(TAG,"Edit vehicle");
        // Open VehicleEntryBasicActivity page
        Intent intent = new Intent(this, VehicleEntryBasicActivity.class);
        intent.putExtra("vehicle_Id",mVehicleId);
        startActivityForResult(intent, Constants.RequestCode.REQUEST_VEHICLE_INFO);
        Log.d(TAG,"open vehicle editor with vehicle ID: "+ mVehicleId);
    }

}
