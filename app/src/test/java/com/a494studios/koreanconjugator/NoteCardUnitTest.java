package com.a494studios.koreanconjugator;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.a494studios.koreanconjugator.display.NoteCard;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
public class NoteCardUnitTest {
    private static final String NOTE = "note";
    private NoteCard card;
    private Context context;

    @Before
    public void init() {
        card = new NoteCard(NOTE);
        context = RuntimeEnvironment.application.getApplicationContext();
    }

    @Test(expected =  NullPointerException.class)
    public void test_nullNote() {
        new NoteCard(null);
    }

    @Test
    public void test_getBodyView() {
        ViewGroup group = new LinearLayout(context);
        card.addBodyView(context,group);
        TextView note = group.findViewById(R.id.simpleCard_text);

        assertEquals(NOTE,note.getText());
        assertEquals(group.getChildAt(0).getId(),R.id.simpleCard);
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
        assertEquals("Note", card.getHeading());
    }
}
