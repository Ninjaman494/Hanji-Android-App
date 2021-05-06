package com.a494studios.koreanconjugator;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Checkable;

import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.ViewInteraction;

import com.a494studios.koreanconjugator.settings.SettingsActivity;
import com.eggheadgames.aboutbox.activity.AboutActivity;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import okhttp3.mockwebserver.RecordedRequest;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.Matchers.isA;

public class Utils {

    // From:
    // https://stackoverflow.com/questions/37819278/android-espresso-click-checkbox-if-not-checked/39650813#39650813
    public static ViewAction setChecked(final boolean checked) {
        return new ViewAction() {
            @Override
            public BaseMatcher<View> getConstraints() {
                return new BaseMatcher<View>() {
                    @Override
                    public boolean matches(Object item) {
                        return isA(Checkable.class).matches(item);
                    }

                    @Override
                    public void describeMismatch(Object item, Description mismatchDescription) {
                    }

                    @Override
                    public void describeTo(Description description) {
                    }
                };
            }

            @Override
            public String getDescription() {
                return null;
            }

            @Override
            public void perform(UiController uiController, View view) {
                Checkable checkableView = (Checkable) view;
                checkableView.setChecked(checked);
            }
        };
    }

    // From: https://stackoverflow.com/questions/38773804/espress-accessing-nth-child-element-of-linear-layout
    public static Matcher<View> nthChildOf(final Matcher<View> parentMatcher, final int childPosition) {
        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("with "+childPosition+" child view of type parentMatcher");
            }

            @Override
            public boolean matchesSafely(View view) {
                if (!(view.getParent() instanceof ViewGroup)) {
                    return parentMatcher.matches(view.getParent());
                }

                ViewGroup group = (ViewGroup) view.getParent();
                return parentMatcher.matches(view.getParent()) && group.getChildAt(childPosition).equals(view);
            }
        };
    }

    public static void testActionBar() {
        ViewInteraction overflowMenuButton = onView(withContentDescription("More options"));

        overflowMenuButton.perform(click());
        onView(withText("Settings")).perform(click());

        intended(hasComponent(SettingsActivity.class.getName()));

        overflowMenuButton.perform(click());
        onView(withText("About")).perform(click());

        intended(hasComponent(AboutActivity.class.getName()));

        overflowMenuButton.perform(click());
        onView(withText("Report a Bug")).check(matches(isDisplayed()));
    }

    public static void assertBodyContains(RecordedRequest request, String contains) {
        assertNotNull(request);
        String requestBody = request.getBody().clone().readUtf8();
        assertTrue(requestBody.contains(contains));
    }
}
