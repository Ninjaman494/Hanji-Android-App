package com.a494studios.koreanconjugator.display.cards;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;

import com.a494studios.koreanconjugator.display.ConjInfoActivity;
import com.a494studios.koreanconjugator.display.adapters.ConjugationAdapter;
import com.a494studios.koreanconjugator.ConjugationQuery;
import com.a494studios.koreanconjugator.R;
import com.a494studios.koreanconjugator.Utils;
import com.linearlistview.LinearListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ConjugationCard implements DisplayCardBody {

    private View view;
    private String heading;
    private ConjugationAdapter adapter;

    public ConjugationCard(List<ConjugationQuery.Conjugation> conjugations) {
        this.adapter = new ConjugationAdapter(Objects.requireNonNull(conjugations));
        if (conjugations.isEmpty()) {
            heading = "Conjugations";
        } else {
            heading = Utils.toTitleCase(conjugations.get(0).type());
        }
    }

    @Override
    public View addBodyView(Context context, ViewGroup parentView) {
        if(view == null) {
            view = View.inflate(context, R.layout.dcard_list,parentView);
        }
        LinearListView listView = view.findViewById(R.id.listCard_list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new LinearListView.OnItemClickListener() {
            @Override
            public void onItemClick(LinearListView parent, View view, int position, long id) {
                ConjugationQuery.Conjugation conjugation = adapter.getItem(position);
                Intent i = new Intent(view.getContext(), ConjInfoActivity.class);
                i.putExtra(ConjInfoActivity.EXTRA_NAME, conjugation.name());
                i.putExtra(ConjInfoActivity.EXTRA_CONJ,conjugation.conjugation());
                i.putExtra(ConjInfoActivity.EXTRA_PRON,conjugation.pronunciation());
                i.putExtra(ConjInfoActivity.EXTRA_ROME,conjugation.romanization());
                i.putExtra(ConjInfoActivity.EXTRA_EXPL,new ArrayList<>(conjugation.reasons()));
                i.putExtra(ConjInfoActivity.EXTRA_HONO, conjugation.honorific());
                view.getContext().startActivity(i);
            }
        });
        return view;
    }

    @Override
    public void onButtonClick() {
        // Empty on purpose
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
