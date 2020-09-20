package com.a494studios.koreanconjugator;

import android.content.Intent;
import android.content.pm.ActivityInfo;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import com.a494studios.koreanconjugator.conjugations.ConjugationActivity;
import com.a494studios.koreanconjugator.conjugations.ConjugationCardsAdapter;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static com.a494studios.koreanconjugator.Utils.setChecked;
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

        RecyclerView recyclerView = activityRule.getActivity().findViewById(R.id.conj_list);
        int numItem = recyclerView.getAdapter().getItemCount();
        assertTrue(numItem > 0);
    }

    @Test
    public void test_displayAfterRotation() {
        try {
            Thread.sleep(100);
            RecyclerView recyclerView = activityRule.getActivity().findViewById(R.id.conj_list);
            int numItem = recyclerView.getAdapter().getItemCount();
            assertTrue(numItem > 0);

            activityRule.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            Thread.sleep(100);
            assertEquals(numItem,recyclerView.getAdapter().getItemCount());

            activityRule.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            Thread.sleep(100);
            assertEquals(numItem,recyclerView.getAdapter().getItemCount());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test_honorificSwitch() {
        RecyclerView recyclerView = activityRule.getActivity().findViewById(R.id.conj_list);
        int numItems = recyclerView.getAdapter().getItemCount();

        try {
            Thread.sleep(1000);
        } catch (Exception e){
            e.printStackTrace(System.err);
        }

        // Honorific
        onView(withId(R.id.conj_switch)).perform(setChecked(true));
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ConjugationCardsAdapter adapter = (ConjugationCardsAdapter)recyclerView.getAdapter();
        assertEquals(numItems,adapter.getItemCount());
        List<ConjugationQuery.Conjugation> conjugations = adapter.getItem(0);
        assertTrue(conjugations.get(0).honorific);

        // Back to regular
        onView(withId(R.id.conj_switch)).perform(setChecked(false));
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        adapter = (ConjugationCardsAdapter) recyclerView.getAdapter();
        assertEquals(numItems, adapter.getItemCount());
        conjugations = adapter.getItem(0);
        assertFalse(conjugations.get(0).honorific);
    }
}
