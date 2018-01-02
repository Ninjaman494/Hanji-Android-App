package com.a494studios.koreanconjugator.parsing;

import java.util.ArrayList;

/**
 * Created by akash on 1/2/2018.
 */

public interface Category {
    String printName();

    class Categories{
        public static ArrayList<Conjugation> getSubSet(ArrayList<Conjugation> conjugations, Category... categories){
            ArrayList<Conjugation> subset = new ArrayList<>();
            for(Conjugation c: conjugations){
                if(c.matchesCategories(categories)){
                    subset.add(c);
                }
            }
            return subset;
        }
    }
}
