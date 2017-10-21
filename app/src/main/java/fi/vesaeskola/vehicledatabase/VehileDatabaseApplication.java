/*++

Module Name:

vehicleDatabaseApplication.java

Abstract:

Implements a static method to get application context.

Copyright (C) 2016 Vesa Eskola.

--*/

package fi.vesaeskola.vehicledatabase;

import android.app.Application;
import android.content.Context;
import android.util.Log;


public class VehileDatabaseApplication extends Application {
    private static final String TAG = "VehileDBApplication";

    private static Context context;

    public void onCreate () {
        super.onCreate ();

        VehileDatabaseApplication.context = getApplicationContext ();
    }

    public static Context getAppContext() {
        return VehileDatabaseApplication.context;
    }

    /*++
    Routine Description:

    Convert String to integer using roundation rules ("100.5562" -> "100.56")
    Return Value: int. Integer containing the converted value
    --*/
    public static int ConvertStringToInt(String Value)
    {
        // Conversion from text -> float -> 100 x int
        Value = Value.replace(',','.');
        Float fValue= Float.parseFloat(Value);
        return (int)((fValue + 0.005) * 100.0);
    }

    /*++
    Routine Description:

    Convert integer value to String using roundation rules (100 51 -> "100.51")
    Return Value: String. String containing the converted value
    --*/
    public static String ConvertIntToPlatformString (int value)
    {
        float fValue = value / (float)100.0;
        String sValue = String.format("%.02f", fValue) ;
        sValue = sValue.replace(',','.');
        return sValue;
    }

    /*++
    Routine Description:

    Convert String to integer using roundation rules ("100.5562" -> "100.56")
    Return Value: int. Integer containing the converted value
    --*/
    public static int ConvertStringToIntNoRound(String Value) {
        // Conversion from text -> float -> 100 x int
        int iValue = -1;

        Value = Value.replace(',', '.');
        try {
            iValue = Integer.parseInt(Value);
        } catch (NumberFormatException nfe) {
            Log.d(TAG, "Could not parse " + nfe);
        }
        return iValue;
    }
}
