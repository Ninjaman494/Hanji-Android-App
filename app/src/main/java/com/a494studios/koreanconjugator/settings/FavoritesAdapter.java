package com.a494studios.koreanconjugator.settings;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.a494studios.koreanconjugator.R;
import com.a494studios.koreanconjugator.parsing.Favorite;
import com.a494studios.koreanconjugator.utils.Utils;

import java.util.ArrayList;

class FavoritesAdapter extends BaseAdapter {

    private ArrayList<Favorite> entries;
    private static final int RESOURCE_ID = R.layout.item_setting_fav;

    FavoritesAdapter(ArrayList<Favorite> entries) {
        this.entries = entries;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(RESOURCE_ID, viewGroup, false);
        }

        Favorite favorite = entries.get(i);
        ((TextView)view.findViewById(R.id.item_fav_title)).setText(favorite.getName());
        ((TextView)view.findViewById(R.id.item_fav_subtitle)).setText(Utils.toTitleCase(favorite.getConjugationName()));
        if(favorite.isHonorific()) {
            view.findViewById(R.id.item_fav_honorific).setVisibility(View.VISIBLE);
        } else {
            view.findViewById(R.id.item_fav_honorific).setVisibility(View.GONE);
        }
        return view;
    }

    @Override
    public int getCount() {
        return entries.size();
    }

    @Override
    public Favorite getItem(int i) {
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

    public ArrayList<Favorite> remove(Favorite entry){
        entries.remove(entry);
        return entries;
    }

    public ArrayList<Favorite> add(Favorite entry){
        entries.add(entry);
        return entries;
    }
}
