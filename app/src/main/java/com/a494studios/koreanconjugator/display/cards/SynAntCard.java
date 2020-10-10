package com.a494studios.koreanconjugator.display.cards;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.a494studios.koreanconjugator.R;
import com.a494studios.koreanconjugator.display.DisplayCardView;

import java.util.List;
import java.util.Objects;

public class SynAntCard implements DisplayCardBody {
    private View view;
    private List<String> wordList;
    private boolean isSyn;

    public SynAntCard(List<String> wordList, boolean isSyn) {
        this.wordList = Objects.requireNonNull(wordList);
        this.isSyn = isSyn;
    }

    @Override
    public View addBodyView(Context context, ViewGroup parentView, DisplayCardView cardView) {
        if(view == null) {
            view = View.inflate(context, R.layout.dcard_simpletext,parentView);
        }

        TextView textView = view.findViewById(R.id.simpleCard_text);
        textView.setText(TextUtils.join(", ", wordList));

        cardView.hideButton(true);

        return view;
    }

    @Override
    public void onButtonClick() {
        // Empty on purpose
    }

    @Override
    public int getCount() {
        return wordList.size();
    }

    @Override
    public String getHeading() {
        return isSyn ? "Synonyms" : "Antonyms";
    }
}
