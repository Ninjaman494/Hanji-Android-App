package com.a494studios.koreanconjugator.display;

import androidx.appcompat.app.ActionBar;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.a494studios.koreanconjugator.CustomApplication;
import com.a494studios.koreanconjugator.R;
import com.a494studios.koreanconjugator.display.cards.ConjInfoCard;
import com.a494studios.koreanconjugator.utils.BaseActivity;
import com.a494studios.koreanconjugator.utils.ScrollViewAnimationHandler;

import java.util.List;

public class ConjInfoActivity extends BaseActivity {

    public static final String EXTRA_NAME = "NAME";
    public static final String EXTRA_CONJ = "CONJ";
    public static final String EXTRA_PRON = "PRON";
    public static final String EXTRA_ROME = "ROME";
    public static final String EXTRA_EXPL = "EXPL";
    public static final String EXTRA_HONO = "HONO";
    private ScrollViewAnimationHandler animationHandler;
    private View extendedBar;
    private LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conj_info);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setTitle("");
            actionBar.setElevation(0);
        }

        String name = getIntent().getStringExtra(EXTRA_NAME);
        String conjugated = getIntent().getStringExtra(EXTRA_CONJ);
        String pronunciation = getIntent().getStringExtra(EXTRA_PRON);
        String romanization = getIntent().getStringExtra(EXTRA_ROME);
        List<String> explanations = getIntent().getStringArrayListExtra(EXTRA_EXPL);
        boolean isHonorific = getIntent().getBooleanExtra(EXTRA_HONO, false);

        DisplayCardView infoCard = findViewById(R.id.info_infoCard);
        infoCard.setCardBody(new ConjInfoCard(name,conjugated,pronunciation,romanization,explanations));
        infoCard.showHonorificChip(isHonorific);

        CustomApplication.handleAdCard((DisplayCardView)findViewById(R.id.info_adCard));

        extendedBar = findViewById(R.id.info_extendedBar);
        linearLayout = findViewById(R.id.info_root);
        ScrollView scrollView = findViewById(R.id.info_scroll);
        animationHandler = new ScrollViewAnimationHandler(this, extendedBar, scrollView);
        animationHandler.setupScrollAnimation(linearLayout);
    }

    @Override
    public void onResume() {
        super.onResume();
        animationHandler.slideInViews(extendedBar, linearLayout);
    }
}
