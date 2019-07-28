package com.a494studios.koreanconjugator.display;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.DialogInterface;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import com.a494studios.koreanconjugator.ConjugationQuery;
import com.a494studios.koreanconjugator.EntryQuery;
import com.a494studios.koreanconjugator.ExamplesQuery;
import com.a494studios.koreanconjugator.R;
import com.a494studios.koreanconjugator.Utils;
import com.a494studios.koreanconjugator.display.cards.AdCard;
import com.a494studios.koreanconjugator.display.cards.FavoritesCard;
import com.a494studios.koreanconjugator.display.cards.ExamplesCard;
import com.a494studios.koreanconjugator.display.cards.NoteCard;
import com.a494studios.koreanconjugator.display.cards.SynAntCard;
import com.a494studios.koreanconjugator.parsing.Favorite;
import com.a494studios.koreanconjugator.parsing.Server;
import com.a494studios.koreanconjugator.settings.SettingsActivity;
import com.a494studios.koreanconjugator.display.cards.DefPOSCard;
import com.a494studios.koreanconjugator.utils.ErrorDialogFragment;
import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.crashlytics.android.Crashlytics;

import org.jetbrains.annotations.NotNull;
import org.rm3l.maoni.Maoni;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;

import static com.eggheadgames.aboutbox.activity.AboutActivity.*;

public class DisplayActivity extends AppCompatActivity {

    public static final String EXTRA_CONJ = "conj";
    public static final String EXTRA_DEF = "definition";
    public static final String EXTRA_ID = "id";
    public static final String EXTRA_TERM = "term";

    private String definition;
    private boolean overflowClicked;
    private DisplayCardView conjCardView;
    private boolean isLoading;
    private SearchView searchView;
    private EntryQuery.Entry entry;

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
            ErrorDialogFragment.newInstance().setListener(new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    onBackPressed();
                }
            }).show(getSupportFragmentManager(),"error_dialog");
            Crashlytics.log("Infinitive was null in DisplayActivity");
            return;
        }
        if(id == null){
            ErrorDialogFragment.newInstance().setListener(new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    onBackPressed();
                }
            }).show(getSupportFragmentManager(),"error_dialog");
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

        // Setting up display cards
        final DisplayCardView displayCardView = findViewById(R.id.disp_dcv);
        final DisplayCardView noteCardView = findViewById(R.id.disp_noteCard);
        final DisplayCardView examplesCardView = findViewById(R.id.disp_examplesCard);
        final DisplayCardView synCardView = findViewById(R.id.disp_synCard);
        final DisplayCardView antCardView = findViewById(R.id.disp_antCard);
        conjCardView = findViewById(R.id.disp_conjCard);

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

        // Get Entry and Conjugations
        final ArrayList<Favorite> favorites = Utils.getFavorites(this);

        ObservableSource<Response<ConjugationQuery.Data>> observable = Server.doEntryQuery(id)
                .flatMap((Function<Response<EntryQuery.Data>, ObservableSource<Response<ConjugationQuery.Data>>>) dataResponse -> {
                    assert dataResponse.data() != null;
                    entry = dataResponse.data().entry();
                    boolean isAdj = entry.pos().equals("Adjective");
                    observer.setEntry(entry);

                    // Get favorite conjugations and fetch them
                    List<String> conjugations = Observable.fromIterable(favorites)
                            .map(Favorite::getConjugationName)
                            .toList()
                            .blockingGet();
                    return Server.doConjugationQuery(entry.term(), false, isAdj, conjugations);
                });

        // Get Examples
        Server.doExamplesQuery(id)
                .zipWith(observable, (dataResponse, response) -> new Pair<>(response.data(), dataResponse.data()))
                .subscribeWith(observer);

        /*Server.doExamplesQuery(id, new ApolloCall.Callback<ExamplesQuery.Data>() {
            @Override
            public void onResponse(@NotNull final Response<ExamplesQuery.Data> response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(response.data() != null && !response.data().examples().isEmpty()){
                            examplesCardView.setCardBody(new ExamplesCard(response.data().examples()));
                        } else {
                            examplesCardView.setVisibility(View.GONE);
                        }
                    }
                });
            }

            @Override
            public void onFailure(@NotNull ApolloException e) {
                e.printStackTrace();
            }
        });*/

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

            // Animations
            LinearLayout layout = (LinearLayout) rootLinearLayout;
            Animation topBot = AnimationUtils.loadAnimation(DisplayActivity.this,R.anim.slide_top_to_bot);
            Animation botTop = AnimationUtils.loadAnimation(DisplayActivity.this, R.anim.slide_bot_to_top);
            extendedBar.startAnimation(topBot);
            layout.startAnimation(botTop);
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

    //TODO Implement
    private void requestDefinition(){
       /* Server.requestKorDefinition(term, this, new Server.DefinitionListener() {
            @Override
            public void onDefinitionReceived(String result) {
                definition = result;
                //defView.setText(definition);
            }

            @Override
            public void onErrorOccurred(Exception error) {
                handleError(error);
            }
        });*/
    }
}