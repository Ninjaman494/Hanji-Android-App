package com.a494studios.koreanconjugator.parsing;

/**
 * Created by akash on 1/1/2018.
 */

public enum Tense implements Category{
    PAST, PRESENT, FUTURE, FUT_COND, NONE;

    @Override
    public String printName() {
        switch(this) {
            case PAST:      return "past";
            case PRESENT:   return "present";
            case FUTURE:    return "future";
            case FUT_COND:  return "future conditional";
            case NONE:      return "none";
            default: throw new IllegalArgumentException();
        }
    }

    @Override
    public String toString(){
        return printName();
    }
}
