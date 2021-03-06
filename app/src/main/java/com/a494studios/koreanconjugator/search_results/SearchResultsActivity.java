package com.a494studios.koreanconjugator.search_results;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;

import com.a494studios.koreanconjugator.CustomApplication;
import com.a494studios.koreanconjugator.R;
import com.a494studios.koreanconjugator.SearchQuery;
import com.a494studios.koreanconjugator.utils.BaseActivity;
import com.a494studios.koreanconjugator.parsing.Server;
import com.a494studios.koreanconjugator.utils.RecyclerAnimationHandler;
import com.a494studios.koreanconjugator.utils.Utils;
import com.apollographql.apollo.api.Response;

import io.reactivex.observers.DisposableObserver;

public class SearchResultsActivity extends BaseActivity {

    public static final String EXTRA_QUERY = "query";

    private SearchResultsAdapter adapter;
    private boolean overflowClicked;
    private boolean dataLoaded = false;
    private int cursor = 0;
    private boolean loading = false;
    private RecyclerAnimationHandler animationHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        RecyclerView recyclerView = findViewById(R.id.search_listView);
        String query = getIntent().getStringExtra(EXTRA_QUERY);
        if(query == null){ // Null check for extra
            Exception exception = new Exception("Query was null in SearchResultsActivity");
            Utils.handleError(exception, this, 6, (dialogInterface, i) -> finish());
            return;
        }

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
                if (pos >= lastItemIndex && cursor != -1 && !loading) {
                    loading = true;
                    adapter.loadMore();
                }
            }
        });

        View extendedBar = findViewById(R.id.search_results_extendedBar);
        animationHandler = new RecyclerAnimationHandler(extendedBar, recyclerView, this);
        animationHandler.setupScrollAnimations(layoutManager);

        // Checks made to handle screen rotation
        if (cursor != -1 && !loading) {
            loading = true;
            fetchSearchResponse(query, cursor);
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

        if(dataLoaded) {
            animationHandler.animateListView(); // Called when returning to this activity from another one
        }
    }

    @SuppressLint("CheckResult")
    private void fetchSearchResponse(String query, int cur){
        Server.doSearchQuery(query, cur, (CustomApplication)getApplication())
                .subscribeWith(new DisposableObserver<Response<SearchQuery.Data>>() {
                    @Override
                    public void onNext(Response<SearchQuery.Data> dataResponse) {
                        assert dataResponse.getData() != null; // Check should be done in Server
                        adapter.addAll(dataResponse.getData().search().results());
                        String returnedCursor = dataResponse.getData().search().cursor();
                        if(returnedCursor != null) {
                            cursor = Integer.parseInt(dataResponse.getData().search().cursor());
                        } else {
                            cursor = -1; // No more results left to load
                        }
                        if(!dataLoaded) {
                            animationHandler.animateListView();
                            dataLoaded = true;
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        Utils.handleError(e, SearchResultsActivity.this, 3, (dialogInterface, i) -> SearchResultsActivity.this.finish());
                    }

                    @Override
                    public void onComplete() {
                        loading = false; // Finished loading data for this request
                        this.dispose();
                    }
                });
    }
}