package com.a494studios.koreanconjugator;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.a494studios.koreanconjugator.display.cards.SynAntCard;

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
public class SynAntCardUnitTest {
    private SynAntCard synCard;
    private SynAntCard antCard;
    private List<String> wordList;
    private Context context;

    @Before
    public void init() {
        wordList = new ArrayList<>();
        wordList.add("syn1");
        wordList.add("syn2");
        wordList.add("syn3");
        synCard = new SynAntCard(wordList,true);
        antCard = new SynAntCard(wordList,false);
        context = RuntimeEnvironment.application.getApplicationContext();
    }

    @Test(expected =  NullPointerException.class)
    public void test_nullNote() {
        new SynAntCard(null,true);
    }

    @Test
    public void test_addBodyViewSyn() {
        ViewGroup group = new LinearLayout(context);
        synCard.addBodyView(context,group);
        TextView note = group.findViewById(R.id.simpleCard_text);
        assertEquals("syn1, syn2, syn3",note.getText());
        assertEquals(group.getChildAt(0).getId(),R.id.simpleCard);
    }

    @Test
    public void test_addBodyViewAnt() {
        ViewGroup group = new LinearLayout(context);
        antCard.addBodyView(context,group);
        TextView note = group.findViewById(R.id.simpleCard_text);
        assertEquals("syn1, syn2, syn3",note.getText());
        assertEquals(group.getChildAt(0).getId(),R.id.simpleCard);
    }

    @Test
    public void test_shouldHideButton() {
        assertTrue(synCard.shouldHideButton());
        assertTrue(antCard.shouldHideButton());
    }

    @Test
    public void test_getCount() {
        assertEquals(wordList.size(),synCard.getCount());
        assertEquals(wordList.size(),antCard.getCount());
    }

    @Test
    public void test_getButtonText() {
        assertEquals("Button",synCard.getButtonText());
        assertEquals("Button",antCard.getButtonText());
    }

    @Test
    public void test_getHeading() {
        assertEquals("Synonyms",synCard.getHeading());
        assertEquals("Antonyms",antCard.getHeading());
    }
}
