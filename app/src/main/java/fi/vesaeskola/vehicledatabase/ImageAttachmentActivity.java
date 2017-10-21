/*++

Module Name:

ImageAttachmentActivity.java

Abstract:

This class is handle image attachments

Environment:

Android

Copyright (C) 2016 Vesa Eskola.

--*/

package fi.vesaeskola.vehicledatabase;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.R.attr.visible;


public class ImageAttachmentActivity extends AppCompatActivity {
    private static final String TAG = "ImageAttAct.";
    protected String mCurrentPhotoPath;
    protected int mAttachmentCount = 0;


    public void pickImageWithCamera(View view) {
        Log.d(TAG, "pickImageWithCamera");

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed, we can request the permission.

                // TBD: Consider this ? String[] perms = {"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE"};
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constants.PERMISSIONS_REQUEST_READ_CONTACTS_WRITE_EXTERNAL_STORAGE);
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
        else {
            performPickImageWithCamera ();
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case Constants.PERMISSIONS_REQUEST_READ_CONTACTS_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Let's use camera app
                    performPickImageWithCamera ();
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }

    // Permission has been aldeary asked or now granted
    void performPickImageWithCamera () {
        String timeStamp = new SimpleDateFormat(getResources().getString(R.string.general_image_name_date_template)).format(new Date());
        String imageFileName = Constants.JPEG_FILE_PREFIX + timeStamp + "_";

        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        File file = null;
        try {
            file = File.createTempFile(imageFileName, ".jpg", storageDir);
            //File file2 = File.createTempFile(imageFileName, ".jpg", storageDir2);
        } catch (IOException ex) {
            Log.d(TAG, "createImageFile caused exception");
            ex.printStackTrace();
            return;
        }

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        String authority1 = Constants.AUTHORITY;  // "fi.vesaeskola.vehicledatabase.fileprovider";
        String authority2 = VehileDatabaseApplication.getAppContext().getPackageName() + ".fileprovider";



        Uri imageURI = FileProvider.getUriForFile(this, authority1, file);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageURI);

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, Constants.RequestCode.REQUEST_IMAGE_CAPTURE);
        }


        /*
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            Uri imageUri = FileProvider.getUriForFile(this, Constants.AUTHORITY, file);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            Log.d(TAG, "pickImageWithCamera. start activity to take photo");
            startActivityForResult(takePictureIntent, Constants.RequestCode.REQUEST_IMAGE_CAPTURE);
            */

            mCurrentPhotoPath = file.getAbsolutePath();
    }

    public void showAttachments(View view) {
        Log.d(TAG, "showAttachments");

    }

    protected void imageCaptured () {

        galleryAddPic();

        mCurrentPhotoPath = null;
    }

    // Invoke the system's media scanner to add vehicle database photos to the Media Provider's
    // database, making it available in the Android Gallery application and to other apps.
    private void galleryAddPic() {
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

}
