/*++

Module Name:

ServiceEntryActivity.java

Abstract:

This class is collect data related to service.

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
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import database.VehicleContract;

public class ServiceEntryActivity extends ActionEntryActivity implements MultiSelectionSpinner.OnMultipleItemsSelectedListener  {
    private static final String TAG = "ServiceEntryActivity";
    int mServiceType = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_service_entry);
        super.onCreate(savedInstanceState);

        TextView title = (TextView)findViewById(R.id.title);
        TextView textDate = (TextView) findViewById(R.id.date_text);

        ImageView attachmentIcon = (ImageView) findViewById(R.id.attachment_icon);
        TextView attachmentText = (TextView) findViewById(R.id.attachment_text);


        SQLiteDatabase db = mDatabaseEngine.getReadableDatabase();

        // Read all services types from db to multiSelectionSpinner widget
        ArrayList<String> serviceTypes = mDatabaseEngine.getServiceTypeList ();


        MultiSelectionSpinner multiSelectionSpinner = (MultiSelectionSpinner) findViewById(R.id.type_of_service);
        multiSelectionSpinner.setItems(serviceTypes);
        multiSelectionSpinner.setListener(this);

        if (mVehicleId != -1) {
            title.setText(R.string.service_entry_new_service);

            multiSelectionSpinner.setSelection(new int[]{0});

            SimpleDateFormat simpleDataFormat = new SimpleDateFormat(getResources().getString(R.string.general_short_date));
            Date now = new Date();
            now.setTime(mDateLong);
            textDate.setText(simpleDataFormat.format(now));

            attachmentIcon.setVisibility(View.INVISIBLE);
            attachmentText.setVisibility(View.INVISIBLE);

        } else if (mActionId != -1) {
            title.setText(R.string.service_entry_edit_service);

            String selectQuery = "SELECT  * FROM " + VehicleContract.ServiceEntry.TABLE + " WHERE " + VehicleContract.ServiceEntry._ID + " = " + mActionId;
            Log.d(TAG, "edit existing service: selectQuery: " + selectQuery);

            // Open or create database file
            //SQLiteDatabase db = mDatabaseEngine.getWritableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);

            if (cursor.moveToNext()) {

                int expense = cursor.getInt(cursor.getColumnIndex(VehicleContract.ServiceEntry.COL_EXPENSE));
                if (expense != 0) {
                    mExpense.setText(VehileDatabaseApplication.ConvertIntToPlatformString(expense));
                }

                int mileage = cursor.getInt(cursor.getColumnIndex(VehicleContract.ServiceEntry.COL_MILEAGE));
                if (mileage != 0) {
                    mMileage.setText(String.valueOf(mileage));
                }

                mServiceType = cursor.getInt(cursor.getColumnIndex(VehicleContract.ServiceEntry.COL_SERVICETYPE));

                if (mServiceType !=  0) {
                    int tempType = mServiceType;
                    ArrayList<Integer> selectedItems = new ArrayList<Integer>();
                    for (int i = 0; i < serviceTypes.size(); i++) {
                        if ((tempType % 2) > 0) {
                            selectedItems.add(i);
                        }
                        tempType = tempType >> 1;
                    }
                    multiSelectionSpinner.setSelection(selectedItems);
                }

                mDescription.setText(cursor.getString(cursor.getColumnIndex(VehicleContract.ServiceEntry.COL_DESCRIPTION)));
                mDateLong = cursor.getLong(cursor.getColumnIndex(VehicleContract.ServiceEntry.COL_DATE));
                SimpleDateFormat simpleDataFormat = new SimpleDateFormat(getResources().getString(R.string.general_short_date));
                Date now = new Date();
                now.setTime(mDateLong);
                textDate.setText(simpleDataFormat.format(now));
            }

            super.readImageLinkCount (db, mActionId, Constants.RequestCode.REQUEST_SERVICE);

            db.close();

            if (mImageLinkCount == 0) {
                attachmentIcon.setVisibility(View.INVISIBLE);
                attachmentText.setVisibility(View.INVISIBLE);
            } else {
                attachmentIcon.setVisibility(View.VISIBLE);
                attachmentText.setVisibility(View.VISIBLE);
                attachmentText.setText(String.valueOf(mImageLinkCount));
            }

        }
    }

    public void OnServiceEntered(View view) {
        View parent = (View) view.getParent();
        Log.d(TAG, "OnServiceEntered, selected vehicle: " + mVehicleId);

        // Open or create database file
        SQLiteDatabase db = mDatabaseEngine.getWritableDatabase();
        int rowID = -1;
        ContentValues values = new ContentValues();

        if (mVehicleId != -1) {
            values.put(VehicleContract.ServiceEntry.COL_VEHICLEID, mVehicleId);
            values.put(VehicleContract.ServiceEntry.COL_DATE, mDateLong);
            if (mMileage.length() > 0) {
                int iMileage = VehileDatabaseApplication.ConvertStringToIntNoRound(mMileage.getText().toString());
                values.put(VehicleContract.ServiceEntry.COL_MILEAGE, iMileage);
            }
            if (mExpense.length() > 0) {
                int expense = VehileDatabaseApplication.ConvertStringToInt(mExpense.getText().toString());
                values.put(VehicleContract.ServiceEntry.COL_EXPENSE, expense);
            }
            values.put(VehicleContract.ServiceEntry.COL_SERVICETYPE, mServiceType);
            values.put(VehicleContract.ServiceEntry.COL_DESCRIPTION, mDescription.getText().toString());

            rowID = (int)db.insertWithOnConflict(VehicleContract.ServiceEntry.TABLE,
                    null,
                    values,
                    SQLiteDatabase.CONFLICT_REPLACE);
            db.close();

            super.storeImageLinks (db, rowID, Constants.RequestCode.REQUEST_SERVICE);
            Log.d(TAG, "New service entered to database");
        } else if (mActionId != -1) {
            values.put(VehicleContract.ServiceEntry.COL_DATE, mDateLong);
            if (mMileage.length() > 0) {
                int iMileage = VehileDatabaseApplication.ConvertStringToIntNoRound(mMileage.getText().toString());
                values.put(VehicleContract.ServiceEntry.COL_MILEAGE, iMileage);
            }
            if (mExpense.length() > 0) {
                int expense = VehileDatabaseApplication.ConvertStringToInt(mExpense.getText().toString());
                values.put(VehicleContract.ServiceEntry.COL_EXPENSE, expense);
            }
            values.put(VehicleContract.ServiceEntry.COL_SERVICETYPE, mServiceType);
            values.put(VehicleContract.ServiceEntry.COL_DESCRIPTION, mDescription.getText().toString());

            db.update(VehicleContract.ServiceEntry.TABLE, values, VehicleContract.ServiceEntry._ID + " = " + mActionId, null);
            rowID = mActionId;

            Log.d(TAG, "Existing service updated from database");
        }

        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "OnActivityResult: RESULT_OK");

        if (resultCode == Activity.RESULT_OK) {

            // Let the super class handle activity result, typically this increase count
            // of attachments and UI update
            super.onActivityResult(requestCode, resultCode, data);

            // If image was captured into existing action store new imagelink immediately
            if (requestCode == Constants.RequestCode.REQUEST_IMAGE_CAPTURE && mActionId != -1) {
                SQLiteDatabase db = mDatabaseEngine.getWritableDatabase();
                super.storeImageLinks (db, mActionId, Constants.RequestCode.REQUEST_SERVICE);
            }
        }
    }

    @Override
    public void selectedIndices(List<Integer> indices) {
        mServiceType = 0;
        for (int i = 0; i < indices.size(); i++) {
            mServiceType += 1<<(indices.get(i));
        }
    }

    @Override
    public void selectedStrings(List<String> strings) {
        //Toast.makeText(this, strings.toString(), Toast.LENGTH_LONG).show();
    }

}
