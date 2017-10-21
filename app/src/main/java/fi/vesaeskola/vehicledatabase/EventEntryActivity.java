/*++

Module Name:

EventEntryActivity.java

Abstract:

This class is collect data related to common event.

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import database.VehicleContract;

public class EventEntryActivity extends ActionEntryActivity implements AdapterView.OnItemSelectedListener {
    private static final String TAG = "ActionEntryActivity";
    int mEventType = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_event_entry);
        super.onCreate(savedInstanceState);

        TextView title = (TextView)findViewById(R.id.title);
        TextView textDate = (TextView) findViewById(R.id.date_text);

        ImageView attachmentIcon = (ImageView) findViewById(R.id.attachment_icon);
        TextView attachmentText = (TextView) findViewById(R.id.attachment_text);

        SQLiteDatabase db = mDatabaseEngine.getWritableDatabase();

        // Read all services types from db to multiSelectionSpinner widget
        ArrayList<String> eventTypes = mDatabaseEngine.getEventTypeList ();

        Spinner selectionSpinner = (Spinner) findViewById(R.id.type_of_event);
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, R.layout.spinner_layout/*android.R.layout.simple_spinner_item*/, eventTypes);
        selectionSpinner.setAdapter(spinnerArrayAdapter);
        selectionSpinner.setOnItemSelectedListener(this);

        if (mVehicleId != -1) {
            title.setText(R.string.event_entry_new_event);

            selectionSpinner.setSelection(0);
            SimpleDateFormat simpleDataFormat = new SimpleDateFormat(getResources().getString(R.string.general_short_date));
            Date now = new Date();
            now.setTime(mDateLong);
            textDate.setText(simpleDataFormat.format(now));

            attachmentIcon.setVisibility(View.INVISIBLE);
            attachmentText.setVisibility(View.INVISIBLE);
        } else if (mActionId != -1) {
            title.setText(R.string.event_entry_edit_event);

            String selectQuery = "SELECT  * FROM " + VehicleContract.EventEntry.TABLE + " WHERE " + VehicleContract.EventEntry._ID + " = " + mActionId;
            Log.d(TAG, "edit existing action: selectQuery: " + selectQuery);

            // Open or create database file
            Cursor cursor = db.rawQuery(selectQuery, null);

            if (cursor.moveToNext()) {

                int expense = cursor.getInt(cursor.getColumnIndex(VehicleContract.EventEntry.COL_EXPENSE));
                if (expense != 0) {
                    mExpense.setText(VehileDatabaseApplication.ConvertIntToPlatformString(expense));
                }

                int mileage = cursor.getInt(cursor.getColumnIndex(VehicleContract.EventEntry.COL_MILEAGE));
                if (mileage != 0) {
                    mMileage.setText(String.valueOf(mileage));
                }

                mEventType = cursor.getInt(cursor.getColumnIndex(VehicleContract.EventEntry.COL_EVENTTYPE));
                selectionSpinner.setSelection(mEventType);

                mDescription.setText(cursor.getString(cursor.getColumnIndex(VehicleContract.EventEntry.COL_DESCRIPTION)));
                mDateLong = cursor.getLong(cursor.getColumnIndex(VehicleContract.EventEntry.COL_DATE));
                SimpleDateFormat simpleDataFormat = new SimpleDateFormat(getResources().getString(R.string.general_short_date));
                Date now = new Date();
                now.setTime(mDateLong);
                textDate.setText(simpleDataFormat.format(now));
            }

            super.readImageLinkCount (db, mActionId, Constants.RequestCode.REQUEST_EVENT);

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

    public void OnEventEntered(View view) {
        View parent = (View) view.getParent();
        Log.d(TAG, "OnEventEntered, selected vehicle: " + mVehicleId);

        // Open or create database file
        SQLiteDatabase db = mDatabaseEngine.getWritableDatabase();
        int rowID = -1;
        ContentValues values = new ContentValues();

        if (mVehicleId != -1) {
            values.put(VehicleContract.EventEntry.COL_VEHICLEID, mVehicleId);
            values.put(VehicleContract.EventEntry.COL_DATE, mDateLong);
            if (mMileage.length() > 0) {
                int iMileage = VehileDatabaseApplication.ConvertStringToIntNoRound(mMileage.getText().toString());
                values.put(VehicleContract.EventEntry.COL_MILEAGE, iMileage);
            }
            if (mExpense.length() > 0) {
                int expense = VehileDatabaseApplication.ConvertStringToInt(mExpense.getText().toString());
                values.put(VehicleContract.EventEntry.COL_EXPENSE, expense);
            }
            values.put(VehicleContract.EventEntry.COL_EVENTTYPE, mEventType);
            values.put(VehicleContract.EventEntry.COL_DESCRIPTION, mDescription.getText().toString());

            rowID = (int)db.insertWithOnConflict(VehicleContract.EventEntry.TABLE,
                    null,
                    values,
                    SQLiteDatabase.CONFLICT_REPLACE);

            super.storeImageLinks (db, rowID, Constants.RequestCode.REQUEST_EVENT);

            Log.d(TAG, "New event entered to database");
        } else if (mActionId != -1) {
            values.put(VehicleContract.EventEntry.COL_DATE, mDateLong);
            if (mMileage.length() > 0) {
                int iMileage = VehileDatabaseApplication.ConvertStringToIntNoRound(mMileage.getText().toString());
                values.put(VehicleContract.EventEntry.COL_MILEAGE, iMileage);
            }
            if (mExpense.length() > 0) {
                int expense = VehileDatabaseApplication.ConvertStringToInt(mExpense.getText().toString());
                values.put(VehicleContract.EventEntry.COL_EXPENSE, expense);
            }
            values.put(VehicleContract.EventEntry.COL_EVENTTYPE, mEventType);
            values.put(VehicleContract.EventEntry.COL_DESCRIPTION, mDescription.getText().toString());

            db.update(VehicleContract.EventEntry.TABLE, values, VehicleContract.EventEntry._ID + " = " + mActionId, null);
            rowID = mActionId;

            Log.d(TAG, "Existing event updated from database");
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
                super.storeImageLinks (db, mActionId, Constants.RequestCode.REQUEST_EVENT);
            }
        }
    }
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        String itemText = (String)parent.getItemAtPosition(pos);

        Log.d(TAG, "Event type selected: " + itemText);
        mEventType = pos;

    }

    public void onNothingSelected(AdapterView<?> parent) {
    }
}
