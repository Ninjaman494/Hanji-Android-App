package com.a494studios.koreanconjugator.display.cards;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.test.core.app.ApplicationProvider;

import com.a494studios.koreanconjugator.R;
import com.a494studios.koreanconjugator.display.DisplayCardView;
import com.a494studios.koreanconjugator.utils.WordInfoView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
public class DefPOSCardUnitTest {
    private final String TERM = "term";
    private final String POS = "pos";

    private Context context;

    private List<String> definitions3;
    private DefPOSCard card3;
    private DisplayCardView cardView3;

    private List<String> definitions4;
    private DefPOSCard card4;
    private DisplayCardView cardView4;

    @Before
    public void init(){
        context = ApplicationProvider.getApplicationContext();

        definitions3 = new ArrayList<>();
        definitions3.add("def 1");
        definitions3.add("def 2");
        definitions3.add("def 3");

        definitions4 = new ArrayList<>(definitions3);
        definitions4.add("def 4");

        cardView3 = new DisplayCardView(context);
        cardView4 = new DisplayCardView(context);

        card3 = new DefPOSCard(TERM, POS, definitions3);
        card4 = new DefPOSCard(TERM, POS, definitions4);

        //card3.addBodyView(context,  new LinearLayout(context), cardView3);
        //card4.addBodyView(context,  new LinearLayout(context), cardView4);
    }

    @Test(expected =  NullPointerException.class)
    public void test_nullTerm() {
        new DefPOSCard(null,POS, definitions3);
    }

    @Test(expected =  NullPointerException.class)
    public void test_nullPOS() {
        new DefPOSCard(TERM,null, definitions3);
    }

    @Test(expected =  NullPointerException.class)
    public void test_nullDefinitions() {
        new DefPOSCard(TERM,POS,null);
    }

    @Test
    public void test_shouldHideButton() {
        card3.addBodyView(context, new LinearLayout(context), cardView3);
        assertEquals(cardView3.findViewById(R.id.displayCard_button).getVisibility(), ViewGroup.GONE);

        card4.addBodyView(context, new LinearLayout(context), cardView4);
        assertEquals(cardView4.findViewById(R.id.displayCard_button).getVisibility(), ViewGroup.VISIBLE);
    }

    @Test
    public void test_addBodyView3() {
        ViewGroup group = new LinearLayout(context);
        card3.addBodyView(context,  group, cardView3);
        assertTrue(group.getChildAt(0) instanceof WordInfoView);
    }

    @Test
    public void test_addBodyView4() {
        ViewGroup group = new LinearLayout(context);
        card4.addBodyView(context,  group, cardView4);
        assertTrue(group.getChildAt(0) instanceof WordInfoView);
    }

    @Test
    public void test_onButtonClick() {
        card3.addBodyView(context, new LinearLayout(context), cardView3);
        Button btn = cardView3.findViewById(R.id.displayCard_button);

        card3.onButtonClick();
        assertEquals(btn.getText(),"COLLAPSE");
        card3.onButtonClick();
        assertEquals(btn.getText(),"0 MORE");

        card4.addBodyView(context, new LinearLayout(context), cardView4);
        btn = cardView4.findViewById(R.id.displayCard_button);

        card4.onButtonClick();
        assertEquals(btn.getText(),"COLLAPSE");
        card4.onButtonClick();
        assertEquals(btn.getText(),"1 MORE");
    }

    @Test
    public void test_getCount() {
        assertEquals(definitions3.size(), card3.getCount());
        assertEquals(definitions4.size(), card4.getCount());
    }

    @Test
    public void test_getButtonTest() {
        card3.addBodyView(context, new LinearLayout(context), cardView3);
        Button btn = cardView3.findViewById(R.id.displayCard_button);
        assertEquals(btn.getText(), "0 MORE");

        card4.addBodyView(context, new LinearLayout(context), cardView4);
        btn = cardView4.findViewById(R.id.displayCard_button);
        assertEquals(btn.getText(), "1 MORE");
    }

    @Test
    public void test_getHeading() {
        assertNull(card3.getHeading());
        assertNull(card4.getHeading());
    }
}
