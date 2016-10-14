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

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import android.app.Activity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import database.DBEngine;

import static android.support.v4.content.FileProvider.getUriForFile;


public class ActionEntryActivity extends AppCompatActivity {
    private static final String TAG = "ActionEntryActivity";
    private static final String AUTHORITY="fi.vesaeskola.vehicledatabase.fileprovider";

    protected DBEngine mDatabaseEngine;
    protected int mVehicleId = -1;
    protected int mActionId = -1;
    public long mDateLong;
    public EditText mMileage, mExpense, mDescription;
    protected String mCurrentPhotoPath;

    private static final String JPEG_FILE_PREFIX = "IMG_";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // SQLiteOpenHelper based helper to open or create database.
        // Note: Will delete existing data if new database structure is used.
        mDatabaseEngine = new DBEngine(this);

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
    }

    protected void showDatePickerDialog(View view) {

        DatePickerFragment newFragment = new DatePickerFragment();

        // User edit existing action, date read from database will be set as initial
        // value for the date picker
        if (mActionId != -1) {
            newFragment.setDate (mDateLong);
        }

        android.app.FragmentManager fragMan = getFragmentManager();
        newFragment.show(fragMan, "datePicker");
    }

    static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            // 1: setPic (scale image and set into layout widget)
            setPic();

            // 2: galleryAddPic()
            galleryAddPic();

            mCurrentPhotoPath = null;
        }
    }


    public void pickImageWithCamera(View view) {
        Log.d(TAG, "pickImageWithCamera");

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";
        File f = null;
        try {
            f = File.createTempFile (imageFileName, ".jpg", getFilesDir());
        }
        catch (IOException ex) {
            Log.d(TAG, "createImageFile caused exception");
            mCurrentPhotoPath = null;
            ex.printStackTrace();
            return;
        }

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            Uri imageUri = FileProvider.getUriForFile(this, AUTHORITY, f);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            Log.d(TAG, "pickImageWithCamera. start activity to take photo");
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);

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

}
