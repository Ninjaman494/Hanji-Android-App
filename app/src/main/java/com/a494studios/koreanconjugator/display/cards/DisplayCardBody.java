package com.a494studios.koreanconjugator.display.cards;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.a494studios.koreanconjugator.display.DisplayCardView;

public interface DisplayCardBody {
    View addBodyView(Context context, ViewGroup parentView, DisplayCardView cardView);
    void onButtonClick();
    int getCount();
    String getHeading();
}
