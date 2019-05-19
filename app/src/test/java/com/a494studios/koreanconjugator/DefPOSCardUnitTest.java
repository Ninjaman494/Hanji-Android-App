package com.a494studios.koreanconjugator;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.a494studios.koreanconjugator.display.DefPOSCard;
import com.a494studios.koreanconjugator.utils.WordInfoView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
public class DefPOSCardUnitTest {
    private final String TERM = "term";
    private final String POS = "pos";
    private List<String> definitions3;
    private List<String> definitions4;
    private DefPOSCard card3;
    private DefPOSCard card4;
    private Context context;

    @Before
    public void init(){
        definitions3 = new ArrayList<>();
        definitions3.add("def 1");
        definitions3.add("def 2");
        definitions3.add("def 3");
        definitions4 = new ArrayList<>(definitions3);
        definitions4.add("def 4");
        context = RuntimeEnvironment.application.getApplicationContext();

        card3 = new DefPOSCard(TERM,POS,definitions3);
        card4 = new DefPOSCard(TERM,POS,definitions4);
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
        assertTrue(card3.shouldHideButton());
        assertFalse(card4.shouldHideButton());
    }

    @Test
    public void test_addBodyView3() {
        ViewGroup group = new LinearLayout(context);
        card3.addBodyView(context,group);
        assertTrue(group.getChildAt(0) instanceof WordInfoView);
    }

    @Test
    public void test_addBodyView4() {
        ViewGroup group = new LinearLayout(context);
        card4.addBodyView(context,group);
        assertTrue(group.getChildAt(0) instanceof WordInfoView);
    }

    @Test(expected =  NullPointerException.class)
    public void test_onButtonClickNull() {
        card3.onButtonClick();
    }

    @Test
    public void test_onButtonClick() {
        card4.addBodyView(context,new LinearLayout(context));
        card4.onButtonClick();
        assertEquals(card4.getButtonText(),"COLLAPSE");
        card3.addBodyView(context,new LinearLayout(context));
        card3.onButtonClick();
        assertEquals(card3.getButtonText(),"COLLAPSE");

        // Click again
        card4.onButtonClick();
        assertEquals(card4.getButtonText(),"1 MORE");
        card3.onButtonClick();
        assertEquals(card3.getButtonText(),"0 MORE");
    }

    @Test
    public void test_getCount() {
        assertEquals(definitions4.size(),card4.getCount());
        assertEquals(definitions3.size(),card3.getCount());
    }

    @Test
    public void test_getButtonTest() {
        assertEquals("1 MORE",card4.getButtonText());
        assertEquals("0 MORE",card3.getButtonText());
    }

    @Test
    public void test_getHeading() {
        assertNull(card3.getHeading());
        assertNull(card4.getHeading());
    }
}
