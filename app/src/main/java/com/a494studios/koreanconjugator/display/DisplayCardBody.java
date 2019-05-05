package com.a494studios.koreanconjugator.display;

import android.content.Context;
import android.view.View;

public interface DisplayCardBody {
    boolean shouldHideButton();
    boolean shouldHideHeading();
    View getBodyView(Context context);
    int getCount();
    String getButtonText();
}
