package com.a494studios.koreanconjugator.display.cards;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

public interface DisplayCardBody {
    View addBodyView(Context context, ViewGroup parentView);
    void onButtonClick();
    boolean shouldHideButton();
    int getCount();
    String getButtonText();
    String getHeading();
}
