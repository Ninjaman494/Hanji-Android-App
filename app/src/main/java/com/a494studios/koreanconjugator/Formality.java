package com.a494studios.koreanconjugator;

/**
 * Created by akash on 1/1/2018.
 */

public enum Formality {
    INFORMAL_LOW, INFORMAL_HIGH, FORMAL_LOW, FORMAL_HIGH,NONE;

    @Override
    public String toString() {
        switch(this) {
            case INFORMAL_LOW:  return "informal low";
            case INFORMAL_HIGH: return "informal high";
            case FORMAL_LOW:    return "formal low";
            case FORMAL_HIGH:   return "formal high";
            case NONE:          return "none";
            default: throw new IllegalArgumentException();
        }
    }
}