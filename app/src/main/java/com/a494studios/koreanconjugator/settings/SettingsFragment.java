package com.a494studios.koreanconjugator.settings;


import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.widget.Toast;

import com.a494studios.koreanconjugator.CustomApplication;
import com.a494studios.koreanconjugator.R;
import com.a494studios.koreanconjugator.utils.Utils;
import com.android.billingclient.api.BillingClient;

/**
 * A simple {@link PreferenceFragment} subclass.
 */
public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        Activity activity = getActivity();
        Preference adFreeCheck = findPreference("adFreeCheck");
        adFreeCheck.setOnPreferenceClickListener(preference -> {
            adFreeCheck.setSummary("Checking ad-free status...");

            BillingClient client = CustomApplication.getBillingClient();
            client.queryPurchaseHistoryAsync(BillingClient.SkuType.INAPP, (result, list) -> {
                String msg;
                int responseCode = result.getResponseCode();
                switch (responseCode) {
                    case BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED:
                    case BillingClient.BillingResponseCode.OK:
                        boolean isAdFree = list.get(0).getSkus().get(0).equals(Utils.SKU_AD_FREE);
                        Utils.setAdFree(activity, isAdFree);
                        if (isAdFree) {
                            msg = "Ad-free purchase activated, thank you for supporting Hanji!";
                        } else {
                            msg = "Ad-free purchase not found";
                        }
                        break;
                    case BillingClient.BillingResponseCode.BILLING_UNAVAILABLE:
                    case BillingClient.BillingResponseCode.FEATURE_NOT_SUPPORTED:
                        msg = "You're device is not compatible. If you purchased an upgrade please contact support for a refund";
                        break;
                    case BillingClient.BillingResponseCode.ITEM_NOT_OWNED:
                        msg = "Ad-free purchase not found";
                        break;
                    case BillingClient.BillingResponseCode.SERVICE_DISCONNECTED:
                    case BillingClient.BillingResponseCode.SERVICE_TIMEOUT:
                    case BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE:
                        msg = "Unable to connect to Google Play store. Please try again later";
                        break;
                    default:
                        msg = "An error occurred. Please try again later";
                        break;
                }

                activity.runOnUiThread(() -> {
                    Toast.makeText(activity, msg, Toast.LENGTH_LONG).show();
                    adFreeCheck.setSummary("Click here to check your ad-free status");
                });
            });

            return true;
        });
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
