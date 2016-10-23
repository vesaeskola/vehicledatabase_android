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

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.Locale;

import database.DBEngine;
import utilities.EnginePool;


public class ActionEntryActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {
    private static final String TAG = "ActionEntryActivity";

    protected DBEngine mDatabaseEngine;
    protected int mVehicleId = -1;
    protected int mActionId = -1;
    public long mDateLong;
    public EditText mMileage, mExpense, mDescription;
    protected String mCurrentPhotoPath;
    protected int mAttachmentCount = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // SQLiteOpenHelper based helper to open or create database.
        // Note: Will delete existing data if new database structure is used.
        //mDatabaseEngine = new DBEngine(this);
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
        inflater.inflate(R.menu.vehicle_info, popup.getMenu());

        popup.show();
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.RequestCode.REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            // 1: setPic (scale image and set into layout widget)
            //setPic();

            // 2: galleryAddPic()
            galleryAddPic();

            mAttachmentCount = mAttachmentCount + 1;

            // Change the camera icon
            ImageView imageView = (ImageView) findViewById(R.id.attachment_icon);
            imageView.setImageResource(R.drawable.klemmari);

            TextView textAttachmentCout = (TextView) findViewById(R.id.attachment_text);
            textAttachmentCout.setText(String.valueOf(mAttachmentCount));

            mCurrentPhotoPath = null;
        }
    }


    public void pickImageWithCamera(View view) {
        Log.d(TAG, "pickImageWithCamera");

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = Constants.JPEG_FILE_PREFIX + timeStamp + "_";
        File f = null;
        try {
            f = File.createTempFile(imageFileName, ".jpg", getFilesDir());
        } catch (IOException ex) {
            Log.d(TAG, "createImageFile caused exception");
            mCurrentPhotoPath = null;
            ex.printStackTrace();
            return;
        }

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            Uri imageUri = FileProvider.getUriForFile(this, Constants.AUTHORITY , f);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            Log.d(TAG, "pickImageWithCamera. start activity to take photo");
            startActivityForResult(takePictureIntent, Constants.RequestCode.REQUEST_IMAGE_CAPTURE);

            mCurrentPhotoPath = f.getAbsolutePath();
        }
    }


    // Invoke the system's media scanner to add vehicle database photos to the Media Provider's
    // database, making it available in the Android Gallery application and to other apps.
    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    // TBD: if target size is zero do nothing !
    /*
    private void setPic() {
        // Get the dimensions of the View
        ImageView imageView = (ImageView) findViewById(R.id.actionImageView);

        int targetW = imageView.getWidth();
        int targetH = imageView.getHeight();

        // TBD: Consider how layout is able to define image size, currently the other
        // dimension is zero
        if (targetW == 0 || targetH == 0 )
        {
            targetW = 480;
            targetH = 360;
        }
        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        imageView.setImageBitmap(bitmap);
        imageView.setVisibility(View.VISIBLE);
    }
    */
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuitem_show_privacy_policy: {
                Log.d(TAG, "Menu item selected: Privacy policy");

                new AlertDialog.Builder(this)
                        .setTitle(R.string.privacy_policy_title)
                        .setMessage(R.string.privacy_policy_content)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                Log.d(TAG, "Vehicle delete selected");
                            }
                        })
                        .setNegativeButton(android.R.string.no, null).show();
                return true;
            }

            default: {
                return false;
            }
        }
    }

}
