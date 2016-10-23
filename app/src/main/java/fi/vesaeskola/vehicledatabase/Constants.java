/*++

Module Name:

Constants.java

Abstract:

This class declare project wide constants

Environment:

Android

Copyright (C) 2016 Vesa Eskola.

--*/

package fi.vesaeskola.vehicledatabase;

public class Constants {

    public static final String AUTHORITY = "fi.vesaeskola.vehicledatabase.fileprovider";
    public static final String JPEG_FILE_PREFIX = "IMG_";

    public class RequestCode {
        public static final int REQUEST_IMAGE_CAPTURE = 1;
        public static final int REQUEST_NEW_VEHICLE_STEP1 = 2;
        public static final int REQUEST_EDIT_VEHICLE = 3;
        public static final int REQUEST_NEW_FUELING = 4;
        public static final int REQUEST_NEW_SERVICE = 5;
        public static final int REQUEST_NEW_EVENT = 6;
        //public static final int REQUEST_NEW_VEHICLE = 7;


    }



    public class OdometerUnitId {
        public static final int ODOMETER_UNIT_MILES = 1;
        public static final int ODOMETER_UNIT_KM = 2;
    }

    public class FuelUnitId {
        public static final int FUEL_UNIT_GALLON = 1;
        public static final int FUEL_UNIT_LITER = 2;
    }

}
