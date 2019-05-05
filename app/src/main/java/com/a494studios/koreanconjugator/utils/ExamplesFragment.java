package com.a494studios.koreanconjugator.utils;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.a494studios.koreanconjugator.ExampleAdapter;
import com.a494studios.koreanconjugator.ExamplesQuery;
import com.a494studios.koreanconjugator.R;
import com.linearlistview.LinearListView;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ExamplesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ExamplesFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private List<ExamplesQuery.Example> examples;
    private LinearListView listView;


    public ExamplesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ExamplesFragment.
     */
    public static ExamplesFragment newInstance() {
        ExamplesFragment fragment = new ExamplesFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_examples, container, false);
        listView = view.findViewById(R.id.exCard_list);

        if(examples != null) {
            listView.setAdapter(new ExampleAdapter(examples));
        }

        return view;
    }

    public void setExamples(final List<ExamplesQuery.Example> examples){
        this.examples = examples;
        if(listView != null){
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    listView.setAdapter(new ExampleAdapter(examples));
                }
            });
        }
    }

}
