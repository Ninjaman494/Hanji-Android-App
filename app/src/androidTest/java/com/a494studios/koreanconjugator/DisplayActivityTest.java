package com.a494studios.koreanconjugator;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.idling.CountingIdlingResource;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import com.a494studios.koreanconjugator.display.DisplayActivity;
import com.a494studios.koreanconjugator.parsing.Server;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static com.a494studios.koreanconjugator.Utils.testActionBar;
import static org.hamcrest.Matchers.not;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class DisplayActivityTest {

    private static final CountingIdlingResource idler = Server.getIdler();
    private MockWebServer server;

    @Rule
    @SuppressWarnings("deprecation")
    public ActivityTestRule<DisplayActivity> activityRule =
            new ActivityTestRule<DisplayActivity>(DisplayActivity.class, true, false) {
                @Override
                protected Intent getActivityIntent() {
                    Intent intent = new Intent();
                    intent.putExtra(DisplayActivity.EXTRA_ID, "id");
                    return intent;
                }
            };

    @Before
    public void stubIntents() throws IOException {
        // Register idling resource
        IdlingRegistry.getInstance().register(idler);

        // Stub intents
        Intents.init();
        Intent resultData = new Intent();
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, resultData);
        intending(not(hasComponent(DisplayActivity.class.getName()))).respondWith(result);

        // Setup mock server
        server = new MockWebServer();
        server.start();
        MockApplication testApp = ApplicationProvider.getApplicationContext();
        testApp.setServerUrl(server.url("/").toString());

        // Enqueue responses
        server.enqueue(new MockResponse().setBody(MockedResponses.ENTRY));
        server.enqueue(new MockResponse().setBody(MockedResponses.CONJUGATIONS));

        // Start test
        activityRule.launchActivity(null);
    }

    @After
    public void releaseIntents() throws IOException {
        Intents.release();
        IdlingRegistry.getInstance().unregister(idler);
        server.shutdown();
    }

    @Test
    public void overflowOptions() {
        testActionBar();
    }
}
