package com.a494studios.koreanconjugator.display;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.a494studios.koreanconjugator.ExampleAdapter;
import com.a494studios.koreanconjugator.ExamplesQuery;
import com.a494studios.koreanconjugator.R;
import com.linearlistview.LinearListView;

import java.util.List;

public class ExamplesCard implements DisplayCardBody{
    private View view;
    private ExampleAdapter adapter;

    public ExamplesCard(List<ExamplesQuery.Example> examples) {
        adapter = new ExampleAdapter(examples);
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
    public void onButtonClick() {
        //Empty on purpose
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
        return "Examples";
    }
}
