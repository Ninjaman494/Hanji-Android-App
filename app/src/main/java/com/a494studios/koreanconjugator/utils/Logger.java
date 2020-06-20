package com.a494studios.koreanconjugator.utils;

import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

public class Logger {

    private static final String EVENT_SELECT_CONJ = "select_conjugation";
    private static final String EVENT_VIEW_UPGRADE = "view_upgrade";

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
}
