/*++

Module Name:

VehicleListItem.java

Abstract:

This class is used to store and populate listview with vehicle basic information (make, model,
regplate, vincode).

Environment:

Android

Copyright (C) 2016 Vesa Eskola.

--*/

package fi.vesaeskola.vehicledatabase;

public class VehicleListItem {
    public String make;
    public String model;
    public String regplate;
    public String vincode;

    public VehicleListItem(String make, String model, String regplate, String vincode) {
        this.make = make;
        this.model = model;
        this.regplate = regplate;
        this.vincode = vincode;
    }
}
