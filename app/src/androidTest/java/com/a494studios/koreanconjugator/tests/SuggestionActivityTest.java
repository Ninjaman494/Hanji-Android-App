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
import com.a494studios.koreanconjugator.parsing.Server;
import com.a494studios.koreanconjugator.rules.MockServerRule;
import com.a494studios.koreanconjugator.rules.StubIntentsRule;
import com.a494studios.koreanconjugator.suggestions.SuggestionActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.a494studios.koreanconjugator.MockReader.readStringFromFile;
import static com.a494studios.koreanconjugator.Utils.assertBodyContains;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class SuggestionActivityTest {

    private final CountingIdlingResource idler = Server.getIdler();

    private final String korean = "\uc548\ub155\ud558\uc138\uc694";

    @Rule
    @SuppressWarnings("deprecation")
    public ActivityTestRule<SuggestionActivity> activityRule = new ActivityTestRule<SuggestionActivity>(SuggestionActivity.class) {
        @Override
        protected Intent getActivityIntent() {
            Intent intent = new Intent();
            intent.putExtra(SuggestionActivity.EXTRA_ENTRY_ID, "1");
            return intent;
        }
    };

    @Rule
    public MockServerRule serverRule = new MockServerRule(ApplicationProvider.getApplicationContext());

    @Rule
    public StubIntentsRule intentsRule = new StubIntentsRule(SuggestionActivity.class.getName());

    @Before
    public void init() {
        // Register idling resource
        IdlingRegistry.getInstance().register(idler);
    }

    @Test
    public void form_canSubmitPartial() throws InterruptedException {
        serverRule.server.enqueue(new MockResponse().setBody(readStringFromFile(MockReader.CREATE_SUGGESTION)));

        onView(withId(R.id.suggestion_antonym)).perform(replaceText(korean));
        onView(withText("Submit")).perform(click());

        assertBodyContains(serverRule.server.takeRequest(500, TimeUnit.MILLISECONDS),
                "\"antonyms\":[\"안녕하세요\"]");

        onView(withText("OK")).perform(click());

        assertTrue(activityRule.getActivity().isFinishing());
    }

    @Test
    public void form_canSubmitFull() throws InterruptedException {
        serverRule.server.enqueue(new MockResponse().setBody(readStringFromFile(MockReader.CREATE_SUGGESTION)));

        onView(withId(R.id.suggestion_antonym)).perform(replaceText(korean));
        onView(withId(R.id.suggestion_synonym)).perform(replaceText(korean));
        onView(withId(R.id.suggestion_sentence)).perform(replaceText(korean));
        onView(withId(R.id.suggestion_translation)).perform(replaceText("translation"));
        onView(withText("Submit")).perform(click());

        RecordedRequest request = serverRule.server.takeRequest(500, TimeUnit.MILLISECONDS);
        assertBodyContains(request, "\"antonyms\":[\"안녕하세요\"]");
        assertBodyContains(request, "\"synonyms\":[\"안녕하세요\"]");
        assertBodyContains(request, "\"sentence\":\"안녕하세요\"");
        assertBodyContains(request, "\"translation\":\"translation\"");

        onView(withText("OK")).perform(click());

        assertTrue(activityRule.getActivity().isFinishing());
    }

    @Test
    public void activity_canHandleError() {
        serverRule.server.enqueue(new MockResponse().setBody(readStringFromFile(MockReader.CREATE_SUGGESTION_ERROR)));

        onView(withId(R.id.suggestion_antonym)).perform(replaceText(korean));
        onView(withText("Submit")).perform(click());

        onView(withText("Error: This is a mock error.Please try again later or contact support."))
                .check(matches(isDisplayed()));
    }
}
