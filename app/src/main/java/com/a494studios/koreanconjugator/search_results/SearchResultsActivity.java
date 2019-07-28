package com.a494studios.koreanconjugator.search_results;

import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.Intent;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;

import com.a494studios.koreanconjugator.R;
import com.a494studios.koreanconjugator.SearchQuery;
import com.a494studios.koreanconjugator.Utils;
import com.a494studios.koreanconjugator.display.DisplayActivity;
import com.a494studios.koreanconjugator.parsing.Server;
import com.a494studios.koreanconjugator.settings.SettingsActivity;
import com.a494studios.koreanconjugator.utils.ErrorDialogFragment;
import com.a494studios.koreanconjugator.utils.WordInfoView;
import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.linearlistview.LinearListView;

import org.jetbrains.annotations.NotNull;
import org.rm3l.maoni.Maoni;

import java.util.List;

import static com.eggheadgames.aboutbox.activity.AboutActivity.*;

public class SearchResultsActivity extends AppCompatActivity {

    public static final String EXTRA_QUERY = "query";

    private SearchResultsAdapter adapter;
    private RecyclerView listView;
    private boolean snackbarShown;
    private boolean overflowClicked;
    private String query;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        AdView adView = findViewById(R.id.search_results_adView);
        listView = findViewById(R.id.search_listView);
        snackbarShown = false;
        query = getIntent().getStringExtra(EXTRA_QUERY);
        if(query == null){ // Null check for extra
            ErrorDialogFragment.newInstance().setListener(new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    onBackPressed();
                }
            }).show(getSupportFragmentManager(),"error_dialog");
            //Crashlytics.log("Query was null in SearchResultsActivity");
            return;
        }

        adView.loadAd(new AdRequest.Builder().build());

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setTitle("Results: "+ query);
            actionBar.setElevation(0);
        }

        // use a linear layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        listView.setLayoutManager(layoutManager);
        listView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        fetchSearchResponse();

        /*listView.setOnItemClickListener(new LinearListView.OnItemClickListener() {
            @Override
            public void onItemClick(LinearListView parent, View view, int i, long itemId) {
                if(adapter != null) {
                    String id = adapter.getItem(i).id();
                    String term = adapter.getItem(i).term();
                    sendIntent(id, term);
                }
            }
        });*/
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
        animateListView();
    }

    private void fetchSearchResponse(){
        Server.doSearchQuery(query, new ApolloCall.Callback<SearchQuery.Data>() {
            @Override
            public void onResponse(@NotNull Response<SearchQuery.Data> response) {
                if(response.data() != null) {
                    adapter = new SearchResultsAdapter(response.data().search(),SearchResultsActivity.this);//new SearchAdapter(response.data().search());
                    SearchResultsActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            listView.setAdapter(adapter);
                            animateListView();
                        }
                    });
                }
            }

            @Override
            public void onFailure(@NotNull ApolloException e) {
                e.printStackTrace();
                handleError(e);
            }
        });
    }

    private void handleError(Exception error){
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

    private void sendIntent(String id, String term){
        Intent intent = new Intent(this,DisplayActivity.class);
        intent.putExtra(DisplayActivity.EXTRA_ID,id);
        intent.putExtra(DisplayActivity.EXTRA_TERM,term);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    private void animateListView() {
        Animation topBot = AnimationUtils.loadAnimation(this,R.anim.slide_top_to_bot);
        Animation botTop = AnimationUtils.loadAnimation(this, R.anim.slide_bot_to_top);
        //findViewById(R.id.search_results_extendedBar).startAnimation(topBot);
        listView.startAnimation(botTop);
    }

    private class SearchAdapter extends BaseAdapter {

        private List<SearchQuery.Result> results;

        SearchAdapter(SearchQuery.Search search) {
            this.results = search.results();
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            final SearchQuery.Result result = results.get(i);
            if (view == null) {
                view = getLayoutInflater().inflate(R.layout.item_search_result, viewGroup, false);
            }
            WordInfoView infoView = view.findViewById(R.id.item_search_result_word_info);
            infoView.setTerm(result.term());
            infoView.setPos(result.pos());
            infoView.setDefinitions(result.definitions());

            Button btn = view.findViewById(R.id.item_search_result_button);
            btn.setText(getString(R.string.see_entry));
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sendIntent(result.id(),result.term());
                }
            });
            return view;
        }

        @Override
        public int getCount() {
            return results.size();
        }

        @Override
        public SearchQuery.Result getItem(int i) {
            return results.get(i);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }
}