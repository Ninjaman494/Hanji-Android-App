package com.a494studios.koreanconjugator;

import android.content.Intent;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.a494studios.koreanconjugator.display.DisplayCardView;
import com.a494studios.koreanconjugator.parsing.Favorite;
import com.a494studios.koreanconjugator.settings.SettingsActivity;
import com.a494studios.koreanconjugator.utils.ScrollViewAnimationHandler;
import com.a494studios.koreanconjugator.utils.SlackHandler;
import com.codemybrainsout.ratingdialog.RatingDialog;
import com.crashlytics.android.Crashlytics;
import com.eggheadgames.aboutbox.AboutBoxUtils;
import com.eggheadgames.aboutbox.AboutConfig;
import com.google.android.gms.ads.MobileAds;

import org.rm3l.maoni.Maoni;

import java.util.ArrayList;

import static com.eggheadgames.aboutbox.activity.AboutActivity.*;

public class MainActivity extends AppCompatActivity {

    private static final String APP_ID = BuildConfig.ADMOB_KEY;

    private SearchCard searchCard;
    private TextView logo;
    private ScrollViewAnimationHandler animationHandler;
    private LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MobileAds.initialize(this, APP_ID);
        logo = findViewById(R.id.main_extendedBar);

        // Set up Ad, Search, and Word of the Day cards
        DisplayCardView wodCard = findViewById(R.id.main_wodCard);
        DisplayCardView searchCard = findViewById(R.id.main_searchCard);
        this.searchCard = new SearchCard(this);

        CustomApplication.handleAdCard((DisplayCardView)findViewById(R.id.main_adView));
        wodCard.setCardBody(new WordOfDayCard());
        searchCard.setCardBody(this.searchCard);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setTitle("");
            actionBar.setElevation(0);
        }

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
            // TODO Clear old favorites, or at least find a way to handle old favorite form
            ArrayList<Favorite> favs = new ArrayList<>();
            favs.add(new Favorite("Past","declarative past informal high",false));
            favs.add(new Favorite("Present","declarative present informal high",true));
            favs.add(new Favorite("Future","declarative future informal high",false));
            Utils.setFavorites(favs,this);
            Utils.setFirstBoot(this,false);
        }

        // Setting up Feedback dialog
        final RatingDialog ratingDialog = new RatingDialog.Builder(this)
                .threshold(5) // Get feedback if less than 5 stars
                .session(5)
                .title(getString(R.string.feed_title))
                .formTitle(getString(R.string.feed_form_title))
                .formHint(getString(R.string.feed_form_hint))
                .onThresholdCleared((ratingDialog1, rating, thresholdCleared) -> {
                    ratingDialog1.dismiss();
                    final AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                            .setTitle(R.string.feed_rate_title)
                            .setMessage(R.string.feed_rate_msg)
                            .setPositiveButton(R.string.feed_rate_sure, (dialogInterface, i) ->
                                    AboutBoxUtils.openApp(MainActivity.this, AboutConfig.BuildType.GOOGLE,"com.a494studios.koreanconjugator"))
                            .setNegativeButton(R.string.feed_rate_never, (dialogInterface, i) -> {/*Do Nothing*/})
                            .create();
                    dialog.setOnShowListener(arg0 ->
                            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.grey_500)));
                    dialog.show();
                })
                .onRatingBarFormSumbit(feedback -> {
                    SlackHandler handler = new SlackHandler(MainActivity.this);
                    if(handler.auth()) {
                        handler.sendFeedback(feedback);
                        Toast.makeText(MainActivity.this,"Thanks for the feedback!",Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(MainActivity.this,"Couldn't connect to server",Toast.LENGTH_LONG).show();
                    }

                })
                .build();
        ratingDialog.show();

        // Setup animations
        linearLayout = findViewById(R.id.main_linearLayout);
        animationHandler = new ScrollViewAnimationHandler(this, logo, findViewById(R.id.main_scrollView));
        animationHandler.setupScrollAnimation(linearLayout);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.overflow_settings){
            startActivity(new Intent(getBaseContext(), SettingsActivity.class));
            return true;
        }else if(item.getItemId() == R.id.overflow_about){
            Utils.makeAboutBox(MainActivity.this);
            launch(MainActivity.this);
            return true;
        }else if(item.getItemId() == R.id.overflow_bug){
            Maoni maoni = Utils.makeMaoniActivity(MainActivity.this);
            if(maoni != null){
                maoni.start(MainActivity.this);
            }
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onResume(){
        super.onResume();
        this.searchCard.getSearchView().setQuery("", false);
        this.searchCard.getSearchView().clearFocus();
        animationHandler.slideInViews(logo, linearLayout);
    }
}