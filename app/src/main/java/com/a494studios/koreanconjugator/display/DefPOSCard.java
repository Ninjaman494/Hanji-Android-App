package com.a494studios.koreanconjugator.display;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.a494studios.koreanconjugator.utils.WordInfoView;

import java.util.List;

public class DefPOSCard implements DisplayCardBody {

    private String term;
    private String pos;
    private List<String> definitions;
    private WordInfoView view;
    private String buttonText;

    public DefPOSCard(String term, String pos, List<String> definitions) {
        this.term = term;
        this.pos = pos;
        this.definitions = definitions;
        this.buttonText = (definitions.size() - 3) + " MORE";
    }

    @Override
    public boolean shouldHideButton() {
        return definitions.size() <= 3; // show if more than 3 definitions
    }

    @Override
    public View addBodyView(Context context, ViewGroup parentView) {
        if(view == null) {
            view = new WordInfoView(context, term, pos, definitions, false);
        }

        parentView.addView(view);
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
        view.setShowAll(!showingAll);
    }

    @Override
    public int getCount() {
        return definitions.size();
    }

    @Override
    public String getButtonText() {
        return buttonText;
    }

    @Override
    public String getHeading() {
        return null;
    }
}
