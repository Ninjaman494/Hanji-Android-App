package com.a494studios.koreanconjugator.conjugator;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.a494studios.koreanconjugator.ConjugationQuery;
import com.a494studios.koreanconjugator.R;
import com.a494studios.koreanconjugator.StemQuery;
import com.a494studios.koreanconjugator.conjugations.ConjugationCardsAdapter;
import com.a494studios.koreanconjugator.parsing.Server;
import com.a494studios.koreanconjugator.utils.Utils;
import com.apollographql.apollo.api.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import io.reactivex.observers.DisposableObserver;

public class ConjugatorActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    public static final String EXTRA_TERM = "term";
    private static final int SPINNER_ITEM = R.layout.item_spinner;

    private RecyclerView recyclerView;
    private LinearLayout linearLayout;
    private ProgressBar loadingBar;
    private Spinner stemSpinner;
    private Spinner posSpinner;
    private Spinner regSpinner;

    // Used to prevent multiple refreshes for the same data
    private boolean isRefreshing = false;
    // Used to hide fields when first fetching possible stems
    private boolean fetchingStems = false;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conjugator);

        final String term = getIntent().getStringExtra(EXTRA_TERM);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Conjugator");
            actionBar.setElevation(0);
        }

        recyclerView = findViewById(R.id.conjugator_list);
        linearLayout = findViewById(R.id.conjugator_linearLayout);
        loadingBar = findViewById(R.id.conjugator_progress);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setNestedScrollingEnabled(false);

        stemSpinner = findViewById(R.id.conjugator_stemSpinner);
        posSpinner = findViewById(R.id.conjugator_posSpinner);
        regSpinner = findViewById(R.id.conjugator_regSpinner);

        ArrayAdapter posAdapter = ArrayAdapter.createFromResource(this,
                R.array.pos, SPINNER_ITEM);
        ArrayAdapter regAdapter = ArrayAdapter.createFromResource(this,
                R.array.regularity, SPINNER_ITEM);

        posSpinner.setAdapter(posAdapter);
        regSpinner.setAdapter(regAdapter);

        stemSpinner.setOnItemSelectedListener(this);
        posSpinner.setOnItemSelectedListener(this);
        regSpinner.setOnItemSelectedListener(this);

        fetchingStems = true;
        setLoading(true);

        Server.doStemQuery(term)
                .subscribeWith(new DisposableObserver<Response<StemQuery.Data>>() {
                    @Override
                    public void onNext(Response<StemQuery.Data> response) {
                        if (response.data() == null) {
                            return;
                        }

                        List<String> stems = response.data().stems();
                        runOnUiThread(() -> {
                            ArrayAdapter<String> adapter =
                                    new ArrayAdapter<>(ConjugatorActivity.this, SPINNER_ITEM, stems);
                            stemSpinner.setAdapter(adapter);
                            stemSpinner.setEnabled(true);

                            fetchingStems = false;
                        });
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        AppCompatActivity activity = ConjugatorActivity.this;
                        Utils.handleError(e, activity,4,
                                (dialogInterface, i) -> activity.onBackPressed());
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    @SuppressLint("CheckResult")
    private void getConjugations(String stem, boolean honorific, boolean isAdj, Boolean regular) {
        Server.doConjugationQuery(stem, honorific, isAdj, regular)
                .subscribeWith(new DisposableObserver<Response<ConjugationQuery.Data>>() {
                    @Override
                    public void onNext(Response<ConjugationQuery.Data> response) {
                        if(response.data() == null) {
                            return;
                        }

                        List<ConjugationQuery.Conjugation> conjugations = response.data().conjugations();
                        final TreeMap<String,List<ConjugationQuery.Conjugation>> conjMap = new TreeMap<>();
                        for(ConjugationQuery.Conjugation conjugation : conjugations){
                            String type = conjugation.type();
                            if(conjMap.containsKey(type)){
                                conjMap.get(type).add(conjugation);
                            }else{
                                List<ConjugationQuery.Conjugation> value = new ArrayList<>();
                                value.add(conjugation);
                                conjMap.put(type,value);
                            }
                        }

                        List<List<ConjugationQuery.Conjugation>> conjugations1 = new ArrayList<>(conjMap.values());
                        recyclerView.setAdapter(new ConjugationCardsAdapter(conjugations1, stem, isAdj ? "Adjective" : "Verb"));
                        setLoading(false);
                        isRefreshing = false;
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        Utils.handleError(e, ConjugatorActivity.this,4,
                                (dialogInterface, i) -> ConjugatorActivity.this.onBackPressed());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void setLoading(boolean loading){
        if(loading) {
            loadingBar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);

            if(fetchingStems) {
                linearLayout.setVisibility(View.INVISIBLE);
            }

        } else {
            loadingBar.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            linearLayout.setVisibility(View.VISIBLE);
/*
            if(dataLoaded) {
                animationHandler.slideInConjugations();
            } else {
                animationHandler.animateListView();
            }*/
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        Object stemItem = stemSpinner.getSelectedItem();
        Object posItem = posSpinner.getSelectedItem();
        Object regItem = regSpinner.getSelectedItem();

        // Already refreshing conjugations, wait for that to finish first, or not
        // all spinners have finished being set up
        if (isRefreshing || stemItem == null || posItem == null || regItem == null) {
            return;
        }

        isRefreshing = true;
        setLoading(true);

        String stem = stemItem.toString();
        boolean isAdj = posItem.toString().equals("Adjective");
        boolean regular = regItem.toString().equals("Regular verb/adjective");
        getConjugations(stem, false, isAdj, regular);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
