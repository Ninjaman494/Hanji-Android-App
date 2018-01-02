package com.a494studios.koreanconjugator;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.a494studios.koreanconjugator.parsing.Conjugation;
import com.linearlistview.LinearListView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ConjugationCardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ConjugationCardFragment extends Fragment {
    private static final String ARG_HEADING = "HEADINGS";
    private static final String ARG_CONJUGATIONS = "CONJUGATIONS";

    private String heading;
    private ArrayList<Conjugation> conjugations;


    public ConjugationCardFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param heading Parameter 1.
     * @param conjugations Parameter 2.
     * @return A new instance of fragment ConjugationCardFragment.
     */
    public static ConjugationCardFragment newInstance(String heading, ArrayList<Conjugation> conjugations) {
        ConjugationCardFragment fragment = new ConjugationCardFragment();
        Bundle args = new Bundle();
        args.putString(ARG_HEADING, heading);
        args.putSerializable(ARG_CONJUGATIONS, conjugations);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            heading = getArguments().getString(ARG_HEADING);
            conjugations = (ArrayList<Conjugation>)getArguments().getSerializable(ARG_CONJUGATIONS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_conjugation_card, container, false);
        TextView textView = view.findViewById(R.id.conjCard_heading);
        LinearListView listView = view.findViewById(R.id.conjCard_list);
        ConjugationAdapter adapter = new ConjugationAdapter(conjugations);

        textView.setText(heading);
        listView.setAdapter(adapter);

        return view;
    }

}
