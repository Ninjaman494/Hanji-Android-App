package com.a494studios.koreanconjugator;

import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.a494studios.koreanconjugator.display.AdCard;
import com.a494studios.koreanconjugator.display.FavoritesCard;
import com.a494studios.koreanconjugator.display.ExamplesCard;
import com.a494studios.koreanconjugator.display.NoteCard;
import com.a494studios.koreanconjugator.display.SynAntCard;
import com.a494studios.koreanconjugator.parsing.Server;
import com.a494studios.koreanconjugator.settings.SettingsActivity;
import com.a494studios.koreanconjugator.display.DefPOSCard;
import com.a494studios.koreanconjugator.display.DisplayCardView;
import com.a494studios.koreanconjugator.utils.ErrorDialogFragment;
import com.android.volley.NoConnectionError;
import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.crashlytics.android.Crashlytics;
import com.eggheadgames.aboutbox.activity.AboutActivity;
import com.transitionseverywhere.Fade;
import com.transitionseverywhere.Transition;
import com.transitionseverywhere.TransitionManager;

import org.jetbrains.annotations.NotNull;
import org.rm3l.maoni.Maoni;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Map;

public class DisplayActivity extends AppCompatActivity {

    public static final String EXTRA_CONJ = "conj";
    public static final String EXTRA_DEF = "definition";
    public static final String EXTRA_ID = "id";
    public static final String EXTRA_TERM = "term";

    private String definition;
    private boolean overflowClicked;
    private DisplayCardView conjCardView;

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

        // Show progress bar and hide cards
        findViewById(R.id.disp_progress).setVisibility(View.VISIBLE);
        findViewById(R.id.disp_root).setVisibility(View.GONE);

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

        // Get Entry and Conjugations
        Server.doEntryQuery(id, new ApolloCall.Callback<EntryQuery.Data>() {
            @Override
            public void onResponse(@NotNull Response<EntryQuery.Data> response) {
                if(response.data() == null || response.data().entry() == null){
                    return;
                }

                final EntryQuery.Entry entry = response.data().entry();
                System.out.println(entry);
                assert entry != null;
                fetchConjugations(entry.term(), false, entry.pos());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Definitions and POS
                        displayCardView.setCardBody(new DefPOSCard(entry.term,entry.pos,entry.definitions));

                        // Note
                        if(entry.note() != null ) {
                            noteCardView.setCardBody(new NoteCard(entry.note()));
                        }

                        // Synonyms and Antonyms
                        if(entry.synonyms() != null) {
                            synCardView.setCardBody(new SynAntCard(entry.synonyms(),true));
                        }
                        if(entry.antonyms() != null ) {
                            antCardView.setCardBody(new SynAntCard(entry.antonyms(),false));
                        }
                    }
                });
            }

            @Override
            public void onFailure(@NotNull ApolloException e) {
                e.printStackTrace();
                /*Utils.handleError(e,DisplayActivity.this,new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        onBackPressed();
                    }
                });*/
            }
        });

        // Get Examples
        Server.doExamplesQuery(id, new ApolloCall.Callback<ExamplesQuery.Data>() {
            @Override
            public void onResponse(@NotNull final Response<ExamplesQuery.Data> response) {
                if(response.data() != null){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            examplesCardView.setCardBody(new ExamplesCard(response.data().examples()));
                        }
                    });
                }
            }

            @Override
            public void onFailure(@NotNull ApolloException e) {
                e.printStackTrace();
            }
        });

    }

    public void fetchConjugations(final String term, final boolean honorific, final String pos){
        if(!pos.equals("Adjective") && !pos.equals("Verb")) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // Hide progress bar and show cards
                    findViewById(R.id.disp_root).setVisibility(View.VISIBLE);
                    findViewById(R.id.disp_progress).setVisibility(View.GONE);
                }
            });
            return;
        }

        final boolean isAdj = pos.equals("Adjective");
        Server.doConjugationQuery(term, honorific, isAdj, new ApolloCall.Callback<ConjugationQuery.Data>() {
            @Override
            public void onResponse(@NotNull Response<ConjugationQuery.Data> response) {
                if(response.data() == null){
                    return;
                }

                // Favorites
                ArrayList<Map.Entry<String, String>> favs = Utils.getFavorites(DisplayActivity.this);
                final ArrayList<Map.Entry<String, ConjugationQuery.Conjugation>> favConjugations = new ArrayList<>();
                for (Map.Entry<String, String> entry : favs) {
                    for (ConjugationQuery.Conjugation conjugation : response.data().conjugation()) {
                        if (conjugation.name().equals(entry.getValue())) {
                            favConjugations.add(new AbstractMap.SimpleEntry<>(entry.getKey(), conjugation));
                            break;
                        }
                    }
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        conjCardView.setCardBody(new FavoritesCard(favConjugations,term, honorific, isAdj));

                        // Hide progress bar and show cards
                        findViewById(R.id.disp_root).setVisibility(View.VISIBLE);
                        findViewById(R.id.disp_progress).setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void onFailure(@NotNull ApolloException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
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
            AboutActivity.launch(this);
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
    public void onWindowFocusChanged (boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus){
            ArrayList<ViewGroup> fragViews = makeFragViewList();
            for (int i = 0; i < fragViews.size(); i++) {
                ViewGroup v = fragViews.get(i);
                Transition t = new Fade(Fade.IN);
                t.setStartDelay(((i + 1) * 80));
                TransitionManager.beginDelayedTransition(v, t);
                v.setVisibility(View.VISIBLE);
            }
        }
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
    }

    private void handleError(Exception error) {
        Snackbar snackbar;
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
        snackbar.show();
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

    private ArrayList<ViewGroup> makeFragViewList(){
        ArrayList<ViewGroup> views = new ArrayList<>();
        /*views.add((ViewGroup) findViewById(R.id.frag_1));
        views.add((ViewGroup) findViewById(R.id.frag_2));
        views.add((ViewGroup) findViewById(R.id.frag_3));
        views.add((ViewGroup) findViewById(R.id.frag_4));
        views.add((ViewGroup) findViewById(R.id.frag_5));
        views.add((ViewGroup) findViewById(R.id.frag_6));
        views.add((ViewGroup) findViewById(R.id.frag_7));
        views.add((ViewGroup) findViewById(R.id.frag_8));
        views.add((ViewGroup) findViewById(R.id.frag_9));
        views.add((ViewGroup) findViewById(R.id.frag_10));*/
        return views;
    }
}