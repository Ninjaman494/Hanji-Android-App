package com.a494studios.koreanconjugator.tests;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;

import androidx.core.util.Pair;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.idling.CountingIdlingResource;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import com.a494studios.koreanconjugator.MockApplication;
import com.a494studios.koreanconjugator.MockedResponses;
import com.a494studios.koreanconjugator.R;
import com.a494studios.koreanconjugator.conjugations.ConjugationActivity;
import com.a494studios.koreanconjugator.display.DisplayActivity;
import com.a494studios.koreanconjugator.parsing.Favorite;
import com.a494studios.koreanconjugator.parsing.Server;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static com.a494studios.koreanconjugator.Utils.nthChildOf;
import static com.a494studios.koreanconjugator.Utils.testActionBar;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class DisplayActivityTest {

    private static final CountingIdlingResource idler = Server.getIdler();
    private MockWebServer server;

    @Rule
    @SuppressWarnings("deprecation")
    public ActivityTestRule<DisplayActivity> activityRule =
            new ActivityTestRule<DisplayActivity>(DisplayActivity.class, true, false) {
                @Override
                protected Intent getActivityIntent() {
                    Intent intent = new Intent();
                    intent.putExtra(DisplayActivity.EXTRA_ID, "id");
                    return intent;
                }
            };

    @Before
    public void stubIntents() throws IOException {
        // Register idling resource
        IdlingRegistry.getInstance().register(idler);

        // Stub intents
        Intents.init();
        Intent resultData = new Intent();
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, resultData);
        intending(not(hasComponent(DisplayActivity.class.getName()))).respondWith(result);

        // Setup mock server
        server = new MockWebServer();
        server.start();
        MockApplication testApp = ApplicationProvider.getApplicationContext();
        testApp.setServerUrl(server.url("/").toString());

        // Enqueue responses
        server.enqueue(new MockResponse().setBody(MockedResponses.ENTRY));
        server.enqueue(new MockResponse().setBody(MockedResponses.CONJUGATIONS));

        // Write favorites in SharedPreferences
        Context context = getInstrumentation().getTargetContext();
        ArrayList<Favorite> favs = new ArrayList<>();
        favs.add(new Favorite("Past","declarative past informal high",false));
        favs.add(new Favorite("Present","declarative present informal high",false));
        favs.add(new Favorite("Future","declarative future informal high",false));
        com.a494studios.koreanconjugator.utils.Utils.setFavorites(favs,context);

        // Start test
        activityRule.launchActivity(null);
    }

    @After
    public void releaseIntents() throws IOException {
        Intents.release();
        IdlingRegistry.getInstance().unregister(idler);
        server.shutdown();
    }

    @Test
    public void overflowOptions() {
        testActionBar();
    }

    @Test
    public void contents_areDisplayed() {
        // Term and POS
        onView(ViewMatchers.withId(R.id.word_info_term)).check(matches(withText("가다")));
        onView(withId(R.id.word_info_pos)).check(matches(withText("Verb")));

        // Definitions
        onView(nthChildOf(withId(R.id.word_info_recycler), 0))
                .check(matches(withText("to go")));
        onView(nthChildOf(withId(R.id.word_info_recycler), 1))
                .check(matches(withText("second definition")));
        onView(nthChildOf(withId(R.id.word_info_recycler), 2))
                .check(matches(withText("third definition")));

        // Show all definitions
        onView(withText("1 MORE")).perform(click());

        onView(nthChildOf(withId(R.id.word_info_recycler), 3))
                .check(matches(withText("fourth definition")));

        onView(withText("COLLAPSE")).perform(click());

        // Examples
        onView(allOf(isDescendantOfA(withId(R.id.disp_examplesCard)),
                withId(R.id.displayCard_heading)))
                .check(matches(withText("Examples")));

        List<Pair<String, String>> examples = Arrays.asList(
                new Pair<>("가자!", "Let's go!"),
                new Pair<>("Second sentence", "Second translation"),
                new Pair<>("Third sentence", "Third translation"));

        for(int i = 0;i<examples.size();i++) {
            String sentence = examples.get(i).first;
            String translation = examples.get(i).second;

            onView(allOf(isDescendantOfA(withId(R.id.disp_examplesCard)),
                    isDescendantOfA(nthChildOf(withId(R.id.listCard_list), i)),
                    withId(R.id.item_example_sentence)))
                    .check(matches(withText(sentence)));
            onView(allOf(isDescendantOfA(withId(R.id.disp_examplesCard)),
                    isDescendantOfA(nthChildOf(withId(R.id.listCard_list), i)),
                    withId(R.id.item_example_translation)))
                    .check(matches(withText(translation)));
        }

        // Antonyms
        onView(allOf(isDescendantOfA(withId(R.id.disp_antCard)),
                withId(R.id.displayCard_heading)))
                .check(matches(withText("Antonyms")));
        onView(allOf(isDescendantOfA(withId(R.id.disp_antCard)),
                withId(R.id.simpleCard_text)))
                .check(matches(withText("오다, antonym two, antonym three")));

        // Synonyms
        onView(withId(R.id.disp_synCard))
                .check(matches(withEffectiveVisibility(Visibility.GONE)));

        // Note
        onView(withId(R.id.disp_noteCard))
                .check(matches(withEffectiveVisibility(Visibility.GONE)));
    }

    @Test
    public void favorites_areDisplayed() {
        onView(allOf(isDescendantOfA(withId(R.id.disp_conjCard)),
                withId(R.id.displayCard_heading)))
                .check(matches(withText("Conjugations")));

        List<Pair<String, String>> favorites = Arrays.asList(
                new Pair<>("Past", "갔어요"),
                new Pair<>("Present", "가요"),
                new Pair<>("Future", "갈 거예요"));

        // Verify favorite conjugations are shown
        for(int i = 0;i<favorites.size();i++) {
            String name = favorites.get(i).first;
            String conjugation = favorites.get(i).second;

            onView(allOf(isDescendantOfA(withId(R.id.disp_conjCard)),
                    isDescendantOfA(nthChildOf(withId(R.id.listCard_list), i)),
                    withId(R.id.conjFormal)))
                    .check(matches(withText(name)));
            onView(allOf(isDescendantOfA(withId(R.id.disp_conjCard)),
                    isDescendantOfA(nthChildOf(withId(R.id.listCard_list), i)),
                    withId(R.id.conjText)))
                    .check(matches(withText(conjugation)));
        }
    }

    @Test
    public void favorites_goesToConjugations() {
        onView(withText("SEE ALL")).perform(click());

        intended(allOf(hasComponent(ConjugationActivity.class.getName()),
                hasExtra(ConjugationActivity.EXTRA_STEM, "가다"),
                hasExtra(ConjugationActivity.EXTRA_ISADJ, false),
                hasExtra(ConjugationActivity.EXTRA_HONORIFIC, false),
                hasExtra(ConjugationActivity.EXTRA_REGULAR, null)));
    }
}
