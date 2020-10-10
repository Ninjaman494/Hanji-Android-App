package com.a494studios.koreanconjugator.display.cards;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.test.core.app.ApplicationProvider;

import com.a494studios.koreanconjugator.EntryQuery;
import com.a494studios.koreanconjugator.R;
import com.a494studios.koreanconjugator.display.DisplayCardView;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
public class ExamplesCardUnitTest {
    private List<EntryQuery.Example> examples;
    private Context context;
    private ExamplesCard card;
    private DisplayCardView cardView;
    private LinearLayout viewGroup;

    @Before
    public void init() {
        context = ApplicationProvider.getApplicationContext();
        examples = new ArrayList<>();
        card = new ExamplesCard(examples);
        cardView = new DisplayCardView(context);
        viewGroup = new LinearLayout(context);

        card.addBodyView(context, viewGroup, cardView);
    }

    @Test(expected = NullPointerException.class)
    public void test_nullExamples() {
        new ExamplesCard(null);
    }

    @Test
    public void test_addBodyView() {
        Assert.assertEquals(viewGroup.getChildAt(0).getId(), R.id.listCard);
    }

    @Test
    public void test_shouldHideButton() {
        assertEquals(cardView.findViewById(R.id.displayCard_button).getVisibility(), ViewGroup.GONE);
    }

    @Test
    public void test_getCount() {
        assertEquals(examples.size(),card.getCount());
    }

    @Test
    public void test_getHeading() {
        assertEquals("Examples",card.getHeading());
    }
}
