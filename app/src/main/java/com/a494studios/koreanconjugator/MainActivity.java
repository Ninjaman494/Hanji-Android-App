package com.a494studios.koreanconjugator;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.a494studios.koreanconjugator.parsing.Conjugation;
import com.a494studios.koreanconjugator.parsing.Server;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    ProgressBar progressBar;
    TextView loadingText;
    CardView searchCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar = findViewById(R.id.main_loadingBar);
        loadingText = findViewById(R.id.main_loadingText);
        searchCard = findViewById(R.id.main_searchCard);
        final EditText editText = findViewById(R.id.main_editText);

        progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_IN);
        progressBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_IN);

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    progressBar.setVisibility(View.VISIBLE);
                    loadingText.setVisibility(View.VISIBLE);
                    searchCard.setVisibility(View.INVISIBLE);

                    final String entry = editText.getText().toString().trim();
                    if(isHangul(entry)) {
                       doKoreanSearch(entry);
                    }else{
                        Server.requestEngDefinition(entry, getApplicationContext(), new Server.ServerListener() {
                            @Override
                            public void onResultReceived(ArrayList<Conjugation> conjugations, HashMap<String, String> searchResults) {
                                if(searchResults != null) {
                                    if (searchResults.size() == 1) {
                                        doKoreanSearch(searchResults.keySet().iterator().next()); // Get the first, and only, key in map
                                    } else {
                                        progressBar.setIndeterminate(false);
                                        progressBar.setProgress(100);
                                        loadingText.setText(R.string.main_results_found);

                                        Intent intent = new Intent(getApplicationContext(), SearchResultsActivity.class);
                                        intent.putExtra(SearchResultsActivity.EXTRA_RESULTS, searchResults);
                                        intent.putExtra(SearchResultsActivity.EXTRA_SEARCHED,entry);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                        startActivity(intent);
                                    }
                                }
                            }

                            @Override
                            public void onErrorOccurred(String errorMsg) {

                            }
                        });
                    }
                }
                return false;
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        searchCard.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
        loadingText.setVisibility(View.INVISIBLE);
        loadingText.setText(R.string.loading);
        progressBar.setIndeterminate(true);
    }

    private void doKoreanSearch(final String entry){
        Server.requestKoreanSearch(entry, getApplicationContext(), new Server.ServerListener() {
            @Override
            public void onResultReceived(final ArrayList<Conjugation> conjugations, HashMap<String, String> searchResults) {
                progressBar.setIndeterminate(false);
                progressBar.setProgress(100);
                loadingText.setText(R.string.main_results_found);

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
            public void onErrorOccurred(String errorMsg) {
                System.out.println("error:" + errorMsg);
            }
        });
    }

    private static boolean isHangul(String korean){
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