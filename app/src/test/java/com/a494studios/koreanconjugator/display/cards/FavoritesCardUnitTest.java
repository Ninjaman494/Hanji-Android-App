package com.a494studios.koreanconjugator.display.cards;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.a494studios.koreanconjugator.R;
import com.a494studios.koreanconjugator.conjugations.ConjugationActivity;
import com.a494studios.koreanconjugator.display.DisplayCardView;
import com.a494studios.koreanconjugator.fragment.ConjugationFragment;
import com.a494studios.koreanconjugator.type.SpeechLevel;
import com.a494studios.koreanconjugator.type.Tense;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
public class FavoritesCardUnitTest {
    private final String STEM = "stem";
    private final boolean HONORIFIC = false;
    private final boolean IS_ADJ = false;
    private ArrayList<Map.Entry<String, ConjugationFragment>> entries;
    private FavoritesCard card;
    private DisplayCardView cardView;
    private LinearLayout viewGroup;
    private Activity activity;

    @Before
    public void init() {
        entries = new ArrayList<>();
        activity = Robolectric.buildActivity(Activity.class).create().start().visible().get();
        card = new FavoritesCard(entries,STEM,HONORIFIC,IS_ADJ, true);
        cardView = new DisplayCardView(activity);
        viewGroup = new LinearLayout(activity);

        card.addBodyView(activity, viewGroup, cardView);
    }

    @Test(expected =  NullPointerException.class)
    public void test_nullEntries() {
        new FavoritesCard(null,STEM,HONORIFIC,IS_ADJ, true);
    }

    @Test(expected =  NullPointerException.class)
    public void test_nullStem() {
        new FavoritesCard(entries,null,HONORIFIC,IS_ADJ, true);
    }

    @Test
    public void test_nullRegular() {
        new FavoritesCard(entries,STEM,HONORIFIC,IS_ADJ, null);
    }

    @Test
    public void test_addBodyView() {
        Assert.assertEquals(viewGroup.getChildAt(0).getId(), R.id.listCard);
    }

    @Test
    public void test_onButtonClick() {
        card.onButtonClick();

        Intent intent = Shadows.shadowOf(activity).peekNextStartedActivityForResult().intent;
        assertEquals(intent.getComponent(),new ComponentName(activity, ConjugationActivity.class));
        assertEquals(intent.getStringExtra(ConjugationActivity.EXTRA_STEM),STEM);
        assertEquals(intent.getBooleanExtra(ConjugationActivity.EXTRA_HONORIFIC,!HONORIFIC),HONORIFIC);
        assertEquals(intent.getBooleanExtra(ConjugationActivity.EXTRA_ISADJ,!IS_ADJ),IS_ADJ);
    }

    @Test
    public void test_shouldHideButton() {
        assertEquals(cardView.findViewById(R.id.displayCard_button).getVisibility(), ViewGroup.VISIBLE);
    }

    @Test
    public void test_getCount() {
        assertEquals(entries.size(),card.getCount());
    }

    @Test
    public void test_getButtonText() {
        Button btn = cardView.findViewById(R.id.displayCard_button);
        assertEquals(btn.getText(), "SEE ALL");
    }

    @Test
    public void test_getHeading() {
        assertEquals("Conjugations",card.getHeading());
    }

    @Test
    public void test_addConjugation() {
        ConjugationFragment conjugation =
                new ConjugationFragment("type", "name", "conj",
                        "type", Tense.PAST, SpeechLevel.FORMAL_HIGH, false,
                        "pronunc", "romani", new ArrayList<>());

        Map.Entry<String, ConjugationFragment> entry = new AbstractMap.SimpleEntry<>("new", conjugation);
        card.addConjugation(entry,0);
        assertEquals(1,card.getCount());
    }
}
