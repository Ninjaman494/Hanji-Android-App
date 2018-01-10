package com.a494studios.koreanconjugator.settings;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.a494studios.koreanconjugator.R;
import com.a494studios.koreanconjugator.Utils;

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
        switch (key) {
            case Utils.PREF_LUCKY_KOR:
                if (sharedPreferences.getBoolean(key, false)) {
                    findPreference(key).setSummary(R.string.lucky_kor_true);
                } else {
                    findPreference(key).setSummary(R.string.lucky_kor_false);
                }
                break;
            case Utils.PREF_LUCKY_ENG:
                if (sharedPreferences.getBoolean(key, false)) {
                    findPreference(key).setSummary(R.string.lucky_eng_true);
                } else {
                    findPreference(key).setSummary(R.string.lucky_eng_false);
                }
                break;
        }
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
