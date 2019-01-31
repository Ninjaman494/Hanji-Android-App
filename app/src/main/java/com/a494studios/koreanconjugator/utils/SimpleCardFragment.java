package com.a494studios.koreanconjugator.utils;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.a494studios.koreanconjugator.R;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * to handle interaction events.
 * Use the {@link SimpleCardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SimpleCardFragment extends Fragment {

    private TextView headingView;
    private TextView contentView;
    private Button moreButton;

    public SimpleCardFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SimpleCardFragment.
     */
    public static SimpleCardFragment newInstance() {
        return new SimpleCardFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_simple_card, container, false);
        headingView = view.findViewById(R.id.frag_simplecard_heading);
        contentView = view.findViewById(R.id.frag_simplecard_content);
        moreButton = view.findViewById(R.id.frag_simplecard_button);
        return view;
    }

    public void setHeading(String heading){
        headingView.setText(heading);
    }

    public void setContent(String content){
        contentView.setText(content);
    }

    public void setContent(List<String> contentList){
        contentView.setText(contentList.get(0));
    }
}
