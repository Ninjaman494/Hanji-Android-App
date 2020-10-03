package com.a494studios.koreanconjugator.utils;

import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

public class Logger {

    private static final String EVENT_SELECT_CONJ = "select_conjugation";
    private static final String EVENT_VIEW_UPGRADE = "view_upgrade";
    private static final String EVENT_ADD_FAVORITE = "add_favorite";
    private static final String EVENT_SELECT_FAV = "select_fav";

    private FirebaseAnalytics mFirebaseAnalytics;
    private static Logger logger;


    private Logger(FirebaseAnalytics firebaseAnalytics) {
        this.mFirebaseAnalytics = firebaseAnalytics;
    }

    public static void initialize(FirebaseAnalytics firebaseAnalytics) {
        logger = new Logger(firebaseAnalytics);
    }

    public static Logger getInstance() {
        if(logger == null) {
            throw new RuntimeException("Logger not initialized");
        }
        return logger;
    }

    public void logSelectContent(String term, String pos) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, term);
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, pos);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

    public void logSelectConjugation(String term, String pos, String conjugation) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.TERM, term);
        bundle.putString("pos", pos);
        bundle.putString("conjugation", conjugation);
        mFirebaseAnalytics.logEvent(EVENT_SELECT_CONJ, bundle);
    }

    public void logViewUpgrade() {
        mFirebaseAnalytics.logEvent(EVENT_VIEW_UPGRADE, new Bundle());
    }

    public void logSearch(String term){
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.TERM, term);
        bundle.putString("language", detectLanguage(term));
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SEARCH, bundle);
    }

    public void logFavoriteAdded(String name, String conjugation, boolean honorific) {
        Bundle bundle = new Bundle();
        bundle.putString("favorite_name", name);
        bundle.putString("conjugation", conjugation);
        bundle.putBoolean("is_honorific", honorific);
        mFirebaseAnalytics.logEvent(EVENT_ADD_FAVORITE, bundle);
    }

    public void logSelectFavorite(String favName, String conjugation, String conjugated) {
        Bundle bundle = new Bundle();
        bundle.putString("favorite_name", favName);
        bundle.putString("conjugation", conjugation);
        bundle.putString("conjugated", conjugated);
        mFirebaseAnalytics.logEvent(EVENT_SELECT_FAV, bundle);
    }

    private String detectLanguage(String term){
        boolean isEnglish = true;
        for (char c : term.toCharArray()) {
            if (!(c >= 'a' && c <= 'z') && !(c >= 'A' && c <= 'Z')) {
                isEnglish = false;
                break;
            }
        }

        return isEnglish ? "English" : "Korean";
    }
}
