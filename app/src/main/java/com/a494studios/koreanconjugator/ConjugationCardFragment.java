package com.a494studios.koreanconjugator;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.linearlistview.LinearListView;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ConjugationCardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ConjugationCardFragment extends Fragment {

    private TextView textView;
    private LinearListView listView;
    private String heading;
    private List<ConjugationQuery.Conjugation> conjugations;

    public ConjugationCardFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ConjugationCardFragment.
     */
    public static ConjugationCardFragment newInstance() {
        ConjugationCardFragment fragment = new ConjugationCardFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_conjugation_card, container, false);
        textView = view.findViewById(R.id.conjCard_heading);
        listView = view.findViewById(R.id.conjCard_list);

        if(heading != null){
            textView.setText(heading);
        }
        if(conjugations != null){
            listView.setAdapter(new ConjugationAdapter(conjugations));
        }

        return view;
    }

    public void setHeading(final String heading){
        this.heading = heading;
        if(textView != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    textView.setText(heading);
                }
            });
        }
    }

    public void setConjugations(List<ConjugationQuery.Conjugation> conjugations) {
        this.conjugations = conjugations;
        if (listView != null) {
            final ConjugationAdapter adapter = new ConjugationAdapter(conjugations);
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    listView.setAdapter(adapter);
                }
            });
        }
    }
}
