package com.a494studios.koreanconjugator.display.cards;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.a494studios.koreanconjugator.R;
import com.a494studios.koreanconjugator.display.ConjInfoActivity;
import com.a494studios.koreanconjugator.display.DisplayCardView;
import com.a494studios.koreanconjugator.fragment.ConjugationFragment;
import com.a494studios.koreanconjugator.type.SpeechLevel;
import com.a494studios.koreanconjugator.type.Tense;
import com.linearlistview.LinearListView;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(RobolectricTestRunner.class)
public class ConjugationCardUnitTest {
    private List<ConjugationFragment> conjugations;
    private ConjugationCard card;
    private DisplayCardView cardView;
    private LinearLayout viewGroup;
    private Activity activity;

    @Before
    public void init() {
        ConjugationFragment c = new ConjugationFragment("type",
                "name","conj", "TYPE", Tense.PRESENT, SpeechLevel.INFORMAL_HIGH,
                false,"","",new ArrayList<>());

        conjugations = new ArrayList<>();
        conjugations.add(c);
        activity = Robolectric.buildActivity(Activity.class).create().start().visible().get();
        card = new ConjugationCard(conjugations, "term", "pos");
        cardView = new DisplayCardView(activity);
        viewGroup = new LinearLayout(activity);

        card.addBodyView(activity, viewGroup, cardView);
    }

    @Test(expected = NullPointerException.class)
    public void test_nullConjugations() {
        new ConjugationCard(null, "term", "pos");
    }

    @Test
    public void test_addBodyView() {
        Assert.assertEquals(viewGroup.getChildAt(0).getId(), R.id.listCard);
    }

    @Test
    public void test_clickConjugation() {
        RelativeLayout relativeLayout = (RelativeLayout)viewGroup.getChildAt(0);

        Assert.assertEquals(relativeLayout.getId(), R.id.listCard);

        LinearListView listView = relativeLayout.findViewById(R.id.listCard_list);
        listView.getChildAt(0).callOnClick();

        assertNotNull(Shadows.shadowOf(activity).peekNextStartedActivity());

        Intent intent = Shadows.shadowOf(activity).peekNextStartedActivityForResult().intent;
        ConjugationFragment c = conjugations.get(0);

        assertEquals(intent.getComponent(),new ComponentName(activity, ConjInfoActivity.class));
        assertEquals(intent.getStringExtra(ConjInfoActivity.EXTRA_NAME), c.name());
        assertEquals(intent.getStringExtra(ConjInfoActivity.EXTRA_CONJ), c.conjugation());
        assertEquals(intent.getStringExtra(ConjInfoActivity.EXTRA_PRON), c.pronunciation());
        assertEquals(intent.getStringExtra(ConjInfoActivity.EXTRA_ROME), c.romanization());
        assertEquals(intent.getStringArrayListExtra(ConjInfoActivity.EXTRA_EXPL), new ArrayList<>(c.reasons()));
    }

    @Test
    public void test_shouldHideButton() {
        assertEquals(cardView.findViewById(R.id.displayCard_button).getVisibility(), ViewGroup.GONE);
    }

    @Test
    public void test_getCount() {
        assertEquals(conjugations.size(), card.getCount());
    }

    @Test
    public void test_getHeading() {
        ConjugationCard empty = new ConjugationCard(new ArrayList<>(), "term", "pos");
        assertEquals("Conjugations",empty.getHeading());
        assertEquals("Type",card.getHeading());
    }
}
