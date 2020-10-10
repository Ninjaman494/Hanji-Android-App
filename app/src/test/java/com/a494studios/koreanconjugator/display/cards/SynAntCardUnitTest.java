package com.a494studios.koreanconjugator.display.cards;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.test.core.app.ApplicationProvider;

import com.a494studios.koreanconjugator.R;
import com.a494studios.koreanconjugator.display.DisplayCardView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
public class SynAntCardUnitTest {
    private SynAntCard card;
    private List<String> wordList;
    private Context context;
    private DisplayCardView cardView;
    private LinearLayout viewGroup;

    @Before
    public void init() {
        wordList = new ArrayList<>();
        wordList.add("syn1");
        wordList.add("syn2");
        wordList.add("syn3");
        card = new SynAntCard(wordList,true);
        context = ApplicationProvider.getApplicationContext();
        cardView = new DisplayCardView(context);
        viewGroup = new LinearLayout(context);

        card.addBodyView(context, viewGroup, cardView);
    }

    @Test(expected =  NullPointerException.class)
    public void test_nullNote() {
        new SynAntCard(null,true);
    }

    @Test
    public void test_addBodyView() {
        card.addBodyView(context, viewGroup, cardView);
        TextView note = viewGroup.findViewById(R.id.simpleCard_text);
        assertEquals("syn1, syn2, syn3", note.getText().toString());
        assertEquals(viewGroup.getChildAt(0).getId(), R.id.simpleCard);
    }

    @Test
    public void test_shouldHideButton() {
        assertEquals(cardView.findViewById(R.id.displayCard_button).getVisibility(), ViewGroup.GONE);
    }

    @Test
    public void test_getCount() {
        assertEquals(wordList.size(), card.getCount());
    }

    @Test
    public void test_getHeading() {
        assertEquals("Synonyms", card.getHeading());

        SynAntCard card = new SynAntCard(wordList,false);
        assertEquals("Antonyms", card.getHeading());
    }
}
