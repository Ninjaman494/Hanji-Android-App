package com.a494studios.koreanconjugator;

import com.a494studios.koreanconjugator.parsing.Favorite;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FavoriteUnitTest {
    private static final String NAME = "NAME";
    private static final String CONJ_NAME = "CONJUGATION";
    private static final boolean HONORIFIC = false;

    @Test
    public void getName_isCorrect() throws Exception {
        Favorite favorite = new Favorite(NAME,CONJ_NAME,HONORIFIC);
        assertEquals(favorite.getName(),NAME);
    }

    @Test
    public void getConjugationName_isCorrect() throws Exception {
        Favorite favorite = new Favorite(NAME,CONJ_NAME,HONORIFIC);
        assertEquals(favorite.getConjugationName(),CONJ_NAME);
    }

    @Test
    public void isHonorific_isCorrect() throws Exception {
        Favorite favorite = new Favorite(NAME,CONJ_NAME,HONORIFIC);
        assertEquals(favorite.isHonorific(),HONORIFIC);
    }
}
