package com.a494studios.koreanconjugator;

import android.app.SearchManager;
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
import com.android.volley.NoConnectionError;
import com.eggheadgames.aboutbox.activity.AboutActivity;
import com.github.andkulikov.materialin.MaterialIn;

import java.util.ArrayList;
import java.util.HashMap;

public class SearchResultsActivity extends AppCompatActivity {

    public static final String EXTRA_RESULTS = "RESULTS";
    public static final String EXTRA_SEARCHED = "SEARCHED";
    private static final String SAVED_RESULT_CONJS = "RESULT_CONJS";

    private HashMap<String, String> results;
    private HashMap<String, ArrayList<Conjugation>> resultConjs;
    private SearchAdapter adapter;
    private boolean snackbarShown;
    private boolean overflowClicked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        ListView listView = findViewById(R.id.search_listView);
        snackbarShown = false;
        if(savedInstanceState != null){
            results = (HashMap<String,String>)savedInstanceState.getSerializable(EXTRA_RESULTS);
            resultConjs = (HashMap<String,ArrayList<Conjugation>>)savedInstanceState.getSerializable(SAVED_RESULT_CONJS);
        }else {
            results = (HashMap<String, String>) getIntent().getSerializableExtra(EXTRA_RESULTS);
            resultConjs = new HashMap<>();
        }

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setTitle("Multiple results: "+getIntent().getStringExtra(EXTRA_SEARCHED));
        }

        adapter = new SearchAdapter(results);
        listView.setAdapter(adapter);
        requestData();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final String term = adapter.getKey(i);
                ArrayList<Conjugation> conjugations = resultConjs.get(term);
                if(conjugations == null){
                    Server.requestConjugation(term, getBaseContext(), new Server.ServerListener() {
                        @Override
                        public void onResultReceived(ArrayList<Conjugation> conjugations, HashMap<String, String> searchResults) {
                            sendIntent(conjugations,results.get(term));
                        }

                        @Override
                        public void onErrorOccurred(Exception error) {
                            handleError(error);
                        }
                    });
                }else {
                    sendIntent(conjugations,results.get(term));
                }
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
        }else{
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
                snackbar = Snackbar.make(findViewById(R.id.disp_root), "Couldn't connect to server", Snackbar.LENGTH_INDEFINITE);
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

    private void sendIntent(ArrayList<Conjugation> conjugations,String definition){
        Intent intent = new Intent(this,DisplayActivity.class);
        if(definition.equals(getString(R.string.loading))){
            intent.putExtra(DisplayActivity.EXTRA_DEF,(String)null);
        }else {
            intent.putExtra(DisplayActivity.EXTRA_DEF, definition);
        }
        intent.putExtra(DisplayActivity.EXTRA_CONJ,conjugations);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }
}

class SearchAdapter extends BaseAdapter {

    private HashMap<String,String> results;
    private ArrayList<String> keyList;
    private static final int RESOURCE_ID = R.layout.item_conjugation;

    public SearchAdapter(HashMap<String,String> results) {
        this.results = results;
        keyList = new ArrayList<>(results.keySet());
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(RESOURCE_ID, viewGroup, false);
        }

        String key = keyList.get(i);
        String value = results.get(key);
        TextView typeView = view.findViewById(R.id.conjFormal);
        TextView conjView = view.findViewById(R.id.conjText);
        typeView.setText(key);
        conjView.setText(value);
        return view;
    }

    @Override
    public int getCount() {
        return results.size();
    }

    @Override
    public Object getItem(int i) {
        return results.get(keyList.get(i));
    }

    public String getKey(int i){
        return keyList.get(i);
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