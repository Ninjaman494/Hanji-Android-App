package com.a494studios.koreanconjugator.search_results;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.a494studios.koreanconjugator.R;
import com.a494studios.koreanconjugator.SearchQuery;
import com.a494studios.koreanconjugator.Utils;
import com.a494studios.koreanconjugator.parsing.Server;
import com.a494studios.koreanconjugator.settings.SettingsActivity;
import com.a494studios.koreanconjugator.utils.ErrorDialogFragment;
import com.apollographql.apollo.api.Response;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.rm3l.maoni.Maoni;

import io.reactivex.observers.DisposableObserver;

import static com.eggheadgames.aboutbox.activity.AboutActivity.*;

public class SearchResultsActivity extends AppCompatActivity {

    public static final String EXTRA_QUERY = "query";

    private SearchResultsAdapter adapter;
    private boolean snackbarShown;
    private boolean overflowClicked;
    private SearchView searchView;
    private boolean dataLoaded = false;
    private String cursor = null;
    private boolean loading = false;
    private SearchResultsAnimationHandler animationHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        AdView adView = findViewById(R.id.search_results_adView);
        RecyclerView recyclerView = findViewById(R.id.search_listView);
        snackbarShown = false;
        String query = getIntent().getStringExtra(EXTRA_QUERY);
        if(query == null){ // Null check for extra
            ErrorDialogFragment.newInstance()
                    .setListener((dialogInterface, i) -> onBackPressed())
                    .show(getSupportFragmentManager(),"error_dialog");
            //Crashlytics.log("Query was null in SearchResultsActivity");
            return;
        }

        adView.loadAd(new AdRequest.Builder().build());

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setTitle("Results: "+ query);
            actionBar.setElevation(0);
        }

        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new SearchResultsAdapter(this) {
            @Override
            public void loadMore() {
                fetchSearchResponse(query, cursor);
            }
        };
        recyclerView.setAdapter(adapter);

        // Pagination
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(dx == 0 && dy == 0) {
                    return; // Bad event
                }

                int pos = layoutManager.findLastVisibleItemPosition();
                int lastItemIndex = adapter.getItemCount() - 1;

                // Don't make a new request if the previous one is still loading
                if(pos >= lastItemIndex && cursor != null && !loading) {
                    loading = true;
                    adapter.loadMore();
                }
            }
        });

        View extendedBar = findViewById(R.id.search_results_extendedBar);
        animationHandler = new SearchResultsAnimationHandler(extendedBar, recyclerView, this);
        animationHandler.setupScrollAnimations(layoutManager);

        fetchSearchResponse(query, null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.overflow_settings){
            startActivity(new Intent(getBaseContext(), SettingsActivity.class));
            return true;
        }else if(item.getItemId() == R.id.overflow_about){
            Utils.makeAboutBox(this);
            launch(this);
            return true;
        }else if(item.getItemId() == R.id.overflow_bug) {
            Maoni maoni = Utils.makeMaoniActivity(SearchResultsActivity.this);
            if(maoni != null){
                maoni.start(SearchResultsActivity.this);
            }
            return true;
        } else{
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        if(!overflowClicked) overridePendingTransition(0,0);
    }

    @Override
    public void onResume(){
        super.onResume();
        overflowClicked = false;
        if(searchView != null) {
            searchView.setQuery("", false);
            searchView.setIconified(true);
            searchView.clearFocus();
        }

        if(dataLoaded) {
            animationHandler.animateListView(); // Called when returning to this activity from another one
        }
    }

    @SuppressLint("CheckResult")
    private void fetchSearchResponse(String query,String cur){
        Server.doSearchQuery(query, cur)
                .subscribeWith(new DisposableObserver<Response<SearchQuery.Data>>() {
                    @Override
                    public void onNext(Response<SearchQuery.Data> dataResponse) {
                        assert dataResponse.data() != null; // Check should be done in Server
                        adapter.addAll(dataResponse.data().search().results());
                        cursor = dataResponse.data().search().cursor();
                        if(!dataLoaded) {
                            animationHandler.animateListView();
                            dataLoaded = true;
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        handleError(e);
                    }

                    @Override
                    public void onComplete() {
                        loading = false; // Finished loading data for this request
                        this.dispose();
                    }
                });
    }

    private void handleError(Throwable t){
        if (!snackbarShown) {
           /* Snackbar snackbar;
            if (error instanceof NoConnectionError) {
                snackbar = Snackbar.make(findViewById(R.id.search_listView), "Lost connection", Snackbar.LENGTH_INDEFINITE);
            } else {
                snackbar = Snackbar.make(findViewById(R.id.search_listView), "Couldn't connect to server", Snackbar.LENGTH_INDEFINITE);
                System.err.println(error.toString());
            }
            snackbar.setAction("Retry", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    fetchSearchResponse();
                    snackbarShown = false;
                }
            });
            snackbar.show();
            snackbarShown = true;*/
        }
    }
}