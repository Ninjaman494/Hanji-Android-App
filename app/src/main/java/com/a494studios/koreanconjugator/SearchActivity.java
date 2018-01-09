package com.a494studios.koreanconjugator;

import android.app.SearchManager;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.a494studios.koreanconjugator.parsing.Conjugation;
import com.a494studios.koreanconjugator.parsing.Server;

import java.util.ArrayList;
import java.util.HashMap;

public class SearchActivity extends AppCompatActivity {

    ProgressBar progressBar;
    TextView loadingText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        progressBar = findViewById(R.id.main_loadingBar);
        loadingText = findViewById(R.id.main_loadingText);

        if (!Intent.ACTION_SEARCH.equals(getIntent().getAction())) {
            finishAffinity();
            return;
        }

        final String entry = getIntent().getStringExtra(SearchManager.QUERY).trim();
        if (getSupportActionBar() != null) getSupportActionBar().setTitle("Searching: " + entry);
        progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_IN);
        progressBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_IN);

        // Search
        if (entry.equals("")) {
            finishAffinity();
        } else if (MainActivity.isHangul(entry)) {
            doKoreanSearch(entry);
        } else {
            Server.requestEngDefinition(entry, getApplicationContext(), new Server.ServerListener() {
                @Override
                public void onResultReceived(ArrayList<Conjugation> conjugations, HashMap<String, String> searchResults) {
                    if (searchResults != null) {
                        loadingText.setText(R.string.main_results_found);
                        progressBar.setIndeterminate(false);
                        progressBar.setProgress(100);

                        if (searchResults.size() == 1) {
                            doKoreanSearch(searchResults.keySet().iterator().next()); // Get the first, and only, key in map
                        } else {
                            Intent intent = new Intent(getApplicationContext(), SearchResultsActivity.class);
                            intent.putExtra(SearchResultsActivity.EXTRA_RESULTS, searchResults);
                            intent.putExtra(SearchResultsActivity.EXTRA_SEARCHED, entry);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            startActivity(intent);
                        }
                    }
                }

                @Override
                public void onErrorOccurred(String errorMsg) {

                }
            });
        }
    }

    private void doKoreanSearch(final String entry) {
        Server.requestKoreanSearch(entry, getApplicationContext(), new Server.ServerListener() {
            @Override
            public void onResultReceived(final ArrayList<Conjugation> conjugations, HashMap<String, String> searchResults) {
                loadingText.setText(R.string.main_results_found);
                progressBar.setIndeterminate(false);
                progressBar.setProgress(100);

                if (conjugations != null) {
                    Intent intent = new Intent(getApplicationContext(), DisplayActivity.class);
                    intent.putExtra(DisplayActivity.EXTRA_CONJ, conjugations);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                } else if (searchResults != null) {
                    Intent intent = new Intent(getApplicationContext(), SearchResultsActivity.class);
                    intent.putExtra(SearchResultsActivity.EXTRA_RESULTS, searchResults);
                    intent.putExtra(SearchResultsActivity.EXTRA_SEARCHED, entry);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                }
            }

            @Override
            public void onErrorOccurred(String errorMsg) {
                System.out.println("error:" + errorMsg);
            }
        });
    }
}