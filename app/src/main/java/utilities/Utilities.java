/*++

Module Name:

Utilities.java

Abstract:

This module implements helper class Utilities.

Environment:

Android

Copyright (C) 2016 Vesa Eskola.

--*/

package utilities;

import android.app.backup.BackupManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
//import android.graphics.Point;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

import fi.vesaeskola.vehicledatabase.VehileDatabaseApplication;


public class Utilities {
    private static final String TAG = "Utilities";

    // TBD: Use String abbreviateLongString instead of this method
    public static void abbreviateLongString (TextView tvDestination, String text, int maxLenght) {
        if (text == null) {
            tvDestination.setText("");
        } else if (text.length() < 1) {
            tvDestination.setText("");
        } else if (text.length() > maxLenght) {
            String shortDescription = text.substring(0, maxLenght);
            tvDestination.setText(shortDescription + "..");
        } else {
        tvDestination.setText(text);
        }
    }

    public static String abbreviateLongString (String text, int maxLenght) {
        if (text == null || text.length() < 1) {
            return "";
        } else if (text.length() > maxLenght) {
            return text.substring(0, maxLenght) + "..";
        } else {
            return text;
        }
    }

    //  Load and scale bitmap image from file
    public static void setupImage(ImageView image, int width, int height, String imagePath) {

        if (imagePath == null) {
            Log.d(TAG, "setupImage: imagePath == NULL -> Nothing to do");
            return;
        }

        // Clear current image
        image.setImageResource(android.R.color.transparent);
        //image.setImageBitmap(null);

        Bitmap bmp;

        // TBD: Clarify why following return 640x480 for the image, is it only in the emulator
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        Log.d(TAG, "setupImage: Image info loaded from file, width: " + photoW + ", height:" + photoH);

        // TBD: Consider integer usage here, what is real purpose of scaling ?
        // Decode the image file into a Bitmap sized to fill the View
        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/width, photoH/height);

        Log.d(TAG, "scaleFactor: " + scaleFactor);

        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        Bitmap bitmap = BitmapFactory.decodeFile(imagePath, bmOptions);
        image.setImageBitmap(bitmap);

    }

    //  Load and scale bitmap image from file
    public static Bitmap loadImage(String imagePath, int width, int height) {

        Log.d(TAG, "loadImage: " + imagePath + "width: " + width + "height: " + height);

        if (imagePath == null) {
            Log.d(TAG, "setupImage: imagePath == NULL -> Nothing to do");
            return null;
        }

        // Clear current image
        /*
        ImageView iv = (ImageView)image.get();
        if (iv == null) {
            Log.d(TAG, "loadImage: Weak reference of ImageView is NULL");
            return;
        }
        */
        //image.setImageBitmap(null);

        Bitmap bmp;

        // TBD: Clarify why following return 640x480 for the image, is it only in the emulator
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        Log.d(TAG, "setupImage: Image info loaded from file, width: " + photoW + ", height:" + photoH);

        // TBD: Consider integer usage here, what is real purpose of scaling ?
        // Decode the image file into a Bitmap sized to fill the View
        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/width, photoH/height);

        Log.d(TAG, "scaleFactor: " + scaleFactor);

        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        Bitmap bitmap = BitmapFactory.decodeFile(imagePath, bmOptions);
        return bitmap;
        //iv.setImageBitmap(bitmap);
    }

    public static void importDB(String currentDBPath, String backupDBPath) {
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();
            if (sd.canWrite()) {
                /*
                String currentDBPath = "//data//" + "<package name>"
                        + "//databases//" + "<database name>";*/

                //String backupDBPath = "<backup db filename>"; // From SD directory.
                File backupDB = new File(data, currentDBPath);
                File currentDB = new File(sd, backupDBPath);

                FileChannel src = new FileInputStream(backupDB).getChannel();
                FileChannel dst = new FileOutputStream(currentDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
                Toast.makeText(VehileDatabaseApplication.getAppContext(), "Import Successful!",
                        Toast.LENGTH_LONG).show();

            }
        } catch (Exception e) {

            Toast.makeText(VehileDatabaseApplication.getAppContext(), "Import Failed!", Toast.LENGTH_LONG)
                    .show();

        }
    }

    public static void exportDB(String currentDBPath, String backupDBPath) {

        //DatabaseBackupHelper bHelper = new DatabaseBackupHelper();

        BackupManager backupManager = new BackupManager(VehileDatabaseApplication.getAppContext());
        backupManager.dataChanged();

        /*
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            if (sd.canWrite()) {
                //String backupDBPath = "<destination>";

                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                FileChannel src = new FileInputStream(currentDB).getChannel();
                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
                Toast.makeText(VehileDatabaseApplication.getAppContext(), "Backup Successful!",
                        Toast.LENGTH_LONG).show();

            }
        } catch (Exception e) {

            Toast.makeText(VehileDatabaseApplication.getAppContext(), "Backup Failed!", Toast.LENGTH_LONG)
                    .show();

        }
        */
    }

}
