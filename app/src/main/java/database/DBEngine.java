/*++

Module Name:

DBEngine.java

Abstract:

This module implements DBEngine open helper for the SQLite database.
Database is used to store relational database tables into one file.
This project store vehicle data into SQLite database using following tables:
- EVENTS
- EVENT_TYPE
- FUELING
- SERVICE
- SERVCE_TYPE
- VECHILES

Environment:

Android

Copyright (C) 2016 Vesa Eskola.

--*/

package database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import fi.vesaeskola.vehicledatabase.R;
import fi.vesaeskola.vehicledatabase.VehileDatabaseApplication;

public class DBEngine extends SQLiteOpenHelper {
    private static final String TAG = "DBEngine";
    private ArrayList<String> mServiceTypes;
    private ArrayList<String> mEventTypes;


    public DBEngine(Context context) {
        super(context, VehicleContract.DB_NAME, null, VehicleContract.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String query = "select sqlite_version() AS sqlite_version";
        SQLiteDatabase db_x = SQLiteDatabase.openOrCreateDatabase(":memory:", null);

        Cursor cursor = db_x.rawQuery(query, null);
        String sqliteVersion = "";
        if (cursor.moveToNext()) {
            sqliteVersion = cursor.getString(0);
            Log.d(TAG, "sqliteVersion: " + sqliteVersion);
        }


        String createTable = "CREATE TABLE " + VehicleContract.VehicleEntry.TABLE + " ( " +
                VehicleContract.VehicleEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                VehicleContract.VehicleEntry.COL_VINCODE + " TEXT, " +
                VehicleContract.VehicleEntry.COL_MAKE + " TEXT, " +
                VehicleContract.VehicleEntry.COL_MODEL + " TEXT, " +
                VehicleContract.VehicleEntry.COL_YEAR + " INT, " +
                VehicleContract.VehicleEntry.COL_REGPLATE + " TEXT, " +
                VehicleContract.VehicleEntry.COL_DESCRIPTION + " TEXT, " +
                VehicleContract.VehicleEntry.COL_FUEL_UNIT_ID + " INT, " +
                VehicleContract.VehicleEntry.COL_ODOMETER_UNIT_ID + " INT, " +
                VehicleContract.VehicleEntry.COL_IMAGEPATH + " TEXT" +
                " );";
        Log.d(TAG, "createTable: " + createTable);
        db.execSQL(createTable);

        createTable = "CREATE TABLE " + VehicleContract.FuelingEntry.TABLE + " ( " +
                VehicleContract.FuelingEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                VehicleContract.FuelingEntry.COL_VEHICLEID + " TEXT, " +
                VehicleContract.FuelingEntry.COL_DATE + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                VehicleContract.FuelingEntry.COL_AMOUNT + " INT, " +
                VehicleContract.FuelingEntry.COL_MILEAGE + " INT, " +
                VehicleContract.FuelingEntry.COL_FULL + " INT, " +
                VehicleContract.FuelingEntry.COL_EXPENSE + " INT, " +
                VehicleContract.FuelingEntry.COL_DESCRIPTION + " TEXT" +
                " );";
        Log.d(TAG, "createTable: " + createTable);
        db.execSQL(createTable);

        createTable = "CREATE TABLE " + VehicleContract.EventEntry.TABLE + " ( " +
                VehicleContract.EventEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                VehicleContract.EventEntry.COL_VEHICLEID + " TEXT, " +
                VehicleContract.EventEntry.COL_DATE + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                VehicleContract.EventEntry.COL_EVENTTYPE + " INT, " +
                VehicleContract.EventEntry.COL_MILEAGE + " INT, " +
                VehicleContract.EventEntry.COL_EXPENSE + " INT, " +
                VehicleContract.EventEntry.COL_DESCRIPTION + " TEXT" +
                " );";
        Log.d(TAG, "createTable: " + createTable);
        db.execSQL(createTable);

        createTable = "CREATE TABLE " + VehicleContract.ServiceEntry.TABLE + " ( " +
                VehicleContract.ServiceEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                VehicleContract.ServiceEntry.COL_VEHICLEID + " TEXT, " +
                VehicleContract.ServiceEntry.COL_DATE + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                VehicleContract.ServiceEntry.COL_SERVICETYPE + " INT, " +
                VehicleContract.ServiceEntry.COL_MILEAGE + " INT, " +
                VehicleContract.ServiceEntry.COL_EXPENSE + " INT, " +
                VehicleContract.ServiceEntry.COL_DESCRIPTION + " TEXT" +
                " );";
        Log.d(TAG, "createTable: " + createTable);
        db.execSQL(createTable);

        createTable = "CREATE TABLE " + VehicleContract.ServiceTypeEntry.TABLE + " ( " +
                VehicleContract.ServiceTypeEntry._ID + " INTEGER PRIMARY KEY NOT NULL, " +
                VehicleContract.ServiceTypeEntry.COL_DESCRIPTION + " TEXT" +
                " );";
        Log.d(TAG, "createTable: " + createTable);
        db.execSQL(createTable);

        createTable = "CREATE TABLE " + VehicleContract.EventTypeEntry.TABLE + " ( " +
                VehicleContract.EventTypeEntry._ID + " INTEGER PRIMARY KEY NOT NULL, " +
                VehicleContract.EventTypeEntry.COL_DESCRIPTION + " TEXT" +
                " );";
        Log.d(TAG, "createTable: " + createTable);
        db.execSQL(createTable);

        createTable = "CREATE TABLE " + VehicleContract.ImageLinkEntry.TABLE + " ( " +
                VehicleContract.ImageLinkEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                VehicleContract.ImageLinkEntry.COL_ACTIONTYPE + " INT, " +
                VehicleContract.ImageLinkEntry.COL_ACTIONID + " INT, " +
                VehicleContract.ImageLinkEntry.COL_IMAGEPATH + " TEXT, " +
                VehicleContract.ImageLinkEntry.COL_DESCRIPTION + " TEXT" +
                " );";
        Log.d(TAG, "createTable: " + createTable);
        db.execSQL(createTable);

        createInitialData (db);


    }

    private void createInitialData (SQLiteDatabase db) {

        ContentValues values = new ContentValues();

        // Default service types begin
        final String[] serviceTypes = VehileDatabaseApplication.getAppContext().getResources().getStringArray(R.array.service_types);

        for (int i = 0; i < serviceTypes.length; i++) {
            values.clear();
            values.put(VehicleContract.ServiceTypeEntry.COL_DESCRIPTION, serviceTypes[i]);
            db.insertWithOnConflict(VehicleContract.ServiceTypeEntry.TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
            Log.d(TAG, "Default service type: " + serviceTypes[i] + " created");
        }
        // Default service types end


        final String[] eventTypes = VehileDatabaseApplication.getAppContext().getResources().getStringArray(R.array.event_types);
        for (int i = 0; i < eventTypes.length; i++) {
            values.clear();
            values.put(VehicleContract.EventTypeEntry.COL_DESCRIPTION, eventTypes[i]);
            db.insertWithOnConflict(VehicleContract.EventTypeEntry.TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
            Log.d(TAG, "Default event type: " + eventTypes[i] + " created");
        }
        // Basic event types end

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + VehicleContract.VehicleEntry.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + VehicleContract.FuelingEntry.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + VehicleContract.EventEntry.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + VehicleContract.ServiceEntry.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + VehicleContract.ServiceTypeEntry.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + VehicleContract.EventTypeEntry.TABLE);
        onCreate(db);
    }

    public void delete(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + VehicleContract.VehicleEntry.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + VehicleContract.FuelingEntry.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + VehicleContract.EventEntry.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + VehicleContract.ServiceEntry.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + VehicleContract.ServiceTypeEntry.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + VehicleContract.EventTypeEntry.TABLE);
        onCreate(db);
    }

    public void deleteVehicle (int vehicleID) {
        SQLiteDatabase db = this.getReadableDatabase();

        db.delete(VehicleContract.VehicleEntry.TABLE, VehicleContract.VehicleEntry._ID + "=?", new String[]{"" + vehicleID});

        // TBD: handle IMAGELINKS table before lost action Id's
        db.delete(VehicleContract.FuelingEntry.TABLE, VehicleContract.EventEntry.COL_VEHICLEID + "=?", new String[]{"" + vehicleID});
        db.delete(VehicleContract.EventEntry.TABLE, VehicleContract.FuelingEntry.COL_VEHICLEID + "=?", new String[]{"" + vehicleID});
        db.delete(VehicleContract.ServiceEntry.TABLE, VehicleContract.ServiceEntry.COL_VEHICLEID + "=?", new String[]{"" + vehicleID});
    }

    public void deleteAttachmentLink (int attachmentID) {
        SQLiteDatabase db = this.getReadableDatabase();

        db.delete(VehicleContract.ImageLinkEntry.TABLE, VehicleContract.ImageLinkEntry._ID + "=?", new String[]{"" + attachmentID});
    }

    /*
    public void removeVehicleImageLink (int vehicleID) {
        SQLiteDatabase db = this.getReadableDatabase();
        //db.delete(VehicleContract.ImageLinkEntry.TABLE, VehicleContract.ImageLinkEntry._ID + "=?", new String[]{"" + attachmentID});
    }
    */

    public void updateVehicleImagePath (int vehicleID, String imagePath) {
        ContentValues values = new ContentValues();
        values.put(VehicleContract.VehicleEntry.COL_IMAGEPATH, imagePath);

        SQLiteDatabase db = this.getReadableDatabase();

        db.update(VehicleContract.VehicleEntry.TABLE, values, "_id=" + vehicleID, null);
    }

    public ArrayList<String> getServiceTypeList () {
        if (mServiceTypes == null) {
            mServiceTypes = new ArrayList<String>();
            // Read all services types from db to multiSelectionSpinner widget
            String selectQuery = "SELECT  * FROM " + VehicleContract.ServiceTypeEntry.TABLE;
            Log.d(TAG, "selectQuery: " + selectQuery);
            SQLiteDatabase db = getReadableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex(VehicleContract.ServiceTypeEntry._ID));
                String serviceType = cursor.getString(cursor.getColumnIndex(VehicleContract.ServiceTypeEntry.COL_DESCRIPTION));
                mServiceTypes.add(serviceType);
                Log.d(TAG, "service type[" + id + "]: " + serviceType);
            }
        }
        return mServiceTypes;
    }

    public ArrayList<String> getEventTypeList () {
        if (mEventTypes == null) {
            mEventTypes = new ArrayList<String>();
            // Read all services types from db to multiSelectionSpinner widget
            String selectQuery = "SELECT  * FROM " + VehicleContract.EventTypeEntry.TABLE;
            Log.d(TAG, "selectQuery: " + selectQuery);
            SQLiteDatabase db = getReadableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex(VehicleContract.EventTypeEntry._ID));
                String eventType = cursor.getString(cursor.getColumnIndex(VehicleContract.EventTypeEntry.COL_DESCRIPTION));
                mEventTypes.add(eventType);
                Log.d(TAG, "event type[" + id + "]: " + eventType);
            }
        }
        return mEventTypes;
    }

}
