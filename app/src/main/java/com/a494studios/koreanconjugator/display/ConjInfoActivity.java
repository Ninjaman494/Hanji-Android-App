package com.a494studios.koreanconjugator.display;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.a494studios.koreanconjugator.R;
import com.a494studios.koreanconjugator.display.cards.ConjInfoCard;

import java.util.List;

public class ConjInfoActivity extends AppCompatActivity {

    public static final String EXTRA_NAME = "NAME";
    public static final String EXTRA_CONJ = "CONJ";
    public static final String EXTRA_PRON = "PRON";
    public static final String EXTRA_ROME = "ROME";
    public static final String EXTRA_EXPL = "EXPL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conj_info);

        String name = getIntent().getStringExtra(EXTRA_NAME);
        String conjugated = getIntent().getStringExtra(EXTRA_CONJ);
        String pronunciation = getIntent().getStringExtra(EXTRA_PRON);
        String romanization = getIntent().getStringExtra(EXTRA_ROME);
        List<String> explanations = getIntent().getStringArrayListExtra(EXTRA_EXPL);

        DisplayCardView infoCard = findViewById(R.id.info_infoCard);
        infoCard.setCardBody(new ConjInfoCard(name,conjugated,pronunciation,romanization,explanations));
    }
}
