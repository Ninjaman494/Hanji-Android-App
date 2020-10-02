package com.a494studios.koreanconjugator.display.cards;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.test.core.app.ApplicationProvider;

import com.a494studios.koreanconjugator.EntryQuery;
import com.a494studios.koreanconjugator.R;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
public class ExamplesCardUnitTest {
    private List<EntryQuery.Example> examples;
    private Context context;
    private ExamplesCard card;

    @Before
    public void init() {
        context = ApplicationProvider.getApplicationContext();
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
        Assert.assertEquals(group.getChildAt(0).getId(), R.id.listCard);
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
