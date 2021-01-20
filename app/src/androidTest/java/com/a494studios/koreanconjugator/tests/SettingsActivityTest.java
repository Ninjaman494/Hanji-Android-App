package com.a494studios.koreanconjugator.tests;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import com.a494studios.koreanconjugator.parsing.Favorite;
import com.a494studios.koreanconjugator.rules.StubIntentsRule;
import com.a494studios.koreanconjugator.settings.FavoritesActivity;
import com.a494studios.koreanconjugator.settings.SettingsActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasData;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class SettingsActivityTest {

    @Rule
    @SuppressWarnings("deprecation")
    public ActivityTestRule<SettingsActivity> activityRule =
            new ActivityTestRule<>(SettingsActivity.class, true, false);

    @Rule
    public StubIntentsRule intentsRule = new StubIntentsRule(SettingsActivity.class.getName());


    @Before
    public void setup() {
        // Create favorites
        Context context = getInstrumentation().getTargetContext();
        ArrayList<Favorite> favs = new ArrayList<>();
        favs.add(new Favorite("Past", "declarative past informal high", false));
        favs.add(new Favorite("Present Honorific", "declarative present informal high", true));
        com.a494studios.koreanconjugator.utils.Utils.setFavorites(favs, context);

        // Start test
        activityRule.launchActivity(null);
    }

    @Test
    public void favorites_redirectsToFavorites() {
        onView(withText("You have 2 favorites")).check(matches(isDisplayed()));

        onView(withText("Favorites")).perform(click());

        intended(hasComponent(FavoritesActivity.class.getName()));
    }

    @Test
    public void tcu_redirectsToWebsite() {
        onView(withText("Terms and Conditions of Use")).perform(click());

        intended(allOf(hasAction(Intent.ACTION_VIEW),
                hasData(Uri.parse("https://hanji-website.vercel.app/terms"))));
    }

    @Test
    public void privacy_redirectsToWebsite() {
        onView(withText("Privacy Policy")).perform(click());

        intended(allOf(hasAction(Intent.ACTION_VIEW),
                hasData(Uri.parse("https://hanji-website.vercel.app/privacy"))));
    }
}
