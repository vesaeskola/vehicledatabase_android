/*++

Module Name:

VehicleListAdapter.java

Abstract:

This module the connect VehicleListActivity's ListView and vehicle list model together.

Environment:

Android

Copyright (C) 2016 Vesa Eskola.

--*/

package fi.vesaeskola.vehicledatabase;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;


public class VehicleListAdapter extends ArrayAdapter<VehicleListItem> implements BitmapWorkerTask.OnImageLoadedListener {
    private static final String TAG = "VehicleListAdapter";
    private Context mContext;

    public VehicleListAdapter(Context context, ArrayList<VehicleListItem> vehicles) {
        super(context, 0, vehicles);
        this.mContext = context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final VehicleListItem vehicle = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.vehicle_list_item, parent, false);
        }

        convertView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Log.d(TAG, "elected item # " + position);

                // Open VehicleEntryBasicActivity page
                Intent intent;
                intent = new Intent(mContext, VehicleInfoActivity.class);
                intent.putExtra("vehicle_Id", vehicle.mVehicleId);


                // Here we set request code REQUEST_EDIT_VEHICLE to indicate user could edit vehicle while viewing the info of it
                ((Activity) mContext).startActivityForResult(intent, Constants.RequestCode.REQUEST_VEHICLE_INFO);
            }
        });

        // Lookup view for data population
        Button bOpenVehicle = (Button) convertView.findViewById(R.id.vehicle_open);
        bOpenVehicle.setTag(vehicle.mVehicleId);

        TextView tvMake = (TextView) convertView.findViewById(R.id.vehicle_make);
        TextView tvModel = (TextView) convertView.findViewById(R.id.vehicle_model);
        TextView tvRegPlate = (TextView) convertView.findViewById(R.id.vehicle_regplate);
        TextView tvVinCode = (TextView) convertView.findViewById(R.id.vehicle_vincode);
        // Populate the data into the template view using the data object
        tvMake.setText(vehicle.make);
        tvModel.setText(vehicle.model);
        tvRegPlate.setText(vehicle.regplate);
        tvVinCode.setText(vehicle.vincode);

        ImageView imageView = (ImageView) convertView.findViewById(R.id.vehicleThumbIcon);
        if (imageView.getDrawable() instanceof ImageWorker.AsyncDrawable) {
            Log.d(TAG, "Already loading this bitmap");
        } else {
            ImageWorker imageWorker = new ImageWorker(mContext);
            imageWorker.setLoadingImage(R.drawable.your_vehicle);
            imageWorker.loadImage(vehicle.mImagePath, imageView, this);
        }

        return convertView;
    }

    @Override
    public void onImageLoaded(Bitmap bitmap) {
        if (bitmap == null) {
            //ImageView imageView = (ImageView) convertView.findViewById(R.id.vehicleThumbIcon);
            //imageView.setImageResource(R.drawable.your_vehicle);
        }
    }
}
