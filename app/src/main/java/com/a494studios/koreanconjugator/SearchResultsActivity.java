package com.a494studios.koreanconjugator;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.a494studios.koreanconjugator.parsing.Conjugation;
import com.a494studios.koreanconjugator.parsing.Formality;

import java.util.ArrayList;
import java.util.HashMap;

public class SearchResultsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        HashMap<String,String> results = (HashMap<String,String>)getIntent().getSerializableExtra("search");
        ListView listView = findViewById(R.id.search_listView);
        listView.setAdapter(new SearchAdapter(results));
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
