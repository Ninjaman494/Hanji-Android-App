package com.a494studios.koreanconjugator.parsing;

import java.util.ArrayList;

/**
 * Created by akash on 1/2/2018.
 */

public interface Category {
    String printName();
    String getType();

    class Categories{
        public static ArrayList<Conjugation> getSubSet(ArrayList<Conjugation> conjugations, Category... categories){
            ArrayList<Conjugation> subset = new ArrayList<>();
            for(Conjugation c: conjugations){
                if(c.inCategories(categories)){
                    subset.add(c);
                }
            }
            return subset;
        }

        public static ArrayList<Conjugation> getSubSet(ArrayList<Conjugation> conjugations,
                                                       Formality formality, Form form, Tense tense ){
            ArrayList<Conjugation> subset = new ArrayList<>();
            for(Conjugation c: conjugations){
                if((formality == null || c.getFormality() == formality) &&
                        (c.getForm() == form) &&
                        (tense == null || c.getTense() == tense)){
                    subset.add(c);
                }
            }
            return subset;
        }

        public static Category valueOf(String string){
            Tense t = Tense.fromString(string);
            if(t != null){
                return t;
            }
            Form f = Form.fromString(string);
            if(f != null){
                return f;
            }
            Formality fr = Formality.fromString(string);
            if(fr != null){
                return fr;
            }
            return null;
        }

    }
}
