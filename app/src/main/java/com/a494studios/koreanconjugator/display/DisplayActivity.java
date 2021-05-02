package com.a494studios.koreanconjugator.display;

import android.annotation.SuppressLint;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.ListPopupWindow;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.a494studios.koreanconjugator.CustomApplication;
import com.a494studios.koreanconjugator.EntryQuery;
import com.a494studios.koreanconjugator.FavoritesQuery;
import com.a494studios.koreanconjugator.R;
import com.a494studios.koreanconjugator.suggestions.ExampleSuggestionActivity;
import com.a494studios.koreanconjugator.type.FavInput;
import com.a494studios.koreanconjugator.utils.BaseActivity;
import com.a494studios.koreanconjugator.utils.Logger;
import com.a494studios.koreanconjugator.utils.Utils;
import com.a494studios.koreanconjugator.parsing.Favorite;
import com.a494studios.koreanconjugator.parsing.Server;
import com.a494studios.koreanconjugator.utils.ScrollViewAnimationHandler;
import com.apollographql.apollo.api.Response;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.Observable;

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
            Exception exception = new Exception("ID was null in DisplayActivity");
            Utils.handleError(exception, this, 5, (dialogInterface, i) -> finish());
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
        CustomApplication.handleAdCard(findViewById(R.id.disp_adCard), getString(R.string.DISPLAY_AD_ID));

        // Creating DisplayObserver
        View rootView = findViewById(android.R.id.content);
        DisplayObserver observer = new DisplayObserver(rootView, new DisplayObserver.DisplayObserverInterface() {
            @Override
            public void onError(Throwable t) {
                t.printStackTrace();
                Utils.handleError(t, DisplayActivity.this,2, (dialogInterface, i) -> DisplayActivity.this.finish());
            }

            @Override
            public void onComplete() {
                displayLoading(false);
            }
        });

        // Create Entry and Conjugations Observable
        final ArrayList<Favorite> favorites = Utils.getFavorites(this);
        CustomApplication app = (CustomApplication)getApplication();

        Server.doEntryQuery(id, app)
                .concatMap(dataResponse -> {
                    assert dataResponse.getData() != null;

                    entry = dataResponse.getData().entry();
                    boolean isAdj = entry.pos().equals("Adjective");
                    Boolean regular = entry.regular();
                    observer.setEntry(entry);

                    if (!entry.pos().equals("Verb") && !isAdj) {
                        return Observable.just("");
                    }

                    // Log select content event
                    Logger.getInstance().logSelectContent(entry.term(), entry.pos());

                    // Create favorites input list
                    List<FavInput> conjugations = Observable.fromIterable(favorites)
                            .map((favorite -> FavInput.builder()
                                    .name(favorite.getName())
                                    .conjugationName(favorite.getConjugationName())
                                    .honorific(favorite.isHonorific())
                                    .build()
                            ))
                            .toList()
                            .blockingGet();

                    return Server.doFavoritesQuery(entry.term(),  isAdj, regular, conjugations, app);
                })
                // If no conjugations, create an empty list to prevent a null exception
                .map(o -> o instanceof String
                        ? new FavoritesQuery.Data(new ArrayList<>())
                        : ((Response<FavoritesQuery.Data>) o).getData())
                .subscribeWith(observer);

        LinearLayout linearLayout = findViewById(R.id.disp_root);
        View extendedBar = findViewById(R.id.disp_extendedBar);
        ScrollView scrollView = findViewById(R.id.disp_scroll);
        animationHandler = new ScrollViewAnimationHandler(this, extendedBar, scrollView);
        animationHandler.setupScrollAnimation(linearLayout);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (animationHandler != null) {
            displayLoading(isLoading);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       return this.setupMenu(R.menu.display_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.menu_add){
            ListPopupWindow popup = new ListPopupWindow(this);
            ArrayList<String> options = new ArrayList<>(Arrays.asList("Add Example", "Add Synonym", "Add Antonym"));
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, options);

            popup.setAdapter(adapter);
            popup.setAnchorView(findViewById(item.getItemId()));
            popup.setWidth(500);
            popup.setVerticalOffset(-170);
            popup.setDropDownGravity(Gravity.END);

            popup.setOnItemClickListener((adapterView, view, i, l) -> {
                Context context = view.getContext();
                if (entry != null) {
                    switch (i) {
                        case 0:
                            Intent intent = new Intent(context, ExampleSuggestionActivity.class);
                            intent.putExtra(ExampleSuggestionActivity.EXTRA_ENTRY_ID, entry.id());
                            context.startActivity(intent);
                            break;
                        case 1:
                            Toast.makeText(context, "Add Synonyms", Toast.LENGTH_SHORT).show();
                            break;
                        case 2:
                            Toast.makeText(context, "Add Antonyms", Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
                popup.dismiss();
            });

            popup.show();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
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

            animationHandler.slideInViews(extendedBar,rootLinearLayout);
        }
    }
}