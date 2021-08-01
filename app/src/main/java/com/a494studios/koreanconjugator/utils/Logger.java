package com.a494studios.koreanconjugator.utils;

import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.text.MessageFormat;

public class Logger {

    private static final String EVENT_SELECT_CONJ = "select_conjugation";
    private static final String EVENT_VIEW_UPGRADE = "view_upgrade";
    private static final String EVENT_ADD_FAVORITE = "add_favorite";
    private static final String EVENT_SELECT_FAV = "select_fav";

    private FirebaseAnalytics mFirebaseAnalytics;
    private FirebaseCrashlytics mFirebaseCrashlytics;
    private static Logger logger;


    private Logger(FirebaseAnalytics firebaseAnalytics) {
        this.mFirebaseAnalytics = firebaseAnalytics;
        this.mFirebaseCrashlytics = FirebaseCrashlytics.getInstance();
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
        mFirebaseCrashlytics.log("Entry selected: " + term);
    }

    public void logSelectConjugation(String term, String pos, String conjugation) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.TERM, term);
        bundle.putString("pos", pos);
        bundle.putString("conjugation", conjugation);
        mFirebaseAnalytics.logEvent(EVENT_SELECT_CONJ, bundle);

        Object[] params = new Object[]{term, pos, conjugation};
        String message = MessageFormat.format("Conjugation selected - term: {0}, pos: {1}, conjugation: {2}", params);
        mFirebaseCrashlytics.log(message);
    }

    public void logViewUpgrade() {
        mFirebaseAnalytics.logEvent(EVENT_VIEW_UPGRADE, new Bundle());
        mFirebaseCrashlytics.log("Viewed ad free upgrade");
    }

    public void logSearch(String term){
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.TERM, term);
        bundle.putString("language", detectLanguage(term));
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SEARCH, bundle);
        mFirebaseCrashlytics.log("Did search: " + term);
    }

    public void logFavoriteAdded(String name, String conjugation, boolean honorific) {
        Bundle bundle = new Bundle();
        bundle.putString("favorite_name", name);
        bundle.putString("conjugation", conjugation);
        bundle.putBoolean("is_honorific", honorific);
        mFirebaseAnalytics.logEvent(EVENT_ADD_FAVORITE, bundle);

        Object[] params = new Object[]{name, conjugation, honorific};
        String message = MessageFormat.format("Favorite added - name: {0}, conjugation: {1}, honorific: {2}", params);
        mFirebaseCrashlytics.log(message);
    }

    public void logSelectFavorite(String favName, String conjugation, String conjugated) {
        Bundle bundle = new Bundle();
        bundle.putString("favorite_name", favName);
        bundle.putString("conjugation", conjugation);
        bundle.putString("conjugated", conjugated);
        mFirebaseAnalytics.logEvent(EVENT_SELECT_FAV, bundle);

        Object[] params = new Object[]{favName, conjugation, conjugated};
        String message = MessageFormat.format("Favorite selected - name: {0}, conjugation: {1}, conjugated: {2}", params);
        mFirebaseCrashlytics.log(message);
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
