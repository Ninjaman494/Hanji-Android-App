package com.a494studios.koreanconjugator.display.adapters;

import android.content.ComponentName;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.a494studios.koreanconjugator.ConjugationQuery;
import com.a494studios.koreanconjugator.MainActivity;
import com.a494studios.koreanconjugator.R;
import com.a494studios.koreanconjugator.display.ConjInfoActivity;
import com.a494studios.koreanconjugator.type.SpeechLevel;
import com.a494studios.koreanconjugator.type.Tense;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
public class ConjugationAdapterUnitTest {
    private List<ConjugationQuery.Conjugation> conjugations;
    private ConjugationAdapter adapter;

    @Before
    public void init() {
        ConjugationQuery.Conjugation c1 = new ConjugationQuery.Conjugation("type",
                "name1","conj1", "TYPE", Tense.PRESENT, SpeechLevel.INFORMAL_HIGH,
                false,"pron1","rome1",new ArrayList<String>());
        ConjugationQuery.Conjugation c2 = new ConjugationQuery.Conjugation("type",
                "name2","conj2", "TYPE", Tense.PRESENT, SpeechLevel.INFORMAL_HIGH,
                false,"pron2","rome2",new ArrayList<String>());
        ConjugationQuery.Conjugation c3 = new ConjugationQuery.Conjugation("type",
                "name3","conj3", "TYPE", Tense.PRESENT, SpeechLevel.NONE,
                false,"pron3","rome3",new ArrayList<String>());

        conjugations = new ArrayList<>();
        conjugations.add(c1);
        conjugations.add(c2);
        conjugations.add(c3);
        adapter = new ConjugationAdapter(conjugations);
    }

    @Test(expected = NullPointerException.class)
    public void test_nullConjugations() {
        new ConjugationAdapter(null);
    }

    @Test
    public void test_getView() {
        MainActivity activity = Robolectric.buildActivity(MainActivity.class).create().start().visible().get();
        ViewGroup group = new LinearLayout(activity);
        for(int i = 0;i<conjugations.size();i++) {
            View view = adapter.getView(i,null,group);
            ConjugationQuery.Conjugation c = conjugations.get(i);
            TextView nameView = view.findViewById(R.id.conjFormal);
            TextView conjView = view.findViewById(R.id.conjText);

            if(c.speechLevel() == SpeechLevel.NONE){
                assertEquals(c.name(),nameView.getText());
            } else {
                assertEquals(formatSpeechLevel(c.speechLevel()), nameView.getText());
            }
            assertEquals(c.conjugation(),conjView.getText());

            conjView.callOnClick();
            Intent intent = Shadows.shadowOf(activity).peekNextStartedActivityForResult().intent;
            assertEquals(intent.getComponent(),new ComponentName(activity, ConjInfoActivity.class));
            assertEquals(intent.getStringExtra(ConjInfoActivity.EXTRA_NAME),c.name());
            assertEquals(intent.getStringExtra(ConjInfoActivity.EXTRA_CONJ),c.conjugation());
            assertEquals(intent.getStringExtra(ConjInfoActivity.EXTRA_PRON),c.pronunciation());
            assertEquals(intent.getStringExtra(ConjInfoActivity.EXTRA_ROME),c.romanization());
            assertEquals(intent.getStringArrayListExtra(ConjInfoActivity.EXTRA_EXPL),new ArrayList<>(c.reasons()));
        }
    }

    @Test
    public void test_getCount() {
        assertEquals(conjugations.size(),adapter.getCount());
    }

    @Test
    public void test_getItem() {
        for(int i = 0;i<conjugations.size();i++) {
            assertEquals(conjugations.get(i),adapter.getItem(i));
        }
    }

    @Test
    public void test_getItemId() {
        for(int i = 0;i<conjugations.size();i++) {
            assertEquals(i,adapter.getItemId(i));
        }
    }

    @Test
    public void test_hashStableIds() {
        assertTrue(adapter.hasStableIds());
    }

    private String formatSpeechLevel(SpeechLevel speechLevel) {
        String string = speechLevel.toString();
        return string.replace('_', ' ').toLowerCase();
    }
}
