package com.a494studios.koreanconjugator.conjugations;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.recyclerview.widget.RecyclerView;

import com.a494studios.koreanconjugator.R;
import com.a494studios.koreanconjugator.utils.RecyclerAnimationHandler;

class ConjugationAnimationHandler extends RecyclerAnimationHandler {

    private RecyclerView recyclerView;

    ConjugationAnimationHandler(View extendedBar, RecyclerView recyclerView, Context context) {
        super(extendedBar, recyclerView, context);
        this.recyclerView = recyclerView;
    }

    public void slideInConjugations() {
        Animation botTop = AnimationUtils.loadAnimation(context, R.anim.slide_bot_to_top);
        recyclerView.startAnimation(botTop);
    }
}
