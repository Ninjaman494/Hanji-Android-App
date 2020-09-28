package com.a494studios.koreanconjugator.tests;

import android.content.Intent;
import android.content.pm.ActivityInfo;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.idling.CountingIdlingResource;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import com.a494studios.koreanconjugator.ConjugationQuery;
import com.a494studios.koreanconjugator.MockedResponses;
import com.a494studios.koreanconjugator.R;
import com.a494studios.koreanconjugator.conjugations.ConjugationActivity;
import com.a494studios.koreanconjugator.conjugations.ConjugationCardsAdapter;
import com.a494studios.koreanconjugator.parsing.Server;
import com.a494studios.koreanconjugator.rules.MockServerRule;
import com.a494studios.koreanconjugator.rules.StubIntentsRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.mockwebserver.MockResponse;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static com.a494studios.koreanconjugator.Utils.assertBodyContains;
import static com.a494studios.koreanconjugator.Utils.setChecked;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ConjugationActivityTest {
    private final static String STEM = "stem";
    private final static boolean HONORIFIC = false;
    private final static boolean ISADJ = false;

    private static final CountingIdlingResource idler = Server.getIdler();

    @Rule
    @SuppressWarnings("deprecation")
    public ActivityTestRule<ConjugationActivity> activityRule =
            new ActivityTestRule<ConjugationActivity>(ConjugationActivity.class, true, false) {
                @Override
                protected Intent getActivityIntent() {
                    Intent intent = new Intent();
                    intent.putExtra(ConjugationActivity.EXTRA_STEM, STEM);
                    intent.putExtra(ConjugationActivity.EXTRA_HONORIFIC, HONORIFIC);
                    intent.putExtra(ConjugationActivity.EXTRA_ISADJ, ISADJ);
                    return intent;
                }
            };

    @Rule
    public MockServerRule serverRule = new MockServerRule(ApplicationProvider.getApplicationContext());

    @Rule
    public StubIntentsRule intentsRule = new StubIntentsRule(ConjugationActivity.class.getName());

    @Before
    public void setup() {
        // Register idling resource
        IdlingRegistry.getInstance().register(idler);

        // Enqueue responses
        serverRule.server.enqueue(new MockResponse().setBody(MockedResponses.CONJUGATIONS));
        serverRule.server.enqueue(new MockResponse().setBody(MockedResponses.CONJUGATIONS_HONORIFIC));

        // Start test
        activityRule.launchActivity(null);
    }

    @After
    public void tearDown() {
        IdlingRegistry.getInstance().unregister(idler);
    }

    @Test
    public void test_displaysData() {
        RecyclerView recyclerView = activityRule.getActivity().findViewById(R.id.conj_list);
        int numItem = recyclerView.getAdapter().getItemCount();
        assertTrue(numItem > 0);
    }

    @Test
    public void test_displayAfterRotation() {
        RecyclerView recyclerView = activityRule.getActivity().findViewById(R.id.conj_list);
        int numItem = recyclerView.getAdapter().getItemCount();
        assertTrue(numItem > 0);

        activityRule.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        assertEquals(numItem, recyclerView.getAdapter().getItemCount());

        activityRule.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        assertEquals(numItem, recyclerView.getAdapter().getItemCount());
    }

    @Test
    public void test_honorificSwitch() throws InterruptedException {
        assertBodyContains(serverRule.server.takeRequest(2, TimeUnit.SECONDS),
                "\"honorific\":false");

        RecyclerView recyclerView = activityRule.getActivity().findViewById(R.id.conj_list);
        int numItems = recyclerView.getAdapter().getItemCount();

        // Honorific
        onView(withId(R.id.conj_switch)).perform(setChecked(true));

        Thread.sleep(300);

        ConjugationCardsAdapter adapter = (ConjugationCardsAdapter)recyclerView.getAdapter();
        assertEquals(numItems, adapter.getItemCount());
        List<ConjugationQuery.Conjugation> conjugations = adapter.getItem(0);
        assertBodyContains(serverRule.server.takeRequest(2, TimeUnit.SECONDS),
                "\"honorific\":true");
        assertTrue(conjugations.get(0).honorific());

        // Back to regular, it's cached so we can't check the request
        onView(withId(R.id.conj_switch)).perform(setChecked(false));

        Thread.sleep(300);

        adapter = (ConjugationCardsAdapter) recyclerView.getAdapter();
        assertEquals(numItems, adapter.getItemCount());
        conjugations = adapter.getItem(0);
        assertFalse(conjugations.get(0).honorific());
    }
}
