package com.a494studios.koreanconjugator.display.cards;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.a494studios.koreanconjugator.R;
import com.a494studios.koreanconjugator.display.DisplayCardView;
import com.a494studios.koreanconjugator.utils.Utils;
import com.a494studios.koreanconjugator.display.adapters.ExplanationsAdapter;
import com.linearlistview.LinearListView;

import java.util.List;
import java.util.Objects;

public class ConjInfoCard implements DisplayCardBody {

    private View view;
    private String name;
    private String conjugated;
    private String pronunciation;
    private String romanization;
    private List<String> explanations;

    public ConjInfoCard(String name, String conjugated, String pronunciation, String romanization, List<String> explanations) {
        this.name = Utils.toTitleCase(Objects.requireNonNull(name));
        this.conjugated = Objects.requireNonNull(conjugated);
        this.pronunciation = Objects.requireNonNull(pronunciation);
        this.romanization = Objects.requireNonNull(romanization);
        this.explanations = Objects.requireNonNull(explanations);
    }

    @Override
    public View addBodyView(Context context, ViewGroup parentView, DisplayCardView cardView) {
        if(view == null) {
            view = View.inflate(context, R.layout.dcard_conj_info,parentView);
        }

        ((TextView)view.findViewById(R.id.conjInfo_conjugated)).setText(conjugated);
        ((TextView)view.findViewById(R.id.conjInfo_hpronc)).setText(pronunciation);
        ((TextView)view.findViewById(R.id.conjInfo_roman)).setText(romanization);

        LinearListView listView = view.findViewById(R.id.conjInfo_explainList);
        listView.setAdapter(new ExplanationsAdapter(explanations));

        cardView.hideButton(true);

        return view;
    }

    @Override
    public void onButtonClick(){
        // Empty on purpose
    }

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public String getHeading() {
        return name;
    }

}
