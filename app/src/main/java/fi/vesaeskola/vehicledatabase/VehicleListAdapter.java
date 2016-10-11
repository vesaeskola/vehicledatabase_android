/*++

Module Name:

VehicleAdapter.java

Abstract:

This module the connect MainActivity ListView and vehicle list model together.

Environment:

Android

Copyright (C) 2016 Vesa Eskola.

--*/

package fi.vesaeskola.vehicledatabase;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class VehicleListAdapter extends ArrayAdapter<VehicleListItem> {
    public VehicleListAdapter(Context context, ArrayList<VehicleListItem> vehicles) {
        super(context, 0, vehicles);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        VehicleListItem vehicle = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.vehicle_list_item, parent, false);
        }

        // Lookup view for data population
        Button bOpenVehicle = (Button) convertView.findViewById(R.id.vehicle_open);
        bOpenVehicle.setTag(position);

        //bOpenVehicle.setTag() .setId(position);
        TextView tvMake = (TextView) convertView.findViewById(R.id.vehicle_make);
        TextView tvModel = (TextView) convertView.findViewById(R.id.vehicle_model);
        TextView tvRegPlate = (TextView) convertView.findViewById(R.id.vehicle_regplate);
        TextView tvVinCode = (TextView) convertView.findViewById(R.id.vehicle_vincode);
        // Populate the data into the template view using the data object
        tvMake.setText(vehicle.make);
        tvModel.setText(vehicle.model);
        tvRegPlate.setText(vehicle.regplate);
        tvVinCode.setText(vehicle.vincode);
        // Return the completed view to render on screen
        return convertView;
    }
}
