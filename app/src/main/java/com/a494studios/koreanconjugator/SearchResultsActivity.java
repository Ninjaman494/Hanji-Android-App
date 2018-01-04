package com.a494studios.koreanconjugator;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.a494studios.koreanconjugator.parsing.Conjugation;
import com.a494studios.koreanconjugator.parsing.Formality;
import com.a494studios.koreanconjugator.parsing.Server;

import java.util.ArrayList;
import java.util.HashMap;

public class SearchResultsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        HashMap<String,String> results = (HashMap<String,String>)getIntent().getSerializableExtra("search");
        ListView listView = findViewById(R.id.search_listView);
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
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ArrayList<Conjugation> conjugations = resultConjs.get(adapter.getKey(i));
                if(conjugations == null){
                    Server.requestConjugation(adapter.getKey(i), getBaseContext(), new Server.ServerListener() {
                        @Override
                        public void onResultReceived(ArrayList<Conjugation> conjugations, HashMap<String, String> searchResults) {
                            Intent intent = new Intent(getApplicationContext(),DisplayActivity.class);
                            intent.putExtra(DisplayActivity.EXTRA_CONJ,conjugations);
                            startActivity(intent);
                        }

                        @Override
                        public void onErrorOccurred(String errorMsg) {

                        }
                    });
                }else {
                    Intent intent = new Intent(getApplicationContext(), DisplayActivity.class);
                    intent.putExtra(DisplayActivity.EXTRA_CONJ,conjugations );
                    startActivity(intent);
                }
            }
        });
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
