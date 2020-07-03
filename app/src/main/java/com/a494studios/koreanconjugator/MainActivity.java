package com.a494studios.koreanconjugator;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.a494studios.koreanconjugator.display.DisplayCardView;
import com.a494studios.koreanconjugator.utils.BaseActivity;
import com.a494studios.koreanconjugator.utils.ScrollViewAnimationHandler;
import com.a494studios.koreanconjugator.utils.SlackHandler;
import com.a494studios.koreanconjugator.utils.Utils;
import com.codemybrainsout.ratingdialog.RatingDialog;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.eggheadgames.aboutbox.AboutBoxUtils;
import com.eggheadgames.aboutbox.AboutConfig;

public class MainActivity extends BaseActivity {

    private SearchCard searchCard;
    private TextView logo;
    private ScrollViewAnimationHandler animationHandler;
    private LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        logo = findViewById(R.id.main_extendedBar);

        // Set up Ad, Search, and Word of the Day cards
        DisplayCardView wodCard = findViewById(R.id.main_wodCard);
        DisplayCardView searchCard = findViewById(R.id.main_searchCard);
        this.searchCard = new SearchCard(this);

        CustomApplication.handleAdCard(findViewById(R.id.main_adView), getString(R.string.MAIN_AD_ID));
        wodCard.setCardBody(new WordOfDayCard());
        searchCard.setCardBody(this.searchCard);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setTitle("");
            actionBar.setElevation(0);
        }

        FirebaseCrashlytics crashlytics = FirebaseCrashlytics.getInstance();
        try {
            if (getIntent().getExtras() != null) {
                crashlytics.log("Extra in Main");
                for (String s : getIntent().getExtras().keySet()) {
                    FirebaseCrashlytics.getInstance().log("Key: " + s);
                    if (getIntent().getStringExtra(s) != null) {
                        crashlytics.setCustomKey(s, getIntent().getStringExtra(s));
                    } else if (getIntent().getSerializableExtra(s) != null) {
                       crashlytics.setCustomKey(s, getIntent().getSerializableExtra(s).toString());
                    } else {
                        crashlytics.setCustomKey(s, "null");
                    }

                }

                String showDialog = getIntent().getStringExtra("dialog");
                if (showDialog != null && showDialog.equals("true")) {
                    FirebaseCrashlytics.getInstance().log("Dialog is not null and true");
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    String title = getIntent().getStringExtra("title");
                    String msg = getIntent().getStringExtra("message");
                    if (title != null && msg != null) {
                        FirebaseCrashlytics.getInstance().log("Title and msg not null, building dialog");
                        builder.setTitle(title);
                        builder.setMessage(msg);
                        builder.create().show();
                    }
                }
            }
        } catch (Exception e) {
            crashlytics.log("Exception caught by try-catch block");
            crashlytics.recordException(e);
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

        if(Utils.isAdFree(this) != null && Utils.isAdFree(this)) {
            menu.findItem(R.id.overflow_ad_free).setVisible(false);
        }

        return true;
    }

    @Override
    public void onResume(){
        super.onResume();
        this.searchCard.getSearchView().setQuery("", false);
        this.searchCard.getSearchView().clearFocus();
        animationHandler.slideInViews(logo, linearLayout);
    }
}