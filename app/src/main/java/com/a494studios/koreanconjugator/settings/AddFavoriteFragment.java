package com.a494studios.koreanconjugator.settings;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.a494studios.koreanconjugator.R;
import com.a494studios.koreanconjugator.parsing.Favorite;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AddFavoriteFragmentListener} interface
 * to handle interaction events.
 * Use the {@link AddFavoriteFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddFavoriteFragment extends DialogFragment implements DialogInterface.OnClickListener {
    public static final String ARG_NAMES = "NAMES";
    private static final int ITEM_LAYOUT = R.layout.item_spinner;

    private EditText nameEditText;
    private Spinner conjSpinner;
    private Spinner speechLevelSpinner;
    private CheckBox honorificBox;
    private AddFavoriteFragmentListener mListener;
    private HashMap<String,Boolean> conjData;

    public AddFavoriteFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment TimePickerFragment.
     */
    public static AddFavoriteFragment newInstance(HashMap<String,Boolean> data) {
        AddFavoriteFragment frag =  new AddFavoriteFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_NAMES,data);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            conjData = (HashMap<String,Boolean>)getArguments().getSerializable(ARG_NAMES);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Set up builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(R.layout.fragment_add_favorites);
        builder.setPositiveButton(getString(android.R.string.ok),this);
        builder.setNegativeButton(getString(android.R.string.cancel),this);

        // Build and set up dialog view
        final AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
        speechLevelSpinner = dialog.findViewById(R.id.addFav_speechLevelSpinner);
        conjSpinner = dialog.findViewById(R.id.addFav_conjSpinner);
        nameEditText = dialog.findViewById(R.id.addFav_name);
        honorificBox = dialog.findViewById(R.id.addFav_checkbox);

        // Set up speechLevel spinner
        speechLevelSpinner.setAdapter(ArrayAdapter.createFromResource(getContext(), R.array.formality, ITEM_LAYOUT));
        speechLevelSpinner.setVisibility(View.GONE);
        speechLevelSpinner.setEnabled(false);

        // Set up conj spinner
        ArrayList<String> conjNames = new ArrayList<>(conjData.keySet());
        Collections.sort(conjNames); // sort conjugations alphabetically
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),ITEM_LAYOUT,conjNames);
        adapter.setDropDownViewResource(ITEM_LAYOUT);
        conjSpinner.setAdapter(adapter);
        conjSpinner.setEnabled(true);

        conjSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selection = adapter.getItem(i);

                if(!nameEditText.getText().toString().isEmpty()){
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                }

                boolean hasSpeechLevel = conjData.get(selection);
                speechLevelSpinner.setEnabled(hasSpeechLevel);
                if(hasSpeechLevel) {
                    speechLevelSpinner.setVisibility(View.VISIBLE);
                } else {
                    speechLevelSpinner.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        nameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!nameEditText.getText().toString().isEmpty() && !conjSpinner.getSelectedItem().toString().equals("Select a Form…")){
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                }else{
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        return dialog;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof AddFavoriteFragmentListener) {
            mListener = (AddFavoriteFragmentListener) context;
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
        if(which == DialogInterface.BUTTON_NEGATIVE){
            dialog.dismiss();
        } else {
            if (nameEditText.getText().toString().isEmpty()) {
                Toast.makeText(getContext(), "Invalid input", Toast.LENGTH_LONG).show();
            } else {
                String name = nameEditText.getText().toString();
                String conjName = conjSpinner.getSelectedItem().toString().trim();
                if(conjData.get(conjName)) {
                    conjName += " " + speechLevelSpinner.getSelectedItem().toString().trim();
                }
                boolean honorific = honorificBox.isChecked();
                mListener.onFavoriteAdded(new Favorite(name,conjName.toLowerCase(),honorific));
            }
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
    public interface AddFavoriteFragmentListener {
        void onFavoriteAdded(Favorite entry);
    }
}
