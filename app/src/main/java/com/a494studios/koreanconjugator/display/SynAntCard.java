package com.a494studios.koreanconjugator.display;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.a494studios.koreanconjugator.R;

import java.util.List;

public class SynAntCard implements DisplayCardBody {
    private View view;
    private List<String> wordList;
    private boolean isSyn;

    public SynAntCard(List<String> wordList, boolean isSyn) {
        this.wordList = wordList;
        this.isSyn = isSyn;
    }

    @Override
    public View addBodyView(Context context, ViewGroup parentView) {
        if(view == null) {
            view = View.inflate(context, R.layout.dcard_simpletext,parentView);
        }

        TextView textView = view.findViewById(R.id.simpleCard_text);
        textView.setText(TextUtils.join(", ", wordList));
        return view;
    }

    @Override
    public boolean shouldHideButton() {
        return true;
    }

    @Override
    public int getCount() {
        return wordList.size();
    }

    @Override
    public String getButtonText() {
        return "Button";
    }

    @Override
    public String getHeading() {
        return isSyn ? "Synonyms" : "Antonyms";
    }
}
