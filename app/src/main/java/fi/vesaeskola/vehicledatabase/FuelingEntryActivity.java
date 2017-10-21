/*++

Module Name:

FuelingEntryActivity.java

Abstract:

This class is collect data related to re-fueling.

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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import database.DBEngine;
import database.VehicleContract;

public class FuelingEntryActivity extends ActionEntryActivity {
    private static final String TAG = "FuelingEntryActivity";
    public EditText mAmount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_fueling_entry);
        super.onCreate(savedInstanceState);

        TextView title = (TextView)findViewById(R.id.title);
        TextView textDate = (TextView) findViewById(R.id.date_text);
        TextView textRefuelingSymbol = (TextView) findViewById(R.id.refueling_unit);
        mAmount = (EditText) findViewById(R.id.refueling_amount);

        // TBD: Select correct string according user preferencies
        textRefuelingSymbol.setText(R.string.vehicle_entry_detail_short_liter);

        ImageView attachmentIcon = (ImageView) findViewById(R.id.attachment_icon);
        TextView attachmentText = (TextView) findViewById(R.id.attachment_text);


        if (mVehicleId != -1) {
            title.setText(R.string.fueling_entry_new_refueling);

            SimpleDateFormat simpleDataFormat = new SimpleDateFormat(getResources().getString(R.string.general_short_date));
            Date now = new Date();
            now.setTime(mDateLong);
            textDate.setText(simpleDataFormat.format(now));

            // TBD: Study how these properties could be binded together
            attachmentIcon.setVisibility(View.INVISIBLE);
            attachmentText.setVisibility(View.INVISIBLE);

        } else if (mActionId != -1) {
            // Edit old action
            title.setText(R.string.fueling_entry_edit_refueling);

            String selectQuery = "SELECT  * FROM " + VehicleContract.FuelingEntry.TABLE + " WHERE " + VehicleContract.FuelingEntry._ID + " = " + mActionId;
            Log.d(TAG, "edit existing action: selectQuery: " + selectQuery);

            // Open or create database file
            SQLiteDatabase db = mDatabaseEngine.getWritableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);

            // TBD: Change to be if clause
            if (cursor.moveToNext()) {

                int amount = cursor.getInt(cursor.getColumnIndex(VehicleContract.FuelingEntry.COL_AMOUNT));
                if (amount != 0) {
                    mAmount.setText(VehileDatabaseApplication.ConvertIntToPlatformString(amount));
                }

                int expense = cursor.getInt(cursor.getColumnIndex(VehicleContract.FuelingEntry.COL_EXPENSE));
                if (expense != 0) {
                    mExpense.setText(VehileDatabaseApplication.ConvertIntToPlatformString(expense));
                }

                int mileage = cursor.getInt(cursor.getColumnIndex(VehicleContract.FuelingEntry.COL_MILEAGE));
                if (mileage != 0) {
                    mMileage.setText(String.valueOf(mileage));
                }

                mDescription.setText(cursor.getString(cursor.getColumnIndex(VehicleContract.FuelingEntry.COL_DESCRIPTION)));
                mDateLong = cursor.getLong(cursor.getColumnIndex(VehicleContract.FuelingEntry.COL_DATE));
                SimpleDateFormat simpleDataFormat = new SimpleDateFormat(getResources().getString(R.string.general_short_date));
                Date now = new Date();
                now.setTime(mDateLong);
                textDate.setText(simpleDataFormat.format(now));
            }

            super.readImageLinkCount (db, mActionId, Constants.RequestCode.REQUEST_FUELING);

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

    public void OnFuelingEntered(View view) {
        View parent = (View) view.getParent();
        Log.d(TAG, "OnFuelingEntered, selected vehicle: " + mVehicleId);

        // Open or create database file
        SQLiteDatabase db = mDatabaseEngine.getWritableDatabase();
        ContentValues values = new ContentValues();
        int rowID = -1;

        if (mVehicleId != -1) {
            values.put(VehicleContract.FuelingEntry.COL_VEHICLEID, mVehicleId);
            values.put(VehicleContract.FuelingEntry.COL_DATE, mDateLong);

            if (mMileage.length() > 0) {
                int iMileage = VehileDatabaseApplication.ConvertStringToIntNoRound(mMileage.getText().toString());
                values.put(VehicleContract.FuelingEntry.COL_MILEAGE, iMileage);
            }

            if (mAmount.length() > 0) {
                int amount = VehileDatabaseApplication.ConvertStringToInt(mAmount.getText().toString());
                values.put(VehicleContract.FuelingEntry.COL_AMOUNT, amount);
            }

            if (mMileage.length() > 0) {
                int expense = VehileDatabaseApplication.ConvertStringToInt(mExpense.getText().toString());
                values.put(VehicleContract.FuelingEntry.COL_EXPENSE, expense);
            }

            values.put(VehicleContract.FuelingEntry.COL_DESCRIPTION, mDescription.getText().toString());

            rowID = (int)db.insertWithOnConflict(VehicleContract.FuelingEntry.TABLE,
                    null,
                    values,
                    SQLiteDatabase.CONFLICT_REPLACE);

            super.storeImageLinks (db, rowID, Constants.RequestCode.REQUEST_FUELING);
            Log.d(TAG, "New fueling entered to database");
        } else if (mActionId != -1) {
            values.put(VehicleContract.FuelingEntry.COL_DATE, mDateLong);
            values.put(VehicleContract.FuelingEntry.COL_MILEAGE, mMileage.getText().toString());

            if (mAmount.length() > 0) {
                int amount = VehileDatabaseApplication.ConvertStringToInt(mAmount.getText().toString());
                values.put(VehicleContract.FuelingEntry.COL_AMOUNT, amount);
            }
            if (mExpense.length() > 0) {
                int expense = VehileDatabaseApplication.ConvertStringToInt(mExpense.getText().toString());
                values.put(VehicleContract.FuelingEntry.COL_EXPENSE, expense);
            }
            values.put(VehicleContract.FuelingEntry.COL_DESCRIPTION, mDescription.getText().toString());

            db.update(VehicleContract.FuelingEntry.TABLE, values, VehicleContract.FuelingEntry._ID + " = " + mActionId, null);

            rowID = mActionId;
            Log.d(TAG, "Existing fueling updated from database");
        }

        db.close();

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
                super.storeImageLinks (db, mActionId, Constants.RequestCode.REQUEST_FUELING);
            }
        }
    }
}
