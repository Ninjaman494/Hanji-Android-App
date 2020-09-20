package com.a494studios.koreanconjugator.parsing;

public class Favorite {
    private String name;
    private String conjugationName;
    private boolean honorific;

    Favorite() {
        // require no-args constructor for gson
    }

    public Favorite(String name, String conjugationName, boolean honorific) {
        this.name = name;
        this.conjugationName = conjugationName;
        this.honorific = honorific;
    }

    public String getName() {
        return name;
    }

    public String getConjugationName() {
        return conjugationName;
    }

    public boolean isHonorific() {
        return honorific;
    }
}
