package com.a494studios.koreanconjugator;

import java.util.ArrayList;

/**
 * Created by akash on 1/1/2018.
 */

public enum Tense {
    PAST, PRESENT, FUTURE, FUT_COND, NONE;

    @Override
    public String toString() {
        switch(this) {
            case PAST:      return "past";
            case PRESENT:   return "present";
            case FUTURE:    return "future";
            case FUT_COND:  return "future conditional";
            case NONE:      return "none";
            default: throw new IllegalArgumentException();
        }
    }

    public static ArrayList<Conjugation> getSubSet(ArrayList<Conjugation> conjugations, Tense t){
        ArrayList<Conjugation> subset = new ArrayList<>();
        for(Conjugation c: conjugations){
            if(c.getTense() == t){
                subset.add(c);
            }
        }
        return subset;
    }
}
