package com.a494studios.koreanconjugator;

import android.view.View;
import android.widget.Checkable;

import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.ViewInteraction;

import com.a494studios.koreanconjugator.settings.SettingsActivity;
import com.eggheadgames.aboutbox.activity.AboutActivity;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.rm3l.maoni.ui.MaoniActivity;

import okhttp3.mockwebserver.RecordedRequest;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.Matchers.isA;

class Utils {

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

    public static void testActionBar() {
        ViewInteraction overflowMenuButton = onView(withContentDescription("More options"));

        overflowMenuButton.perform(click());
        onView(withText("Settings")).perform(click());

        intended(hasComponent(SettingsActivity.class.getName()));

        overflowMenuButton.perform(click());
        onView(withText("About")).perform(click());

        intended(hasComponent(AboutActivity.class.getName()));

        overflowMenuButton.perform(click());
        onView(withText("Report a Bug")).perform(click());

        intended(hasComponent(MaoniActivity.class.getName()));
    }

    public static void assertBodyContains(RecordedRequest request, String contains) {
        String requestBody = request.getBody().readByteString().utf8();
        assertTrue(requestBody.contains(contains));
    }
}
