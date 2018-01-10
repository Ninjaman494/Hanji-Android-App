package com.a494studios.koreanconjugator.parsing;

import com.a494studios.koreanconjugator.Utils;

import java.io.Serializable;

/**
 * Created by akash on 12/31/2017.
 */

public class Conjugation implements Serializable {
    private String infinitive;
    private String type;
    private String conjugated;
    private String pronunciation;
    private String romanization;
    private Formality formality;
    private Form form;
    private Tense tense;

    Conjugation(String infinitive, String type, String conjugated, String pronunciation, String romanization) {
        this.infinitive = infinitive;
        this.type = type;
        this.conjugated = conjugated;
        this.pronunciation = pronunciation;
        this.romanization = romanization;
        this.formality = Utils.generateFormality(type);
        this.form = Utils.generateForm(type);
        this.tense = Utils.generateTense(type);
    }

    public boolean inCategories(Category... categories){
        for(Category c: categories){
            if(formality == c || form == c || tense == c ){
                return true;
            }
        }
        return false;
    }

    public String getInfinitive() {
        return infinitive;
    }

    public String getType() {
        return type;
    }

    public String getConjugated() {
        return conjugated;
    }

    public String getPronunciation() {
        return pronunciation;
    }

    public String getRomanization() {
        return romanization;
    }

    public Formality getFormality() {
        return formality;
    }

    public Form getForm() {
        return form;
    }

    public Tense getTense(){
        return tense;
    }
}
