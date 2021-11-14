package com.a494studios.koreanconjugator.settings;


import android.preference.Preference;

import androidx.fragment.app.FragmentActivity;

import com.a494studios.koreanconjugator.utils.Utils;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.PurchaseHistoryRecord;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowToast;

import java.util.ArrayList;
import java.util.Collections;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class AdFreeCheckListenerTest {
    private FragmentActivity activity;
    private Preference preference;
    AdFreeCheckListener listener;

    @Before
    public void init() {
        activity = Robolectric.buildActivity(FragmentActivity.class)
                .create()
                .start()
                .resume()
                .get();

        preference = mock(Preference.class);
        listener = new AdFreeCheckListener(activity, preference);
    }

    @Test
    public void enablesAdFreeWhenOK() {
        // Setup mock
        PurchaseHistoryRecord record = mock(PurchaseHistoryRecord.class);
        ArrayList<String> skus = new ArrayList<>(Collections.singletonList(Utils.SKU_AD_FREE));
        when(record.getSkus()).thenReturn(skus);

        String msg = "Ad-free purchase activated, thank you for supporting Hanji!";
        testListener(BillingClient.BillingResponseCode.OK, msg, record);
        assertTrue(Utils.isAdFree(activity));
    }

    @Test
    public void disableWhenNotOwned() {
        // Setup mock
        PurchaseHistoryRecord record = mock(PurchaseHistoryRecord.class);
        ArrayList<String> skus = new ArrayList<>(Collections.singletonList("foobar"));
        when(record.getSkus()).thenReturn(skus);

        String msg = "Ad-free purchase not found";
        testListener(BillingClient.BillingResponseCode.OK, msg, record);
        assertFalse(Utils.isAdFree(activity));
    }

    @Test
    public void handlesNotOwned() {
        String msg = "Ad-free purchase not found";
        testListener(BillingClient.BillingResponseCode.ITEM_NOT_OWNED, msg);
        assertNull(Utils.isAdFree(activity));
    }

    @Test
    public void handlesIncompatableDevice() {
        String msg = "You're device is not compatible. If you purchased an upgrade please contact support for a refund";
        testListener(BillingClient.BillingResponseCode.FEATURE_NOT_SUPPORTED, msg);
        assertNull(Utils.isAdFree(activity));
    }

    @Test
    public void handlesDisconnect() {
        String msg = "Unable to connect to Google Play store. Please try again later";
        testListener(BillingClient.BillingResponseCode.SERVICE_DISCONNECTED, msg);
        assertNull(Utils.isAdFree(activity));
    }

    @Test
    public void handlesError() {
        String msg = "An error occurred. Please try again later";
        testListener(BillingClient.BillingResponseCode.ERROR, msg);
        assertNull(Utils.isAdFree(activity));
    }

    private void testListener(int code, String message) {
        testListener(code, message, null);
    }

    private void testListener(int code, String message, PurchaseHistoryRecord record) {
        // Setup mocks
        BillingResult result = BillingResult
                .newBuilder()
                .setResponseCode(code)
                .build();
        if(record == null) {
            record = mock(PurchaseHistoryRecord.class);
        }
        assertNull(Utils.isAdFree(activity));

        listener.onPurchaseHistoryResponse(result, Collections.singletonList(record));

        verify(preference, times(1)).setSummary("Click here to check your ad-free status");
        assertTrue(ShadowToast.showedToast(message));
    }
}
