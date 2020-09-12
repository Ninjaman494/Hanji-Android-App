package com.a494studios.koreanconjugator.search;


import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.a494studios.koreanconjugator.R;
import com.a494studios.koreanconjugator.conjugator.ConjugatorActivity;

/**
 * A simple {@link DialogFragment} subclass.
 */
public class NoResultsFragment extends DialogFragment implements DialogInterface.OnClickListener {

    private DialogInterface.OnClickListener onCancelListener = null;

    public NoResultsFragment() {
        // Required empty public constructor
    }

    public static NoResultsFragment newInstance(DialogInterface.OnClickListener listener) {
        NoResultsFragment frag = new NoResultsFragment();
        frag.setOnCancelListener(listener);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        String title = getString(R.string.no_results_title);
        String msg = getString(R.string.no_results_msg);

        builder.setTitle(title);
        builder.setMessage(msg);

        String cancelBtnText = getResources().getString(android.R.string.cancel);
        if(onCancelListener != null) {
            builder.setNegativeButton(cancelBtnText, onCancelListener);
        } else {
            builder.setNegativeButton(cancelBtnText,this);
        }

        String okBtnText = getResources().getString(android.R.string.ok);
        builder.setPositiveButton(okBtnText, (dialogInterface, i) -> {
            Intent intent = new Intent(getContext(), ConjugatorActivity.class);
            startActivity(intent);
        });

        return builder.create();
    }

    private void setOnCancelListener(DialogInterface.OnClickListener onCancelListener){
        this.onCancelListener = onCancelListener;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        //Empty on Purpose
    }

    @Override
    public void onDismiss(DialogInterface dialog){
        super.onDismiss(dialog);
        if (onCancelListener != null) {
            onCancelListener.onClick(dialog, -1);
        }
    }
}
