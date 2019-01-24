package com.a494studios.koreanconjugator;

import android.app.Application;

import com.apollographql.apollo.ApolloClient;
import okhttp3.OkHttpClient;

public class CustomApplication extends Application {
    private static final String SERVER_URL = com.a494studios.koreanconjugator.BuildConfig.SERVER_URL;
    private static ApolloClient apolloClient;

    // Called when the application is starting, before any other application objects have been created.
    @Override
    public void onCreate() {
        super.onCreate();

        //Build the Apollo Client
        OkHttpClient okHttpClient = new OkHttpClient.Builder().build();
        apolloClient =  ApolloClient.builder()
                .serverUrl(SERVER_URL)
                .okHttpClient(okHttpClient)
                .build();
    }

    public static ApolloClient getApolloClient() {
        return apolloClient;
    }

}
