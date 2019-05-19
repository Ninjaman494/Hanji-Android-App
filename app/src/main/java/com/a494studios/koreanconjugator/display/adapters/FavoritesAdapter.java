package com.a494studios.koreanconjugator.display.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.a494studios.koreanconjugator.ConjugationQuery;
import com.a494studios.koreanconjugator.R;

import java.util.ArrayList;
import java.util.Map;

public class FavoritesAdapter extends BaseAdapter {

    private ArrayList<Map.Entry<String, ConjugationQuery.Conjugation>> entries;
    private static final int RESOURCE_ID = R.layout.item_conjugation;

    public FavoritesAdapter(ArrayList<Map.Entry<String,ConjugationQuery.Conjugation>> entries) {
        this.entries = entries;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(RESOURCE_ID, viewGroup, false);
        }
        Map.Entry<String,ConjugationQuery.Conjugation> entry = entries.get(i);
        TextView typeView = view.findViewById(R.id.conjFormal);
        TextView conjView = view.findViewById(R.id.conjText);
        typeView.setText(entry.getKey());
        conjView.setText(entry.getValue().conjugation());
        return view;
    }

    public void addConjugation(Map.Entry<String, ConjugationQuery.Conjugation> entry, int index) {
        if(index < entries.size()) {
            entries.add(index, entry);
        } else {
            entries.add(entry);
        }
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return entries.size();
    }

    @Override
    public Object getItem(int i) {
        return entries.get(i);
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
