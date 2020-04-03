package com.a494studios.koreanconjugator.settings;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.a494studios.koreanconjugator.R;
import com.a494studios.koreanconjugator.utils.Utils;

/**
 * A simple {@link PreferenceFragment} subclass.
 *
 */
public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
       // Empty on purpose
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

        int count = Utils.getFavCount(getActivity());
        findPreference(Utils.PREF_FAV_COUNT).setSummary("You have " + count + " favorites");
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }
}
