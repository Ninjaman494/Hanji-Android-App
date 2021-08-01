package com.a494studios.koreanconjugator.suggestions;

import android.content.Intent;

import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.idling.CountingIdlingResource;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.a494studios.koreanconjugator.R;
import com.a494studios.koreanconjugator.parsing.Server;
import com.google.android.material.textfield.TextInputLayout;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.shadows.ShadowToast;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertEquals;

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

    @Before
    public void init() {
        // Register idling resource
        IdlingRegistry.getInstance().register(idler);
    }

    @Test
    public void form_failsWhenEmpty() {
        onView(withText("Submit")).perform(click());

        assertEquals("At least one addition is required", ShadowToast.getTextOfLatestToast());
    }

    @Test
    public void form_failsWhenSentenceMissing() {
        onView(withId(R.id.suggestion_translation)).perform(typeText("translation"));
        onView(withText("Submit")).perform(click());

        TextInputLayout sentenceLayout = activityRule.getActivity().findViewById(R.id.suggestion_sentenceLayout);
        assertEquals("Sentence is required for example", sentenceLayout.getError());
    }

    @Test
    public void form_failsWhenTranslationMissing() {
        onView(withId(R.id.suggestion_sentence)).perform(typeText("sentence"));
        onView(withText("Submit")).perform(click());

        TextInputLayout translationLayout = activityRule.getActivity().findViewById(R.id.suggestion_translationLayout);
        assertEquals("Translation is required for example", translationLayout.getError());
    }

    @Test
    public void form_failsWhenEnglishAntonym() {
        onView(withId(R.id.suggestion_antonym)).perform(typeText("antonym"));
        onView(withText("Submit")).perform(click());

        TextInputLayout antonymLayout = activityRule.getActivity().findViewById(R.id.suggestion_antonymLayout);
        assertEquals("Antonym must be in Korean", antonymLayout.getError());
    }

    @Test
    public void form_failsWhenEnglishSynonym() {
        onView(withId(R.id.suggestion_synonym)).perform(typeText("synonym"));
        onView(withText("Submit")).perform(click());

        TextInputLayout synonymLayout = activityRule.getActivity().findViewById(R.id.suggestion_synonymLayout);
        assertEquals("Synonym must be in Korean", synonymLayout.getError());
    }

    @Test
    public void form_failsWhenEnglishSentence() {
        onView(withId(R.id.suggestion_sentence)).perform(typeText("sentence"));
        onView(withId(R.id.suggestion_translation)).perform(typeText("translation"));
        onView(withText("Submit")).perform(click());

        TextInputLayout sentenceLayout = activityRule.getActivity().findViewById(R.id.suggestion_sentenceLayout);
        assertEquals("Sentence must be in Korean", sentenceLayout.getError());
    }

    @Test
    public void form_failsWhenKoreanTranslation() {
        // Unicode for 안녕하세요
        onView(withId(R.id.suggestion_sentence)).perform(replaceText("\uc548\ub155\ud558\uc138\uc694"));
        onView(withId(R.id.suggestion_translation)).perform(replaceText("\uc548\ub155\ud558\uc138\uc694"));
        onView(withText("Submit")).perform(click());

        TextInputLayout translationLayout = activityRule.getActivity().findViewById(R.id.suggestion_translationLayout);
        assertEquals("Translation must be in English", translationLayout.getError());
    }
}
