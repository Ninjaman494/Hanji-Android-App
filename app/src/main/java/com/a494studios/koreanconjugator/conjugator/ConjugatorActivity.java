package com.a494studios.koreanconjugator.conjugator;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.a494studios.koreanconjugator.R;
import com.a494studios.koreanconjugator.StemQuery;
import com.a494studios.koreanconjugator.parsing.Server;
import com.a494studios.koreanconjugator.utils.Utils;
import com.apollographql.apollo.api.Response;

import java.util.List;

import io.reactivex.observers.DisposableObserver;

public class ConjugatorActivity extends AppCompatActivity {

    public static final String EXTRA_TERM = "term";
    private static final int SPINNER_ITEM = R.layout.item_spinner;

    private LinearLayout linearLayout;
    private ProgressBar loadingBar;

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

        linearLayout = findViewById(R.id.conjugator_linearLayout);
        loadingBar = findViewById(R.id.conjugator_progress);
        Spinner stemSpinner = findViewById(R.id.conjugator_stemSpinner);
        Spinner posSpinner = findViewById(R.id.conjugator_posSpinner);
        Spinner regSpinner = findViewById(R.id.conjugator_regSpinner);

        ArrayAdapter posAdapter = ArrayAdapter.createFromResource(this,
                R.array.pos, SPINNER_ITEM);
        ArrayAdapter regAdapter = ArrayAdapter.createFromResource(this,
                R.array.regularity, SPINNER_ITEM);

        posSpinner.setAdapter(posAdapter);
        regSpinner.setAdapter(regAdapter);

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

                            setLoading(false);
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

    private void setLoading(boolean loading){
        if(loading) {
            loadingBar.setVisibility(View.VISIBLE);
            linearLayout.setVisibility(View.GONE);
        } else {
            loadingBar.setVisibility(View.GONE);
            linearLayout.setVisibility(View.VISIBLE);
/*
            if(dataLoaded) {
                animationHandler.slideInConjugations();
            } else {
                animationHandler.animateListView();
            }*/
        }
    }
}
