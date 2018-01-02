package com.a494studios.koreanconjugator;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.a494studios.koreanconjugator.parsing.Conjugation;
import com.a494studios.koreanconjugator.parsing.Form;

import java.util.ArrayList;

public class ConjugationAdapter extends BaseAdapter {

    private ArrayList<Conjugation> conjugations;
    private static final int RESOURCE_ID = R.layout.item_conjugation;

    public ConjugationAdapter(ArrayList<Conjugation> conjugations) {
        this.conjugations = conjugations;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(RESOURCE_ID, viewGroup, false);
        }
        Conjugation c = conjugations.get(i);
        if(c.getForm() == Form.DECLARATIVE) {
            TextView typeView = view.findViewById(R.id.conjFormal);
            TextView conjView = view.findViewById(R.id.conjText);
            typeView.setText(c.getFormality().toString());
            conjView.setText(c.getConjugated());
        }else{
            view.setVisibility(View.GONE);
        }
        return view;
    }

    @Override
    public int getCount() {
        return conjugations.size();
    }

    @Override
    public Object getItem(int i) {
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
