package com.a494studios.koreanconjugator;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.a494studios.koreanconjugator.parsing.Conjugation;
import com.a494studios.koreanconjugator.parsing.Tense;
import com.linearlistview.LinearListView;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FavoritesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FavoritesFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PAST = "PAST_CONJUGATION";
    private static final String ARG_PRESENT = "PRESENT_CONJUGATION";
    private static final String ARG_FUTURE = "FUTURE_CONJUGATION";

    private Conjugation past;
    private Conjugation present;
    private Conjugation future;

    public FavoritesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param past Parameter 1.
     * @param present Parameter 2.
     * @return A new instance of fragment FavoritesFragment.
     */
    public static FavoritesFragment newInstance(Conjugation past, Conjugation present, Conjugation future) {
        FavoritesFragment fragment = new FavoritesFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PAST, past);
        args.putSerializable(ARG_PRESENT, present);
        args.putSerializable(ARG_FUTURE, future);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            past = (Conjugation)getArguments().getSerializable(ARG_PAST);
            present = (Conjugation)getArguments().getSerializable(ARG_PRESENT);
            future = (Conjugation)getArguments().getSerializable(ARG_FUTURE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);

        LinearListView listView = view.findViewById(R.id.favCard_list);
        listView.setAdapter(new FavoritesAdapter(past,present,future));
        return view;
    }


    private class FavoritesAdapter extends BaseAdapter {

        private Conjugation[] conjugations;
        private static final int RESOURCE_ID = R.layout.item_conjugation;

        public FavoritesAdapter(Conjugation past, Conjugation present, Conjugation future) {
            conjugations = new Conjugation[3];
            conjugations[0] = past;
            conjugations[1] = present;
            conjugations[2] = future;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = LayoutInflater.from(viewGroup.getContext()).inflate(RESOURCE_ID, viewGroup, false);
            }
            Conjugation c = conjugations[i];
            TextView typeView = view.findViewById(R.id.conjFormal);
            TextView conjView = view.findViewById(R.id.conjText);
            if(c.getTense() == Tense.NONE){
                typeView.setText(c.getForm().toString());
            }else {
                typeView.setText(c.getTense().toString());
            }
            conjView.setText(c.getConjugated());
            return view;
        }

        @Override
        public int getCount() {
            return conjugations.length;
        }

        @Override
        public Object getItem(int i) {
            return conjugations[i];
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
