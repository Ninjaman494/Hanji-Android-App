package com.a494studios.koreanconjugator;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.a494studios.koreanconjugator.display.ConjugationCard;
import com.a494studios.koreanconjugator.display.DisplayCardView;
import com.a494studios.koreanconjugator.parsing.Server;
import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.linearlistview.LinearListView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class ConjugationActivity extends AppCompatActivity {

    public static final String EXTRA_STEM = "stem";
    public static final String EXTRA_HONORIFIC = "honorific";
    public static final String EXTRA_ISADJ = "isAdj";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conjugation);

        final String stem = getIntent().getStringExtra(EXTRA_STEM);
        final boolean honorific = getIntent().getBooleanExtra(EXTRA_HONORIFIC,false);
        final boolean isAdj = getIntent().getBooleanExtra(EXTRA_ISADJ,false);

        setLoading(true);
        getConjugations(stem, honorific, isAdj);
        ((Switch)findViewById(R.id.conj_switch)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                setLoading(true);
                if(checked) {
                    getConjugations(stem,true,isAdj);
                } else {
                    getConjugations(stem,false,isAdj);
                }
            }
        });
    }

    private void getConjugations(String stem, boolean honorific, boolean isAdj) {
        Server.doConjugationQuery(stem, honorific, isAdj, new ApolloCall.Callback<ConjugationQuery.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<ConjugationQuery.Data> response) {
                        if(response.data() == null) {
                            return;
                        }

                        List<ConjugationQuery.Conjugation> conjugations = response.data().conjugations();
                        final TreeMap<String,List<ConjugationQuery.Conjugation>> conjMap = new TreeMap<>();
                        for(ConjugationQuery.Conjugation conjugation : conjugations){
                            String type = conjugation.type;
                            if(conjMap.keySet().contains(type)){
                                conjMap.get(type).add(conjugation);
                            }else{
                                List<ConjugationQuery.Conjugation> value = new ArrayList<>();
                                value.add(conjugation);
                                conjMap.put(type,value);
                            }
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                List<List<ConjugationQuery.Conjugation>> conjugations = new ArrayList<>(conjMap.values());
                                ((LinearListView)findViewById(R.id.conj_list)).setAdapter(new ConjugationsAdapter(conjugations));
                                setLoading(false);
                            }
                        });
                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {
                        e.printStackTrace();
                    }
                });
    }

    private void setLoading(boolean loading){
        if(loading) {
            findViewById(R.id.conj_progress).setVisibility(View.VISIBLE);
            findViewById(R.id.conj_list).setVisibility(View.GONE);
        } else {
            findViewById(R.id.conj_progress).setVisibility(View.GONE);
            findViewById(R.id.conj_list).setVisibility(View.VISIBLE);
        }
    }

    private class ConjugationsAdapter extends BaseAdapter {
        List<List<ConjugationQuery.Conjugation>> conjugations;

        ConjugationsAdapter(List<List<ConjugationQuery.Conjugation>> conjugations){
            this.conjugations = conjugations;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if(view != null){
                return view;
            }
            DisplayCardView conjView = new DisplayCardView(viewGroup.getContext());
            conjView.setCardBody(new ConjugationCard(conjugations.get(i)));

            view = conjView;
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            return view;
        }

        @Override
        public int getCount() {
            return conjugations.size();
        }

        @Override
        public Object getItem(int i) {
            return conjugations.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }
    }
}
