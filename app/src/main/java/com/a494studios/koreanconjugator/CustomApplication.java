package com.a494studios.koreanconjugator;

import android.app.Application;
import android.view.View;

import androidx.annotation.Nullable;

import com.a494studios.koreanconjugator.display.DisplayCardView;
import com.a494studios.koreanconjugator.display.cards.AdCard;
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

import java.io.File;
import java.util.List;

import okhttp3.OkHttpClient;

public class CustomApplication extends Application implements PurchasesUpdatedListener {
    private static final String APP_ID = BuildConfig.ADMOB_KEY;
    private static final String SERVER_URL = com.a494studios.koreanconjugator.BuildConfig.SERVER_URL;
    private static ApolloClient apolloClient;
    private static boolean isAdFree = false;
    private static boolean billingConnected = false;
    private static BillingClient billingClient;

    // Called when the application is starting, before any other application objects have been created.
    @Override
    public void onCreate() {
        super.onCreate();

        // Setup ads
        MobileAds.initialize(this, APP_ID);

        // Setup response cache for Apollo
        File file = this.getCacheDir();
        int size = 1024*1024;
        DiskLruHttpCacheStore cacheStore = new DiskLruHttpCacheStore(file,size);

        //Build the Apollo Client
        OkHttpClient okHttpClient = new OkHttpClient.Builder().build();
        apolloClient =  ApolloClient.builder()
                .serverUrl(SERVER_URL)
                .okHttpClient(okHttpClient)
                .httpCache(new ApolloHttpCache(cacheStore))
                .build();

        // Setup Google Play Billing
        billingClient = BillingClient.newBuilder(this)
                .setListener(this)
                .enablePendingPurchases()
                .build();
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() ==  BillingClient.BillingResponseCode.OK) {
                    System.out.println("Connected to Google  Play Billing");
                    billingConnected = true;
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
    }

    public static ApolloClient getApolloClient() {
        return apolloClient;
    }

    public static void handleAdCard(DisplayCardView cardView){
        if(isAdFree) {
            cardView.setVisibility(View.GONE);
        } else {
            cardView.setCardBody(new AdCard());
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
    public void onPurchasesUpdated(BillingResult billingResult, @Nullable List<Purchase> list) {
        System.out.println("Bought stuff?");
    }

    public static boolean isBillingConnected() {
        return billingConnected;
    }

    public static BillingClient getBillingClient() {
        return billingClient;
    }
}
