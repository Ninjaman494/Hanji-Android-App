package com.a494studios.koreanconjugator;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.a494studios.koreanconjugator.parsing.Conjugation;
import com.a494studios.koreanconjugator.parsing.Server;
import com.github.andkulikov.materialin.MaterialIn;

import java.util.ArrayList;
import java.util.HashMap;

public class SearchResultsActivity extends AppCompatActivity {

    public static final String EXTRA_RESULTS = "RESULTS";
    public static final String EXTRA_SEARCHED = "SEARCHED";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        ListView listView = findViewById(R.id.search_listView);
        final HashMap<String,String> results = (HashMap<String,String>)getIntent().getSerializableExtra(EXTRA_RESULTS);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setTitle("Multiple results: "+getIntent().getStringExtra(EXTRA_SEARCHED));
        }

        final SearchAdapter adapter = new SearchAdapter(results);
        listView.setAdapter(adapter);

        final HashMap<String, ArrayList<Conjugation>> resultConjs = new HashMap<>();
        for(final String key : results.keySet()){
            Server.requestConjugation(key, this, new Server.ServerListener() {
                @Override
                public void onResultReceived(ArrayList<Conjugation> conjugations, HashMap<String, String> searchResults) {
                    resultConjs.put(key,conjugations);
                }

                @Override
                public void onErrorOccurred(String errorMsg) {

                }
            });

            if(results.get(key) == null){ // No definition, so we have to send a request for one.
                results.put(key,getString(R.string.loading));
                Server.requestKorDefinition(key, this, new Server.DefinitionListener() {
                    @Override
                    public void onDefinitionReceived(String definition) {
                        results.put(key,definition);
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onErrorOccurred(String errorMsg) {

                    }
                });
            }
        }

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
                        public void onErrorOccurred(String errorMsg) {

                        }
                    });
                }else {
                    sendIntent(conjugations,results.get(term));
                }
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        MaterialIn.animate(findViewById(R.id.search_listView), Gravity.BOTTOM, Gravity.BOTTOM);
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
