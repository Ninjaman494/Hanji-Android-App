package com.a494studios.koreanconjugator;

import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.a494studios.koreanconjugator.parsing.Conjugation;
import com.a494studios.koreanconjugator.parsing.Server;
import com.a494studios.koreanconjugator.settings.SettingsActivity;
import com.a494studios.koreanconjugator.utils.ErrorDialogFragment;
import com.android.volley.NoConnectionError;
import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.crashlytics.android.Crashlytics;
import com.eggheadgames.aboutbox.activity.AboutActivity;
import com.github.andkulikov.materialin.MaterialIn;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.jetbrains.annotations.NotNull;
import org.rm3l.maoni.Maoni;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SearchResultsActivity extends AppCompatActivity {

    public static final String EXTRA_RESULTS = "RESULTS";
    public static final String EXTRA_SEARCHED = "SEARCHED";
    private static final String SAVED_RESULT_CONJS = "RESULT_CONJS";

    public static final String EXTRA_QUERY = "query";

    private HashMap<String, String> results;
    private HashMap<String, ArrayList<Conjugation>> resultConjs;
    private SearchAdapter adapter;
    private boolean snackbarShown;
    private boolean overflowClicked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        final ListView listView = findViewById(R.id.search_listView);
        AdView adView = findViewById(R.id.search_results_adView);
        snackbarShown = false;
        String query = getIntent().getStringExtra(EXTRA_QUERY);

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
            actionBar.setTitle("Multiple results: "+ query);
        }

        Server.doSearchQuery(query, new ApolloCall.Callback<SearchQuery.Data>() {
            @Override
            public void onResponse(@NotNull Response<SearchQuery.Data> response) {
                adapter = new SearchAdapter(response.data().search());
                SearchResultsActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listView.setAdapter(adapter);
                    }
                });
            }

            @Override
            public void onFailure(@NotNull ApolloException e) {
                e.printStackTrace();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String id = adapter.getItem(i).id();
                String term = adapter.getItem(i).term();
                sendIntent(id,term);
            }
        });
    }

    private void requestData(){
        for(final String key : results.keySet()){
            if(resultConjs.get(key) == null) { // No conjugation, so we have to request one.
                Server.requestConjugation(key, this, new Server.ServerListener() {
                    @Override
                    public void onResultReceived(ArrayList<Conjugation> conjugations, HashMap<String, String> searchResults) {
                        resultConjs.put(key, conjugations);
                    }

                    @Override
                    public void onErrorOccurred(Exception error) {
                        handleError(error);
                    }
                });
            }

            if(results.get(key) == null || results.get(key).equals(getString(R.string.loading))){ // No definition, so we have to send a request for one.
                results.put(key,getString(R.string.loading));
                Server.requestKorDefinition(key, this, new Server.DefinitionListener() {
                    @Override
                    public void onDefinitionReceived(String definition) {
                        results.put(key,definition);
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onErrorOccurred(Exception error) {
                        handleError(error);
                    }
                });
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
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
            AboutActivity.launch(this);
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
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putSerializable(EXTRA_RESULTS, results);
        savedInstanceState.putSerializable(SAVED_RESULT_CONJS,resultConjs);
        super.onSaveInstanceState(savedInstanceState);
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
        MaterialIn.animate(findViewById(R.id.search_listView), Gravity.BOTTOM, Gravity.BOTTOM);
    }

    private void handleError(Exception error){
        if (!snackbarShown) {
            Snackbar snackbar;
            if (error instanceof NoConnectionError) {
                snackbar = Snackbar.make(findViewById(R.id.search_listView), "Lost connection", Snackbar.LENGTH_INDEFINITE);
            } else {
                snackbar = Snackbar.make(findViewById(R.id.search_listView), "Couldn't connect to server", Snackbar.LENGTH_INDEFINITE);
                System.err.println(error.toString());
            }
            snackbar.setAction("Retry", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    requestData();
                    snackbarShown = false;
                }
            });
            snackbar.show();
            snackbarShown = true;
        }
    }

    private void sendIntent(String id, String term){
        Intent intent = new Intent(this,DisplayActivity.class);
        intent.putExtra(DisplayActivity.EXTRA_ID,id);
        intent.putExtra(DisplayActivity.EXTRA_TERM,term);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }
}

class SearchAdapter extends BaseAdapter {

    private List<SearchQuery.Search> results;
    private static final int RESOURCE_ID = R.layout.item_search_result;

    public SearchAdapter(List<SearchQuery.Search> results) {
        this.results = results;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(RESOURCE_ID, viewGroup, false);
        }

        TextView termView = view.findViewById(R.id.item_search_result_term);
        TextView posView = view.findViewById(R.id.item_search_result_pos);
        TextView def1View = view.findViewById(R.id.item_search_result_def1);
        TextView def2View = view.findViewById(R.id.item_search_result_def2);
        TextView def3View = view.findViewById(R.id.item_search_result_def3);

        SearchQuery.Search result = results.get(i);
        List<String> definitions = result.definitions();
        termView.setText(result.term());
        posView.setText(result.pos());
        def1View.setText(definitions.get(0));
        if(definitions.size() >= 2)
            def2View.setText(definitions.get(1));
        if(definitions.size() >= 3)
            def3View.setText(definitions.get(2));

        return view;
    }

    @Override
    public int getCount() {
        return results.size();
    }

    @Override
    public SearchQuery.Search getItem(int i) {
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