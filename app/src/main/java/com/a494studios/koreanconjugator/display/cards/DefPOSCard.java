package com.a494studios.koreanconjugator.display.cards;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.a494studios.koreanconjugator.display.DisplayCardView;
import com.a494studios.koreanconjugator.utils.WordInfoView;

import java.util.List;
import java.util.Objects;

public class DefPOSCard implements DisplayCardBody {

    private String term;
    private String pos;
    private List<String> definitions;
    private WordInfoView view;
    private String buttonText;
    private DisplayCardView cardView;

    public DefPOSCard(String term, String pos, List<String> definitions) {
        this.term = Objects.requireNonNull(term);
        this.pos = Objects.requireNonNull(pos);
        this.definitions = Objects.requireNonNull(definitions);
        this.buttonText = (definitions.size() - 3) + " MORE";
    }

    @Override
    public View addBodyView(Context context, ViewGroup parentView, DisplayCardView cardView) {
        if(view == null) {
            view = new WordInfoView(context, term, pos, definitions, false);
        }

        if(view.getParent() == null) {
            parentView.addView(view);
        }

        this.cardView = cardView;
        cardView.hideButton(definitions.size() <= 3);
        cardView.setButtonText(buttonText);

        return view;
    }

    @Override
    public void onButtonClick() {
        boolean showingAll = view.getShowAll();
        if(showingAll) {
            buttonText = (definitions.size() - 3) + " MORE";
        } else {
            buttonText = "COLLAPSE";
        }
        view.clickShowAll(!showingAll);
        cardView.setButtonText(buttonText);
    }

    @Override
    public int getCount() {
        return definitions.size();
    }

    @Override
    public String getHeading() {
        return null;
    }
}
