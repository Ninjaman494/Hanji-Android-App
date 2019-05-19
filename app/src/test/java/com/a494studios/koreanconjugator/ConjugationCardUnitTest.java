package com.a494studios.koreanconjugator;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.a494studios.koreanconjugator.display.cards.ConjugationCard;
import com.a494studios.koreanconjugator.type.SpeechLevel;
import com.a494studios.koreanconjugator.type.Tense;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
public class ConjugationCardUnitTest {
    private List<ConjugationQuery.Conjugation> conjugations;
    private Context context;
    private ConjugationCard card;

    @Before
    public void init() {
        ConjugationQuery.Conjugation c = new ConjugationQuery.Conjugation("type",
                "name","conj", "TYPE", Tense.PRESENT, SpeechLevel.INFORMAL_HIGH,
                false,"","",new ArrayList<String>());

        conjugations = new ArrayList<>();
        conjugations.add(c);
        context = RuntimeEnvironment.application.getApplicationContext();
        card = new ConjugationCard(conjugations);
    }

    @Test(expected = NullPointerException.class)
    public void test_nullConjugations() {
        new ConjugationCard(null);
    }

    @Test
    public void test_addBodyView() {
        ViewGroup group = new LinearLayout(context);
        card.addBodyView(context,group);
        assertEquals(group.getChildAt(0).getId(),R.id.listCard);
    }

    @Test
    public void test_shouldHideButton() {
        assertTrue(card.shouldHideButton());
    }

    @Test
    public void test_getCount() {
        assertEquals(conjugations.size(), card.getCount());
    }

    @Test
    public void test_getButtonText() {
        assertEquals("Button", card.getButtonText());
    }

    @Test
    public void test_getHeading() {
        ConjugationCard empty = new ConjugationCard(new ArrayList<ConjugationQuery.Conjugation>());
        assertEquals("Conjugations",empty.getHeading());
        assertEquals("Type",card.getHeading());
    }
}
