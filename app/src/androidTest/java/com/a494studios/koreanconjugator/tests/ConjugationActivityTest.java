package com.a494studios.koreanconjugator.tests;

import android.content.Intent;
import android.content.pm.ActivityInfo;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.idling.CountingIdlingResource;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import com.a494studios.koreanconjugator.MockReader;
import com.a494studios.koreanconjugator.R;
import com.a494studios.koreanconjugator.conjugations.ConjugationActivity;
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
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.a494studios.koreanconjugator.MockReader.readStringFromFile;
import static com.a494studios.koreanconjugator.RecyclerViewMatchers.withRecyclerView;
import static com.a494studios.koreanconjugator.Utils.assertBodyContains;
import static com.a494studios.koreanconjugator.Utils.nthChildOf;
import static com.a494studios.koreanconjugator.Utils.setChecked;
import static com.a494studios.koreanconjugator.Utils.testActionBar;
import static org.hamcrest.Matchers.allOf;

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
        serverRule.server.enqueue(new MockResponse().setBody(readStringFromFile(MockReader.CONJUGATIONS)));
        serverRule.server.enqueue(new MockResponse().setBody(readStringFromFile(MockReader.CONJUGATIONS_HONORIFIC)));

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
    public void test_displaysData() {
        // Connective If
        onView(allOf(isDescendantOfA(nthChildOf(withId(R.id.conj_list),0)),
                isDescendantOfA(nthChildOf(withId(R.id.listCard_list), 0)),
                withId(R.id.conjFormal)))
                .check(matches(withText("connective if")));

        onView(allOf(isDescendantOfA(nthChildOf(withId(R.id.conj_list), 0)),
                isDescendantOfA(nthChildOf(withId(R.id.listCard_list), 0)),
                withId(R.id.conjText)))
                .check(matches(withText("가면")));

        // Declarative Future
        onView(allOf(isDescendantOfA(nthChildOf(withId(R.id.conj_list),1)),
                isDescendantOfA(nthChildOf(withId(R.id.listCard_list), 1)),
                withId(R.id.conjFormal)))
                .check(matches(withText("informal high")));

        onView(allOf(isDescendantOfA(nthChildOf(withId(R.id.conj_list),1)),
                isDescendantOfA(nthChildOf(withId(R.id.listCard_list), 1)),
                withId(R.id.conjText)))
                .check(matches(withText("갈 거예요")));

        // Declarative Past
        onView(allOf(isDescendantOfA(nthChildOf(withId(R.id.conj_list),2)),
                isDescendantOfA(nthChildOf(withId(R.id.listCard_list), 3)),
                withId(R.id.conjFormal)))
                .check(matches(withText("formal high")));

        onView(allOf(isDescendantOfA(nthChildOf(withId(R.id.conj_list),2)),
                isDescendantOfA(nthChildOf(withId(R.id.listCard_list), 3)),
                withId(R.id.conjText)))
                .check(matches(withText("갔습니다")));
    }

    @Test
    public void test_displayAfterRotation() {
        activityRule.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        checkConjugations(false);

        activityRule.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        checkConjugations(false);
    }

    @Test
    public void test_honorificSwitch() throws InterruptedException {
        assertBodyContains(serverRule.server.takeRequest(2, TimeUnit.SECONDS),
                "\"honorific\":false");
        checkConjugations(false);

        // Honorific
        onView(withId(R.id.conj_switch)).perform(setChecked(true));

        assertBodyContains(serverRule.server.takeRequest(2, TimeUnit.SECONDS),
                "\"honorific\":true");
        checkConjugations(true);

        // Back to regular, it's cached so we can't check the request
        onView(withId(R.id.conj_switch)).perform(setChecked(false));
        checkConjugations(false);
    }

    private void checkConjugations(boolean honorific) {
        onView(withId(R.id.conj_list)).perform(scrollToPosition(0));
        onView(withRecyclerView(R.id.conj_list).atPosition(0))
                .check(matches(hasDescendant(withText(honorific ? "가시면" : "가면"))));

        onView(withId(R.id.conj_list)).perform(scrollToPosition(3));
        onView(withRecyclerView(R.id.conj_list).atPosition(3))
                .check(matches(hasDescendant(withText(honorific ? "가세요" : "가요"))));

        onView(withId(R.id.conj_list)).perform(scrollToPosition(7));
        onView(withRecyclerView(R.id.conj_list).atPosition(7))
                .check(matches(hasDescendant(withText(honorific ? "가십니까" : "갑니까"))));
    }
}
