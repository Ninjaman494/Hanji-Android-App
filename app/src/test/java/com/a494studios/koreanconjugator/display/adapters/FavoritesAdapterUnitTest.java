package com.a494studios.koreanconjugator.display.adapters;

import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.a494studios.koreanconjugator.ConjugationQuery;
import com.a494studios.koreanconjugator.R;
import com.a494studios.koreanconjugator.type.SpeechLevel;
import com.a494studios.koreanconjugator.type.Tense;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
public class FavoritesAdapterUnitTest {
    private final int NAME_VIEW_ID = R.id.conjFormal;
    private final int CONJ_VIEW_ID = R.id.conjText;

    private ArrayList<Map.Entry<String, ConjugationQuery.Conjugation>> entries;
    private FavoritesAdapter adapter;


    @Before
    public void init() {
        ConjugationQuery.Conjugation c1 = new ConjugationQuery.Conjugation("type",
                "name1","conj1", "TYPE", Tense.PRESENT, SpeechLevel.INFORMAL_HIGH,
                false,"","",new ArrayList<String>());
        ConjugationQuery.Conjugation c2 = new ConjugationQuery.Conjugation("type",
                "name2","conj2", "TYPE", Tense.PRESENT, SpeechLevel.INFORMAL_HIGH,
                false,"","",new ArrayList<String>());
        ConjugationQuery.Conjugation c3 = new ConjugationQuery.Conjugation("type",
                "name3","conj3", "TYPE", Tense.PRESENT, SpeechLevel.INFORMAL_HIGH,
                false,"","",new ArrayList<String>());

        entries = new ArrayList<>();
        entries.add(new AbstractMap.SimpleEntry<>("Fav 1",c1));
        entries.add(new AbstractMap.SimpleEntry<>("Fav 2",c2));
        entries.add(new AbstractMap.SimpleEntry<>("Fav 3",c3));
        adapter = new FavoritesAdapter(entries);
    }

    @Test(expected = NullPointerException.class)
    public void test_nullList() {
        new FavoritesAdapter(null);
    }

    @Test
    public void test_getView() {
        ViewGroup group = new LinearLayout(RuntimeEnvironment.application.getApplicationContext());

        View view1 = adapter.getView(0,null,group);
        View view2 = adapter.getView(1,null,group);
        View view3 = adapter.getView(2,null,group);
        Map.Entry<String, ConjugationQuery.Conjugation> e1 = entries.get(0);
        Map.Entry<String, ConjugationQuery.Conjugation> e2 = entries.get(1);
        Map.Entry<String, ConjugationQuery.Conjugation> e3 = entries.get(2);

        assertEquals(e1.getKey(),((TextView)view1.findViewById(NAME_VIEW_ID)).getText());
        assertEquals(e1.getValue().conjugation(),((TextView)view1.findViewById(CONJ_VIEW_ID)).getText());

        assertEquals(e2.getKey(),((TextView)view2.findViewById(NAME_VIEW_ID)).getText());
        assertEquals(e2.getValue().conjugation(),((TextView)view2.findViewById(CONJ_VIEW_ID)).getText());

        assertEquals(e3.getKey(),((TextView)view3.findViewById(NAME_VIEW_ID)).getText());
        assertEquals(e3.getValue().conjugation(),((TextView)view3.findViewById(CONJ_VIEW_ID)).getText());
    }

    @Test
    public void test_addConjugation() {
        ConjugationQuery.Conjugation c = new ConjugationQuery.Conjugation("type",
                "name4","conj4", "TYPE", Tense.PRESENT, SpeechLevel.INFORMAL_HIGH,
                false,"","",new ArrayList<String>());
        final AbstractMap.SimpleEntry<String, ConjugationQuery.Conjugation> entry = new AbstractMap.SimpleEntry<>("Fav 4", c);
        adapter.addConjugation(entry,2);

        assertEquals(4,adapter.getCount());
        assertEquals(entry,adapter.getItem(2));
    }

    @Test
    public void test_addConjugationNegIndex() {
        ConjugationQuery.Conjugation c = new ConjugationQuery.Conjugation("type",
                "name4","conj4", "TYPE", Tense.PRESENT, SpeechLevel.INFORMAL_HIGH,
                false,"","",new ArrayList<String>());
        final AbstractMap.SimpleEntry<String, ConjugationQuery.Conjugation> entry = new AbstractMap.SimpleEntry<>("Fav 4", c);
        adapter.addConjugation(entry,-1);

        assertEquals(4,adapter.getCount());
        assertEquals(entry,adapter.getItem(3));
    }

    @Test
    public void test_addConjugationLargeIndex() {
        ConjugationQuery.Conjugation c = new ConjugationQuery.Conjugation("type",
                "name4","conj4", "TYPE", Tense.PRESENT, SpeechLevel.INFORMAL_HIGH,
                false,"","",new ArrayList<String>());
        final AbstractMap.SimpleEntry<String, ConjugationQuery.Conjugation> entry = new AbstractMap.SimpleEntry<>("Fav 4", c);
        adapter.addConjugation(entry,10);

        assertEquals(4,adapter.getCount());
        assertEquals(entry,adapter.getItem(3));
    }

    @Test
    public void test_getCount() {
        assertEquals(entries.size(),adapter.getCount());
    }

    @Test
    public void test_getItem() {
        for(int i = 0;i<entries.size();i++) {
            assertEquals(entries.get(i),adapter.getItem(i));
        }
    }

    @Test
    public void test_getItemId() {
        for(int i = 0;i<entries.size();i++) {
            assertEquals(i,adapter.getItemId(i));
        }
    }

    @Test
    public void test_hasStableIds() {
        assertTrue(adapter.hasStableIds());
    }
}
