package com.a494studios.koreanconjugator;

/**
 * Created by akash on 12/31/2017.
 */

public class Conjugation {
    private String infinitive;
    private String type;
    private String conjugated;
    private String pronunciation;
    private String romanization;

    public Conjugation(String infinitive, String type, String conjugated, String pronunciation, String romanization) {
        this.infinitive = infinitive;
        this.type = type;
        this.conjugated = conjugated;
        this.pronunciation = pronunciation;
        this.romanization = romanization;
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

    public void setInfinitive(String infinitive) {
        this.infinitive = infinitive;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setConjugated(String conjugated) {
        this.conjugated = conjugated;
    }

    public void setPronunciation(String pronunciation) {
        this.pronunciation = pronunciation;
    }

    public void setRomanization(String romanization) {
        this.romanization = romanization;
    }
}
