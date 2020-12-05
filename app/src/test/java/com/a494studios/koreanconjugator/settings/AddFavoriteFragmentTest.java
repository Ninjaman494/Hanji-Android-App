package com.a494studios.koreanconjugator.settings;

import android.app.Dialog;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentManager;

import com.a494studios.koreanconjugator.R;
import com.a494studios.koreanconjugator.parsing.Favorite;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
public class AddFavoriteFragmentTest {

    private AddFavoritesFragmentActivity activity;
    private HashMap<String,Boolean> conjData;

    @Before
    public void init() {
        activity = Robolectric.buildActivity(AddFavoritesFragmentActivity.class)
                .create()
                .start()
                .resume()
                .get();

        conjData = new HashMap<>();
        conjData.put("Connective And", false);
        conjData.put("Declarative Present", true);
    }

    @Test
    public void btn_isDisabledUntilFormFilled() {
        AddFavoriteFragment fragment = AddFavoriteFragment.newInstance(conjData);
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        fragment.show(fragmentManager, "tag");

        AlertDialog dialog = (AlertDialog)fragment.getDialog();
        assertTrue(dialog.isShowing());

        Button btn = dialog.getButton(Dialog.BUTTON_POSITIVE);
        assertFalse(btn.isEnabled());

        EditText nameView = dialog.findViewById(R.id.addFav_name);
        nameView.setText("foo");

        assertTrue(btn.isEnabled());
    }

    @Test
    public void form_canSubmitSimple() {
        AddFavoriteFragment fragment = AddFavoriteFragment.newInstance(conjData);
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        fragment.show(fragmentManager, "tag");

        AlertDialog dialog = (AlertDialog)fragment.getDialog();
        assertTrue(dialog.isShowing());

        EditText nameView = dialog.findViewById(R.id.addFav_name);
        nameView.setText("foo");

        Spinner spinner = dialog.findViewById(R.id.addFav_conjSpinner);
        spinner.setSelection(0);

        Button btn = dialog.getButton(Dialog.BUTTON_POSITIVE);
        btn.performClick();

        Favorite favorite = activity.getFavorite();
        assertEquals("foo" , favorite.getName());
        assertEquals("connective and", favorite.getConjugationName());
        assertFalse(favorite.isHonorific());
    }

    @Test
    public void form_canSubmitComplex() {
        AddFavoriteFragment fragment = AddFavoriteFragment.newInstance(conjData);
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        fragment.show(fragmentManager, "tag");

        AlertDialog dialog = (AlertDialog)fragment.getDialog();
        assertTrue(dialog.isShowing());

        EditText nameView = dialog.findViewById(R.id.addFav_name);
        nameView.setText("foo");

        Spinner spinner = dialog.findViewById(R.id.addFav_conjSpinner);
        spinner.setSelection(1);

        Spinner speechLevelSpinner = dialog.findViewById(R.id.addFav_speechLevelSpinner);
        speechLevelSpinner.setSelection(1);

        CheckBox checkBox = dialog.findViewById(R.id.addFav_checkbox);
        checkBox.setChecked(true);

        Button btn = dialog.getButton(Dialog.BUTTON_POSITIVE);
        btn.performClick();

        Favorite favorite = activity.getFavorite();
        assertEquals("foo" , favorite.getName());
        assertEquals("declarative present informal high", favorite.getConjugationName());
        assertTrue(favorite.isHonorific());
    }
}
