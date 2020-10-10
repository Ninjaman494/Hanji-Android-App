package com.a494studios.koreanconjugator.display.cards;

import android.app.Activity;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.a494studios.koreanconjugator.R;
import com.a494studios.koreanconjugator.display.DisplayCardView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
public class AdCardUnitTest {
    private AdCard card;
    private Activity activity;
    private DisplayCardView cardView;
    private LinearLayout viewGroup;

    @Before
    public void init() {
        card = new AdCard("Ad unit id");
        activity = Robolectric.buildActivity(Activity.class).create().start().visible().get();
        cardView = new DisplayCardView(activity);
        viewGroup = new LinearLayout(activity);
    }

    @Test
    public void test_shouldHideButton() {
        card.addBodyView(activity, viewGroup, cardView);

        assertEquals(cardView.findViewById(R.id.displayCard_button).getVisibility(), ViewGroup.GONE);
    }

    @Test
    public void test_getCount() {
        assertEquals(1,card.getCount());
    }

    @Test
    public void test_getHeading() {
        assertEquals("Ad", card.getHeading());
    }
}
