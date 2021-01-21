package com.a494studios.koreanconjugator.tests;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.idling.CountingIdlingResource;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import com.a494studios.koreanconjugator.LaunchActivity;
import com.a494studios.koreanconjugator.MainActivity;
import com.a494studios.koreanconjugator.MockApplication;
import com.a494studios.koreanconjugator.parsing.Favorite;
import com.a494studios.koreanconjugator.parsing.Server;
import com.a494studios.koreanconjugator.rules.StubIntentsRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.swipeLeft;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class LaunchActivityTest {

    private static final CountingIdlingResource idler = Server.getIdler();

    @Rule
    @SuppressWarnings("deprecation")
    public ActivityTestRule<LaunchActivity> activityRule = new ActivityTestRule<>(LaunchActivity.class, true, false);

    @Rule
    public StubIntentsRule intentsRule = new StubIntentsRule(LaunchActivity.class.getName());

    @Before
    public void setup() {
        // Register idling resource
        IdlingRegistry.getInstance().register(idler);

        // Set ad free to true by default
        MockApplication testApp = ApplicationProvider.getApplicationContext();
        testApp.setAdFree(true);
    }


    @After
    public void teardown() {
        IdlingRegistry.getInstance().unregister(idler);
    }

    @Test
    public void existingUsers_getRedirected() {
        Context context = getInstrumentation().getTargetContext();
        com.a494studios.koreanconjugator.utils.Utils.setFirstBoot(context, false);
        com.a494studios.koreanconjugator.utils.Utils.setFirstTwo(context, false);

        activityRule.launchActivity(null);

        intended(hasComponent(MainActivity.class.getName()));
    }

    @Test
    public void newUsers_setsSharedPrefs() {
        Context context = getInstrumentation().getTargetContext();
        com.a494studios.koreanconjugator.utils.Utils.setFirstBoot(context, true);
        com.a494studios.koreanconjugator.utils.Utils.setFirstTwo(context, false);

        activityRule.launchActivity(null);

        // Verify favorites
        ArrayList<Favorite> favs = com.a494studios.koreanconjugator.utils.Utils.getFavorites(context);
        assert favs.get(0).getName().equals("Past");
        assert favs.get(0).getConjugationName().equals("declarative past informal high");
        assert !favs.get(0).isHonorific();

        assert favs.get(1).getName().equals("Present");
        assert favs.get(1).getConjugationName().equals("declarative past informal high");
        assert !favs.get(1).isHonorific();

        assert favs.get(2).getName().equals("Future");
        assert favs.get(2).getConjugationName().equals("declarative future informal high");
        assert !favs.get(2).isHonorific();

        // Verify firstBoot and firstTwo
        assert !com.a494studios.koreanconjugator.utils.Utils.isFirstBoot(context);
        assert !com.a494studios.koreanconjugator.utils.Utils.isFirstTwo(context);
    }

    @Test
    public void twoUsers_setsSharedPrefs() {
        Context context = getInstrumentation().getTargetContext();
        com.a494studios.koreanconjugator.utils.Utils.setFirstBoot(context, false);
        com.a494studios.koreanconjugator.utils.Utils.setFirstTwo(context, true);

        activityRule.launchActivity(null);

        // Verify favorites
        ArrayList<Favorite> favs = com.a494studios.koreanconjugator.utils.Utils.getFavorites(context);
        assert favs.get(0).getName().equals("Past");
        assert favs.get(0).getConjugationName().equals("declarative past informal high");
        assert !favs.get(0).isHonorific();

        assert favs.get(1).getName().equals("Present");
        assert favs.get(1).getConjugationName().equals("declarative past informal high");
        assert !favs.get(1).isHonorific();

        assert favs.get(2).getName().equals("Future");
        assert favs.get(2).getConjugationName().equals("declarative future informal high");
        assert !favs.get(2).isHonorific();

        // Verify firstBoot and firstTwo
        assert !com.a494studios.koreanconjugator.utils.Utils.isFirstBoot(context);
        assert !com.a494studios.koreanconjugator.utils.Utils.isFirstTwo(context);
    }

    @Test
    public void btn_redirectsToMain() {
        activityRule.launchActivity(null);

        onView(withId(android.R.id.content)).perform(swipeLeft());
        onView(withId(android.R.id.content)).perform(swipeLeft());

        onView(withText("Start Learning!")).perform(click());

        intended(hasComponent(MainActivity.class.getName()));
    }

}
