package com.a494studios.koreanconjugator.display.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.a494studios.koreanconjugator.R;

import java.util.List;
import java.util.Objects;

public class ExplanationsAdapter extends BaseAdapter {
    private static final int RESOURCE_ID = R.layout.item_example;
    private List<String> explanations;

    public ExplanationsAdapter(List<String> explanations){
        this.explanations = Objects.requireNonNull(explanations);
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(RESOURCE_ID, viewGroup, false);
        }

        String explanation = explanations.get(i);
        TextView sentenceView = view.findViewById(R.id.item_example_sentence);
        TextView transView = view.findViewById(R.id.item_example_translation);
        int index = explanation.indexOf('(');
        if(index == -1){
            sentenceView.setText(explanation);
            transView.setVisibility(View.GONE);
        } else {
            String header = explanation.substring(0, index).trim();
            String sub = explanation.substring(index).trim();
            sentenceView.setText(header);
            transView.setText(sub);
        }
        return view;
    }

    @Override
    public int getCount() {
        return explanations.size();
    }

    @Override
    public String getItem(int i) {
        return explanations.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }
}
