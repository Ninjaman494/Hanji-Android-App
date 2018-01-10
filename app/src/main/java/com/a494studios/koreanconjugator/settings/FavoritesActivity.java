package com.a494studios.koreanconjugator.settings;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.a494studios.koreanconjugator.R;
import com.a494studios.koreanconjugator.Utils;
import com.a494studios.koreanconjugator.parsing.Category;

import java.util.ArrayList;
import java.util.Map;


public class FavoritesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        ListView listView = findViewById(R.id.fav_listView);
        listView.setAdapter(new FavoritesAdapter(Utils.getFavorites(this)));
    }

    private class FavoritesAdapter extends BaseAdapter {

        private ArrayList<Map.Entry<String,Category[]>> entries;
        private static final int RESOURCE_ID = R.layout.item_setting_fav;

        public FavoritesAdapter(ArrayList<Map.Entry<String,Category[]>> entries) {
            this.entries = entries;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = LayoutInflater.from(viewGroup.getContext()).inflate(RESOURCE_ID, viewGroup, false);
            }
            Map.Entry<String,Category[]> entry = entries.get(i);
            Category[] categories = entry.getValue();
            TextView typeView = view.findViewById(R.id.fav_title);
            TextView formView = view.findViewById(R.id.fav_form);
            TextView formalityView = view.findViewById(R.id.fav_formality);
            TextView tenseView = view.findViewById(R.id.fav_tense);

            typeView.setText(entry.getKey());
            formView.setText(categories[1].printName());
            if(categories[0] != null) {
                formalityView.setText(categories[0].printName());
            }else{
                formalityView.setText("");
            }
            if(categories[2] != null) {
                tenseView.setText(categories[2].printName());
            }else{
                tenseView.setText("");
            }
            return view;
        }

        @Override
        public int getCount() {
            return entries.size();
        }

        @Override
        public Object getItem(int i) {
            return entries.get(i);
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
