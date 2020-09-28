package com.a494studios.koreanconjugator.rules;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;

import androidx.test.espresso.intent.Intents;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static org.hamcrest.Matchers.not;

public class StubIntentsRule implements TestRule {

    private String className;

    public StubIntentsRule(String className) {
        this.className = className;
    }

    @Override
    public Statement apply(Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                Intents.init();
                Instrumentation.ActivityResult result =
                        new Instrumentation.ActivityResult(Activity.RESULT_OK, new Intent());
                intending(not(hasComponent(className))).respondWith(result);

                base.evaluate();

                Intents.release();
            }
        };
    }
}
