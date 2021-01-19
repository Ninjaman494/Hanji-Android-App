package com.a494studios.koreanconjugator;

import androidx.test.core.app.ApplicationProvider;

import com.a494studios.koreanconjugator.utils.Logger;
import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.robolectric.TestLifecycleApplication;

import java.lang.reflect.Method;

public class TestCustomApplication extends CustomApplication implements TestLifecycleApplication {

    @Override
    public void onCreate() {
        FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext());
        Logger.initialize(FirebaseAnalytics.getInstance(this));
    }


    @Override
    public void beforeTest(Method method) {

    }

    @Override
    public void prepareTest(Object test) {

    }

    @Override
    public void afterTest(Method method) {

    }

}
