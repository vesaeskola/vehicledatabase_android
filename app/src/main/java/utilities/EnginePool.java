/*++

Module Name:

EnginePool.java

Abstract:

This module implements EnginePool class used to maintain engine lifetime. Client code is able to
request common engines at anytime.

Environment:

Android

Copyright (C) 2016 Vesa Eskola.

--*/

package utilities;


import database.DBEngine;
import fi.vesaeskola.vehicledatabase.VehileDatabaseApplication;

public class EnginePool {
    private static final String TAG = "EnginePool";
    static private DBEngine mDatabaseEngine = null;

    public EnginePool() {
    }

    public static Object getEngine(String engineName) {
        if (engineName == "DBEngine") {
            if (mDatabaseEngine == null) {
                mDatabaseEngine = new DBEngine(VehileDatabaseApplication.getAppContext());
            }
            return (Object)mDatabaseEngine;
        } else {
            return null;
        }
    }
}

