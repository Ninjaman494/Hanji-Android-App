package com.a494studios.koreanconjugator.search;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.a494studios.koreanconjugator.conjugator.ConjugatorActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
public class NoResultsFragmentUnitTest {

    private NoResultsFragment fragment;
    private FragmentActivity activity;

    @Before
    public void init() {
        fragment =  NoResultsFragment.newInstance("term", null);
        activity = Robolectric.buildActivity(FragmentActivity.class)
                .create()
                .start()
                .resume()
                .get();
    }

    @Test
    public void test_correctText() {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        fragment.show(fragmentManager, "tag");

        Dialog dialog = fragment.getDialog();

        assertTrue(dialog.isShowing());

        int titleId = activity.getResources().getIdentifier( "alertTitle", "id", activity.getPackageName() );

        TextView titleView = dialog.findViewById(titleId);
        TextView msgView = dialog.findViewById(android.R.id.message);

        assertEquals(titleView.getText(), "No Results");
        assertEquals(msgView.getText(), "We couldn't find anything matching your search. You can use the conjugator, but the conjugations might not be accurate.");
    }

    @Test
    public void test_cancelBtn() {
        DialogInterface.OnClickListener listener = mock(DialogInterface.OnClickListener.class);
        NoResultsFragment fragment = NoResultsFragment.newInstance("term", listener);

        fragment.show(activity.getSupportFragmentManager(), "tag");

        AlertDialog dialog = (AlertDialog)fragment.getDialog();

        assertTrue(dialog.isShowing());

        Button btn = dialog.getButton(Dialog.BUTTON_NEGATIVE);
        btn.performClick();

        assertEquals(btn.getText(), "Cancel");

        // listener is called a second time when dismissed by test runner
        verify(listener, times(2)).onClick(anyObject(), anyInt());
    }

    @Test
    public void test_okBtn() {
        String term = "term";
        DialogInterface.OnClickListener listener = mock(DialogInterface.OnClickListener.class);
        NoResultsFragment fragment = NoResultsFragment.newInstance(term, listener);

        fragment.show(activity.getSupportFragmentManager(), "tag");

        AlertDialog dialog = (AlertDialog)fragment.getDialog();

        assertTrue(dialog.isShowing());

        Button btn = dialog.getButton(Dialog.BUTTON_POSITIVE);
        btn.performClick();

        assertEquals(btn.getText(), "Use Conjugator");

        Intent intent = Shadows.shadowOf(activity).peekNextStartedActivityForResult().intent;

        assertEquals(intent.getComponent(), new ComponentName(activity, ConjugatorActivity.class));
        assertEquals(intent.getStringExtra(ConjugatorActivity.EXTRA_TERM), term);
    }
}
