package com.a494studios.koreanconjugator.display.cards;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.a494studios.koreanconjugator.R;
import com.a494studios.koreanconjugator.display.DisplayCardView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

public class AdCard implements DisplayCardBody {
    private View view;
    private String adUnitId;

    public AdCard(String adUnitId) {
        this.adUnitId = adUnitId;
    }

    @Override
    public View addBodyView(Context context, ViewGroup parentView, DisplayCardView cardView) {
        if(view == null) {
            view = View.inflate(context, R.layout.dcard_ad,parentView);
        }

        AdView adView = new AdView(context);
        adView.setAdSize(AdSize.MEDIUM_RECTANGLE);
        adView.setAdUnitId(adUnitId);

        RelativeLayout container = view.findViewById(R.id.adCard);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                convertToPixels(250, context));
        params.gravity = Gravity.CENTER;
        params.topMargin = convertToPixels(16, context);
        params.bottomMargin = convertToPixels(16, context);

        adView.setLayoutParams(params);
        container.addView(adView);

        adView.loadAd(new AdRequest.Builder().build());

        cardView.hideButton(true);

        return view;
    }

    @Override
    public void onButtonClick() {
        // Empty on purpose
    }

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public String getHeading() {
        return "Ad";
    }

    private int convertToPixels(int dp, Context context) {
        Resources r = context.getResources();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
    }
}
