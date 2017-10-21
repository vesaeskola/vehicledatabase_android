/*++

Module Name:

AttachmentListAdapter.java

Abstract:

This module the connect AttachmentViewerActivity's ListView and attachment list model together.

Environment:

Android

Copyright <C> 2016 Vesa Eskola.

--*/

package fi.vesaeskola.vehicledatabase;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

import utilities.Utilities;


public class AttachmentListAdapter extends ArrayAdapter<AttachmentListItem> implements BitmapWorkerTask.OnImageLoadedListener {
    private static final String TAG = "AttachListAdapter";
    private Context mContext;

    public AttachmentListAdapter(Context context, ArrayList<AttachmentListItem> attachments) {
        super(context, 0, attachments);
        this.mContext = context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        // Get the data item for this position
        final AttachmentListItem attachment = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.attachment_list_item, parent, false);
        }
        convertView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
            // TODO Auto-generated method stub
            Log.d(TAG, "Selected item # " + position);

            File file = new File(attachment.mImagePath);
            Uri imageUri = Uri.fromFile(file);

            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);

            intent.setDataAndType(imageUri, "image/*");
            ((Activity) mContext).startActivity(intent);
            }
        });

        TextView id = (TextView) convertView.findViewById(R.id.id);
        id.setText(String.valueOf(position+1));
        TextView title = (TextView) convertView.findViewById(R.id.title);
        //title.setText(attachment.mTitle);
        title.setText("Attachment: ");
        title.setTag(attachment.mId);


        ImageView imageView = (ImageView) convertView.findViewById(R.id.attachment_image);

        // Start asynchronous loading of bitmap
        ImageWorker imageWorker = new ImageWorker(mContext);
        imageWorker.setLoadingImage(R.drawable.loading_image);
        imageWorker.loadImage(attachment.mImagePath, imageView, this);


        // Return the completed view to render on screen
        return convertView;
    }

    @Override
    public void onImageLoaded(Bitmap bitmap) {

    }

}
