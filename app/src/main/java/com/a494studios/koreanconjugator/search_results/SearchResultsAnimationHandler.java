package com.a494studios.koreanconjugator.search_results;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.a494studios.koreanconjugator.R;
import com.a494studios.koreanconjugator.utils.BaseAnimationHandler;

class SearchResultsAnimationHandler extends BaseAnimationHandler {

    private View extendedBar;
    private RecyclerView recyclerView;

    SearchResultsAnimationHandler(View extendedBar, RecyclerView recyclerView, Context context) {
        super(context);
        this.extendedBar = extendedBar;
        this.recyclerView = recyclerView;
    }

    void setupScrollAnimations(LinearLayoutManager layoutManager) {
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

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(dx == 0 && dy == 0){
                    return; // Bad event
                }

                int pos = layoutManager.findFirstCompletelyVisibleItemPosition();
                if(pos == 0) {
                    Animation anim = AnimationUtils.loadAnimation(context, R.anim.slide_in);
                    anim.setAnimationListener(listener);
                    extendedBar.startAnimation(anim);
                    extendedBar.setVisibility(View.VISIBLE);
                } else if(!isAnimating[0] && extendedBar.getVisibility() == View.VISIBLE) {
                    Animation anim = AnimationUtils.loadAnimation(context, R.anim.slide_out);
                    anim.setAnimationListener(listener);
                    extendedBar.startAnimation(anim);
                    extendedBar.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    void animateListView() {
        this.slideInViews(extendedBar,recyclerView);
    }
}
