package com.a494studios.koreanconjugator;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.a494studios.koreanconjugator.display.cards.ExamplesCard;

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
public class ExamplesCardUnitTest {
    private List<ExamplesQuery.Example> examples;
    private Context context;
    private ExamplesCard card;

    @Before
    public void init() {
        context = RuntimeEnvironment.application.getApplicationContext();
        examples = new ArrayList<>();
        card = new ExamplesCard(examples);
    }

    @Test(expected = NullPointerException.class)
    public void test_nullExamples() {
        new ExamplesCard(null);
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
        assertEquals(examples.size(),card.getCount());
    }

    @Test
    public void test_getButtonText() {
        assertEquals("Button",card.getButtonText());
    }

    @Test
    public void test_getHeading() {
        assertEquals("Examples",card.getHeading());
    }
}
