package com.a494studios.koreanconjugator.display.cards;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.a494studios.koreanconjugator.ConjugationQuery;
import com.a494studios.koreanconjugator.R;
import com.a494studios.koreanconjugator.display.ConjInfoActivity;
import com.a494studios.koreanconjugator.type.SpeechLevel;
import com.a494studios.koreanconjugator.type.Tense;
import com.linearlistview.LinearListView;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.Shadows;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
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
        card = new ConjugationCard(conjugations, "term", "pos");
    }

    @Test(expected = NullPointerException.class)
    public void test_nullConjugations() {
        new ConjugationCard(null, "term", "pos");
    }

    @Test
    public void test_addBodyView() {
        ViewGroup group = new LinearLayout(context);
        card.addBodyView(context,group);
        Assert.assertEquals(group.getChildAt(0).getId(), R.id.listCard);
    }

    @Test
    public void test_clickConjugation() {
        Activity activity = Robolectric.buildActivity(Activity.class).create().start().visible().get();
        ViewGroup group = new LinearLayout(activity);
        card.addBodyView(activity, group);

        RelativeLayout relativeLayout = (RelativeLayout)group.getChildAt(0);
        Assert.assertEquals(relativeLayout.getId(), R.id.listCard);

        LinearListView listView = relativeLayout.findViewById(R.id.listCard_list);
        listView.getChildAt(0).callOnClick();
        assertNotNull(Shadows.shadowOf(activity).peekNextStartedActivity());

        Intent intent = Shadows.shadowOf(activity).peekNextStartedActivityForResult().intent;
        ConjugationQuery.Conjugation c = conjugations.get(0);
        assertEquals(intent.getComponent(),new ComponentName(activity, ConjInfoActivity.class));
        assertEquals(intent.getStringExtra(ConjInfoActivity.EXTRA_NAME),c.name());
        assertEquals(intent.getStringExtra(ConjInfoActivity.EXTRA_CONJ),c.conjugation());
        assertEquals(intent.getStringExtra(ConjInfoActivity.EXTRA_PRON),c.pronunciation());
        assertEquals(intent.getStringExtra(ConjInfoActivity.EXTRA_ROME),c.romanization());
        assertEquals(intent.getStringArrayListExtra(ConjInfoActivity.EXTRA_EXPL),new ArrayList<>(c.reasons()));
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
        ConjugationCard empty = new ConjugationCard(new ArrayList<ConjugationQuery.Conjugation>(), "term", "pos");
        assertEquals("Conjugations",empty.getHeading());
        assertEquals("Type",card.getHeading());
    }
}
