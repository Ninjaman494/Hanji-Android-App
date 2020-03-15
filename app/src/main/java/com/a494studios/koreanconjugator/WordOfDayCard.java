package com.a494studios.koreanconjugator;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.a494studios.koreanconjugator.display.cards.DisplayCardBody;

public class WordOfDayCard implements DisplayCardBody {
    private View view;


    @Override
    public View addBodyView(Context context, ViewGroup parentView) {
        if(view == null) {
            view = View.inflate(context, R.layout.dcard_wod,parentView);
        }

        TextView textView = view.findViewById(R.id.wod_text);
        textView.setText("가나다라바사아하");
        return view;
    }

    @Override
    public void onButtonClick() {
        // Empty on purpose
    }

    @Override
    public boolean shouldHideButton() {
        return false;
    }

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public String getButtonText() {
        return view.getContext().getString(R.string.see_entry);
    }

    @Override
    public String getHeading() {
        return "Word of the Day";
    }
}
