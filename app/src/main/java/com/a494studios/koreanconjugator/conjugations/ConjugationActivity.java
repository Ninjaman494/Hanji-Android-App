package com.a494studios.koreanconjugator.conjugations;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.a494studios.koreanconjugator.CustomApplication;
import com.a494studios.koreanconjugator.R;
import com.a494studios.koreanconjugator.fragment.ConjugationFragment;
import com.a494studios.koreanconjugator.parsing.Server;
import com.a494studios.koreanconjugator.utils.BaseActivity;
import com.a494studios.koreanconjugator.utils.Utils;
import com.apollographql.apollo.api.Error;

import java.util.List;

public class ConjugationActivity extends BaseActivity {

    public static final String EXTRA_STEM = "stem";
    public static final String EXTRA_HONORIFIC = "honorific";
    public static final String EXTRA_ISADJ = "isAdj";
    public static final String EXTRA_REGULAR = "regular";

    private ConjugationAnimationHandler animationHandler;
    private RecyclerView recyclerView;
    private boolean dataLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conjugation);

        final String stem = getIntent().getStringExtra(EXTRA_STEM);
        final boolean honorific = getIntent().getBooleanExtra(EXTRA_HONORIFIC,false);
        final boolean isAdj = getIntent().getBooleanExtra(EXTRA_ISADJ,false);
        final Boolean regular = (Boolean)getIntent().getSerializableExtra(EXTRA_REGULAR);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setTitle("Conjugations");
            actionBar.setElevation(0);
        }

        setLoading(true);
        getConjugations(stem, honorific, isAdj, regular);
        TextView switchText = findViewById(R.id.conj_switchText);
        ((SwitchCompat)findViewById(R.id.conj_switch)).setOnCheckedChangeListener((compoundBtn, checked) -> {
            setLoading(true);
            if(checked) {
                switchText.setText(getString(R.string.honorific_forms));
                getConjugations(stem,true,isAdj, regular);
            } else {
                switchText.setText(getString(R.string.regular_forms));
                getConjugations(stem,false,isAdj, regular);
            }
        });

        View extendedBar = findViewById(R.id.conj_switchBar);
        recyclerView = findViewById(R.id.conj_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        animationHandler = new ConjugationAnimationHandler(extendedBar,recyclerView,this);
        animationHandler.setupScrollAnimations(layoutManager);
    }

    @SuppressLint("CheckResult")
    private void getConjugations(String stem, boolean honorific, boolean isAdj, Boolean regular) {
        AppCompatActivity activity = this;
        DialogInterface.OnClickListener listener = (dialogInterface, i) -> this.finish();

        ConjugationObserver observer = new ConjugationObserver(new ConjugationObserver.ConjugationObserverListener() {
            @Override
            public void onDataReceived(List<List<ConjugationFragment>> conjugations) {
                recyclerView.setAdapter(new ConjugationCardsAdapter(conjugations, stem, isAdj ? "Adjective" : "Verb"));
                setLoading(false);
                dataLoaded = true;
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                Utils.handleError(e, activity,4, listener);
            }

            @Override
            public void onApiError(Error e) {
                Utils.handleError(e, activity, listener);
            }
        });

        Server.doConjugationQuery(stem, honorific, isAdj, regular, (CustomApplication)getApplication())
        .subscribeWith(observer);
    }

    @Override
    public void onResume() {
        super.onResume();
        animationHandler.animateListView();
    }

    private void setLoading(boolean loading){
        if(loading) {
            findViewById(R.id.conj_progress).setVisibility(View.VISIBLE);
            findViewById(R.id.conj_list).setVisibility(View.GONE);
        } else {
            findViewById(R.id.conj_progress).setVisibility(View.GONE);
            findViewById(R.id.conj_list).setVisibility(View.VISIBLE);

            if(dataLoaded) {
                animationHandler.slideInConjugations();
            } else {
                animationHandler.animateListView();
            }
        }
    }
}
