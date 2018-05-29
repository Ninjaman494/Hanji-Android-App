package com.a494studios.koreanconjugator.parsing;

/**
 * Created by akash on 1/1/2018.
 */

public enum Formality implements Category{
    INFORMAL_LOW, INFORMAL_HIGH, FORMAL_LOW, FORMAL_HIGH,HONORIFIC_LOW,HONORIFIC_HIGH,NONE;

    @Override
    public String printName() {
        switch(this) {
            case INFORMAL_LOW:  return "informal low";
            case INFORMAL_HIGH: return "informal high";
            case FORMAL_LOW:    return "formal low";
            case FORMAL_HIGH:   return "formal high";
            case HONORIFIC_LOW: return "honorific low";
            case HONORIFIC_HIGH:return "honorific high";
            case NONE:          return "none";
            default: throw new IllegalArgumentException();
        }
    }

    public static Formality fromString(String string) {
        try{
            return valueOf(string);
        }catch (IllegalArgumentException e){
            return null;
        }
    }

    @Override
    public String toString(){
        return printName();
    }

    public String getType(){
        return super.toString();
    }
}