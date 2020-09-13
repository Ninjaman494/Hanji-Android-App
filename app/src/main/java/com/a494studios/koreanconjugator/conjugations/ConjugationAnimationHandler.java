package com.a494studios.koreanconjugator.conjugations;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.a494studios.koreanconjugator.R;
import com.a494studios.koreanconjugator.utils.RecyclerAnimationHandler;

public class ConjugationAnimationHandler extends RecyclerAnimationHandler {

    private View extendedBar;
    private RecyclerView recyclerView;

    public ConjugationAnimationHandler(View extendedBar, RecyclerView recyclerView, Context context) {
        super(extendedBar, recyclerView, context);
        this.extendedBar = extendedBar;
        this.recyclerView = recyclerView;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void setupScrollAnimations(LinearLayoutManager layoutManager) {
        final boolean[] isAnimating = {false};
        Animation.AnimationListener listener = new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                isAnimating[0] = true;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                isAnimating[0] = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        };

        GestureListener gestureListener = new GestureListener(isAnimating, layoutManager, listener);
        GestureDetector detector = new GestureDetector(context, gestureListener);
        recyclerView.setOnTouchListener((view, motionEvent) -> detector.onTouchEvent(motionEvent));
    }

    public void slideInConjugations() {
        Animation botTop = AnimationUtils.loadAnimation(context, R.anim.slide_bot_to_top);
        recyclerView.startAnimation(botTop);
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        boolean[] isAnimating;
        LinearLayoutManager layoutManager;
        Animation.AnimationListener listener;

        GestureListener(boolean[] isAnimating, LinearLayoutManager layoutManager, Animation.AnimationListener listener) {
            this.isAnimating = isAnimating;
            this.layoutManager = layoutManager;
            this.listener = listener;
        }

        @Override
        public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float dx, float dy) {
            if( (dx == 0 && dy == 0) || isAnimating[0] ){
                return false; // Bad event or animation already in progress
            }

            int pos = layoutManager.findFirstCompletelyVisibleItemPosition();
            int visibility = extendedBar.getVisibility();
            if((dy < 0 || pos == 0) && visibility == View.INVISIBLE) { // Scroll up
                Animation anim = AnimationUtils.loadAnimation(context, R.anim.slide_in);
                anim.setAnimationListener(listener);
                extendedBar.startAnimation(anim);
                extendedBar.setVisibility(View.VISIBLE);
            } else if(dy > 0 && visibility == View.VISIBLE ) { // Scroll down
                Animation anim = AnimationUtils.loadAnimation(context, R.anim.slide_out);
                anim.setAnimationListener(listener);
                extendedBar.startAnimation(anim);
                extendedBar.setVisibility(View.INVISIBLE);
            }

            return false;
        }
    }
}
