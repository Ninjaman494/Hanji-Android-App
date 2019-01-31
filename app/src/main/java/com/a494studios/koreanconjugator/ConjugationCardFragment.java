package com.a494studios.koreanconjugator;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.linearlistview.LinearListView;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ConjugationCardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ConjugationCardFragment extends Fragment {
    private static final String ARG_HEADING = "HEADINGS";
    private static final String ARG_CONJUGATIONS = "CONJUGATIONS";

    private String heading;
    private List<ConjugationQuery.Conjugation> conjugations;
    private TextView textView;
    private LinearListView listView;


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
    public static ConjugationCardFragment newInstance(String heading, ArrayList<ConjugationQuery.Conjugation> conjugations) {
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
            conjugations = (ArrayList<ConjugationQuery.Conjugation>)getArguments().getSerializable(ARG_CONJUGATIONS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_conjugation_card, container, false);
        textView = view.findViewById(R.id.conjCard_heading);
        listView = view.findViewById(R.id.conjCard_list);

        if(conjugations != null && !conjugations.isEmpty()) {
            ConjugationAdapter adapter = new ConjugationAdapter(conjugations);
            listView.setAdapter(adapter);
        }
        if(heading != null) {
            textView.setText(heading);
        }

        return view;
    }

    public void setHeading(String heading){
        this.heading = heading;
        textView.setText(heading);
    }

    public void setConjugations(List<ConjugationQuery.Conjugation> conjugations){
        this.conjugations = conjugations;
        final ConjugationAdapter adapter = new ConjugationAdapter(conjugations);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                listView.setAdapter(adapter);
            }
        });
    }

}
