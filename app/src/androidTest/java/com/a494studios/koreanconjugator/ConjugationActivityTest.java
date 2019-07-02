package com.a494studios.koreanconjugator;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.a494studios.koreanconjugator.display.ConjugationActivity;
import com.linearlistview.LinearListView;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ConjugationActivityTest {
    private final static String STEM = "가다";
    private final static boolean HONORIFIC = false;
    private final static boolean ISADJ = false;

    @Rule
    public ActivityTestRule<ConjugationActivity> activityRule = new ActivityTestRule<ConjugationActivity>(ConjugationActivity.class) {
        @Override
        protected Intent getActivityIntent() {
            Intent intent = new Intent();
            intent.putExtra(ConjugationActivity.EXTRA_STEM,STEM);
            intent.putExtra(ConjugationActivity.EXTRA_HONORIFIC,HONORIFIC);
            intent.putExtra(ConjugationActivity.EXTRA_ISADJ,ISADJ);
            return intent;
        }
    };

    @Test
    public void test_displaysData() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        LinearListView listView = activityRule.getActivity().findViewById(R.id.conj_list);
        int numItem = listView.getAdapter().getCount();
        assertTrue(numItem > 0);
    }

    @Test
    public void test_displayAfterRotation() {
        try {
            Thread.sleep(100);
            LinearListView listView = activityRule.getActivity().findViewById(R.id.conj_list);
            int numItem = listView.getAdapter().getCount();
            assertTrue(numItem > 0);

            activityRule.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            Thread.sleep(100);
            assertEquals(numItem,listView.getAdapter().getCount());

            activityRule.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            Thread.sleep(100);
            assertEquals(numItem,listView.getAdapter().getCount());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test_honorificSwitch() {
        LinearListView listView = activityRule.getActivity().findViewById(R.id.conj_list);
        int numItems = listView.getAdapter().getCount();

        // Honorific
        onView(withId(R.id.conj_switch)).perform(click());
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertEquals(numItems,listView.getAdapter().getCount());
        List<ConjugationQuery.Conjugation> conjugations = (List<ConjugationQuery.Conjugation>)listView.getAdapter().getItem(0);
        assertTrue(conjugations.get(0).honorific);

        // Back to regular
        onView(withId(R.id.conj_switch)).perform(click());
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertEquals(numItems,listView.getAdapter().getCount());
        conjugations = (List<ConjugationQuery.Conjugation>)listView.getAdapter().getItem(0);
        assertFalse(conjugations.get(0).honorific);
    }
}
