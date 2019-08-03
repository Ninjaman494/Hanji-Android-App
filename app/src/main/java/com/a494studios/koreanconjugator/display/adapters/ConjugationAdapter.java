package com.a494studios.koreanconjugator.display.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.a494studios.koreanconjugator.ConjugationQuery;
import com.a494studios.koreanconjugator.R;
import com.a494studios.koreanconjugator.type.SpeechLevel;

import java.util.List;
import java.util.Objects;

public class ConjugationAdapter extends BaseAdapter {

    private List<ConjugationQuery.Conjugation> conjugations;
    private static final int RESOURCE_ID = R.layout.item_conjugation;

    public ConjugationAdapter(List<ConjugationQuery.Conjugation> conjugations) {
        this.conjugations = Objects.requireNonNull(conjugations);
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(RESOURCE_ID, viewGroup, false);
        }
        ConjugationQuery.Conjugation c = conjugations.get(i);
        TextView typeView = view.findViewById(R.id.conjFormal);
        TextView conjView = view.findViewById(R.id.conjText);

        if(c.speechLevel() == SpeechLevel.NONE){
            typeView.setText(c.name());
        }else {
            String speechLevel = c.speechLevel().toString();
            speechLevel = speechLevel.replace('_', ' ').toLowerCase();
            typeView.setText(speechLevel);
        }
        conjView.setText(c.conjugation());

        return view;
    }

    @Override
    public int getCount() {
        return conjugations.size();
    }

    @Override
    public ConjugationQuery.Conjugation getItem(int i) {
        return conjugations.get(i);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
