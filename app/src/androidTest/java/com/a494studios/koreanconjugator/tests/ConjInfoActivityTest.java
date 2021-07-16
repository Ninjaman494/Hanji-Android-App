package com.a494studios.koreanconjugator.tests;

import android.content.Intent;
import android.content.pm.ActivityInfo;

import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import com.a494studios.koreanconjugator.R;
import com.a494studios.koreanconjugator.display.ConjInfoActivity;
import com.a494studios.koreanconjugator.rules.StubIntentsRule;
import com.linearlistview.LinearListView;

import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.a494studios.koreanconjugator.Utils.testActionBar;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ConjInfoActivityTest {
    private final String EXTRA_NAME = "Name";
    private final String EXTRA_CONJ = "conjugation";
    private final String EXTRA_PRON = "pronunciation";
    private final String EXTRA_ROME = "romanization";
    private ArrayList<String> EXTRA_EXPL;

    @Rule
    @SuppressWarnings("deprecation")
    public ActivityTestRule<ConjInfoActivity> activityRule = new ActivityTestRule<ConjInfoActivity>(ConjInfoActivity.class) {
        @Override
        protected Intent getActivityIntent() {
            EXTRA_EXPL = new ArrayList<>();
            EXTRA_EXPL.add("reason 1");
            EXTRA_EXPL.add("reason 2");
            EXTRA_EXPL.add("reason 3");

            Intent intent = new Intent();
            intent.putExtra(ConjInfoActivity.EXTRA_NAME,EXTRA_NAME);
            intent.putExtra(ConjInfoActivity.EXTRA_CONJ,EXTRA_CONJ);
            intent.putExtra(ConjInfoActivity.EXTRA_PRON,EXTRA_PRON);
            intent.putExtra(ConjInfoActivity.EXTRA_ROME,EXTRA_ROME);
            intent.putExtra(ConjInfoActivity.EXTRA_EXPL,EXTRA_EXPL);
            return intent;
        }
    };

    @Rule
    public StubIntentsRule intentsRule = new StubIntentsRule(ConjInfoActivity.class.getName());

    @Test
    public void test_displaysData() {
        checkUI();
    }

    @Test
    public void test_displayAfterRotation() {
        try {
            activityRule.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            Thread.sleep(100);
            checkUI();
            activityRule.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            Thread.sleep(100);
            checkUI();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private void checkUI() {
        onView(Matchers.allOf(ViewMatchers.withId(R.id.displayCard_heading), isDescendantOfA(withId(R.id.info_infoCard))))
                .check(matches(isDisplayed()))
                .check(matches(withText(EXTRA_NAME)));
        onView(withId(R.id.conjInfo_conjugated))
                .check(matches(isDisplayed()))
                .check(matches(withText(EXTRA_CONJ)));
        onView(withId(R.id.conjInfo_hpronc))
                .check(matches(isDisplayed()))
                .check(matches(withText(EXTRA_PRON)));
        onView(withId(R.id.conjInfo_roman))
                .check(matches(isDisplayed()))
                .check(matches(withText(EXTRA_ROME)));

        LinearListView listView = activityRule.getActivity().findViewById(R.id.conjInfo_explainList);
        for(int i = 0;i<EXTRA_EXPL.size();i++) {
            assertEquals(listView.getAdapter().getItem(i),EXTRA_EXPL.get(i));
        }
    }
}
