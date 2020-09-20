package com.a494studios.koreanconjugator.utils;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.a494studios.koreanconjugator.R;

public class ScrollViewAnimationHandler extends BaseAnimationHandler {
    private View extendedBar;
    private ScrollView scrollView;
    private int lastScrollY;

    public ScrollViewAnimationHandler(Context context, View extendedBar, ScrollView scrollView) {
        super(context);
        this.extendedBar = extendedBar;
        this.scrollView = scrollView;
        this.lastScrollY = -1;
    }

    public void setupScrollAnimation(LinearLayout linearLayout) {
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

        View firstView = linearLayout.getChildAt(0);
        scrollView.getViewTreeObserver().addOnScrollChangedListener(() -> {
            int scrollY = scrollView.getScrollY();
            if (lastScrollY != -1 && scrollY - lastScrollY != 0) {

                if (isViewVisible(firstView)) {
                    Animation anim = AnimationUtils.loadAnimation(context, R.anim.slide_in);
                    anim.setAnimationListener(listener);
                    extendedBar.startAnimation(anim);
                    extendedBar.setVisibility(View.VISIBLE);
                } else if (!isAnimating[0] && extendedBar.getVisibility() == View.VISIBLE) {
                    Animation anim = AnimationUtils.loadAnimation(context, R.anim.slide_out);
                    anim.setAnimationListener(listener);
                    extendedBar.startAnimation(anim);
                    extendedBar.setVisibility(View.INVISIBLE);
                }
            }

            lastScrollY = scrollY;
        });
    }

    private boolean isViewVisible(View view) {
        Rect scrollBounds = new Rect();
        scrollView.getDrawingRect(scrollBounds);
        float top = view.getY();
        float bottom = top + view.getHeight();

        return scrollBounds.top <= top && scrollBounds.bottom >= bottom;
    }
}
