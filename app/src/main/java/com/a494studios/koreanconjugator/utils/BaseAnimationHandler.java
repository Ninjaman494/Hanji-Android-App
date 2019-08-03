package com.a494studios.koreanconjugator.utils;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.a494studios.koreanconjugator.R;

public class BaseAnimationHandler {
    protected Context context;

    public BaseAnimationHandler(Context context) {
        this.context = context;
    }

    public void slideInViews(View extendedBar, View bodyView) {
        Animation topBot = AnimationUtils.loadAnimation(context, R.anim.slide_top_to_bot);
        Animation botTop = AnimationUtils.loadAnimation(context, R.anim.slide_bot_to_top);

        extendedBar.setVisibility(View.VISIBLE); // Prevents stuttering
        extendedBar.startAnimation(topBot);
        bodyView.startAnimation(botTop);
    }
}
