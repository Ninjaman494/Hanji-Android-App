package com.a494studios.koreanconjugator.tests;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.idling.CountingIdlingResource;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import com.a494studios.koreanconjugator.MockApplication;
import com.a494studios.koreanconjugator.MockReader;
import com.a494studios.koreanconjugator.R;
import com.a494studios.koreanconjugator.parsing.Server;
import com.a494studios.koreanconjugator.search_results.SearchResultsActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.a494studios.koreanconjugator.MockReader.readStringFromFile;
import static com.a494studios.koreanconjugator.RecyclerViewMatchers.withRecyclerView;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.not;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class SearchResultsActivityTest {

    private static final CountingIdlingResource idler = Server.getIdler();
    private MockWebServer server;

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

    @Before
    public void setup() throws IOException {
        // Register idling resource
        IdlingRegistry.getInstance().register(idler);

        // Stub intents
        Intents.init();
        Intent resultData = new Intent();
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, resultData);
        intending(not(hasComponent(SearchResultsActivity.class.getName()))).respondWith(result);

        // Setup mock server
        server = new MockWebServer();
        server.start();
        MockApplication testApp = ApplicationProvider.getApplicationContext();
        testApp.setServerUrl(server.url("/").toString());

        // Enqueue responses
        server.enqueue(new MockResponse().setBody(readStringFromFile(MockReader.SEARCH_RESULTS)));

        // Set ad free to true by default
        testApp.setAdFree(true);
    }

    @After
    public void teardown() throws IOException {
        Intents.release();
        IdlingRegistry.getInstance().unregister(idler);
        server.shutdown();
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
    public void list_loadsMoreOnScroll() {

    }

    @Test
    public void searchResult_redirectsToDisplay() {

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
