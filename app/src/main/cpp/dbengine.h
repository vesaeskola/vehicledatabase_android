/*++

Module Name:

dbengine.h

Abstract:

This module implements DatabaseEngine for the SQLite database.
Purpose is to store relational database tables into one file.
This project store vehicle data into SQLite database using following tables:
- EVENTS
- EVENT_TYPE
- FUELING
- SERVICE
- SERVCE_TYPE
- VECHILES

Environment:

Universal Windows Platform application

Copyright © 2016 Vesa Eskola.

--*/

#ifndef VEHICLEDATABASE_DBENGINE_H
#define VEHICLEDATABASE_DBENGINE_H


class DatabaseEngine {

public:
    /*++
    Routine Description:

    Constructor

    --*/
    DatabaseEngine();
};


#endif //VEHICLEDATABASE_DBENGINE_H
