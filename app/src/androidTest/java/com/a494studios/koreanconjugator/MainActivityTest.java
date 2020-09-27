package com.a494studios.koreanconjugator;

import android.app.Activity;
import android.app.Instrumentation;
import android.app.SearchManager;
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
import com.a494studios.koreanconjugator.search.SearchActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.pressImeActionButton;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.a494studios.koreanconjugator.Utils.testActionBar;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    private static final CountingIdlingResource idler = Server.getIdler();

    @Rule
    @SuppressWarnings("deprecation")
    public ActivityTestRule<MainActivity> activityRule = new ActivityTestRule<>(MainActivity.class, true, false);
    private MockWebServer server;

    @Before
    public void stubIntents() throws IOException {
        // Register idling resource
        IdlingRegistry.getInstance().register(idler);

        // Stub intents
        Intents.init();
        Intent resultData = new Intent();
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, resultData);
        intending(not(hasComponent(MainActivity.class.getName()))).respondWith(result);

        // Setup mock server
        server = new MockWebServer();
        server.start();
        MockApplication testApp = ApplicationProvider.getApplicationContext();
        testApp.setServerUrl(server.url("/").toString());

        // Enqueue a response
        server.enqueue(new MockResponse().setBody(MockedResponses.WOD));

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

    @Test
    public void wodCard_redirectsToDisplay() {
        onView(withText("Word of the Day"))
                .check(matches(isDisplayed()));

        onView(withText("See Entry"))
                .check(matches(isDisplayed()))
                .perform(click());

        intended(allOf(hasComponent(DisplayActivity.class.getName()),
                hasExtra(DisplayActivity.EXTRA_ID, "가로0")));
    }

    @Test
    public void search_redirectsToSearch() {
        onView(withId(R.id.search_src_text))
                .check(matches(withHint("Search in Korean or English…")))
                .perform(replaceText("text"), pressImeActionButton());

        intended(allOf(hasComponent(SearchActivity.class.getName()),
                    hasAction(Intent.ACTION_SEARCH),
                    hasExtra(SearchManager.QUERY, "text")));
    }
}
