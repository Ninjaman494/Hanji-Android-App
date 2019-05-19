package com.a494studios.koreanconjugator.display.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.a494studios.koreanconjugator.ExamplesQuery;
import com.a494studios.koreanconjugator.R;

import java.util.List;

public class ExampleAdapter extends BaseAdapter {
    private static final int RESOURCE_ID = R.layout.item_example;
    private List<ExamplesQuery.Example> examples;

    public ExampleAdapter(List<ExamplesQuery.Example> examples){
        this.examples = examples;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(RESOURCE_ID, viewGroup, false);
        }

        ExamplesQuery.Example example = examples.get(i);
        TextView sentenceView = view.findViewById(R.id.item_example_sentence);
        TextView transView = view.findViewById(R.id.item_example_translation);
        sentenceView.setText(example.sentence());
        transView.setText(example.translation());
        return view;
    }

    @Override
    public int getCount() {
        return examples.size();
    }

    @Override
    public ExamplesQuery.Example getItem(int i) {
        return examples.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }
}
