package com.a494studios.koreanconjugator;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.linearlistview.LinearListView;

import java.util.ArrayList;
import java.util.Map.Entry;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FavoritesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FavoritesFragment extends Fragment {

    private LinearListView listView;
    private String stem;
    private boolean honorific;
    private boolean isAdj;

    public FavoritesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment FavoritesFragment.
     */
    public static FavoritesFragment newInstance() {
        return new FavoritesFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);
        listView = view.findViewById(R.id.favCard_list);
        Button moreBtn = view.findViewById(R.id.favCard_button);

        moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(),ConjugationActivity.class);
                intent.putExtra(ConjugationActivity.EXTRA_STEM,stem);
                intent.putExtra(ConjugationActivity.EXTRA_HONORIFIC,honorific);
                intent.putExtra(ConjugationActivity.EXTRA_ISADJ,isAdj);
                startActivity(intent);
            }
        });

        return view;
    }

    public void setEntries(final ArrayList<Entry<String,ConjugationQuery.Conjugation>> entries){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                listView.setAdapter(new FavoritesAdapter(entries));
            }
        });
    }

    public void setConjugationInfo(String stem, boolean honorific, boolean isAdj){
        this.stem = stem;
        this.honorific = honorific;
        this.isAdj = isAdj;
    }


    private class FavoritesAdapter extends BaseAdapter {

        private ArrayList<Entry<String,ConjugationQuery.Conjugation>> entries;
        private static final int RESOURCE_ID = R.layout.item_conjugation;

        public FavoritesAdapter(ArrayList<Entry<String,ConjugationQuery.Conjugation>> entries) {
            this.entries = entries;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = LayoutInflater.from(viewGroup.getContext()).inflate(RESOURCE_ID, viewGroup, false);
            }
            Entry<String,ConjugationQuery.Conjugation> entry = entries.get(i);
            TextView typeView = view.findViewById(R.id.conjFormal);
            TextView conjView = view.findViewById(R.id.conjText);
            typeView.setText(entry.getKey());
            conjView.setText(entry.getValue().conjugation());
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
