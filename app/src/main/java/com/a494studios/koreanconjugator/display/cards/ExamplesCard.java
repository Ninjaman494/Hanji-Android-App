package com.a494studios.koreanconjugator.display.cards;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;

import com.a494studios.koreanconjugator.EntryQuery;
import com.a494studios.koreanconjugator.display.DisplayCardView;
import com.a494studios.koreanconjugator.display.adapters.ExampleAdapter;
import com.a494studios.koreanconjugator.R;
import com.a494studios.koreanconjugator.suggestions.ExampleSuggestionActivity;
import com.linearlistview.LinearListView;

import java.util.List;
import java.util.Objects;

public class ExamplesCard implements DisplayCardBody{
    private View view;
    private ExampleAdapter adapter;

    public ExamplesCard(List<EntryQuery.Example> examples) {
        adapter = new ExampleAdapter(Objects.requireNonNull(examples));
    }

    @Override
    public View addBodyView(Context context, ViewGroup parentView, DisplayCardView cardView) {
        if(view == null) {
            view = View.inflate(context, R.layout.dcard_list,parentView);
        }

        LinearListView listView = view.findViewById(R.id.listCard_list);
        listView.setAdapter(adapter);

        cardView.hideButton(false);
        cardView.setButtonText("Add Example");

        return view;
    }

    @Override
    public void onButtonClick() {
        Context context = view.getContext();
        context.startActivity(new Intent(context, ExampleSuggestionActivity.class));
    }

    @Override
    public int getCount() {
        return adapter.getCount();
    }

    @Override
    public String getHeading() {
        return "Examples";
    }
}
