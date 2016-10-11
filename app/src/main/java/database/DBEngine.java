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

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBEngine extends SQLiteOpenHelper {
    private static final String TAG = "DBEngine";
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
                VehicleContract.VehicleEntry._ID                   + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                VehicleContract.VehicleEntry.COL_VINCODE           + " TEXT, " +
                VehicleContract.VehicleEntry.COL_MAKE              + " TEXT, " +
                VehicleContract.VehicleEntry.COL_MODEL             + " TEXT, " +
                VehicleContract.VehicleEntry.COL_YEAR              + " INT, " +
                VehicleContract.VehicleEntry.COL_REGPLATE          + " TEXT, " +
                VehicleContract.VehicleEntry.COL_DESCRIPTION       + " TEXT, " +
                VehicleContract.VehicleEntry.COL_FUEL_UNIT_ID      + " INT, " +
                VehicleContract.VehicleEntry.COL_ODOMETER_UNIT_ID  + " INT, " +
                VehicleContract.VehicleEntry.COL_IMAGEPATH         + " TEXT" +
                " );";
        Log.d(TAG, "createTable: " + createTable);
        db.execSQL(createTable);

        createTable = "CREATE TABLE " + VehicleContract.FuelingEntry.TABLE + " ( " +
                VehicleContract.FuelingEntry._ID                   + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                VehicleContract.FuelingEntry.COL_VEHICLEID         + " TEXT, " +
                VehicleContract.FuelingEntry.COL_DATE              + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                VehicleContract.FuelingEntry.COL_AMOUNT            + " INT, " +
                VehicleContract.FuelingEntry.COL_MILEAGE           + " INT, " +
                VehicleContract.FuelingEntry.COL_FULL              + " INT, " +
                VehicleContract.FuelingEntry.COL_PRISE             + " INT, " +
                VehicleContract.FuelingEntry.COL_DESCRIPTION       + " TEXT" +
                " );";
        Log.d(TAG, "createTable: " + createTable);
        db.execSQL(createTable);

        createTable = "CREATE TABLE " + VehicleContract.EventEntry.TABLE + " ( " +
                VehicleContract.EventEntry._ID                     + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                VehicleContract.EventEntry.COL_VEHICLEID           + " TEXT, " +
                VehicleContract.EventEntry.COL_DATE                + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                VehicleContract.EventEntry.COL_EVENTID             + " INT, " +
                VehicleContract.EventEntry.COL_MILEAGE             + " INT, " +
                VehicleContract.EventEntry.COL_PRISE               + " INT, " +
                VehicleContract.EventEntry.COL_DESCRIPTION         + " TEXT" +
                " );";
        Log.d(TAG, "createTable: " + createTable);
        db.execSQL(createTable);

        createTable = "CREATE TABLE " + VehicleContract.ServiceEntry.TABLE + " ( " +
                VehicleContract.ServiceEntry._ID                   + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                VehicleContract.ServiceEntry.COL_VEHICLEID         + " TEXT, " +
                VehicleContract.ServiceEntry.COL_DATE              + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                VehicleContract.ServiceEntry.COL_SERVICETYPE       + " INT, " +
                VehicleContract.ServiceEntry.COL_MILEAGE           + " INT, " +
                VehicleContract.ServiceEntry.COL_PRISE             + " INT, " +
                VehicleContract.ServiceEntry.COL_DESCRIPTION       + " TEXT" +
                " );";
        Log.d(TAG, "createTable: " + createTable);
        db.execSQL(createTable);

        createTable = "CREATE TABLE " + VehicleContract.ServiceTypeEntry.TABLE + " ( " +
                VehicleContract.ServiceTypeEntry.COL_SERVICETYPE   + " INTEGER PRIMARY KEY NOT NULL, " +
                VehicleContract.ServiceTypeEntry.COL_DESCRIPTION   + " TEXT" +
                " );";
        Log.d(TAG, "createTable: " + createTable);
        db.execSQL(createTable);

        createTable = "CREATE TABLE " + VehicleContract.EventTypeEntry.TABLE + " ( " +
                VehicleContract.EventTypeEntry.COL_EVENTTYPE       + " INTEGER PRIMARY KEY NOT NULL, " +
                VehicleContract.EventTypeEntry.COL_DESCRIPTION     + " TEXT" +
                " );";
        Log.d(TAG, "createTable: " + createTable);

        db.execSQL(createTable);
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

}
