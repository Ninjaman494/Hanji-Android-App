package com.a494studios.koreanconjugator;

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
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.a494studios.koreanconjugator.parsing.Conjugation;
import com.a494studios.koreanconjugator.parsing.Server;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private TextView loadingText;
    private CardView searchCard;
    private EditText editText;
    private boolean searchInProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar = findViewById(R.id.main_loadingBar);
        loadingText = findViewById(R.id.main_loadingText);
        searchCard = findViewById(R.id.main_searchCard);
        editText = findViewById(R.id.main_editText);
        searchInProgress = false;

        progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_IN);
        progressBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_IN);

        // Handle Search
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    progressBar.setVisibility(View.VISIBLE);
                    loadingText.setVisibility(View.VISIBLE);
                    searchCard.setVisibility(View.INVISIBLE);
                    searchInProgress = true;

                    final String entry = editText.getText().toString().trim();
                    if (entry.equals("")) {
                        return false;
                    }

                    if(isHangul(entry)) {
                        doKoreanSearch(entry);
                    }else if(entry.matches("[A-Za-z ]+")){ // Check if String in English
                        Server.requestEngDefinition(entry, getApplicationContext(), new Server.ServerListener() {
                            @Override
                            public void onResultReceived(ArrayList<Conjugation> conjugations, HashMap<String, String> searchResults) {
                                if(searchResults != null) {
                                    if (searchResults.size() == 1) {
                                        doKoreanSearch(searchResults.keySet().iterator().next()); // Get the first, and only, key in map
                                    } else {
                                        prepForIntent();
                                        Intent intent = new Intent(getApplicationContext(), SearchResultsActivity.class);
                                        intent.putExtra(SearchResultsActivity.EXTRA_RESULTS, searchResults);
                                        intent.putExtra(SearchResultsActivity.EXTRA_SEARCHED,entry);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                        startActivity(intent);
                                    }
                                }
                            }

                            @Override
                            public void onErrorOccurred(Exception error) {
                                handleError(error);
                            }
                        });
                    }else{
                        showSearchCard();
                        Toast.makeText(getBaseContext(),"Input not Valid",Toast.LENGTH_LONG).show();
                    }
                }
                return false;
            }
        });

        // Setting up Overflow Menu
        findViewById(R.id.main_menu_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(MainActivity.this, view, Gravity.END);
                popup.inflate(R.menu.menu_overflow);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if(item.getItemId() == R.id.overflow_settings){
                            Toast.makeText(getBaseContext(),"Setings not made yet",Toast.LENGTH_SHORT).show();
                            return true;
                        }else if(item.getItemId() == R.id.overflow_about){
                            Toast.makeText(getBaseContext(),"About not made yet",Toast.LENGTH_SHORT).show();
                            return true;
                        }
                        return false;
                    }
                });
                popup.show();
            }
        });
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
                prepForIntent();
                if (conjugations != null) {
                    Intent intent = new Intent(getApplicationContext(), DisplayActivity.class);
                    intent.putExtra(DisplayActivity.EXTRA_CONJ, conjugations);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                } else if (searchResults != null) {
                    Intent intent = new Intent(getApplicationContext(), SearchResultsActivity.class);
                    intent.putExtra(SearchResultsActivity.EXTRA_RESULTS, searchResults);
                    intent.putExtra(SearchResultsActivity.EXTRA_SEARCHED,entry);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                }
            }

            @Override
            public void onErrorOccurred(Exception error) {
               handleError(error);
            }
        });
    }

    private void handleError(Exception error){
        if(error instanceof NoConnectionError){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Check your network settings and try again")
                    .setTitle("Can't load results");
            builder.create().show();
        } else if(error instanceof ParseError) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("A response was given that we couldn't understand")
                    .setTitle("Can't read results");
            builder.create().show();
        }else{
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Try again later or contact support")
                    .setTitle("Something went wrong");
            builder.create().show();
            System.err.println(error.toString());
        }
        showSearchCard();
    }

    private void showSearchCard(){
        editText.getText().clear();
        searchCard.setVisibility(View.VISIBLE);
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

    public static boolean isHangul(String korean){
        korean = korean.replace(" ","");
        for(int i=0;i<korean.length();i++){
            char c = korean.charAt(i);
            if(!((int)c >= '가' && (int)c <= '힣')){
                return false;
            }
        }
        return true;
    }
}