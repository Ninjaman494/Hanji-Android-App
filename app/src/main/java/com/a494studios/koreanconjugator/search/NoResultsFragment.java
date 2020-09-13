package com.a494studios.koreanconjugator.search;


import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.a494studios.koreanconjugator.R;
import com.a494studios.koreanconjugator.conjugator.ConjugatorActivity;

import org.jetbrains.annotations.NotNull;

/**
 * A simple {@link DialogFragment} subclass.
 */
public class NoResultsFragment extends DialogFragment implements DialogInterface.OnClickListener {

    private static final String ARG_SEARCH_TERM = "search_term";

    private DialogInterface.OnClickListener onCancelListener = null;

    public NoResultsFragment() {
        // Required empty public constructor
    }

    static NoResultsFragment newInstance(String searchTerm,
                                         DialogInterface.OnClickListener listener) {
        NoResultsFragment frag = new NoResultsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_SEARCH_TERM, searchTerm);
        frag.setArguments(args);
        frag.setOnCancelListener(listener);
        return frag;
    }

    @NotNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        String title = getString(R.string.no_results_title);
        String msg = getString(R.string.no_results_msg);
        String searchTerm = getArguments().getString(ARG_SEARCH_TERM);

        builder.setTitle(title);
        builder.setMessage(msg);

        String cancelBtnText = getResources().getString(android.R.string.cancel);
        if(onCancelListener != null) {
            builder.setNegativeButton(cancelBtnText, onCancelListener);
        } else {
            builder.setNegativeButton(cancelBtnText,this);
        }

        String okBtnText = getResources().getString(R.string.no_results_positive_btn);
        builder.setPositiveButton(okBtnText, (dialogInterface, i) -> {
            Intent intent = new Intent(getContext(), ConjugatorActivity.class);
            intent.putExtra(ConjugatorActivity.EXTRA_TERM, searchTerm);
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
    public void onDismiss(@NotNull DialogInterface dialog){
        super.onDismiss(dialog);
        if (onCancelListener != null) {
            onCancelListener.onClick(dialog, -1);
        }
    }
}
