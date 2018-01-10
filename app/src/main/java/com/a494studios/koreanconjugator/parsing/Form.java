package com.a494studios.koreanconjugator.parsing;

/**
 * Created by akash on 1/1/2018.
 */

public enum Form implements Category {
    DECLARATIVE, INQUISITIVE, IMPERATIVE, PROPOSITIVE, CON_IF, CON_AND,
    NOMINAL, PAST_BASE, FUTURE_BASE;

    @Override
    public String printName() {
        switch(this) {
            case DECLARATIVE:   return "declarative";
            case INQUISITIVE:   return "inquisitive";
            case IMPERATIVE:    return "imperative";
            case PROPOSITIVE:   return "propositive";
            case CON_IF:        return "connective if";
            case CON_AND:       return "connective and";
            case NOMINAL:       return "nominal";
            case PAST_BASE:     return "past base";
            case FUTURE_BASE:   return "future base";
            default: throw new IllegalArgumentException();
        }
    }

    public static Form fromString(String string) {
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