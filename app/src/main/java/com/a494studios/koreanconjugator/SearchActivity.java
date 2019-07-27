package com.a494studios.koreanconjugator;

import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.a494studios.koreanconjugator.display.DisplayActivity;
import com.a494studios.koreanconjugator.parsing.Server;
import com.a494studios.koreanconjugator.utils.ErrorDialogFragment;
import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

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
            finish();
            return;
        }

        if(getIntent().getStringExtra(SearchManager.QUERY) == null){
            ErrorDialogFragment.newInstance().setListener(new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            }).show(getSupportFragmentManager(),"error_dialog");
            //Crashlytics.log("Query was null in SearchActivity");
            return;
        }
        final String entry = getIntent().getStringExtra(SearchManager.QUERY).trim();

        if (getSupportActionBar() != null) getSupportActionBar().setTitle("Searching: " + entry);
        progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_IN);
        progressBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_IN);

        // Search
        Server.doSearchQuery(entry, new ApolloCall.Callback<SearchQuery.Data>() {
            @Override
            public void onResponse(@NotNull Response<SearchQuery.Data> response) {
                List<SearchQuery.Result> results = response.data().search().results();
                if(results.isEmpty()) {
                    String title = getString(R.string.no_results_title);
                    String msg = getString(R.string.no_results_msg);
                    ErrorDialogFragment.newInstance(title, msg).setListener(new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    }).show(getSupportFragmentManager(),"error_dialog");
                } else if(results.size() == 1){
                    goToDisplay(results.get(0).id,results.get(0).term);
                } else {
                    goToSearchResults(entry);
                }
            }

            @Override
            public void onFailure(@NotNull ApolloException e) {
                e.printStackTrace();
            }
        });
    }

    private void handleError(Exception error){
        Utils.handleError(error, this, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface,int i) {
                finish(); // Exit this activity
            }
        });
    }

    @Override
    public void onPause(){
        super.onPause();
        overridePendingTransition(0,0);
    }

    private void prepForIntent(){
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setIndeterminate(false);
                progressBar.setProgress(100);
                loadingText.setText(R.string.main_results_found);
            }
        });
    }

    private void goToSearchResults(String query){
        Intent intent = new Intent(getApplicationContext(), SearchResultsActivity.class);
        intent.putExtra(SearchResultsActivity.EXTRA_QUERY,query);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        prepForIntent();
    }

    private void goToDisplay(String id, String term){
        Intent intent = new Intent(getApplicationContext(), DisplayActivity.class);
        intent.putExtra(DisplayActivity.EXTRA_ID, id);
        intent.putExtra(DisplayActivity.EXTRA_TERM, term);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        prepForIntent();
    }
}