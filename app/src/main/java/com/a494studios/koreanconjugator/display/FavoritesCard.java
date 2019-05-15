package com.a494studios.koreanconjugator.display;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.a494studios.koreanconjugator.ConjugationActivity;
import com.a494studios.koreanconjugator.ConjugationQuery;
import com.a494studios.koreanconjugator.R;
import com.linearlistview.LinearListView;

import java.util.ArrayList;
import java.util.Map;

public class FavoritesCard implements DisplayCardBody {

    private View view;

    private String stem;
    private boolean honorific;
    private boolean isAdj;
    private ConjugationAdapter adapter;

    public FavoritesCard(ArrayList<Map.Entry<String,ConjugationQuery.Conjugation>> entries, String stem, boolean honorific, boolean isAdj) {
        this.adapter = new ConjugationAdapter(entries);
        this.stem = stem;
        this.honorific = honorific;
        this.isAdj = isAdj;
    }

    @Override
    public View addBodyView(Context context, ViewGroup parentView) {
        if(view == null) {
            view = View.inflate(context, R.layout.dcard_list, parentView);
        }
        LinearListView listView = view.findViewById(R.id.conjCard_list);
        listView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onButtonClick() {
        Intent i = new Intent(view.getContext(), ConjugationActivity.class);
        i.putExtra(ConjugationActivity.EXTRA_STEM,stem);
        i.putExtra(ConjugationActivity.EXTRA_HONORIFIC,honorific);
        i.putExtra(ConjugationActivity.EXTRA_ISADJ, isAdj);
        view.getContext().startActivity(i);
    }

    @Override
    public boolean shouldHideButton() {
        return false;
    }

    @Override
    public int getCount() {
        return adapter.getCount();
    }

    @Override
    public String getButtonText() {
        return "SEE ALL";
    }

    @Override
    public String getHeading() {
        return "Conjugations";
    }

    private class ConjugationAdapter extends BaseAdapter {

        private ArrayList<Map.Entry<String,ConjugationQuery.Conjugation>> entries;
        private static final int RESOURCE_ID = R.layout.item_conjugation;

        public ConjugationAdapter(ArrayList<Map.Entry<String,ConjugationQuery.Conjugation>> entries) {
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
}
