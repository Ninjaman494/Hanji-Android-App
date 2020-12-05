package com.a494studios.koreanconjugator.utils;


import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AlertDialog;

import org.jetbrains.annotations.NotNull;

/**
 * A simple {@link DialogFragment} subclass.
 */
public class ErrorDialogFragment extends BaseDialogFragment implements DialogInterface.OnClickListener {

    private static final String TITLE = "Error Occurred";
    private static final String MSG = "Something went wrong while loading this page, please contact support or try again later.";
    private static final String ARG_TITLE = "title";
    private static final String ARG_MSG = "message";

    private DialogInterface.OnClickListener listener = null;

    public ErrorDialogFragment() {
        // Required empty public constructor
    }

    public static ErrorDialogFragment newInstance() {
        ErrorDialogFragment frag = new ErrorDialogFragment();
        Bundle args = new Bundle();
        frag.setArguments(args);
        return frag;
    }

    public static ErrorDialogFragment newInstance(String title,String message) {
        ErrorDialogFragment frag = new ErrorDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putString(ARG_MSG, message);
        frag.setArguments(args);
        return frag;
    }

    @NotNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        String title = getArguments().getString(ARG_TITLE, TITLE);
        String msg = getArguments().getString(ARG_MSG, MSG);

        builder.setTitle(title);
        builder.setMessage(msg);
        if(listener != null) {
            builder.setPositiveButton(getResources().getString(android.R.string.ok), listener);
        }else{
            builder.setPositiveButton(getResources().getString(android.R.string.ok),this);
        }

        return builder.create();
    }

    public ErrorDialogFragment setListener(DialogInterface.OnClickListener listener){
        this.listener = listener;
        return this;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        //Empty on Purpose
    }

    @Override
    public void onDismiss(@NotNull DialogInterface dialog){
        super.onDismiss(dialog);
        if (listener != null) {
            listener.onClick(dialog, -1);
        }
    }
}
