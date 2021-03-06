package com.a494studios.koreanconjugator.conjugator;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.widget.NestedScrollView;
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
import android.widget.TextView;

import com.a494studios.koreanconjugator.CustomApplication;
import com.a494studios.koreanconjugator.R;
import com.a494studios.koreanconjugator.StemQuery;
import com.a494studios.koreanconjugator.conjugations.ConjugationCardsAdapter;
import com.a494studios.koreanconjugator.conjugations.ConjugationObserver;
import com.a494studios.koreanconjugator.fragment.ConjugationFragment;
import com.a494studios.koreanconjugator.parsing.Server;
import com.a494studios.koreanconjugator.utils.BaseActivity;
import com.a494studios.koreanconjugator.utils.Utils;
import com.apollographql.apollo.api.Error;
import com.apollographql.apollo.api.Response;

import java.util.List;

import io.reactivex.observers.DisposableObserver;

public class ConjugatorActivity extends BaseActivity implements AdapterView.OnItemSelectedListener {

    public static final String EXTRA_TERM = "term";
    private static final int SPINNER_ITEM = R.layout.item_spinner;

    private RecyclerView recyclerView;
    private LinearLayout linearLayout;
    private ProgressBar loadingBar;
    private SwitchCompat honorificSwitch;
    private TextView honorificText;
    private Spinner stemSpinner;
    private Spinner posSpinner;
    private Spinner regSpinner;

    private ConjugatorAnimationHandler animationHandler;

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
        honorificSwitch = findViewById(R.id.conjugator_switch);
        honorificText = findViewById(R.id.conjugator_switchText);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setNestedScrollingEnabled(true);

        NestedScrollView scrollView = findViewById(R.id.conjugator_scrollView);
        View extendedBar = findViewById(R.id.conjugator_switchBar);
        animationHandler = new ConjugatorAnimationHandler(extendedBar, scrollView, recyclerView, this);
        animationHandler.setupScrollAnimations(layoutManager);

        stemSpinner = findViewById(R.id.conjugator_stemSpinner);
        posSpinner = findViewById(R.id.conjugator_posSpinner);
        regSpinner = findViewById(R.id.conjugator_regSpinner);

        ArrayAdapter<CharSequence> posAdapter = ArrayAdapter.createFromResource(this,
                R.array.pos, SPINNER_ITEM);
        ArrayAdapter<CharSequence> regAdapter = ArrayAdapter.createFromResource(this,
                R.array.regularity, SPINNER_ITEM);

        posSpinner.setAdapter(posAdapter);
        regSpinner.setAdapter(regAdapter);

        stemSpinner.setOnItemSelectedListener(this);
        posSpinner.setOnItemSelectedListener(this);
        regSpinner.setOnItemSelectedListener(this);

        fetchingStems = true;
        setLoading(true);

        honorificSwitch.setOnCheckedChangeListener((compoundBtn, checked) -> {
            setLoading(true);
            if(checked) {
                honorificText.setText(getString(R.string.honorific_forms));
            } else {
                honorificText.setText(getString(R.string.regular_forms));
            }
            getConjugations();
        });

        Server.doStemQuery(term, (CustomApplication)getApplication())
                .subscribeWith(new DisposableObserver<Response<StemQuery.Data>>() {
                    @Override
                    public void onNext(Response<StemQuery.Data> response) {
                        if (response.getData() == null) {
                            return;
                        }

                        List<String> stems = response.getData().stems();
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
                        Utils.handleError(e, activity,11,
                                (dialogInterface, i) -> activity.finish());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        animationHandler.animateListView();
    }

    @SuppressLint("CheckResult")
    private void getConjugations() {
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
        boolean honorific = honorificSwitch.isChecked();

        ConjugationObserver observer = new ConjugationObserver(new ConjugationObserver.ConjugationObserverListener() {
            @Override
            public void onDataReceived(List<List<ConjugationFragment>> conjugations) {
                recyclerView.setAdapter(new ConjugationCardsAdapter(conjugations, stem, isAdj ? "Adjective" : "Verb"));
                setLoading(false);
                isRefreshing = false;
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                Utils.handleError(e, ConjugatorActivity.this,10,
                        (dialogInterface, i) -> ConjugatorActivity.this.finish());
            }

            @Override
            public void onApiError(Error e) {
                Utils.handleError(e, ConjugatorActivity.this,
                        (dialogInterface, i) -> ConjugatorActivity.this.finish());
            }
        });

        Server.doConjugationQuery(stem, honorific, isAdj, regular, (CustomApplication)getApplication())
                .subscribeWith(observer);
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
            animationHandler.slideInConjugations();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        getConjugations();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
