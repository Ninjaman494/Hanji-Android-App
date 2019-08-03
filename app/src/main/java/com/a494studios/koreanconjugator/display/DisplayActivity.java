package com.a494studios.koreanconjugator.display;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Intent;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.SearchView;

import android.util.Pair;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.a494studios.koreanconjugator.ConjugationQuery;
import com.a494studios.koreanconjugator.EntryQuery;
import com.a494studios.koreanconjugator.R;
import com.a494studios.koreanconjugator.Utils;
import com.a494studios.koreanconjugator.display.cards.AdCard;
import com.a494studios.koreanconjugator.parsing.Favorite;
import com.a494studios.koreanconjugator.parsing.Server;
import com.a494studios.koreanconjugator.settings.SettingsActivity;
import com.a494studios.koreanconjugator.utils.ErrorDialogFragment;
import com.apollographql.apollo.api.Response;
import com.crashlytics.android.Crashlytics;

import org.rm3l.maoni.Maoni;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;

import static com.eggheadgames.aboutbox.activity.AboutActivity.*;

public class DisplayActivity extends AppCompatActivity {

    public static final String EXTRA_DEF = "definition";
    public static final String EXTRA_ID = "id";
    public static final String EXTRA_TERM = "term";

    private String definition;
    private boolean overflowClicked;
    private boolean isLoading;
    private SearchView searchView;
    private EntryQuery.Entry entry;
    private AnimationHandler handler;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        String term = getIntent().getStringExtra(EXTRA_TERM);
        String id = getIntent().getStringExtra(EXTRA_ID);
        if(savedInstanceState != null){
            definition = savedInstanceState.getString(EXTRA_DEF);
        }else{
            definition = getIntent().getStringExtra(EXTRA_DEF);
        }

        // Make sure extras were passed
        if(term == null){
            ErrorDialogFragment.newInstance()
                    .setListener((dialogInterface, i) -> onBackPressed())
                    .show(getSupportFragmentManager(),"error_dialog");
            Crashlytics.log("Infinitive was null in DisplayActivity");
            return;
        }
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
        DisplayCardView adCardView = findViewById(R.id.disp_adCard);
        adCardView.setCardBody(new AdCard());

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
                    observer.setEntry(entry);

                    if (!entry.pos().equals("Verb") && !isAdj) {
                        return Observable.just("");
                    }

                    // Get favorite conjugation names and fetch them
                    List<String> conjugations = Observable.fromIterable(favorites)
                            .map(Favorite::getConjugationName)
                            .toList()
                            .blockingGet();
                    return Server.doConjugationQuery(entry.term(), false, isAdj, conjugations);
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
        handler = new AnimationHandler(this, extendedBar, scrollView);
        handler.setupScrollAnimation(linearLayout);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.overflow_settings){
            overflowClicked = true;
            startActivity(new Intent(getBaseContext(), SettingsActivity.class));
            return true;
        }else if(item.getItemId() == R.id.overflow_about){
            overflowClicked = true;
            Utils.makeAboutBox(this);
            launch(this);
            return true;
        }else if(item.getItemId() == R.id.overflow_bug) {
            Maoni maoni = Utils.makeMaoniActivity(DisplayActivity.this);
            if(maoni != null){
                maoni.start(DisplayActivity.this);
            }
            return true;
        } else{
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString(EXTRA_DEF, definition);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onPause(){
        super.onPause();
        if(!overflowClicked) overridePendingTransition(0,0);
    }

    @Override
    public void onResume(){
        super.onResume();
        overflowClicked = false;
        displayLoading(isLoading);

        if(searchView != null) {
            searchView.setQuery("", false);
            searchView.setIconified(true);
            searchView.clearFocus();
        }
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

            handler.slideInViews(extendedBar,rootLinearLayout);
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