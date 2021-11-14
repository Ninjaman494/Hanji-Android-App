package com.a494studios.koreanconjugator.settings;

import android.app.Activity;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.preference.Preference;

import com.a494studios.koreanconjugator.utils.Utils;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.PurchaseHistoryRecord;
import com.android.billingclient.api.PurchaseHistoryResponseListener;

import java.util.List;

public class AdFreeCheckListener implements PurchaseHistoryResponseListener {
    Activity activity;
    Preference preference;

    public AdFreeCheckListener(Activity activity, Preference preference) {
        this.activity = activity;
        this.preference = preference;
    }

    @Override
    public void onPurchaseHistoryResponse(@NonNull BillingResult result, @Nullable List<PurchaseHistoryRecord> list) {
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
            preference.setSummary("Click here to check your ad-free status");
        });
    }
}
