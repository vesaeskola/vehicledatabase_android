package fi.vesaeskola.vehicledatabase;

/**
 * Created by vesae on 6.10.2016.
 */

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
