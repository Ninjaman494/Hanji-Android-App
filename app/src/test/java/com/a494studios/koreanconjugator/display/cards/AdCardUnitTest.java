package com.a494studios.koreanconjugator.display.cards;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.test.core.app.ApplicationProvider;

import com.a494studios.koreanconjugator.R;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
public class AdCardUnitTest {
    private AdCard card;
    private Context context;

    @Before
    public void init() {
        card = new AdCard("Ad unit id");
        context = ApplicationProvider.getApplicationContext();
    }
    @Test
    public void test_addBodyView() {
        ViewGroup group = new LinearLayout(context);
        card.addBodyView(context,group);
        Assert.assertEquals(group.getChildAt(0).getId(), R.id.adCard);
    }

    @Test
    public void test_shouldHideButton() {
        assertTrue(card.shouldHideButton());
    }

    @Test
    public void test_getCount() {
        assertEquals(1,card.getCount());
    }

    @Test
    public void test_getButtonText() {
        assertEquals("Button", card.getButtonText());
    }

    @Test
    public void test_getHeading() {
        assertEquals("Ad", card.getHeading());
    }
}
