package com.a494studios.koreanconjugator.suggestions;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.a494studios.koreanconjugator.R;

public class ExampleSuggestionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example_suggestion);

        if(getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Suggest Example");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }
}