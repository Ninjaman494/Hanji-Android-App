package com.a494studios.koreanconjugator.tests;

import android.content.Intent;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.idling.CountingIdlingResource;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import com.a494studios.koreanconjugator.MockApplication;
import com.a494studios.koreanconjugator.MockReader;
import com.a494studios.koreanconjugator.R;
import com.a494studios.koreanconjugator.display.DisplayActivity;
import com.a494studios.koreanconjugator.parsing.Server;
import com.a494studios.koreanconjugator.rules.MockServerRule;
import com.a494studios.koreanconjugator.rules.StubIntentsRule;
import com.a494studios.koreanconjugator.search_results.SearchResultsActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

import okhttp3.mockwebserver.MockResponse;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.swipeUp;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.a494studios.koreanconjugator.MockReader.readStringFromFile;
import static com.a494studios.koreanconjugator.RecyclerViewMatchers.withRecyclerView;
import static com.a494studios.koreanconjugator.Utils.assertBodyContains;
import static com.a494studios.koreanconjugator.Utils.testActionBar;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.endsWith;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class SearchResultsActivityTest {

    private static final CountingIdlingResource idler = Server.getIdler();

    @Rule
    @SuppressWarnings("deprecation")
    public ActivityTestRule<SearchResultsActivity> activityRule =
            new ActivityTestRule<SearchResultsActivity>(SearchResultsActivity.class, true, false) {
                @Override
                protected Intent getActivityIntent() {
                    Intent intent = new Intent();
                    intent.putExtra(SearchResultsActivity.EXTRA_QUERY, "query");
                    return intent;
                }
            };

    @Rule
    public MockServerRule serverRule = new MockServerRule(ApplicationProvider.getApplicationContext());

    @Rule
    public StubIntentsRule intentsRule = new StubIntentsRule(SearchResultsActivity.class.getName());

    @Before
    public void setup() {
        // Register idling resource
        IdlingRegistry.getInstance().register(idler);

        // Enqueue responses
        serverRule.server.enqueue(new MockResponse().setBody(readStringFromFile(MockReader.SEARCH_RESULTS)));
        serverRule.server.enqueue(new MockResponse().setBody(readStringFromFile(MockReader.SEARCH_RESULTS)));

        // Set ad free to true by default
        MockApplication testApp = ApplicationProvider.getApplicationContext();
        testApp.setAdFree(true);
    }

    @Test
    public void overflowOptions() {
        activityRule.launchActivity(null);
        testActionBar();
    }

    @After
    public void teardown() {
        IdlingRegistry.getInstance().unregister(idler);
    }

    @Test
    public void list_displaysResults() {
        // Start activity
        activityRule.launchActivity(null);

        // Check first search result
        onView(withRecyclerView(R.id.search_listView).atPosition(0))
                .check(matches(hasDescendant(withText("먹다"))));
        onView(withRecyclerView(R.id.search_listView).atPosition(0))
                .check(matches(hasDescendant(withText("to eat, to have"))));
        onView(withRecyclerView(R.id.search_listView).atPosition(0))
                .check(matches(hasDescendant(withText("to have a drink"))));
        onView(withRecyclerView(R.id.search_listView).atPosition(0))
                .check(matches(hasDescendant(withText("+4 More"))));

        // Check fourth search result
        onView(withId(R.id.search_listView)).perform(scrollToPosition(3));
        onView(withRecyclerView(R.id.search_listView).atPosition(3))
                .check(matches(hasDescendant(withText("식사하다"))));
        onView(withRecyclerView(R.id.search_listView).atPosition(3))
                .check(matches(hasDescendant(withText("to eat, to have a meal"))));
    }

    @Test
    public void list_loadsMoreOnScroll() throws InterruptedException {
        activityRule.launchActivity(null);

        // First request
        assertBodyContains(serverRule.server.takeRequest(2, TimeUnit.SECONDS),
                "\"cursor\":0");

        onView(withId(R.id.search_listView)).perform(swipeUp());
        onView(withId(R.id.search_listView)).perform(swipeUp());

        // Second request should use cursor from first request
        assertBodyContains(serverRule.server.takeRequest(2, TimeUnit.SECONDS),
                "\"cursor\":20");
    }

    @Test
    public void searchResult_redirectsToDisplay() {
        activityRule.launchActivity(null);

        onView(allOf(isDescendantOfA(withRecyclerView(R.id.search_listView).atPosition(0)), withText("SEE ENTRY")))
                .perform(click());

        intended(allOf(hasComponent(DisplayActivity.class.getName()),
                hasExtra(DisplayActivity.EXTRA_ID, "먹다0")));
    }

    @Test
    public void ad_isShownCorrectly() {
        // Set ad-free to false
        MockApplication testApp = ApplicationProvider.getApplicationContext();
        testApp.setAdFree(false);

        // Start test
        activityRule.launchActivity(null);

        // Verify third item in list is an ad
        onView(withId(R.id.search_listView)).perform(scrollToPosition(2));
        onView(withRecyclerView(R.id.search_listView).atPosition(2))
                .check(matches(hasDescendant(allOf(
                        withClassName(endsWith("AdView")),
                        withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))));
    }
}
