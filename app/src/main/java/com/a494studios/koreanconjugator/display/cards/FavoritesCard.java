package com.a494studios.koreanconjugator.display.cards;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;

import com.a494studios.koreanconjugator.conjugations.ConjugationActivity;
import com.a494studios.koreanconjugator.ConjugationQuery;
import com.a494studios.koreanconjugator.R;
import com.a494studios.koreanconjugator.display.adapters.FavoritesAdapter;
import com.linearlistview.LinearListView;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class FavoritesCard implements DisplayCardBody {

    private View view;

    private String stem;
    private boolean honorific;
    private boolean isAdj;
    private FavoritesAdapter adapter;

    public FavoritesCard(ArrayList<Map.Entry<String,ConjugationQuery.Conjugation>> entries, String stem, boolean honorific, boolean isAdj) {
        this.adapter = new FavoritesAdapter(Objects.requireNonNull(entries));
        this.stem = Objects.requireNonNull(stem);
        this.honorific = honorific;
        this.isAdj = isAdj;
    }

    @Override
    public View addBodyView(Context context, ViewGroup parentView) {
        if(view == null) {
            view = View.inflate(context, R.layout.dcard_list, parentView);
        }
        LinearListView listView = view.findViewById(R.id.listCard_list);
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

    public void addConjugation(Map.Entry<String, ConjugationQuery.Conjugation> conjugation, int index) {
        adapter.addConjugation(conjugation, index);
        adapter.notifyDataSetChanged();
    }

}
