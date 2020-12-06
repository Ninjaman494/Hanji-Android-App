package com.a494studios.koreanconjugator.display.adapters;

import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.test.core.app.ApplicationProvider;

import com.a494studios.koreanconjugator.R;
import com.a494studios.koreanconjugator.fragment.ConjugationFragment;
import com.a494studios.koreanconjugator.type.SpeechLevel;
import com.a494studios.koreanconjugator.type.Tense;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
public class FavoritesAdapterUnitTest {
    private ArrayList<Map.Entry<String, ConjugationFragment>> entries;
    private FavoritesAdapter adapter;


    @Before
    public void init() {
        ConjugationFragment c1 = new ConjugationFragment("type",
                "name1","conj1", "TYPE", Tense.PRESENT, SpeechLevel.INFORMAL_HIGH,
                false,"","",new ArrayList<>());
        ConjugationFragment c2 = new ConjugationFragment("type",
                "name2","conj2", "TYPE", Tense.PRESENT, SpeechLevel.INFORMAL_HIGH,
                false,"","",new ArrayList<>());
        ConjugationFragment c3 = new ConjugationFragment("type",
                "name3","conj3", "TYPE", Tense.PRESENT, SpeechLevel.INFORMAL_HIGH,
                false,"","",new ArrayList<>());

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
        ViewGroup group = new LinearLayout(ApplicationProvider.getApplicationContext());
        int NAME_VIEW_ID = R.id.conjFormal;
        int CONJ_VIEW_ID = R.id.conjText;
        for(int i = 0;i<entries.size();i++) {
            View view = adapter.getView(i,null,group);
            Map.Entry<String, ConjugationFragment> e = entries.get(i);
            assertEquals(e.getKey(), ((TextView)view.findViewById(NAME_VIEW_ID)).getText().toString());
            assertEquals(e.getValue().conjugation(), ((TextView)view.findViewById(CONJ_VIEW_ID)).getText().toString());
        }
    }

    @Test
    public void test_addConjugation() {
        ConjugationFragment c = new ConjugationFragment("type",
                "name4","conj4", "TYPE", Tense.PRESENT, SpeechLevel.INFORMAL_HIGH,
                false,"","",new ArrayList<>());
        final AbstractMap.SimpleEntry<String, ConjugationFragment> entry = new AbstractMap.SimpleEntry<>("Fav 4", c);
        adapter.addConjugation(entry,2);

        assertEquals(4,adapter.getCount());
        assertEquals(entry,adapter.getItem(2));
    }

    @Test
    public void test_addConjugationNegIndex() {
        ConjugationFragment c = new ConjugationFragment("type",
                "name4","conj4", "TYPE", Tense.PRESENT, SpeechLevel.INFORMAL_HIGH,
                false,"","",new ArrayList<>());
        final AbstractMap.SimpleEntry<String, ConjugationFragment> entry = new AbstractMap.SimpleEntry<>("Fav 4", c);
        adapter.addConjugation(entry,-1);

        assertEquals(4,adapter.getCount());
        assertEquals(entry,adapter.getItem(3));
    }

    @Test
    public void test_addConjugationLargeIndex() {
        ConjugationFragment c = new ConjugationFragment("type",
                "name4","conj4", "TYPE", Tense.PRESENT, SpeechLevel.INFORMAL_HIGH,
                false,"","",new ArrayList<>());
        final AbstractMap.SimpleEntry<String, ConjugationFragment> entry = new AbstractMap.SimpleEntry<>("Fav 4", c);
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
