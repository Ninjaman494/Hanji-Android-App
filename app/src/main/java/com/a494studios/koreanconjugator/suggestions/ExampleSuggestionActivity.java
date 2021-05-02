package com.a494studios.koreanconjugator.suggestions;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.a494studios.koreanconjugator.CreateSuggestionMutation;
import com.a494studios.koreanconjugator.CustomApplication;
import com.a494studios.koreanconjugator.R;
import com.a494studios.koreanconjugator.parsing.Server;
import com.a494studios.koreanconjugator.type.ExampleInput;
import com.apollographql.apollo.api.Response;
import com.google.android.material.textfield.TextInputEditText;

import io.reactivex.observers.DisposableObserver;

public class ExampleSuggestionActivity extends AppCompatActivity implements View.OnClickListener {

    public final static String EXTRA_ENTRY_ID = "EXTRA_ENTRY_ID";

    private String entryID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example_suggestion);

        if(getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Suggest Example");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        entryID = getIntent().getStringExtra(EXTRA_ENTRY_ID);
    }

    @Override
    @SuppressLint("CheckResult")
    public void onClick(View view) {
        TextInputEditText sentenceInput = findViewById(R.id.exampleSuggestion_sentence);
        TextInputEditText translationInput = findViewById(R.id.exampleSuggestion_translation);
        ExampleInput example = ExampleInput.builder()
                .sentence(sentenceInput.getText().toString())
                .translation(translationInput.getText().toString())
                .build();

        Server.createExampleSuggestion(entryID, (CustomApplication)getApplication(), example)
                .subscribeWith(new DisposableObserver<Response<CreateSuggestionMutation.Data>>() {
            @Override
            public void onNext(Response<CreateSuggestionMutation.Data> dataResponse) {
                if(dataResponse.getData().createEntrySuggestion().success()) {
                    Toast.makeText(getBaseContext(), "Succeeeded!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getBaseContext(), "Failed!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace(System.err);
            }

            @Override
            public void onComplete() {

            }
        });

    }
}