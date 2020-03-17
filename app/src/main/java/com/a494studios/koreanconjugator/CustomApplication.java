package com.a494studios.koreanconjugator;

import android.app.Application;
import android.view.View;

import com.a494studios.koreanconjugator.display.DisplayCardView;
import com.a494studios.koreanconjugator.display.cards.AdCard;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.cache.http.ApolloHttpCache;
import com.apollographql.apollo.cache.http.DiskLruHttpCacheStore;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.io.File;

import okhttp3.OkHttpClient;

public class CustomApplication extends Application {
    private static final String SERVER_URL = com.a494studios.koreanconjugator.BuildConfig.SERVER_URL;
    private static ApolloClient apolloClient;
    private static boolean isAdFree = true;

    // Called when the application is starting, before any other application objects have been created.
    @Override
    public void onCreate() {
        super.onCreate();

        //Directory where cached responses will be stored
        File file = this.getCacheDir();

        //Size in bytes of the cache
        int size = 1024*1024;

        //Create the http response cache store
        DiskLruHttpCacheStore cacheStore = new DiskLruHttpCacheStore(file,size);

        //Build the Apollo Client
        OkHttpClient okHttpClient = new OkHttpClient.Builder().build();
        apolloClient =  ApolloClient.builder()
                .serverUrl(SERVER_URL)
                .okHttpClient(okHttpClient)
                .httpCache(new ApolloHttpCache(cacheStore))
                .build();
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

}
