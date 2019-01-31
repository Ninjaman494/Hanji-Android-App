package com.a494studios.koreanconjugator.settings;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
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

import com.a494studios.koreanconjugator.R;
import com.a494studios.koreanconjugator.Utils;
import com.a494studios.koreanconjugator.parsing.Category;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Map;


public class FavoritesActivity extends AppCompatActivity implements AddFavoriteFragment.AddFavoriteFragmentListener
        ,RenameFavoriteFragment.RenameFavoriteFragmentListener{

    FavoritesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Favorites");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        ListView listView = findViewById(R.id.fav_listView);
        //adapter = new FavoritesAdapter(Utils.getFavorites(this));
        listView.setAdapter(adapter);
        registerForContextMenu(listView);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        if( item.getItemId() == R.id.context_delete) {
            ArrayList<Map.Entry<String,Category[]>> data = adapter.remove(adapter.getItem(info.position));
            //Utils.setFavorites(data,this);
            adapter.notifyDataSetChanged();
            return true;
        }else if(item.getItemId() == R.id.context_rename) {
            RenameFavoriteFragment frag = RenameFavoriteFragment.newInstance(info.position);
            frag.show(getSupportFragmentManager(),"rename_frag");
            return true;
        }else{
            return super.onContextItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.fav_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.menu_add) {
            AddFavoriteFragment.newInstance().show(getSupportFragmentManager(),"add_fav_frag");
            return true;
        }else if(item.getItemId() == android.R.id.home) {
            this.onBackPressed();
            return true;
        }else{
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onFavoriteAdded(Map.Entry<String, Category[]> entry) {
        ArrayList<Map.Entry<String,Category[]>> data = adapter.add(entry);
        //Utils.setFavorites(data,this);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onRenameSelected(String newName, int position) {
        Map.Entry<String, Category[]> entry = adapter.getItem(position);
        Map.Entry<String, Category[]> newEntry = new AbstractMap.SimpleEntry<>(newName, entry.getValue());
        ArrayList<Map.Entry<String, Category[]>> data = adapter.replace(entry, newEntry);
        //Utils.setFavorites(data, this);
        adapter.notifyDataSetChanged();
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
        public Map.Entry<String,Category[]> getItem(int i) {
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

        public ArrayList<Map.Entry<String,Category[]>> remove(Map.Entry<String,Category[]> entry){
            entries.remove(entry);
            return entries;
        }

        public ArrayList<Map.Entry<String,Category[]>> add(Map.Entry<String,Category[]> entry){
            entries.add(entry);
            return entries;
        }

        public ArrayList<Map.Entry<String,Category[]>> replace(Map.Entry<String,Category[]> old, Map.Entry<String,Category[]> newEntry){
            entries.set(entries.indexOf(old),newEntry);
            return entries;
        }
    }
}