package com.a494studios.koreanconjugator.tests;

import android.app.SearchManager;
import android.content.Intent;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.idling.CountingIdlingResource;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import com.a494studios.koreanconjugator.MainActivity;
import com.a494studios.koreanconjugator.MockReader;
import com.a494studios.koreanconjugator.R;
import com.a494studios.koreanconjugator.display.DisplayActivity;
import com.a494studios.koreanconjugator.parsing.Server;
import com.a494studios.koreanconjugator.rules.MockServerRule;
import com.a494studios.koreanconjugator.rules.StubIntentsRule;
import com.a494studios.koreanconjugator.search.SearchActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import okhttp3.mockwebserver.MockResponse;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.pressImeActionButton;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.a494studios.koreanconjugator.MockReader.readStringFromFile;
import static com.a494studios.koreanconjugator.Utils.testActionBar;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    private static final CountingIdlingResource idler = Server.getIdler();

    @Rule
    @SuppressWarnings("deprecation")
    public ActivityTestRule<MainActivity> activityRule = new ActivityTestRule<>(MainActivity.class, true, false);

    @Rule
    public MockServerRule serverRule = new MockServerRule(ApplicationProvider.getApplicationContext());

    @Rule
    public StubIntentsRule intentsRule = new StubIntentsRule(MainActivity.class.getName());

    @Before
    public void stubIntents() {
        // Register idling resource
        IdlingRegistry.getInstance().register(idler);

        // Enqueue a response
        serverRule.server.enqueue(new MockResponse().setBody(readStringFromFile(MockReader.WOD)));

        // Start test
        activityRule.launchActivity(null);
    }

    @After
    public void releaseIntents() {
        IdlingRegistry.getInstance().unregister(idler);
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
                hasExtra(DisplayActivity.EXTRA_ID, "속속들이0")));
    }

    @Test
    public void search_redirectsToSearch() {
        onView(ViewMatchers.withId(R.id.search_src_text))
                .check(matches(withHint("Search in Korean or English…")))
                .perform(replaceText("text"), pressImeActionButton());

        intended(allOf(hasComponent(SearchActivity.class.getName()),
                    hasAction(Intent.ACTION_SEARCH),
                    hasExtra(SearchManager.QUERY, "text")));
    }
}
