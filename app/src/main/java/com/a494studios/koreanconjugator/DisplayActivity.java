package com.a494studios.koreanconjugator;

import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
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
import com.a494studios.koreanconjugator.display.ConjugationCard;
import com.a494studios.koreanconjugator.display.ExamplesCard;
import com.a494studios.koreanconjugator.parsing.Server;
import com.a494studios.koreanconjugator.settings.SettingsActivity;
import com.a494studios.koreanconjugator.display.DefPOSCard;
import com.a494studios.koreanconjugator.display.DisplayCardView;
import com.a494studios.koreanconjugator.utils.ErrorDialogFragment;
import com.a494studios.koreanconjugator.utils.ExamplesFragment;
import com.a494studios.koreanconjugator.utils.SimpleCardFragment;
import com.android.volley.NoConnectionError;
import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.crashlytics.android.Crashlytics;
import com.eggheadgames.aboutbox.activity.AboutActivity;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
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
    private String term;
    private String id;
    private boolean overflowClicked;
    private DisplayCardView conjCardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        term = getIntent().getStringExtra(EXTRA_TERM);
        id = getIntent().getStringExtra(EXTRA_ID);
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
            actionBar.setTitle("Result: "+ term);
        }

        // Setting up display cards
        final DisplayCardView displayCardView = findViewById(R.id.disp_dcv);
        final DisplayCardView examplesCardView = findViewById(R.id.disp_examplesCard);
        conjCardView = findViewById(R.id.disp_conjCard);
        DisplayCardView adCardView = findViewById(R.id.disp_adCard);
        adCardView.setCardBody(new AdCard());

        FragmentManager fm = getSupportFragmentManager();
        // Note
        final SimpleCardFragment noteFrag = (SimpleCardFragment) fm.findFragmentById(R.id.disp_noteFrag);
        noteFrag.setHeading("Note");

        // Examples
        final ExamplesFragment exampleFrag = (ExamplesFragment) fm.findFragmentById(R.id.disp_exampleFrag);

        // Synonyms
        final SimpleCardFragment synFrag = (SimpleCardFragment)fm.findFragmentById(R.id.disp_synFrag);
        synFrag.setHeading("Synonyms");

        // Antonyms
        final SimpleCardFragment antFrag = (SimpleCardFragment)fm.findFragmentById(R.id.disp_antFrag);
        antFrag.setHeading("Antonyms");

        Server.doEntryQuery(id, new ApolloCall.Callback<EntryQuery.Data>() {
            @Override
            public void onResponse(@NotNull Response<EntryQuery.Data> response) {
                if(response.data() == null){
                    return;
                }

                final EntryQuery.Entry entry = response.data().entry();
                System.out.println(entry);
                if(entry != null) {
                    FragmentManager fm = getSupportFragmentManager();

                    // Definitions and POS
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            displayCardView.setCardBody(new DefPOSCard(entry.term,entry.pos,entry.definitions));
                        }
                    });

                    // Conjugations
                    if(entry.pos.equals("Adjective") || entry.pos.equals("Verb")) {
                        fetchConjugations(term, false, entry.pos.equals("Adjective"));
                    }

                    // Note
                    SimpleCardFragment noteFrag = (SimpleCardFragment) fm.findFragmentById(R.id.disp_noteFrag);
                    if (entry.note() != null) {
                        noteFrag.setContent(entry.note());
                    }

                    // Synonyms
                    SimpleCardFragment synFrag = (SimpleCardFragment)fm.findFragmentById(R.id.disp_synFrag);
                    if(entry.synonyms() != null && !entry.synonyms().isEmpty()){
                        synFrag.setContent(entry.synonyms());
                    }

                    // Antonyms
                    SimpleCardFragment antFrag = (SimpleCardFragment)fm.findFragmentById(R.id.disp_antFrag);
                    if(entry.antonyms() != null && !entry.antonyms().isEmpty()){
                        antFrag.setContent(entry.antonyms());
                    }
                }
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
            public void onResponse(@NotNull Response<ExamplesQuery.Data> response) {
                if(response.data() != null){
                    exampleFrag.setExamples(response.data().examples);
                    examplesCardView.setCardBody(new ExamplesCard(response.data().examples));
                }
            }

            @Override
            public void onFailure(@NotNull ApolloException e) {
                e.printStackTrace();
            }
        });

    }

    public void fetchConjugations(final String term, final boolean honorific, final boolean isAdj){
        Server.doConjugationQuery(term, honorific, isAdj, new ApolloCall.Callback<ConjugationQuery.Data>() {
            @Override
            public void onResponse(@NotNull Response<ConjugationQuery.Data> response) {
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

                DisplayActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        conjCardView.setCardBody(new ConjugationCard(favConjugations));
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