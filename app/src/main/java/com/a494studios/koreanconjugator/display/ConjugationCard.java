package com.a494studios.koreanconjugator.display;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.a494studios.koreanconjugator.ConjugationAdapter;
import com.a494studios.koreanconjugator.ConjugationQuery;
import com.a494studios.koreanconjugator.R;
import com.a494studios.koreanconjugator.Utils;
import com.linearlistview.LinearListView;

import java.util.List;

public class ConjugationCard implements DisplayCardBody {

    private View view;
    private String heading;
    private ConjugationAdapter adapter;

    public ConjugationCard(List<ConjugationQuery.Conjugation> conjugations) {
        this.adapter = new ConjugationAdapter(conjugations);
        heading  = Utils.toTitleCase(conjugations.get(0).type());
    }

    @Override
    public View addBodyView(Context context, ViewGroup parentView) {
        if(view == null) {
            view = View.inflate(context, R.layout.dcard_list,parentView);
        }
        LinearListView listView = view.findViewById(R.id.conjCard_list);
        listView.setAdapter(adapter);
        return view;
    }

    @Override
    public View.OnClickListener getButtonListener() {
        return null;
    }

    @Override
    public boolean shouldHideButton() {
        return true;
    }

    @Override
    public int getCount() {
        return adapter.getCount();
    }

    @Override
    public String getButtonText() {
        return "Button";
    }

    @Override
    public String getHeading() {
        return heading;
    }
}
