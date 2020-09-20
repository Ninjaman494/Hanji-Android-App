package com.a494studios.koreanconjugator.utils;

import android.content.Context;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

public class BaseAnimationHandler {
    protected Context context;

    public BaseAnimationHandler(Context context) {
        this.context = context;
    }

    public void slideInViews(View extendedBar, View bodyView) {
        DecelerateInterpolator interpolator = new DecelerateInterpolator(2);

        extendedBar.setTranslationY(200 * -1);
        extendedBar.setVisibility(View.VISIBLE); // Prevents stuttering
        extendedBar.animate().setInterpolator(interpolator).translationY(0);

        bodyView.setTranslationY(200);
        bodyView.animate().setInterpolator(interpolator).translationY(0);
    }
}
