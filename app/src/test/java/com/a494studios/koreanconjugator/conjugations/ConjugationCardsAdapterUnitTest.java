package com.a494studios.koreanconjugator.conjugations;

import com.a494studios.koreanconjugator.ConjugationQuery;
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

    private List<List<ConjugationQuery.Conjugation>> conjugations;

    @Before
    public void init() {
        ConjugationQuery.Conjugation c1 = new ConjugationQuery.Conjugation("type",
                "name1","conj1", "TYPE", Tense.PRESENT, SpeechLevel.INFORMAL_HIGH,
                false,"pron1","rome1",new ArrayList<>());
        ConjugationQuery.Conjugation c2 = new ConjugationQuery.Conjugation("type",
                "name2","conj2", "TYPE", Tense.PRESENT, SpeechLevel.INFORMAL_HIGH,
                false,"pron2","rome2",new ArrayList<>());
        ConjugationQuery.Conjugation c3 = new ConjugationQuery.Conjugation("type",
                "name3","conj3", "TYPE", Tense.PRESENT, SpeechLevel.INFORMAL_HIGH,
                false,"pron3","rome3",new ArrayList<>());

        ArrayList<ConjugationQuery.Conjugation> group1 = new ArrayList<>();
        ArrayList<ConjugationQuery.Conjugation> group2 = new ArrayList<>();
        ArrayList<ConjugationQuery.Conjugation> group3 = new ArrayList<>();

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