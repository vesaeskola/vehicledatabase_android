/*++

Module Name:

ConfirmationDialogFragment.java

Abstract:

Dialog implementation

Environment:

Android

Copyright (C) 2016 Vesa Eskola.

--*/

package fi.vesaeskola.vehicledatabase;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
//import android.support.v4.app.DialogFragment;
//import android.support.v7.app.AlertDialog;

public class ConfirmationDialogFragment extends DialogFragment {
    private static final String TAG = "ConfDialogFragment";
    private int mConfDialogReason;
    private int mIconId;
    private String mConfirmationTitle;
    private String mConfirmationMessage;

    public interface NoticeDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog, int ConfDialogReason);
        public void onDialogNegativeClick(DialogFragment dialog, int ConfDialogReason);
    }

    // Use this instance of the interface to deliver action events
    NoticeDialogListener mListener;

    public void configure (int confDialogReason, int iconId, String confirmationTitle, String confirmationMessage) {
        mConfDialogReason = confDialogReason;
        mIconId = iconId;
        mConfirmationTitle = confirmationTitle;
        mConfirmationMessage = confirmationMessage;
    }

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (NoticeDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                // Set Dialog Icon
                .setIcon(mIconId)
                // Set Dialog Title
                .setTitle(mConfirmationTitle)
                // Set Dialog Message
                .setMessage(mConfirmationMessage)

        // Positive button
        .setPositiveButton(R.string.confirmation_dialog_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            mListener.onDialogPositiveClick(ConfirmationDialogFragment.this, mConfDialogReason);
            }
        })
        // Negative Button
        .setNegativeButton(R.string.confirmation_dialog_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,	int which) {
            mListener.onDialogNegativeClick(ConfirmationDialogFragment.this, mConfDialogReason);
            }
        }).create();
    }


    /*
    public interface NoticeDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
    }


    // Use this instance of the interface to deliver action events
    NoticeDialogListener mListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        //LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        //builder.setView(inflater.inflate(R.layout.dialog_signin, null))

        // Add action buttons
        builder.setPositiveButton(R.string.confirmation_dialog_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Log.d(TAG, "User clicked OK button");
            }
        });

        builder.setNegativeButton(R.string.confirmation_dialog_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Log.d(TAG, "User cancelled the dialog");
            }
        });

        return builder.create();
    }
    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (NoticeDialogListener) context;
            } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString()
                + " must implement NoticeDialogListener");
        }
    }
    */
}
