package com.a494studios.koreanconjugator;

import android.app.Activity;
import android.app.Instrumentation;
import android.app.SearchManager;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.a494studios.koreanconjugator.display.DisplayActivity;
import com.a494studios.koreanconjugator.search.SearchActivity;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.pressImeActionButton;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtraWithKey;
import static androidx.test.espresso.intent.matcher.IntentMatchers.isInternal;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.a494studios.koreanconjugator.Utils.testActionBar;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public ActivityScenarioRule<MainActivity> mActivityTestRule = new ActivityScenarioRule<>(MainActivity.class);

    @Before
    public void stubIntents() {
        Intents.init();

        Intent resultData = new Intent();
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, resultData);
        intending(isInternal()).respondWith(result);
    }

    @After
    public void releaseIntents() {
        Intents.release();
    }

    @Test
    public void overflowOptions() {
        testActionBar(false);
    }

    @Test
    public void wodCard_checkTitle() {
        ViewInteraction textView = onView(
                allOf(withId(R.id.displayCard_heading), withText("Word of the Day"),
                        childAtPosition(
                                allOf(withId(R.id.displayCard_relativeLayout),
                                        childAtPosition(
                                                IsInstanceOf.instanceOf(android.widget.FrameLayout.class),
                                                0)),
                                0),
                        isDisplayed()));
        textView.check(matches(withText("Word of the Day")));
    }

    @Test
    public void wodCard_redirectsToDisplay() {
        ViewInteraction wodButton = onView(
                allOf(withId(R.id.displayCard_button), withText("See Entry"),
                        childAtPosition(
                                allOf(withId(R.id.displayCard_relativeLayout),
                                        childAtPosition(
                                                withClassName(is("androidx.cardview.widget.CardView")),
                                                0)),
                                3),
                        isDisplayed()));
        wodButton.perform(click());

        intended(allOf(hasComponent(DisplayActivity.class.getName()),
                hasExtraWithKey(DisplayActivity.EXTRA_ID)));
    }

    @Test
    public void search_redirectsToSearch() {
        ViewInteraction searchAutoComplete = onView(
                allOf(withId(R.id.search_src_text),
                        childAtPosition(
                                allOf(withId(R.id.search_plate),
                                        childAtPosition(
                                                withId(R.id.search_edit_frame),
                                                1)),
                                0),
                        isDisplayed()));

        searchAutoComplete.check(matches(withHint("Search in Korean or English…")));

        searchAutoComplete.perform(replaceText("text"), pressImeActionButton());

        intended(allOf(hasComponent(SearchActivity.class.getName()),
                    hasAction(Intent.ACTION_SEARCH),
                    hasExtraWithKey(SearchManager.QUERY)));
    }

    static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
