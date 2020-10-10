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

import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
public class NoteCardUnitTest {
    private static final String NOTE = "note";
    private NoteCard card;
    private DisplayCardView cardView;
    private LinearLayout viewGroup;

    @Before
    public void init() {
        card = new NoteCard(NOTE);
        Context context = ApplicationProvider.getApplicationContext();
        cardView = new DisplayCardView(context);
        viewGroup = new LinearLayout(context);

        card.addBodyView(context, viewGroup, cardView);
    }

    @Test(expected =  NullPointerException.class)
    public void test_nullNote() {
        new NoteCard(null);
    }

    @Test
    public void test_addBodyView() {
        TextView note = viewGroup.findViewById(R.id.simpleCard_text);

        assertEquals(note.getText().toString(), NOTE);
        assertEquals(viewGroup.getChildAt(0).getId(), R.id.simpleCard);
    }

    @Test
    public void test_shouldHideButton() {
        assertEquals(cardView.findViewById(R.id.displayCard_button).getVisibility(), ViewGroup.GONE);
    }

    @Test
    public void test_getCount() {
        assertEquals(1,card.getCount());
    }

    @Test
    public void test_getHeading() {
        assertEquals("Note", card.getHeading());
    }
}
