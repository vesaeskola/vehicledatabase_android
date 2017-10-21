/*++

Module Name:

AttachmentViewerActivity.java

Abstract:

This module is the attachment list viewer activity page. Page contain ListView populated with
attachment image content.

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
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import database.DBEngine;
import database.VehicleContract;
import utilities.EnginePool;

public class AttachmentViewerActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {
    private static final String TAG = "AttachViewActivity";
    private DBEngine mDatabaseEngine;
    private ListView mAttachmentListView;
    private int mCurrentAttachmentId = 0;
    private int mActionId = 0;
    private int mActionTypeId = 0;
    private AttachmentListAdapter mAttachmentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attachment_viewer);

        // SQLiteOpenHelper based helper to open or create database.
        // Note: Will delete existing data if new database structure is used.
        mDatabaseEngine = (DBEngine) EnginePool.getEngine("DBEngine");

        mAttachmentListView = (ListView) findViewById(R.id.list_attachments);
        mAttachmentListView.setClickable(true);

        // TBD: setClickListenerHere
        Bundle bundle = getIntent().getExtras();

        mActionId = bundle.getInt("action_Id");
        mActionTypeId = bundle.getInt("action_Type");
        String actionDate = bundle.getString("action_date");
        String actionOdometer = bundle.getString("action_odometer");
        String vehicleMake = bundle.getString("vehicle_make");
        String vehicleModel = bundle.getString("vehicle_model");
        String vehicleRegPlate = bundle.getString("vehicle_regplate");


        TextView tvVehicleMake = (TextView) findViewById(R.id.vehicle_make);
        TextView tvVehicleModel = (TextView) findViewById(R.id.vehicle_model);
        TextView tvVehicleRegPlate = (TextView) findViewById(R.id.vehicle_regplate);

        TextView tvActionDate = (TextView) findViewById(R.id.action_date);
        TextView tvActionType = (TextView) findViewById(R.id.action_type);
        TextView tvActionOdometer = (TextView) findViewById(R.id.action_odometer);

        tvActionDate.setText(actionDate);
        tvActionOdometer.setText(actionOdometer);
        tvVehicleMake.setText(vehicleMake);
        tvVehicleModel.setText(vehicleModel);
        tvVehicleRegPlate.setText(vehicleRegPlate);

        switch (mActionTypeId) {
            case Constants.RequestCode.REQUEST_FUELING: {
                tvActionType.setText(R.string.title_fueling);
                break;
            }
            case Constants.RequestCode.REQUEST_SERVICE: {
                tvActionType.setText(R.string.title_service);
                break;
            }
            case Constants.RequestCode.REQUEST_EVENT: {
                tvActionType.setText(R.string.title_event);
                break;
            }
        }


        Log.d(TAG, "Attachments to show, actionId: " + mActionId + "actionType: " + mActionTypeId);

        // Update UI and the list view
        updateUI();
    }
    public void openPopup(View view) {
        View parent = (View) view.getParent();
        TextView textView = (TextView) parent.findViewById(R.id.title);
        String title = textView.getText().toString();
        mCurrentAttachmentId = (int)textView.getTag();
        Log.d(TAG, "openPopup: " + title + "mCurrentAttachmentId: " + mCurrentAttachmentId);

        // Find the button which open the popup menu
        Button menuBtn = (Button) findViewById(R.id.btn_popup);

        PopupMenu popup = new PopupMenu(AttachmentViewerActivity.this, menuBtn);
        popup.getMenuInflater().inflate(R.menu.menu_04, popup.getMenu());
        popup.setOnMenuItemClickListener(AttachmentViewerActivity.this);
        popup.show();

    }

    public void OnOpenMenu(View view) {
        View parent = (View) view.getParent();
        Log.d(TAG, "onOpenMenu");

        PopupMenu popup = new PopupMenu(this, view);
        popup.setOnMenuItemClickListener(AttachmentViewerActivity.this);
        MenuInflater inflater = popup.getMenuInflater();

        inflater.inflate(R.menu.menu_01, popup.getMenu());

        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuitem_remove_attachment_image: {
                Log.d(TAG, "R.id.menuitem_remove_attachment_image");
                mDatabaseEngine.deleteAttachmentLink (mCurrentAttachmentId);
                updateUI();
                return true;
            }
            case R.id.menuitem_delete_attachment_image: {
                Log.d(TAG, "R.id.menuitem_delete_attachment_image");

                //Context context = getApplicationContext();
                CharSequence text = "Not implemented, delete image from gallery application";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(this.getApplicationContext(), text, duration);
                toast.show();
                return true;
            }
            case R.id.menuitem_show_privacy_policy: {
                Log.d(TAG, "Menu item selected: Privacy policy");
                return true;
            }
            default: {
                return false;
            }
        }
    }

    /*
    @Override
    public void onBackPressed() {
        // This is the trick to force VehicleInfoActivity UI to be updated
        Intent returnIntent = new Intent();
        if (mVehicleModified) {
            Log.d(TAG, "onBackPressed: vehicle modified");
            setResult(Activity.RESULT_OK, returnIntent);
        } else {
            Log.d(TAG, "onBackPressed: vehicle not modified");
            setResult(Activity.RESULT_CANCELED, returnIntent);
        }
        finish();
    }
    */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK) {
            Log.d(TAG, "Activity result: OK");
        }
        else if (resultCode == Activity.RESULT_CANCELED) {
            Log.d(TAG, "Activity result CANCELED");
        }

    }

    private void updateUI() {
        Log.d(TAG, "updateUI");

        ArrayList<AttachmentListItem> attachmentList = new ArrayList<>();
        SQLiteDatabase db = mDatabaseEngine.getReadableDatabase();

        String selectQuery = "SELECT " + VehicleContract.ImageLinkEntry.COL_IMAGEPATH + ", "
                + VehicleContract.ImageLinkEntry._ID + ", "
                + VehicleContract.ImageLinkEntry.COL_DESCRIPTION +
                " FROM "  + VehicleContract.ImageLinkEntry.TABLE +
                " WHERE " + VehicleContract.ImageLinkEntry.COL_ACTIONID + " = " + mActionId +
                " AND " + VehicleContract.ImageLinkEntry.COL_ACTIONTYPE + " = " + mActionTypeId;



        Log.d(TAG, "selectQuery: " + selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);

        while (cursor.moveToNext()) {
            String imagePath = cursor.getString(cursor.getColumnIndex(VehicleContract.ImageLinkEntry.COL_IMAGEPATH));
            String description = cursor.getString(cursor.getColumnIndex(VehicleContract.ImageLinkEntry.COL_DESCRIPTION));
            int id = cursor.getInt(cursor.getColumnIndex(VehicleContract.ImageLinkEntry._ID));

            if ( description == null) {
                description=imagePath.substring(imagePath.lastIndexOf("/")+1);
            }
            AttachmentListItem attachment = new AttachmentListItem(id, imagePath, description);
            attachmentList.add(attachment);
        }

        if (mAttachmentListView != null) {
            if (mAttachmentAdapter == null) {
                mAttachmentAdapter = new AttachmentListAdapter(this, attachmentList);
                mAttachmentListView.setAdapter(mAttachmentAdapter);
            } else {
                mAttachmentAdapter.clear();
                mAttachmentAdapter.addAll(attachmentList);
                mAttachmentAdapter.notifyDataSetChanged();
            }
        }
    }

}
