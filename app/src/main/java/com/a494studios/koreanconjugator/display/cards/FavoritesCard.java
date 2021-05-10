package com.a494studios.koreanconjugator.display.cards;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.a494studios.koreanconjugator.conjugations.ConjugationActivity;
import com.a494studios.koreanconjugator.R;
import com.a494studios.koreanconjugator.display.ConjInfoActivity;
import com.a494studios.koreanconjugator.display.DisplayCardView;
import com.a494studios.koreanconjugator.display.adapters.FavoritesAdapter;
import com.a494studios.koreanconjugator.fragment.ConjugationFragment;
import com.a494studios.koreanconjugator.utils.Logger;
import com.linearlistview.LinearListView;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class FavoritesCard implements DisplayCardBody {

    private View view;
    private TextView emptyView;
    private LinearListView listView;

    private String stem;
    private boolean honorific;
    private boolean isAdj;
    private Boolean regular;
    private FavoritesAdapter adapter;

    public FavoritesCard(ArrayList<Map.Entry<String, ConjugationFragment>> entries, String stem, boolean honorific, boolean isAdj, Boolean regular) {
        this.adapter = new FavoritesAdapter(Objects.requireNonNull(entries));
        this.stem = Objects.requireNonNull(stem);
        this.honorific = honorific;
        this.isAdj = isAdj;
        this.regular = regular;
    }

    @Override
    public View addBodyView(Context context, ViewGroup parentView, DisplayCardView cardView) {
        if (view == null) {
            view = View.inflate(context, R.layout.dcard_list, parentView);
        }

        emptyView = view.findViewById(R.id.listCard_empty);
        listView = view.findViewById(R.id.listCard_list);
        emptyView.setText(R.string.empty_favorites);
        toggleEmptyState(adapter.isEmpty());

        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            String favName = adapter.getItem(position).getKey();
            ConjugationFragment conjugation = adapter.getItem(position).getValue();

            // Log select favorite event
            Logger.getInstance().logSelectFavorite(favName, conjugation.name(), conjugation.conjugation());

            Intent i = new Intent(view.getContext(), ConjInfoActivity.class);
            i.putExtra(ConjInfoActivity.EXTRA_NAME, conjugation.name());
            i.putExtra(ConjInfoActivity.EXTRA_CONJ, conjugation.conjugation());
            i.putExtra(ConjInfoActivity.EXTRA_PRON, conjugation.pronunciation());
            i.putExtra(ConjInfoActivity.EXTRA_ROME, conjugation.romanization());
            i.putExtra(ConjInfoActivity.EXTRA_EXPL, new ArrayList<>(conjugation.reasons()));
            i.putExtra(ConjInfoActivity.EXTRA_HONO, conjugation.honorific());
            view.getContext().startActivity(i);
        });

        cardView.hideButton(false);
        cardView.setButtonText("SEE ALL");

        return view;
    }

    @Override
    public void onButtonClick() {
        Intent i = new Intent(view.getContext(), ConjugationActivity.class);
        i.putExtra(ConjugationActivity.EXTRA_STEM,stem);
        i.putExtra(ConjugationActivity.EXTRA_HONORIFIC,honorific);
        i.putExtra(ConjugationActivity.EXTRA_ISADJ, isAdj);
        i.putExtra(ConjugationActivity.EXTRA_REGULAR, regular);
        view.getContext().startActivity(i);
    }

    @Override
    public int getCount() {
        return adapter.getCount();
    }

    @Override
    public String getHeading() {
        return "Conjugations";
    }

    public void addConjugation(Map.Entry<String, ConjugationFragment> conjugation, int index) {
        adapter.addConjugation(conjugation, index);
        adapter.notifyDataSetChanged();
        toggleEmptyState(false);
    }

    private void toggleEmptyState(boolean isEmpty) {
        if(isEmpty) {
            emptyView.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        }
    }

}
