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
    public class ActionType {
        public static final int ACTION_TYPE_FUELINGS = 1;
        public static final int ACTION_TYPE_SERVICES = 2;
        public static final int ACTION_TYPE_EVENTS = 3;
        public static final int ACTION_TYPE_VEHICLE = 4;
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
