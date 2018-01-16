package com.a494studios.koreanconjugator.utils;


import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

/**
 * A simple {@link DialogFragment} subclass.
 */
public class ErrorDialogFragment extends DialogFragment implements DialogInterface.OnClickListener {

    private static final String TITLE = "Error Occurred";
    private static final String MSG = "Something went wrong while loading this page, please contact support or try again later.";
    private DialogInterface.OnClickListener positiveListener = null;

    public ErrorDialogFragment() {
        // Required empty public constructor
    }

    public static ErrorDialogFragment newInstance() {
        ErrorDialogFragment frag = new ErrorDialogFragment();
        Bundle args = new Bundle();
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(TITLE);
        builder.setMessage(MSG);
        if(positiveListener != null) {
            builder.setPositiveButton(getResources().getString(android.R.string.ok), positiveListener);
        }else{
            builder.setPositiveButton(getResources().getString(android.R.string.ok),this);
        }

        return builder.create();
    }

    public ErrorDialogFragment setOnPositiveListener(DialogInterface.OnClickListener listener){
        this.positiveListener = listener;
        return this;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        //Empty on Purpose
    }

    @Override
    public void onDismiss(DialogInterface dialog){
        super.onDismiss(dialog);
        if (positiveListener != null) {
            positiveListener.onClick(dialog, -1);
        }
    }
}
