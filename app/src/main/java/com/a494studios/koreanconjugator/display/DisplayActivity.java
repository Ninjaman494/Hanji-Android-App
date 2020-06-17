package com.a494studios.koreanconjugator.display;

import android.annotation.SuppressLint;
import androidx.appcompat.app.ActionBar;
import android.os.Bundle;

import android.util.Pair;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.a494studios.koreanconjugator.ConjugationQuery;
import com.a494studios.koreanconjugator.CustomApplication;
import com.a494studios.koreanconjugator.EntryQuery;
import com.a494studios.koreanconjugator.R;
import com.a494studios.koreanconjugator.utils.BaseActivity;
import com.a494studios.koreanconjugator.utils.Utils;
import com.a494studios.koreanconjugator.parsing.Favorite;
import com.a494studios.koreanconjugator.parsing.Server;
import com.a494studios.koreanconjugator.utils.ScrollViewAnimationHandler;
import com.a494studios.koreanconjugator.utils.ErrorDialogFragment;
import com.apollographql.apollo.api.Response;
import com.crashlytics.android.Crashlytics;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;

public class DisplayActivity extends BaseActivity {

    public static final String EXTRA_ID = "id";

    private boolean isLoading;
    private EntryQuery.Entry entry;
    private ScrollViewAnimationHandler animationHandler;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        String id = getIntent().getStringExtra(EXTRA_ID);

        // Make sure extras were passed
        if(id == null){
            ErrorDialogFragment.newInstance()
                    .setListener((dialogInterface, i) -> onBackPressed())
                    .show(getSupportFragmentManager(),"error_dialog");
            Crashlytics.log("ID was null in DisplayActivity");
            return;
        }

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setTitle("");
            actionBar.setElevation(0);
        }

        // Display progress bar until data is loaded
        displayLoading(true);

        // Setting up Ad Card
        CustomApplication.handleAdCard((DisplayCardView)findViewById(R.id.disp_adCard));

        // Creating DisplayObserver
        View rootView = findViewById(android.R.id.content);
        DisplayObserver observer = new DisplayObserver(rootView, new DisplayObserver.DisplayObserverInterface() {
            @Override
            public void onError(Throwable t) {
                t.printStackTrace();
            }

            @Override
            public void onComplete() {
                displayLoading(false);
            }
        });

        // Create Entry and Conjugations Observable
        final ArrayList<Favorite> favorites = Utils.getFavorites(this);
        ObservableSource<Object> observable = Server
                .doEntryQuery(id)
                .flatMap(dataResponse -> {
                    assert dataResponse.data() != null;
                    entry = dataResponse.data().entry();
                    boolean isAdj = entry.pos().equals("Adjective");
                    Boolean regular = entry.regular();
                    observer.setEntry(entry);

                    if (!entry.pos().equals("Verb") && !isAdj) {
                        return Observable.just("");
                    }

                    // Get favorite conjugation names and fetch them
                    List<String> conjugations = Observable.fromIterable(favorites)
                            .map(Favorite::getConjugationName)
                            .toList()
                            .blockingGet();
                    return Server.doConjugationQuery(entry.term(), false, isAdj, regular, conjugations);
                });

        // Combine with Examples Observable and execute
        Server.doExamplesQuery(id)
                .zipWith(observable, (examplesResponse, conjResponse) -> {
                    ConjugationQuery.Data conjData = null;
                    if(conjResponse instanceof Response) {
                        conjData = ((Response<ConjugationQuery.Data>) conjResponse).data();
                    }

                    return new Pair<>(conjData, examplesResponse.data());
                })
                .subscribeWith(observer);

        LinearLayout linearLayout = findViewById(R.id.disp_root);
        View extendedBar = findViewById(R.id.disp_extendedBar);
        ScrollView scrollView = findViewById(R.id.disp_scroll);
        animationHandler = new ScrollViewAnimationHandler(this, extendedBar, scrollView);
        animationHandler.setupScrollAnimation(linearLayout);
    }

    @Override
    public void onResume(){
        super.onResume();
        displayLoading(isLoading);
    }

    private void displayLoading(boolean isLoading){
        this.isLoading = isLoading;
        View progressBar = findViewById(R.id.disp_progress);
        View extendedBar = findViewById(R.id.disp_extendedBar);
        View rootLinearLayout = findViewById(R.id.disp_root);
        if(isLoading){
            progressBar.setVisibility(View.VISIBLE);
            extendedBar.setVisibility(View.INVISIBLE);
            rootLinearLayout.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            extendedBar.setVisibility(View.VISIBLE);
            rootLinearLayout.setVisibility(View.VISIBLE);

            animationHandler.slideInViews(extendedBar,rootLinearLayout);
        }
    }

    private void handleError(Exception error) {
        /*Snackbar snackbar;
        if (error instanceof NoConnectionError) {
            snackbar = Snackbar.make(findViewById(R.id.disp_root), "Lost connection", Snackbar.LENGTH_INDEFINITE);
        }else{
            snackbar = Snackbar.make(findViewById(R.id.disp_root), "Couldn't connect to server", Snackbar.LENGTH_INDEFINITE);
            System.err.println(error.toString());
        }
        snackbar.setAction("Retry", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestDefinition();
            }
        });
        snackbar.show();*/
    }
}