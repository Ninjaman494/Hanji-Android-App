package com.a494studios.koreanconjugator;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.a494studios.koreanconjugator.parsing.Conjugation;
import com.a494studios.koreanconjugator.parsing.Server;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final EditText editText = findViewById(R.id.editText);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    String entry = editText.getText().toString().trim();
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
                                        Intent intent = new Intent(getApplicationContext(), SearchResultsActivity.class);
                                        intent.putExtra("search", searchResults);
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

    private void doKoreanSearch(String entry){
        Server.requestKoreanSearch(entry, getApplicationContext(), new Server.ServerListener() {
            @Override
            public void onResultReceived(ArrayList<Conjugation> conjugations, HashMap<String, String> searchResults) {
                if (conjugations != null) {
                    Intent intent = new Intent(getApplicationContext(), DisplayActivity.class);
                    intent.putExtra(DisplayActivity.EXTRA_CONJ, conjugations);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                } else if (searchResults != null) {
                    Intent intent = new Intent(getApplicationContext(), SearchResultsActivity.class);
                    intent.putExtra("search", searchResults);
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
            //System.out.println("char:"+c);
            if(!((int)c >= '가' && (int)c <= '힣')){
                return false;
            }
        }
        return true;
    }
}