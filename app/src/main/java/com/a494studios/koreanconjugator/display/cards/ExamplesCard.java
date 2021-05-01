package com.a494studios.koreanconjugator.display.cards;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.a494studios.koreanconjugator.EntryQuery;
import com.a494studios.koreanconjugator.display.DisplayCardView;
import com.a494studios.koreanconjugator.display.adapters.ExampleAdapter;
import com.a494studios.koreanconjugator.R;
import com.a494studios.koreanconjugator.suggestions.ExampleSuggestionActivity;
import com.linearlistview.LinearListView;

import java.util.List;

public class ExamplesCard implements DisplayCardBody{
    private View view;
    private ExampleAdapter adapter;

    public ExamplesCard(List<EntryQuery.Example> examples) {
        if(examples != null && !examples.isEmpty()) {
            adapter = new ExampleAdapter(examples);
        }
    }

    @Override
    public View addBodyView(Context context, ViewGroup parentView, DisplayCardView cardView) {
        if(adapter == null) {
            if(view == null) {
                view = View.inflate(context, R.layout.dcard_simpletext, parentView);
            }

            TextView textView = view.findViewById(R.id.simpleCard_text);
            textView.setText(R.string.empty_examples);
        } else {
            if(view == null) {
                view = View.inflate(context, R.layout.dcard_list, parentView);
            }

            LinearListView listView = view.findViewById(R.id.listCard_list);
            listView.setAdapter(adapter);
        }

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
