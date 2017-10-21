/*++

Module Name:

VehicleAdapter.java

Abstract:

This module the connect ActionListActivity's ListView and action list model together.

Environment:

Android

Copyright <C> 2016 Vesa Eskola.

--*/

package fi.vesaeskola.vehicledatabase;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;


public class ActionListAdapter extends ArrayAdapter<ActionListItem> {
    public ActionListAdapter(Context context, ArrayList<ActionListItem> actions) {
        super(context, 0, actions);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        ActionListItem action = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.action_list_item, parent, false);
        }

        //bOpenVehicle.setTag() .setId(position);
        TextView tvRow1_Col1 = (TextView) convertView.findViewById(R.id.row1_col1);
        TextView tvRow1_Col2 = (TextView) convertView.findViewById(R.id.row1_col2);
        TextView tvRow2_Col1 = (TextView) convertView.findViewById(R.id.row2_col1);
        TextView tvRow2_Col2 = (TextView) convertView.findViewById(R.id.row2_col2);
        TextView tvRow3 = (TextView) convertView.findViewById(R.id.row3);


        ImageView attachmentIcon = (ImageView)convertView.findViewById(R.id.attachment_icon);
        TextView attachmentText = (TextView) convertView.findViewById(R.id.attachment_text);

        // Populate the data into the template view using the data object
        tvRow1_Col1.setText(action.row1_col1);
        tvRow1_Col2.setText(action.row1_col2);
        tvRow2_Col1.setText(action.row2_col1);
        tvRow2_Col2.setText(action.row2_col2);
        tvRow3.setText(action.row3);

        if (action.mAttachmentCount > 0) {
            attachmentText.setText(String.valueOf(action.mAttachmentCount));
            attachmentIcon.setVisibility(View.VISIBLE);
            attachmentText.setVisibility(View.VISIBLE);

            tvRow1_Col2.setClickable(true);
            tvRow2_Col2.setClickable(true);
            attachmentText.setClickable(true);
            attachmentIcon.setClickable(true);

        } else {
            attachmentIcon.setVisibility(View.INVISIBLE);
            attachmentText.setVisibility(View.INVISIBLE);

            tvRow1_Col2.setClickable(false);
            tvRow2_Col2.setClickable(false);
            attachmentText.setClickable(false);
            attachmentIcon.setClickable(false);


        }
        // Return the completed view to render on screen

        // Set tag of attachment icon to link action id and icon together.
        ImageView ivOpenAttachment = (ImageView) convertView.findViewById(R.id.attachment_icon);
        ivOpenAttachment.setTag(action.mActionId);

        return convertView;
    }
}

