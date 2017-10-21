/*++

Module Name:

ActionEntryActivity.java

Abstract:

This class is collect data related to basic activity.

Environment:

Android

Copyright (C) 2016 Vesa Eskola.

--*/
package fi.vesaeskola.vehicledatabase;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;

import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.Locale;

import database.DBEngine;
import database.VehicleContract;
import utilities.EnginePool;

public class ActionEntryActivity extends ImageAttachmentActivity implements PopupMenu.OnMenuItemClickListener {
    private static final String TAG = "ActionEntryActivity";

    protected DBEngine mDatabaseEngine;
    protected int mVehicleId = -1;
    protected int mActionId = -1;
    protected int mImageLinkCount = 0;
    public long mDateLong;
    public EditText mMileage, mExpense, mDescription;
    protected ArrayList<String> mImagePathList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // SQLiteOpenHelper based helper to open or create database.
        // Note: Will delete existing data if new database structure is used.
        mDatabaseEngine = (DBEngine) EnginePool.getEngine("DBEngine");

        Bundle bundle = getIntent().getExtras();

        // If caller provide vehicleId it means this is new action for the vehicle
        if (getIntent().hasExtra("vehicle_Id")) {
            mVehicleId = bundle.getInt("vehicle_Id");
            Log.d(TAG, "VehicleId: " + mVehicleId);
        }

        // If caller provide actionId it mean user want to edit existing action
        if (getIntent().hasExtra("action_Id")) {
            mActionId = bundle.getInt("action_Id");
            Log.d(TAG, "ActionId: " + mActionId);
        }

        if (getIntent().hasExtra("vehicle_make")) {
            String make = bundle.getString("vehicle_make");
            TextView tvMake = (TextView)findViewById(R.id.vehicle_make);
            tvMake.setText(make);

        }
        if (getIntent().hasExtra("vehicle_model")) {
            String model = bundle.getString("vehicle_model");
            TextView tvModel = (TextView)findViewById(R.id.vehicle_model);
            tvModel.setText(model);
        }
        if (getIntent().hasExtra("vehicle_regplate")) {
            String regplate = bundle.getString("vehicle_regplate");
            TextView tvRegPlate = (TextView)findViewById(R.id.vehicle_regplate);
            tvRegPlate.setText(regplate);
        }



        final Calendar calendar = Calendar.getInstance();
        Date now = calendar.getTime();
        mDateLong = now.getTime();

        mMileage = (EditText) findViewById(R.id.mileage);
        mExpense = (EditText) findViewById(R.id.expense);
        mDescription = (EditText) findViewById(R.id.description);

        TextView textCurrencySymbol = (TextView) findViewById(R.id.currency_unit);

        // TBD: Move following code ito general localization package
        Currency currency = Currency.getInstance(Locale.getDefault());
        Log.v("TAG", currency.getSymbol());
        textCurrencySymbol.setText(currency.getSymbol());
    }

    public void OnOpenMenu(View view) {
        View parent = (View) view.getParent();
        Log.d(TAG, "onOpenMenu");

        PopupMenu popup = new PopupMenu(this, view);
        popup.setOnMenuItemClickListener(ActionEntryActivity.this);
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
    public void onPickDate(View view) {
        DatePickerFragment newFragment = new DatePickerFragment();

        // User edit existing action, date read from database will be set as initial
        // value for the date picker
        if (mActionId != -1) {
            newFragment.setDate(mDateLong);
        }

        android.app.FragmentManager fragMan = getFragmentManager();
        newFragment.show(fragMan, "datePicker");
    }

    public void openMenu(View view) {
        View parent = (View) view.getParent();
        Log.d(TAG, "openMenu, selected vehicle: " + mVehicleId);

        PopupMenu popup = new PopupMenu(this, view);
        popup.setOnMenuItemClickListener(ActionEntryActivity.this);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_02, popup.getMenu());

        popup.show();
    }

    protected void storeImageLinks (SQLiteDatabase db, int rowID, int actionType) {
        ContentValues values = new ContentValues();

        for (int i = 0; i < mImagePathList.size(); i++) {

            // Store image link
            String imageLink = mImagePathList.get(i);
            values.put(VehicleContract.ImageLinkEntry.COL_ACTIONID, rowID);
            values.put(VehicleContract.ImageLinkEntry.COL_ACTIONTYPE, actionType);  // 1 = fueling, 2 = service 3 = event
            values.put(VehicleContract.ImageLinkEntry.COL_IMAGEPATH, imageLink);

            db.insertWithOnConflict(VehicleContract.ImageLinkEntry.TABLE,
                    null,
                    values,
                    SQLiteDatabase.CONFLICT_REPLACE);
            Log.d(TAG, "New image link entered to database: " + mImagePathList.get(i));
        }

        mImagePathList.clear();
    }

    //protected void restoreImageLinks (SQLiteDatabase db, int actionID, int actionType) {
    protected void readImageLinkCount (SQLiteDatabase db, int actionID, int actionType) {
        String selectQuery = "SELECT  * FROM " + VehicleContract.ImageLinkEntry.TABLE +
                " WHERE " + VehicleContract.ImageLinkEntry.COL_ACTIONID + " = " + actionID +
                " AND " + VehicleContract.ImageLinkEntry.COL_ACTIONTYPE + " = " + actionType;
        Log.d(TAG, "restore Image links: selectQuery: " + selectQuery);

        Cursor cursor = db.rawQuery(selectQuery, null);

        while (cursor.moveToNext()) {
            String imagePath = cursor.getString(cursor.getColumnIndex(VehicleContract.ImageLinkEntry.COL_IMAGEPATH));
            if (imagePath != null) {
                mImageLinkCount++;
            }
            //mImagePathList.add(imagePath);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {

            Log.d(TAG, "OnActivityResult: RESULT_OK");

            if (requestCode == Constants.RequestCode.REQUEST_IMAGE_CAPTURE) {

                if (mCurrentPhotoPath != null) {
                    mImagePathList.add(mCurrentPhotoPath);
                }

                // TBD: Count attachments here and update R.id.attachment_text
                ImageView attachmentIcon = (ImageView) findViewById(R.id.attachment_icon);
                TextView attachmentText = (TextView) findViewById(R.id.attachment_text);

                attachmentIcon.setVisibility(View.VISIBLE);
                attachmentText.setVisibility(View.VISIBLE);
                mImageLinkCount += 1;
                attachmentText.setText(String.valueOf(mImageLinkCount));

                // Get thumpnail bitmap of taken image
                //Bundle extras = data.getExtras();
                //Bitmap imageBitmap = (Bitmap) extras.get("data");
                //mImageView.setImageBitmap(imageBitmap);

                super.imageCaptured();

                // Store image link to db already now


            }
        }
    }
}
