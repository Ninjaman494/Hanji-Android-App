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

import com.a494studios.koreanconjugator.parsing.Server;
import com.a494studios.koreanconjugator.settings.SettingsActivity;
import com.a494studios.koreanconjugator.utils.SlackHandler;
import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.codemybrainsout.ratingdialog.RatingDialog;
import com.crashlytics.android.Crashlytics;
import com.eggheadgames.aboutbox.AboutBoxUtils;
import com.eggheadgames.aboutbox.AboutConfig;
import com.eggheadgames.aboutbox.activity.AboutActivity;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import org.jetbrains.annotations.NotNull;
import org.rm3l.maoni.Maoni;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String APP_ID = BuildConfig.ADMOB_KEY;

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
            ArrayList<Map.Entry<String,String>> favs = new ArrayList<>();
            favs.add(new AbstractMap.SimpleEntry<>("Past","declarative past informal high"));
            favs.add(new AbstractMap.SimpleEntry<>("Present","declarative present informal high"));
            favs.add(new AbstractMap.SimpleEntry<>("Future","declarative future informal high"));
            Utils.setFavorites(favs,this);
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
                    Server.doSearchQuery(entry, new ApolloCall.Callback<SearchQuery.Data>() {
                        @Override
                        public void onResponse(@NotNull Response<SearchQuery.Data> response) {
                            List<SearchQuery.Search> results = response.data().search();
                            if(results.size() == 1){
                                goToDisplay(results.get(0).id,results.get(0).term);
                            }else{
                                goToSearchResults(entry);
                            }
                        }

                        @Override
                        public void onFailure(@NotNull ApolloException e) {
                            e.printStackTrace();
                        }
                    });
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
                            Maoni maoni = Utils.makeMaoniActivity(MainActivity.this);
                            if(maoni != null){
                                maoni.start(MainActivity.this);
                            }
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
                .session(5)
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
                                        //AboutConfig config = AboutConfig.getInstance();
                                        //AboutBoxUtils.openApp(MainActivity.this, config.buildType,config.packageName);
                                        AboutBoxUtils.openApp(MainActivity.this, AboutConfig.BuildType.GOOGLE,"com.a494studios.koreanconjugator");
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
                        SlackHandler handler = new SlackHandler(MainActivity.this);
                        if(handler.auth()) {
                            handler.sendFeedback(feedback);
                            Toast.makeText(MainActivity.this,"Thanks for the feedback!",Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(MainActivity.this,"Couldn't connect to server",Toast.LENGTH_LONG).show();
                        }

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
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setIndeterminate(false);
                progressBar.setProgress(100);
                loadingText.setText(R.string.main_results_found);
                searchInProgress = false;
            }
        });
    }

    private void goToSearchResults(String query){
        Intent intent = new Intent(getApplicationContext(), SearchResultsActivity.class);
        intent.putExtra(SearchResultsActivity.EXTRA_QUERY,query);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        prepForIntent();
    }

    private void goToDisplay(String id, String term){
        Intent intent = new Intent(getApplicationContext(), DisplayActivity.class);
        intent.putExtra(DisplayActivity.EXTRA_ID, id);
        intent.putExtra(DisplayActivity.EXTRA_TERM, term);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        prepForIntent();
    }
}