package com.a494studios.koreanconjugator;

import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.a494studios.koreanconjugator.parsing.Conjugation;
import com.a494studios.koreanconjugator.parsing.Server;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

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
        AdView adView = findViewById(R.id.search_adView);
        adView.loadAd(new AdRequest.Builder().build());

        if (!Intent.ACTION_SEARCH.equals(getIntent().getAction())) {
            this.onBackPressed();
            return;
        }

        final String entry = getIntent().getStringExtra(SearchManager.QUERY).trim();
        if (getSupportActionBar() != null) getSupportActionBar().setTitle("Searching: " + entry);
        progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_IN);
        progressBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_IN);

        // Search
        if (Utils.isHangul(entry)) {
            Crashlytics.log("Korean search: "+entry);
            Crashlytics.setString("searchTerm",entry);
            doKoreanSearch(entry);
        } else if(entry.matches("[A-Za-z ]+")){ // Check if String in English
            Crashlytics.log("English search: "+entry);
            Crashlytics.setString("searchTerm",entry);
            Server.requestEngDefinition(entry, getApplicationContext(), new Server.ServerListener() {
                @Override
                public void onResultReceived(ArrayList<Conjugation> conjugations, HashMap<String, String> searchResults) {
                    if(searchResults != null) {
                        if (searchResults.size() == 1 || Utils.getEnglishLuck(getBaseContext())) {
                            doKoreanSearch(searchResults.keySet().iterator().next()); // Get the first key in map
                        } else if(searchResults.isEmpty()){
                            new AlertDialog.Builder(SearchActivity.this)
                                    .setTitle(R.string.no_results_title)
                                    .setMessage(R.string.no_results_msg)
                                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                            onBackPressed();
                                        }
                                    })
                                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                                        @Override
                                        public void onCancel(DialogInterface dialogInterface) {
                                            onBackPressed();
                                        }
                                    })
                                    .create().show();
                        } else {
                            goToSearchResults(searchResults,entry);
                        }
                    }
                }

                @Override
                public void onErrorOccurred(Exception error) {
                    handleError(error);
                }
            });
        }else{
            Crashlytics.log("Invalid Search: "+entry);
            Crashlytics.setString("searchTerm",entry);
            Toast.makeText(getBaseContext(),"Input not Valid",Toast.LENGTH_LONG).show();
            onBackPressed();
        }
    }

    private void doKoreanSearch(final String entry) {
        Server.requestKoreanSearch(entry, getApplicationContext(), new Server.ServerListener() {
            @Override
            public void onResultReceived(final ArrayList<Conjugation> conjugations, HashMap<String, String> searchResults) {
                if (conjugations != null) {
                    goToDisplay(conjugations);
                } else if (searchResults != null) {
                    if(Utils.getKoreanLuck(getApplicationContext())){
                        requestConjugations(searchResults.keySet().iterator().next());
                    }else{
                        goToSearchResults(searchResults,entry);
                    }
                }
            }

            @Override
            public void onErrorOccurred(Exception error) {
                handleError(error);
            }
        });
    }

    private void requestConjugations(String word){
        Server.requestConjugation(word, this, new Server.ServerListener() {
            @Override
            public void onResultReceived(ArrayList<Conjugation> conjugations, HashMap<String, String> searchResults) {
                goToDisplay(conjugations);
            }
            @Override
            public void onErrorOccurred(Exception error) {
                handleError(error);
            }
        });
    }

    private void handleError(Exception error){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if(error instanceof NoConnectionError){
            builder.setMessage("Check your network settings and try again")
                    .setTitle("Can't load results");

        } else if(error instanceof ParseError) {
            builder.setMessage("A response was given that we couldn't understand")
                    .setTitle("Can't read results");
        }else{
            builder.setMessage("Try again later or contact support")
                    .setTitle("Something went wrong");
            System.err.println(error.toString());
        }
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                onBackPressed(); // Go back to previous activity
            }
        });
        builder.create().show();
    }

    @Override
    public void onPause(){
        super.onPause();
        overridePendingTransition(0,0);
    }

    private void prepForIntent(){
        progressBar.setIndeterminate(false);
        progressBar.setProgress(100);
        loadingText.setText(R.string.main_results_found);
    }

    private void goToSearchResults(HashMap<String,String> searchResults, String entry){
        Intent intent = new Intent(getApplicationContext(), SearchResultsActivity.class);
        intent.putExtra(SearchResultsActivity.EXTRA_RESULTS, searchResults);
        intent.putExtra(SearchResultsActivity.EXTRA_SEARCHED,entry);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        prepForIntent();
    }

    private void goToDisplay(ArrayList<Conjugation> conjugations){
        Intent intent = new Intent(getApplicationContext(), DisplayActivity.class);
        intent.putExtra(DisplayActivity.EXTRA_CONJ, conjugations);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        prepForIntent();
    }
}