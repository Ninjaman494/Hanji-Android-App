package com.a494studios.koreanconjugator.display.cards;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.a494studios.koreanconjugator.ConjugationQuery;
import com.a494studios.koreanconjugator.R;
import com.a494studios.koreanconjugator.conjugations.ConjugationActivity;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.Shadows;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@RunWith(RobolectricTestRunner.class)
public class FavoritesCardUnitTest {
    private final String STEM = "stem";
    private final boolean HONORIFIC = false;
    private final boolean IS_ADJ = false;
    private ArrayList<Map.Entry<String, ConjugationQuery.Conjugation>> entries;
    private FavoritesCard card;
    private Context context;

    @Before
    public void init() {
        entries = new ArrayList<>();
        context = RuntimeEnvironment.application.getApplicationContext();
        card = new FavoritesCard(entries,STEM,HONORIFIC,IS_ADJ, true);
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
        ViewGroup group = new LinearLayout(context);
        card.addBodyView(context,group);
        Assert.assertEquals(group.getChildAt(0).getId(), R.id.listCard);
    }

    @Test
    public void test_onButtonClick() {
        Activity activity = Robolectric.buildActivity(Activity.class).create().start().visible().get();
        card.addBodyView(activity,new LinearLayout(activity));
        card.onButtonClick();

        Intent intent = Shadows.shadowOf(activity).peekNextStartedActivityForResult().intent;
        assertEquals(intent.getComponent(),new ComponentName(activity, ConjugationActivity.class));
        assertEquals(intent.getStringExtra(ConjugationActivity.EXTRA_STEM),STEM);
        assertEquals(intent.getBooleanExtra(ConjugationActivity.EXTRA_HONORIFIC,!HONORIFIC),HONORIFIC);
        assertEquals(intent.getBooleanExtra(ConjugationActivity.EXTRA_ISADJ,!IS_ADJ),IS_ADJ);
    }

    @Test
    public void test_shouldHideButton() {
        assertFalse(card.shouldHideButton());
    }

    @Test
    public void test_getCount() {
        assertEquals(entries.size(),card.getCount());
    }

    @Test
    public void test_getButtonText() {
        assertEquals("SEE ALL", card.getButtonText());
    }

    @Test
    public void test_getHeading() {
        assertEquals("Conjugations",card.getHeading());
    }

    @Test
    public void test_addConjugation() {
        Map.Entry<String, ConjugationQuery.Conjugation> entry = new AbstractMap.SimpleEntry<>("new",null);
        card.addConjugation(entry,0);
        assertEquals(1,card.getCount());
    }
}
