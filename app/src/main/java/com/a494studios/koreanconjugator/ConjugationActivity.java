package com.a494studios.koreanconjugator;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.a494studios.koreanconjugator.parsing.Server;
import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;

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

        Server.doConjugationQuery(stem, honorific, isAdj, new ApolloCall.Callback<ConjugationQuery.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<ConjugationQuery.Data> response) {
                        System.out.println(response.data());

                        List<ConjugationQuery.Conjugation> conjugations = response.data().conjugation();
                        TreeMap<String,List<ConjugationQuery.Conjugation>> conjMap = new TreeMap<>();
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

                        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        for(TreeMap.Entry<String, List<ConjugationQuery.Conjugation>> entry : conjMap.entrySet()){
                            ConjugationCardFragment conjFrag = ConjugationCardFragment.newInstance();
                            conjFrag.setHeading(entry.getKey());
                            conjFrag.setConjugations(entry.getValue());
                            fragmentTransaction.add(R.id.conj_root,conjFrag);
                        }
                        fragmentTransaction.commit();
                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {
                        e.printStackTrace();
                    }
                });
    }
}
