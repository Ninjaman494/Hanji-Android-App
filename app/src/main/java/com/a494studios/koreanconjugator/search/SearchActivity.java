package com.a494studios.koreanconjugator.search;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Intent;
import android.graphics.PorterDuff;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.a494studios.koreanconjugator.CustomApplication;
import com.a494studios.koreanconjugator.R;
import com.a494studios.koreanconjugator.SearchQuery;
import com.a494studios.koreanconjugator.display.DisplayActivity;
import com.a494studios.koreanconjugator.parsing.Server;
import com.a494studios.koreanconjugator.search_results.SearchResultsActivity;
import com.a494studios.koreanconjugator.utils.ErrorDialogFragment;
import com.a494studios.koreanconjugator.utils.Logger;
import com.a494studios.koreanconjugator.utils.Utils;
import com.apollographql.apollo.api.Response;
import com.google.android.gms.ads.AdView;

import java.util.List;

import io.reactivex.observers.DisposableObserver;

public class SearchActivity extends AppCompatActivity {

    ProgressBar progressBar;
    TextView loadingText;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        progressBar = findViewById(R.id.main_loadingBar);
        loadingText = findViewById(R.id.main_loadingText);
        AdView adView = findViewById(R.id.search_adView);
        ((CustomApplication)getApplication()).handleAdCard(adView);

        if (!Intent.ACTION_SEARCH.equals(getIntent().getAction())) {
            finish();
            return;
        }

        if(getIntent().getStringExtra(SearchManager.QUERY) == null){
            ErrorDialogFragment.newInstance()
                    .setListener((dialogInterface, i) -> finish())
                    .show(getSupportFragmentManager(),"error_dialog");
            //Crashlytics.log("Query was null in SearchActivity");
            return;
        }
        final String entry = getIntent().getStringExtra(SearchManager.QUERY).trim();

        if (getSupportActionBar() != null) getSupportActionBar().setTitle("Searching: " + entry);
        progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_IN);
        progressBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_IN);

        // Log search event
        Logger.getInstance().logSearch(entry);

        // Search
        Server.doSearchQuery(entry, (CustomApplication)getApplication())
                .subscribeWith(new DisposableObserver<Response<SearchQuery.Data>>() {
                    @Override
                    public void onNext(Response<SearchQuery.Data> dataResponse) {
                        List<SearchQuery.Result> results = dataResponse.getData().search().results();
                        if(results.isEmpty()) {
                            NoResultsFragment.newInstance(entry, (dialogInterface, i) -> finish())
                                    .show(getSupportFragmentManager(), "no_results_dialog");
                        } else if(results.size() == 1){
                            goToDisplay(results.get(0).id());
                        } else {
                            goToSearchResults(entry);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        Utils.handleError(e, SearchActivity.this, 1, (dialogInterface, i) -> SearchActivity.this.onBackPressed());
                    }

                    @Override
                    public void onComplete() {
                        this.dispose();
                    }
                });
    }

    @Override
    public void onPause(){
        super.onPause();
        overridePendingTransition(0,0);
    }

    private void prepForIntent(){
        this.runOnUiThread(() -> {
            progressBar.setIndeterminate(false);
            progressBar.setProgress(100);
            loadingText.setText(R.string.main_results_found);
        });
    }

    private void goToSearchResults(String query){
        Intent intent = new Intent(getApplicationContext(), SearchResultsActivity.class);
        intent.putExtra(SearchResultsActivity.EXTRA_QUERY,query);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        prepForIntent();
    }

    private void goToDisplay(String id){
        Intent intent = new Intent(getApplicationContext(), DisplayActivity.class);
        intent.putExtra(DisplayActivity.EXTRA_ID, id);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        prepForIntent();
    }
}