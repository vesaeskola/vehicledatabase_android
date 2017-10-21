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
    public static final int PERMISSIONS_REQUEST_READ_CONTACTS_WRITE_EXTERNAL_STORAGE = 79;

    public class RequestCode {
        public static final int REQUEST_FUELING = 1;
        public static final int REQUEST_SERVICE = 2;
        public static final int REQUEST_EVENT = 3;
        public static final int REQUEST_IMAGE_CAPTURE = 4;
        public static final int REQUEST_VEHICLE_INFO = 5;
    }

    public class ConfirmationDialogReason {
        public static final int CONF_REASON_TEST = 0;
        public static final int CONF_REASON_DELETE_VEHICLE = 1;
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
