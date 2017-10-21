/*++

Module Name:

DatabaseBackupHelper.java

Abstract:

This module implements helper to backup database files

Environment:

Android

Copyright (C) 2017 Vesa Eskola.

--*/

import android.app.backup.BackupAgentHelper;
import android.app.backup.FileBackupHelper;
import android.app.backup.SharedPreferencesBackupHelper;
import android.util.Log;

import database.VehicleContract;

public class DatabaseBackupHelper extends BackupAgentHelper {

    public final static String TAG = DatabaseBackupHelper.class.getName();

    @Override
    public void onCreate() {

        Log.v(TAG, "onCreate called");

        SharedPreferencesBackupHelper prefs = new SharedPreferencesBackupHelper(this, getPackageName() +
                "_preferences");
        addHelper(VehicleContract.DB_BACKUP_KEY_PREFIX, prefs);

        FileBackupHelper streams = new FileBackupHelper(this, "../databases/" + VehicleContract.DB_NAME);
        addHelper(VehicleContract.DB_NAME, streams);


        FileBackupHelper alarms = new FileBackupHelper(this, "../databases/" + VehicleContract.DB_NAME);
        addHelper(VehicleContract.DB_NAME, alarms);
    }

}
