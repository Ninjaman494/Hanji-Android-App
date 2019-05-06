package com.a494studios.koreanconjugator.display;

import android.content.Context;
import android.view.View;

import com.a494studios.koreanconjugator.utils.WordInfoView;

import java.util.List;

public class DefPOSCard implements DisplayCardBody {

    private String term;
    private String pos;
    private List<String> definitions;
    private WordInfoView view;

    public DefPOSCard(String term, String pos, List<String> definitions) {
        this.term = term;
        this.pos = pos;
        this.definitions = definitions;
    }

    @Override
    public boolean shouldHideButton() {
        return definitions.size() <= 3; // show if more than 3 definitions
    }

    @Override
    public boolean shouldHideHeading() {
        return true;
    }

    @Override
    public View getBodyView(Context context) {
        if(view == null) {
            view = new WordInfoView(context, term, pos, definitions, false);
        }
        return view;
    }

    @Override
    public int getCount() {
        return definitions.size();
    }

    @Override
    public String getButtonText() {
        return (definitions.size() - 3) + " MORE";
    }
}
