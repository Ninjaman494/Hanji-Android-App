package com.a494studios.koreanconjugator;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.filters.LargeTest;
import androidx.test.runner.AndroidJUnit4;

import com.a494studios.koreanconjugator.display.DisplayActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtraWithKey;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.a494studios.koreanconjugator.Utils.withHint;
import static org.hamcrest.Matchers.allOf;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityTest {

    @Rule
    public IntentsTestRule<MainActivity> intentsTestRule = new IntentsTestRule<>(MainActivity.class);

    @Test
    public void test_view() {
        ActivityScenario.launch(MainActivity.class);

        onView(withId(R.id.searchCard_search))
                .check(matches(isDisplayed()))
                .check(matches(withHint("Search in Korean or English…")));

        onView(withId(R.id.main_wodCard))
                .check(matches(isDisplayed()));
        onView(allOf(withId(R.id.displayCard_heading), isDescendantOfA(withId(R.id.main_wodCard))))
                .check(matches(withText("Word of the Day")));
    }

    @Test
    public void test_wodCard_redirects_to_display() {
        ActivityScenario.launch(MainActivity.class);

        onView(withId(R.id.main_wodCard))
                .check(matches(isDisplayed()));
        onView(allOf(withId(R.id.displayCard_button), isDescendantOfA(withId(R.id.main_wodCard))))
                .perform(click());

        intended(allOf(hasExtraWithKey(DisplayActivity.EXTRA_ID),
                hasComponent(DisplayActivity.class.getName())));
    }
}
