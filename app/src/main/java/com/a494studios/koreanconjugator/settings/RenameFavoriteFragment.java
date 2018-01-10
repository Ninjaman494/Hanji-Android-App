package com.a494studios.koreanconjugator.settings;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.widget.EditText;

import com.a494studios.koreanconjugator.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RenameFavoriteFragmentListener} interface
 * to handle interaction events.
 * Use the {@link RenameFavoriteFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RenameFavoriteFragment extends DialogFragment implements DialogInterface.OnClickListener {
    public static final String ARG_POS = "POSITION";

    private EditText nameEditText;
    private int position;
    private RenameFavoriteFragmentListener mListener;

    public RenameFavoriteFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment TimePickerFragment.
     */
    public static RenameFavoriteFragment newInstance(int position) {
        RenameFavoriteFragment fragment = new RenameFavoriteFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_POS,position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            position = getArguments().getInt(ARG_POS);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(R.layout.fragment_rename_favorites);
        builder.setPositiveButton(getString(android.R.string.ok),this);
        builder.setNegativeButton(getString(android.R.string.cancel),this);
        builder.setTitle("Rename Favorite");

        Dialog dialog = builder.create();
        dialog.show();
        nameEditText = dialog.findViewById(R.id.renameFav_name);
        return dialog;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof RenameFavoriteFragmentListener) {
            mListener = (RenameFavoriteFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement AddFavoriteFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == DialogInterface.BUTTON_NEGATIVE) {
            dialog.dismiss();
        } else {
            String name = nameEditText.getText().toString();
            mListener.onRenameSelected(name, position);
        }
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface RenameFavoriteFragmentListener {
        void onRenameSelected(String newName, int position);
    }
}
