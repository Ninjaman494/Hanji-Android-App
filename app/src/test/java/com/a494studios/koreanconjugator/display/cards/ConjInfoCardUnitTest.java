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
public class ConjInfoCardUnitTest {
    private final String NAME = "name";
    private final String CONJUGATED = "conjugated";
    private final String PRONC = "pronc";
    private final String ROME = "rome";
    private List<String> reasons;
    private Context context;
    private ConjInfoCard card;
    private DisplayCardView cardView;
    private LinearLayout viewGroup;

    @Before
    public void init() {
        reasons = new ArrayList<>();
        reasons.add("reason (hi + hi -> hihi)");
        reasons.add("reason (hi + hi -> hihi)");
        reasons.add("reason (hi + hi -> hihi)");
        context = ApplicationProvider.getApplicationContext();
        card = new ConjInfoCard(NAME,CONJUGATED,PRONC,ROME,reasons);
        cardView = new DisplayCardView(context);
        viewGroup = new LinearLayout(context);

        card.addBodyView(context, viewGroup, cardView);
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
        card.addBodyView(context, viewGroup, cardView);
        TextView conj = viewGroup.findViewById(R.id.conjInfo_conjugated);
        TextView pronc = viewGroup.findViewById(R.id.conjInfo_hpronc);
        TextView roman = viewGroup.findViewById(R.id.conjInfo_roman);

        assertEquals(viewGroup.getChildAt(0).getId(), R.id.conjInfo);
        assertEquals(CONJUGATED, conj.getText().toString());
        assertEquals(PRONC, pronc.getText().toString());
        assertEquals(ROME, roman.getText().toString());
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
        assertEquals("Name",card.getHeading());
    }
}
