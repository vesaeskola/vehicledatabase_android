/*++

Module Name:

VehicleContract.java

Abstract:

This module define the tables used in SQLite database. This project store vehicle data
 into SQLite database using following tables:
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

import android.provider.BaseColumns;


public class VehicleContract {
    public static final String DB_NAME = "vehicledatabase.db";
    public static final int DB_VERSION = 2;

    // A provider isn't required to have a primary key, and it isn't required to use _ID as the
    // column name of a primary key if one is present. However, if you want to bind data from
    // a provider to a ListView, one of the column names has to be _ID. This requirement is
    // explained in more detail in the section Displaying query results.
    //
    // This is the reason why BaseColumns is used.
    //
    // This class represent one row of VEHICLES table
    public class VehicleEntry implements BaseColumns {
        public static final String TABLE = "VEHICLES";
        public static final String COL_VINCODE = "VinCode";
        public static final String COL_MAKE = "Make";
        public static final String COL_MODEL = "Model";
        public static final String COL_YEAR = "Year";
        public static final String COL_REGPLATE = "RegPlate";
        public static final String COL_DESCRIPTION = "Description";
        public static final String COL_FUEL_UNIT_ID = "FuelUnitId";
        public static final String COL_ODOMETER_UNIT_ID = "OdometerUnitId";
        public static final String COL_IMAGEPATH = "ImagePath";
    }

    // This class represent one row of FUELING table
    public class FuelingEntry implements BaseColumns {
        public static final String TABLE = "FUELLING";
        public static final String COL_VEHICLEID = "VehicleId";
        public static final String COL_DATE = "Date";
        public static final String COL_AMOUNT = "Amount";
        public static final String COL_MILEAGE = "Mileage";
        public static final String COL_FULL = "Full";
        public static final String COL_PRISE = "Prise";
        public static final String COL_DESCRIPTION = "Description";
    }

    // This class represent one row of EVENTS table
    public class EventEntry implements BaseColumns {
        public static final String TABLE = "EVENTS";
        public static final String COL_VEHICLEID = "VehicleId";
        public static final String COL_DATE = "Date";
        public static final String COL_EVENTID = "EventId";
        public static final String COL_MILEAGE = "Mileage";
        public static final String COL_PRISE = "Prise";
        public static final String COL_DESCRIPTION = "Description";
    }

    // This class represent one row of SERVICE table
    public class ServiceEntry implements BaseColumns {
        public static final String TABLE = "SERVICE";
        public static final String COL_VEHICLEID = "VehicleId";
        public static final String COL_DATE = "Date";
        public static final String COL_SERVICETYPE = "ServiceType";
        public static final String COL_MILEAGE = "Mileage";
        public static final String COL_PRISE = "Prise";
        public static final String COL_DESCRIPTION = "Description";
    }

    // This class represent one row of SERVICE_TYPE table
    public class ServiceTypeEntry implements BaseColumns {
        public static final String TABLE = "SERVICE_TYPE";
        public static final String COL_SERVICETYPE = "ServiceType";
        public static final String COL_DESCRIPTION = "Description";
    }

    // This class represent one row of EVENT_TYPE table
    public class EventTypeEntry implements BaseColumns {
        public static final String TABLE = "EVENT_TYPE";
        public static final String COL_EVENTTYPE = "EventType";
        public static final String COL_DESCRIPTION = "Description";
    }
}
