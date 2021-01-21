package com.a494studios.koreanconjugator;

import android.content.res.AssetManager;

import androidx.test.core.app.ApplicationProvider;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MockReader {
    public static final String FAVORITES = "favorites_response.json";
    public static final String ENTRY = "entry_response.json";
    public static final String CONJUGATIONS = "conjugations_response.json";
    public static final String CONJUGATIONS_HONORIFIC = "conjugations_honorific_response.json";
    public static final String WOD = "wod_response.json";
    public static final String SEARCH_RESULTS = "search_results.json";


    public static String readStringFromFile(String fileName) {
        StringBuilder builder = new StringBuilder();

        try {
            InputStream inputStream = ApplicationProvider.getApplicationContext().getAssets().open(fileName, AssetManager.ACCESS_BUFFER);
            DataInputStream in = new DataInputStream(inputStream);
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            String line;
            while((line = reader.readLine()) != null) {
                builder.append(line);
            }

           in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return builder.toString();
    }
}
