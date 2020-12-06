package com.a494studios.koreanconjugator.conjugations;

import com.a494studios.koreanconjugator.fragment.ConjugationFragment;
import com.a494studios.koreanconjugator.type.SpeechLevel;
import com.a494studios.koreanconjugator.type.Tense;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(RobolectricTestRunner.class)
public class ConjugationCardsAdapterUnitTest {

    private List<List<ConjugationFragment>> conjugations;

    @Before
    public void init() {
        ConjugationFragment c1 = new ConjugationFragment("type",
                "name1","conj1", "TYPE", Tense.PRESENT, SpeechLevel.INFORMAL_HIGH,
                false,"pron1","rome1",new ArrayList<>());
        ConjugationFragment c2 = new ConjugationFragment("type",
                "name2","conj2", "TYPE", Tense.PRESENT, SpeechLevel.INFORMAL_HIGH,
                false,"pron2","rome2",new ArrayList<>());
        ConjugationFragment c3 = new ConjugationFragment("type",
                "name3","conj3", "TYPE", Tense.PRESENT, SpeechLevel.INFORMAL_HIGH,
                false,"pron3","rome3",new ArrayList<>());

        ArrayList<ConjugationFragment> group1 = new ArrayList<>();
        ArrayList<ConjugationFragment> group2 = new ArrayList<>();
        ArrayList<ConjugationFragment> group3 = new ArrayList<>();

        group1.add(c1);
        group2.add(c2);
        group3.add(c3);

        conjugations = new ArrayList<>();
        conjugations.add(group1);
        conjugations.add(group2);
        conjugations.add(group3);
    }

    @Test(expected = NullPointerException.class)
    public void test_nullConjugations() {
        new ConjugationCardsAdapter(null, "term", "pos");
    }

    @Test(expected = NullPointerException.class)
    public void test_nullTerm() {
        new ConjugationCardsAdapter(conjugations, null, "pos");
    }

    @Test(expected = NullPointerException.class)
    public void test_nullPos() {
        new ConjugationCardsAdapter(conjugations, "term", null);
    }
}