package com.a494studios.koreanconjugator.display;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

public interface DisplayCardBody {
    View addBodyView(Context context, ViewGroup parentView);
    boolean shouldHideButton();
    int getCount();
    String getButtonText();
    String getHeading();
}
