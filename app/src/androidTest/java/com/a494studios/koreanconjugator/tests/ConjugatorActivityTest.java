package com.a494studios.koreanconjugator.tests;

import android.content.Intent;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.idling.CountingIdlingResource;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import com.a494studios.koreanconjugator.MockReader;
import com.a494studios.koreanconjugator.R;
import com.a494studios.koreanconjugator.conjugator.ConjugatorActivity;
import com.a494studios.koreanconjugator.parsing.Server;
import com.a494studios.koreanconjugator.rules.MockServerRule;
import com.a494studios.koreanconjugator.rules.StubIntentsRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

import okhttp3.mockwebserver.MockResponse;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.a494studios.koreanconjugator.MockReader.readStringFromFile;
import static com.a494studios.koreanconjugator.Utils.assertBodyContains;
import static com.a494studios.koreanconjugator.Utils.testActionBar;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ConjugatorActivityTest {
    private static final String TERM = "term";

    private static final CountingIdlingResource idler = Server.getIdler();

    @Rule
    @SuppressWarnings("deprecation")
    public ActivityTestRule<ConjugatorActivity> activityRule =
            new ActivityTestRule<ConjugatorActivity>(ConjugatorActivity.class, true, false) {
                @Override
                protected Intent getActivityIntent() {
                    Intent intent = new Intent();
                    intent.putExtra(ConjugatorActivity.EXTRA_TERM, TERM);
                    return intent;
                }
            };

    @Rule
    public MockServerRule serverRule = new MockServerRule(ApplicationProvider.getApplicationContext());

    @Rule
    public StubIntentsRule intentsRule = new StubIntentsRule(ConjugatorActivity.class.getName());

    @Before
    public void setup() {
        // Register idling resource
        IdlingRegistry.getInstance().register(idler);

        // Enqueue responses
        serverRule.server.enqueue(new MockResponse().setBody(readStringFromFile(MockReader.STEMS)));
        serverRule.server.enqueue(new MockResponse().setBody(readStringFromFile(MockReader.CONJUGATIONS)));

        // Start test
        activityRule.launchActivity(null);
    }

    @After
    public void tearDown() {
        IdlingRegistry.getInstance().unregister(idler);
    }

    @Test
    public void overflowOptions() {
        testActionBar();
    }

    @Test
    public void stems_areDisplayed() {
        onView(withId(R.id.conjugator_stemSpinner)).check(matches(withSpinnerText("노랄다")));

        onView(withId(R.id.conjugator_stemSpinner)).perform(click());

        onView(withText("노랗다")).check(matches(isDisplayed()));
        onView(withText("노라다")).check(matches(isDisplayed()));
    }

    @Test
    public void changingParams_causesNewRequest() throws InterruptedException {
        // Handle first initial requests first
        serverRule.server.takeRequest(2, TimeUnit.SECONDS);
        serverRule.server.takeRequest(2, TimeUnit.SECONDS);

        serverRule.server.enqueue(new MockResponse().setBody(readStringFromFile(MockReader.CONJUGATIONS)));
        serverRule.server.enqueue(new MockResponse().setBody(readStringFromFile(MockReader.CONJUGATIONS)));
        serverRule.server.enqueue(new MockResponse().setBody(readStringFromFile(MockReader.CONJUGATIONS)));

        // Change stem
        onView(withId(R.id.conjugator_stemSpinner)).perform(click());
        onView(withText("노랗다")).perform(click());
        assertBodyContains(serverRule.server.takeRequest(2, TimeUnit.SECONDS),
                "\"stem\":\"노랗다\"");

        // Change POS
        onView(withId(R.id.conjugator_posSpinner)).perform(click());
        onView(withText("Adjective")).perform(click());
        assertBodyContains(serverRule.server.takeRequest(2, TimeUnit.SECONDS),
                "\"isAdj\":true");

        // Change regularity
        onView(withId(R.id.conjugator_regSpinner)).perform(click());
        onView(withText("Irregular verb/adjective")).perform(click());
        assertBodyContains(serverRule.server.takeRequest(2, TimeUnit.SECONDS),
                "\"regular\":false");
    }
}
