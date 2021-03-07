package com.a494studios.koreanconjugator.tests;

import android.app.SearchManager;
import android.content.Intent;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.idling.CountingIdlingResource;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import com.a494studios.koreanconjugator.MockReader;
import com.a494studios.koreanconjugator.display.DisplayActivity;
import com.a494studios.koreanconjugator.parsing.Server;
import com.a494studios.koreanconjugator.rules.MockServerRule;
import com.a494studios.koreanconjugator.rules.StubIntentsRule;
import com.a494studios.koreanconjugator.search.SearchActivity;
import com.a494studios.koreanconjugator.search_results.SearchResultsActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import okhttp3.mockwebserver.MockResponse;

import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static com.a494studios.koreanconjugator.MockReader.readStringFromFile;
import static org.hamcrest.Matchers.allOf;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class SearchActivityTest {

    private static final String QUERY = "foobar";

    private static final CountingIdlingResource idler = Server.getIdler();

    @Rule
    @SuppressWarnings("deprecation")
    public ActivityTestRule<SearchActivity> activityRule =
            new ActivityTestRule<SearchActivity>(SearchActivity.class, true, false) {
                @Override
                protected Intent getActivityIntent() {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_SEARCH);
                    intent.putExtra(SearchManager.QUERY, QUERY);
                    return intent;
                }
            };

    @Rule
    public MockServerRule serverRule = new MockServerRule(ApplicationProvider.getApplicationContext());

    @Rule
    public StubIntentsRule intentsRule = new StubIntentsRule(SearchActivity.class.getName());

    @Before
    public void setup() {
        IdlingRegistry.getInstance().register(idler);
    }

    @After
    public void tearDown() {
        IdlingRegistry.getInstance().unregister(idler);
    }

    @Test
    public void oneResult_redirectsToDisplay() {
        serverRule.server.enqueue(new MockResponse().setBody(readStringFromFile(MockReader.SEARCH_RESULTS_ONE)));

        activityRule.launchActivity(null);

        intended(allOf(hasComponent(DisplayActivity.class.getName()),
                hasExtra(DisplayActivity.EXTRA_ID, "먹다0")));
    }

    @Test
    public void multiResult_redirectsToSearchResults() {
        serverRule.server.enqueue(new MockResponse().setBody(readStringFromFile(MockReader.SEARCH_RESULTS)));

        activityRule.launchActivity(null);

        intended(allOf(hasComponent(SearchResultsActivity.class.getName()),
                hasExtra(SearchResultsActivity.EXTRA_QUERY, QUERY)));
    }
}
