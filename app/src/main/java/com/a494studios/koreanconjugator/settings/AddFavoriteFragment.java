package com.a494studios.koreanconjugator.settings;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;

import com.a494studios.koreanconjugator.R;
import com.a494studios.koreanconjugator.parsing.Category;
import com.a494studios.koreanconjugator.parsing.Conjugation;
import com.a494studios.koreanconjugator.parsing.Form;
import com.a494studios.koreanconjugator.parsing.Formality;
import com.a494studios.koreanconjugator.parsing.Tense;

import java.util.AbstractMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AddFavoriteFragmentListener} interface
 * to handle interaction events.
 * Use the {@link AddFavoriteFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddFavoriteFragment extends DialogFragment implements DialogInterface.OnClickListener {
    private static final String title = "Create Favorite";

    private EditText nameEditText;
    private Spinner formSpinner;
    private Spinner formalitySpinner;
    private Spinner tenseSpinner;
    private AddFavoriteFragmentListener mListener;

    public AddFavoriteFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment TimePickerFragment.
     */
    public static AddFavoriteFragment newInstance() {
        AddFavoriteFragment fragment = new AddFavoriteFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(R.layout.fragment_add_favorites);
        builder.setTitle(title);
        builder.setPositiveButton(getString(android.R.string.ok),this);
        builder.setNegativeButton(getString(android.R.string.cancel),this);

        Dialog dialog = builder.create();
        dialog.show();
        formalitySpinner = dialog.findViewById(R.id.addFav_formalitySpinner);
        formSpinner = dialog.findViewById(R.id.addFav_formSpinner);
        tenseSpinner = dialog.findViewById(R.id.addFav_tenseSpinner);
        nameEditText = dialog.findViewById(R.id.addFav_name);

        formalitySpinner.setEnabled(false);
        tenseSpinner.setEnabled(false);

        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.forms, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        formSpinner.setAdapter(adapter);
        formSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selection = adapter.getItem(i).toString().toLowerCase();
                if(selection.equals("select a form…")){
                    return;
                }

                formalitySpinner.setEnabled(true);
                tenseSpinner.setEnabled(true);
                formalitySpinner.setAdapter(ArrayAdapter.createFromResource(getContext(),
                        R.array.formality, android.R.layout.simple_spinner_item));
                if(selection.equals(Form.DECLARATIVE.toString())){
                    tenseSpinner.setAdapter(ArrayAdapter.createFromResource(getContext(),
                            R.array.tense_dec, android.R.layout.simple_spinner_item));
                }else if(selection.equals(Form.INQUISITIVE.toString())){
                    tenseSpinner.setAdapter(ArrayAdapter.createFromResource(getContext(),
                            R.array.tense_inq, android.R.layout.simple_spinner_item));
                }else if(selection.equals(Form.IMPERATIVE.toString())
                        || selection.equals(Form.PROPOSITIVE.toString())){
                    tenseSpinner.setAdapter(ArrayAdapter.createFromResource(getContext(),
                            R.array.tense_imp_prop, android.R.layout.simple_spinner_item));
                }else{
                    formalitySpinner.setEnabled(false);
                    tenseSpinner.setEnabled(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

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
            throw new RuntimeException(context.toString()
                    + " must implement TimerPickerFragmentListener");
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
            return;
        }

        String name = nameEditText.getText().toString();
        Form form = Conjugation.generateForm(formSpinner.getSelectedItem().toString().toLowerCase());
        Formality formality = null;
        Tense tense = null;
        if(!(form == Form.NOMINAL || form == Form.CON_AND || form == Form.CON_IF)){
            tense = Conjugation.generateTense(tenseSpinner.getSelectedItem().toString().toLowerCase());
            formality = Conjugation.generateFormality(formalitySpinner.getSelectedItem().toString().toLowerCase());
        }
        Category[] categories = {formality,form,tense};
        mListener.onFavoriteAdded(new AbstractMap.SimpleEntry<>(name,categories));
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
        void onFavoriteAdded(Map.Entry<String,Category[]> entry);
    }
}
