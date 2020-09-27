package com.a494studios.koreanconjugator;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;
import androidx.test.espresso.idling.CountingIdlingResource;

import com.a494studios.koreanconjugator.display.DisplayCardView;
import com.a494studios.koreanconjugator.display.cards.AdCard;
import com.a494studios.koreanconjugator.utils.Logger;
import com.a494studios.koreanconjugator.utils.Utils;
import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.cache.http.ApolloHttpCache;
import com.apollographql.apollo.cache.http.DiskLruHttpCacheStore;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;

import okhttp3.OkHttpClient;

public class CustomApplication extends MultiDexApplication implements PurchasesUpdatedListener {
    private static final String APP_ID = BuildConfig.ADMOB_KEY;
    private static final String SERVER_URL = com.a494studios.koreanconjugator.BuildConfig.SERVER_URL;

    private static boolean isAdFree = false;
    private static boolean billingConnected = false;
    private static BillingClient billingClient;
    private static CountingIdlingResource idler;

    // Called when the application is starting, before any other application objects have been created.
    @Override
    public void onCreate() {
        super.onCreate();

        // Setup ads
        MobileAds.initialize(this, APP_ID);

        // Check preferences first, to save us a billing request
        Boolean prefAdFree = Utils.isAdFree(getApplicationContext());
        if(prefAdFree != null) {
            System.out.println("Got Ad Free from prefs");
            isAdFree = prefAdFree;
        }

        // Setup Google Play Billing
        billingClient = BillingClient.newBuilder(this)
                .setListener(this)
                .enablePendingPurchases()
                .build();
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@NotNull BillingResult billingResult) {
                if (billingResult.getResponseCode() ==  BillingClient.BillingResponseCode.OK) {
                    System.out.println("Connected to Google  Play Billing");
                    billingConnected = true;

                    if(prefAdFree == null) {
                        // Nothing saved in preferences, check purchase history if an upgrade was bought
                        billingClient.queryPurchaseHistoryAsync(BillingClient.SkuType.INAPP, (result, list) -> {
                            if (result.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                                System.out.println("Got Ad Free from purchases");
                                isAdFree = list.size() > 0 && list.get(0).getSku().equals(Utils.SKU_AD_FREE);
                                Utils.setAdFree(getApplicationContext(), isAdFree);
                            } else {
                                System.out.println("Error occurred when fetching purchase history:" + result.getDebugMessage());
                            }
                        });
                    }
                } else {
                    System.out.println("Error connecting: " + billingResult.getResponseCode());
                    billingConnected = false;
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                System.out.println("Disconnected from Google Play Billing");
                billingConnected = false;
            }
        });

        // Setup Firebase Analytics
        Logger.initialize(FirebaseAnalytics.getInstance(this));
    }

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        MultiDex.install(this);
    }

    public String getServerUrl() {
        return SERVER_URL;
    }

    public static void handleAdCard(DisplayCardView cardView, String adId){
        if(isAdFree) {
            cardView.setVisibility(View.GONE);
        } else {
            cardView.setCardBody(new AdCard(adId));
        }
    }

    public static void handleAdCard(AdView adView) {
        if(isAdFree) {
            adView.setVisibility(View.GONE);
        } else {
            adView.loadAd(new AdRequest.Builder().build());
        }
    }

    @Override
    public void onPurchasesUpdated(@NotNull BillingResult billingResult, @Nullable List<Purchase> list) {
        if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK
                && !list.isEmpty() && list.get(0).getSku().equals(Utils.SKU_AD_FREE)) {

            if(list.get(0).getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
                AcknowledgePurchaseParams consumeParams = AcknowledgePurchaseParams
                        .newBuilder()
                        .setPurchaseToken(list.get(0).getPurchaseToken())
                        .build();

                // Weird bug requires this not be a lambda
                AcknowledgePurchaseResponseListener listener = new AcknowledgePurchaseResponseListener() {
                    @Override
                    public void onAcknowledgePurchaseResponse(@NonNull BillingResult billingResult) {
                        String toastMsg = "";
                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                            System.out.println("Purchase Acknowledged");
                            isAdFree = true;
                            Utils.setAdFree(CustomApplication.this, true);
                            toastMsg = "Upgrade Success! Please restart Hanji for the upgrade to take affect";
                        } else {
                            toastMsg = "An error occurred with your purchase, please contact support";
                        }
                        Toast.makeText(CustomApplication.this, toastMsg, Toast.LENGTH_LONG).show();
                    }
                };

                billingClient.acknowledgePurchase(consumeParams, listener);
            } else if (list.get(0).getPurchaseState() == Purchase.PurchaseState.PENDING) {
                Toast.makeText(this, "Payment pending. Hanji will be upgraded once payment is received", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "An error occurred with your purchase, please contact support", Toast.LENGTH_LONG).show();
            }
        }
    }

    public static boolean isBillingConnected() {
        return billingConnected;
    }

    public static BillingClient getBillingClient() {
        return billingClient;
    }

    public static CountingIdlingResource getIdler(){
        if(idler == null) {
            idler = new CountingIdlingResource("idlingResource");
        }
        return idler;
    }
}
