package com.a494studios.koreanconjugator;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.a494studios.koreanconjugator.display.ConjInfoActivity;
import com.linearlistview.LinearListView;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
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
        onView(withId(R.id.displayCard_heading))
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
