package com.a494studios.koreanconjugator;

import android.app.Application;
import android.content.Context;

import androidx.test.runner.AndroidJUnitRunner;

// Is used in build.gradle
@SuppressWarnings("unused")
public class HanjiTestRunner extends AndroidJUnitRunner {

    @Override
    public Application newApplication(ClassLoader cl, String className, Context context) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        return super.newApplication(cl, TestCustomApplication.class.getName(), context);
    }
}
