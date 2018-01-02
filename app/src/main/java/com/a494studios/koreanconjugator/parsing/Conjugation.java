package com.a494studios.koreanconjugator.parsing;

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
        this.formality = generateFormality(type);
        this.form = generateForm(type);
        this.tense = generateTense(type);
    }

    private Formality generateFormality(String type) {
        if (type.contains(Formality.INFORMAL_LOW.toString())) {
            return Formality.INFORMAL_LOW;
        } else if (type.contains(Formality.INFORMAL_HIGH.toString())) {
            return Formality.INFORMAL_HIGH;
        } else if (type.contains(Formality.FORMAL_LOW.toString())) {
            return Formality.FORMAL_LOW;
        } else if (type.contains(Formality.FORMAL_HIGH.toString())) {
            return Formality.FORMAL_HIGH;
        } else {
            return Formality.NONE;
        }
    }

    private Tense generateTense(String type){
        if(type.contains(Tense.FUT_COND.toString())){
            return Tense.FUT_COND;
        }else if(type.contains(Tense.FUTURE.toString())){
            return Tense.FUTURE;
        }else if(type.contains(Tense.PRESENT.toString())){
            return Tense.PRESENT;
        }else if(type.contains(Tense.PAST.toString())){
            return Tense.PAST;
        }else{
            return Tense.NONE;
        }
    }

    private Form generateForm(String type) {
        if (type.contains(Form.DECLARATIVE.toString())) {
            return Form.DECLARATIVE;
        } else if (type.contains(Form.INQUISITIVE.toString())) {
            return Form.INQUISITIVE;
        } else if (type.contains(Form.IMPERATIVE.toString())) {
            return Form.IMPERATIVE;
        } else if (type.contains(Form.PROPOSITIVE.toString())) {
            return Form.PROPOSITIVE;
        } else if (type.contains(Form.CON_IF.toString())) {
            return Form.CON_IF;
        } else if (type.contains(Form.CON_AND.toString())) {
            return Form.CON_AND;
        } else if (type.contains(Form.NOMINAL.toString())) {
            return Form.NOMINAL;
        } else if (type.contains(Form.PAST_BASE.toString())) {
            return Form.FUTURE_BASE;
        } else {
            return null;
        }
    }

    public boolean matchesCategories(Category... categories){
        for(Category c: categories){
            if(formality != c && form != c && tense != c ){
                return false;
            }
        }
        return true;
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
