package com.a494studios.koreanconjugator.suggestions;

import android.content.Intent;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.idling.CountingIdlingResource;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.a494studios.koreanconjugator.rules.MockServerRule;
import com.a494studios.koreanconjugator.R;
import com.a494studios.koreanconjugator.parsing.Server;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.a494studios.koreanconjugator.Utils.assertBodyContains;

@RunWith(AndroidJUnit4.class)
public class SuggestionActivityTest {

    private final CountingIdlingResource idler = Server.getIdler();

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

    @Before
    public void init() {
        // Register idling resource
        IdlingRegistry.getInstance().register(idler);
    }

    @Test
    public void form_canSubmitPartial() throws InterruptedException {
        onView(withId(R.id.suggestion_antonym)).perform(typeText("antonym"));

        onView(withText("Submit")).perform(click());

        assertBodyContains(serverRule.server.takeRequest(500, TimeUnit.MILLISECONDS),
                "\"antonyms\":[\"antonym\"]");
    }
}
