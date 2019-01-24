package com.a494studios.koreanconjugator.utils;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.a494studios.koreanconjugator.R;

/**
 * A simple {@link Fragment} subclass.
 * to handle interaction events.
 * Use the {@link SimpleCardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SimpleCardFragment extends Fragment {
    // the fragment initialization parameters
    private static final String ARG_HEADING = "heading";
    private static final String ARG_CONTENT = "content";

    private String heading;
    private String content;
    private TextView headingView;
    private TextView contentView;

    public SimpleCardFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param heading Title of card
     * @param content Content for card to display.
     * @return A new instance of fragment SimpleCardFragment.
     */
    public static SimpleCardFragment newInstance(String heading, String content) {
        SimpleCardFragment fragment = new SimpleCardFragment();
        Bundle args = new Bundle();
        args.putString(ARG_HEADING, heading);
        args.putString(ARG_CONTENT, content);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            heading = getArguments().getString(ARG_HEADING);
            content = getArguments().getString(ARG_CONTENT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_simple_card, container, false);
        headingView = view.findViewById(R.id.frag_simplecard_heading);
        contentView = view.findViewById(R.id.frag_simplecard_content);

        if(heading != null){
            headingView.setText(heading);
        }
        if(content != null){
            contentView.setText(content);
        }
        return view;
    }

    public void setHeading(String heading){
        this.heading = heading;
        headingView.setText(heading);
    }

    public void setContent(String content){
        this.content = content;
        contentView.setText(content);
    }
}
