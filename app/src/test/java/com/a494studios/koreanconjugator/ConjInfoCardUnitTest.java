package com.a494studios.koreanconjugator;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.a494studios.koreanconjugator.display.ConjInfoCard;

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
public class ConjInfoCardUnitTest {
    private final String NAME = "name";
    private final String CONJUGATED = "conjugated";
    private final String PRONC = "pronc";
    private final String ROME = "rome";
    private List<String> reasons;
    private Context context;
    private ConjInfoCard card;

    @Before
    public void init() {
        reasons = new ArrayList<>();
        reasons.add("reason (hi + hi -> hihi)");
        reasons.add("reason (hi + hi -> hihi)");
        reasons.add("reason (hi + hi -> hihi)");
        context = RuntimeEnvironment.application.getApplicationContext();
        card = new ConjInfoCard(NAME,CONJUGATED,PRONC,ROME,reasons);
    }

    @Test(expected = NullPointerException.class)
    public void test_nullName() {
        new ConjInfoCard(null,CONJUGATED,PRONC,ROME,reasons);
    }

    @Test(expected = NullPointerException.class)
    public void test_nullConj() {
        new ConjInfoCard(NAME,null,PRONC,ROME,reasons);
    }

    @Test(expected = NullPointerException.class)
    public void test_nullPronc() {
        new ConjInfoCard(NAME,CONJUGATED,null,ROME,reasons);
    }

    @Test(expected = NullPointerException.class)
    public void test_nullRome() {
        new ConjInfoCard(NAME,CONJUGATED,PRONC, null,reasons);
    }

    @Test(expected = NullPointerException.class)
    public void test_nullReasons() {
        new ConjInfoCard(NAME,CONJUGATED,PRONC,ROME,null);
    }

    @Test
    public void test_addBodyView() {
        ViewGroup group = new LinearLayout(context);
        card.addBodyView(context,group);
        TextView conj = group.findViewById(R.id.conjInfo_conjugated);
        TextView pronc = group.findViewById(R.id.conjInfo_hpronc);
        TextView roman = group.findViewById(R.id.conjInfo_roman);

        assertEquals(group.getChildAt(0).getId(),R.id.conjInfo);
        assertEquals(CONJUGATED,conj.getText());
        assertEquals(PRONC,pronc.getText());
        assertEquals(ROME,roman.getText());
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
        assertEquals("Name",card.getHeading());
    }
}
