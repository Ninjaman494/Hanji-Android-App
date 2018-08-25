package com.a494studios.koreanconjugator;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.a494studios.koreanconjugator.parsing.Category;
import com.a494studios.koreanconjugator.parsing.Conjugation;
import com.a494studios.koreanconjugator.parsing.Form;
import com.a494studios.koreanconjugator.parsing.Formality;
import com.a494studios.koreanconjugator.parsing.Server;
import com.a494studios.koreanconjugator.parsing.Tense;
import com.a494studios.koreanconjugator.settings.SettingsActivity;
import com.codemybrainsout.ratingdialog.RatingDialog;
import com.crashlytics.android.Crashlytics;
import com.eggheadgames.aboutbox.AboutBoxUtils;
import com.eggheadgames.aboutbox.AboutConfig;
import com.eggheadgames.aboutbox.activity.AboutActivity;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String APP_ID = "***REMOVED***";

    private ProgressBar progressBar;
    private TextView loadingText;
    private CardView searchCard;
    private EditText editText;
    private TextView logo;
    private ImageView overflowMenu;
    private boolean searchInProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MobileAds.initialize(this, APP_ID);
        progressBar = findViewById(R.id.main_loadingBar);
        loadingText = findViewById(R.id.main_loadingText);
        overflowMenu = findViewById(R.id.main_menu_icon);
        searchCard = findViewById(R.id.main_searchCard);
        editText = findViewById(R.id.main_editText);
        logo = findViewById(R.id.main_logo);
        AdView adView = findViewById(R.id.main_adView);
        searchInProgress = false;

        adView.loadAd(new AdRequest.Builder().build());

        try {
            if (getIntent().getExtras() != null) {
                Crashlytics.log("Extra in Main");
                for (String s : getIntent().getExtras().keySet()) {
                    Crashlytics.log("Key: " + s);
                    if (getIntent().getStringExtra(s) != null) {
                        Crashlytics.setString(s, getIntent().getStringExtra(s));
                    } else if (getIntent().getSerializableExtra(s) != null) {
                        Crashlytics.setString(s, getIntent().getSerializableExtra(s).toString());
                    } else {
                        Crashlytics.setString(s, "null");
                    }

                }

                String showDialog = getIntent().getStringExtra("dialog");
                if (showDialog != null && showDialog.equals("true")) {
                    Crashlytics.log("Dialog is not null and true");
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    String title = getIntent().getStringExtra("title");
                    String msg = getIntent().getStringExtra("message");
                    if (title != null && msg != null) {
                        Crashlytics.log("Title and msg not null, building dialog");
                        builder.setTitle(title);
                        builder.setMessage(msg);
                        builder.create().show();
                    }
                }
            }
        } catch (Exception e) {
            Crashlytics.log("Exception caught by try-catch block");
            Crashlytics.logException(e);
        }

        if(Utils.isFirstBoot(this)){
            // Make default favorites
            Category[] past = {Formality.INFORMAL_HIGH, Form.DECLARATIVE, Tense.PAST};
            Category[] present = {Formality.INFORMAL_HIGH, Form.DECLARATIVE, Tense.PRESENT};
            Category[] future = {Formality.INFORMAL_HIGH, Form.DECLARATIVE, Tense.FUTURE};
            ArrayList<Map.Entry<String,Category[]>> favorites = new ArrayList<>();
            favorites.add(new AbstractMap.SimpleEntry<>("Past",past));
            favorites.add(new AbstractMap.SimpleEntry<>("Present",present));
            favorites.add(new AbstractMap.SimpleEntry<>("Future",future));
            Utils.setFavorites(favorites,this);
            Utils.setFirstBoot(this,false);
        }

        progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_IN);
        progressBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_IN);

        // Handle Search
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    View view = MainActivity.this.getCurrentFocus();
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (view != null && imm != null) {
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }

                    progressBar.setVisibility(View.VISIBLE);
                    loadingText.setVisibility(View.VISIBLE);
                    searchCard.setVisibility(View.INVISIBLE);
                    overflowMenu.setVisibility(View.INVISIBLE);
                    logo.setVisibility(View.INVISIBLE);
                    searchInProgress = true;

                    final String entry = editText.getText().toString().trim();
                    if(Utils.isHangul(entry)) {
                        Crashlytics.log("Korean search: "+entry);
                        Crashlytics.setString("searchTerm",entry);
                        doKoreanSearch(entry);
                    }else if(entry.matches("[A-Za-z ]+")){ // Check if String in English
                        Crashlytics.log("English search: "+entry);
                        Crashlytics.setString("searchTerm",entry);
                        Server.requestEngDefinition(entry, getApplicationContext(), new Server.ServerListener() {
                            @Override
                            public void onResultReceived(ArrayList<Conjugation> conjugations, HashMap<String, String> searchResults) {
                                if(searchResults != null) {
                                    if(searchResults.isEmpty()) {
                                        showSearchCard();
                                        AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                                                .setTitle(R.string.no_results_title)
                                                .setMessage(R.string.no_results_msg)
                                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        dialogInterface.dismiss();
                                                    }
                                                })
                                                .create();
                                        if(!MainActivity.this.isFinishing()) {
                                            dialog.show();
                                        }
                                    }else if (searchResults.size() == 1 || Utils.getEnglishLuck(getBaseContext())) {
                                        doKoreanSearch(searchResults.keySet().iterator().next()); // Get the first key in map
                                    }else{
                                        goToSearchResults(searchResults,entry);
                                    }
                                }
                            }
                            @Override
                            public void onErrorOccurred(Exception error) {
                                Utils.handleError(error,MainActivity.this);
                                showSearchCard();
                            }
                        });
                    }else{
                        Crashlytics.log("Invalid Search: "+entry);
                        Crashlytics.setString("searchTerm",entry);
                        showSearchCard();
                        Toast.makeText(getBaseContext(),"Input not Valid",Toast.LENGTH_LONG).show();
                    }
                }
                return false;
            }
        });

        // Setting up Overflow Menu
        overflowMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(MainActivity.this, view, Gravity.END);
                popup.inflate(R.menu.main_menu);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if(item.getItemId() == R.id.overflow_settings){
                            startActivity(new Intent(getBaseContext(), SettingsActivity.class));
                            return true;
                        }else if(item.getItemId() == R.id.overflow_about){
                            Utils.makeAboutBox(MainActivity.this);
                            AboutActivity.launch(MainActivity.this);
                            return true;
                        }else if(item.getItemId() == R.id.overflow_bug){
                            Utils.makeMaoniActivity(MainActivity.this).start(MainActivity.this);
                            return true;
                        }
                        return false;
                    }
                });
                popup.show();
            }
        });

        // Setting up Feedback dialog
        final RatingDialog ratingDialog = new RatingDialog.Builder(this)
                .threshold(5) // Get feedback if less than 5 stars
                .session(2)
                .title(getString(R.string.feed_title))
                .formTitle(getString(R.string.feed_form_title))
                .formHint(getString(R.string.feed_form_hint))
                .onThresholdCleared(new RatingDialog.Builder.RatingThresholdClearedListener() {
                    @Override
                    public void onThresholdCleared(final RatingDialog ratingDialog, final float rating, boolean thresholdCleared) {
                        ratingDialog.dismiss();
                        final AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                                .setTitle(R.string.feed_rate_title)
                                .setMessage(R.string.feed_rate_msg)
                                .setPositiveButton(R.string.feed_rate_sure, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        AboutConfig config = AboutConfig.getInstance();
                                        AboutBoxUtils.openApp(MainActivity.this, config.buildType,config.packageName);
                                        //AboutBoxUtils.openApp(MainActivity.this, AboutConfig.BuildType.GOOGLE,"com.a494studios.koreanconjugator");
                                    }
                                })
                                .setNegativeButton(R.string.feed_rate_never, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        // Do nothing
                                    }
                                }).create();
                        dialog.setOnShowListener( new DialogInterface.OnShowListener() {
                            @Override
                            public void onShow(DialogInterface arg0) {
                                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.grey_500));
                            }
                        });
                        dialog.show();
                    }
                })
                .onRatingBarFormSumbit(new RatingDialog.Builder.RatingDialogFormListener() {
                    @Override
                    public void onFormSubmitted(String feedback) {
                        System.out.println("Thanks for your feedback!");
                    }
                }).build();
        ratingDialog.show();
    }

    @Override
    public void onResume(){
        super.onResume();
        if(!searchInProgress) {
            showSearchCard();
        }
    }

    private void doKoreanSearch(final String entry){
        Server.requestKoreanSearch(entry, getApplicationContext(), new Server.ServerListener() {
            @Override
            public void onResultReceived(final ArrayList<Conjugation> conjugations, HashMap<String, String> searchResults) {
                if (conjugations != null) {
                    goToDisplay(conjugations);
                } else if (searchResults != null && !searchResults.isEmpty()) {
                    if(Utils.getKoreanLuck(getApplicationContext())){
                        requestConjugations(searchResults.keySet().iterator().next());
                    }else{
                        goToSearchResults(searchResults,entry);
                    }
                } else{
                    AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                            .setTitle(R.string.no_results_title)
                            .setMessage(R.string.no_results_msg)
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            })
                            .create();
                    if(!MainActivity.this.isFinishing()) {
                        dialog.show();
                    }
                }
            }

            @Override
            public void onErrorOccurred(Exception error) {
                Utils.handleError(error,MainActivity.this);
                showSearchCard();
            }
        });
    }

    private void requestConjugations(String word){
        Server.requestConjugation(word, this, new Server.ServerListener() {
            @Override
            public void onResultReceived(ArrayList<Conjugation> conjugations, HashMap<String, String> searchResults) {
                goToDisplay(conjugations);
            }
            @Override
            public void onErrorOccurred(Exception error) {
                Utils.handleError(error,MainActivity.this);
                showSearchCard();
            }
        });
    }

    private void showSearchCard(){
        editText.getText().clear();
        logo.setVisibility(View.VISIBLE);
        searchCard.setVisibility(View.VISIBLE);
        overflowMenu.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
        loadingText.setVisibility(View.INVISIBLE);
        loadingText.setText(R.string.loading);
        progressBar.setIndeterminate(true);
    }

    private void prepForIntent(){
        progressBar.setIndeterminate(false);
        progressBar.setProgress(100);
        loadingText.setText(R.string.main_results_found);
        searchInProgress = false;
    }

    private void goToSearchResults(HashMap<String,String> searchResults, String entry){
        Intent intent = new Intent(getApplicationContext(), SearchResultsActivity.class);
        intent.putExtra(SearchResultsActivity.EXTRA_RESULTS, searchResults);
        intent.putExtra(SearchResultsActivity.EXTRA_SEARCHED,entry);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        prepForIntent();
    }

    private void goToDisplay(ArrayList<Conjugation> conjugations){
        Intent intent = new Intent(getApplicationContext(), DisplayActivity.class);
        intent.putExtra(DisplayActivity.EXTRA_CONJ, conjugations);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        prepForIntent();
    }
}