package com.a494studios.koreanconjugator.conjugator;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.a494studios.koreanconjugator.R;
import com.a494studios.koreanconjugator.conjugations.ConjugationAnimationHandler;

public class ConjugatorAnimationHandler extends ConjugationAnimationHandler {

    private View extendedBar;
    private NestedScrollView scrollView;

    ConjugatorAnimationHandler(View extendedBar, NestedScrollView scrollView,
                               RecyclerView recyclerView, Context context) {
        super(extendedBar, recyclerView, context);

        this.extendedBar = extendedBar;
        this.scrollView = scrollView;
    }

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

        scrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener)
                (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                    double scrollViewHeight = scrollView.getChildAt(0).getBottom()
                            - scrollView.getHeight();
                    double scrollPosition = ((double)scrollY/ scrollViewHeight) * 100d;

                    int yDiff = scrollY - oldScrollY;
                    boolean isVisible = extendedBar.getVisibility() == View.VISIBLE;

                    if (yDiff > 0 && scrollPosition > 10 && !isAnimating[0] && isVisible) {
                        // Scroll down, don't hide if at top of page
                        Animation anim = AnimationUtils.loadAnimation(context, R.anim.slide_out);
                        anim.setAnimationListener(listener);
                        extendedBar.startAnimation(anim);
                        extendedBar.setVisibility(View.INVISIBLE);
                    } else if (yDiff < 0 && scrollPosition > 10 && !isAnimating[0] && !isVisible) {
                        // Scroll up
                        Animation anim = AnimationUtils.loadAnimation(context, R.anim.slide_in);
                        anim.setAnimationListener(listener);
                        extendedBar.startAnimation(anim);
                        extendedBar.setVisibility(View.VISIBLE);
                    }
                });
    }
}
