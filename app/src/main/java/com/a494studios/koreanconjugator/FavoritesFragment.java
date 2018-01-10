package com.a494studios.koreanconjugator;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.a494studios.koreanconjugator.parsing.Conjugation;
import com.linearlistview.LinearListView;

import java.util.ArrayList;
import java.util.Map.Entry;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FavoritesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FavoritesFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_ENTRIES = "PAST_CONJUGATION";

    private ArrayList<Entry<String,Conjugation>> entries;
    public FavoritesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param entries Parameter 1.
     * @return A new instance of fragment FavoritesFragment.
     */
    public static FavoritesFragment newInstance(ArrayList<Entry<String,Conjugation>> entries) {
        FavoritesFragment fragment = new FavoritesFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_ENTRIES, entries);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            entries = (ArrayList<Entry<String,Conjugation>>)getArguments().getSerializable(ARG_ENTRIES);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);

        LinearListView listView = view.findViewById(R.id.favCard_list);
        listView.setAdapter(new FavoritesAdapter(entries));
        return view;
    }


    private class FavoritesAdapter extends BaseAdapter {

        private ArrayList<Entry<String,Conjugation>> entries;
        private static final int RESOURCE_ID = R.layout.item_conjugation;

        public FavoritesAdapter(ArrayList<Entry<String,Conjugation>> entries) {
            this.entries = entries;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = LayoutInflater.from(viewGroup.getContext()).inflate(RESOURCE_ID, viewGroup, false);
            }
            Entry<String,Conjugation> entry = entries.get(i);
            TextView typeView = view.findViewById(R.id.conjFormal);
            TextView conjView = view.findViewById(R.id.conjText);
            typeView.setText(entry.getKey());
            conjView.setText(entry.getValue().getConjugated());
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
