package com.a494studios.koreanconjugator.suggestions;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.a494studios.koreanconjugator.CreateSuggestionMutation;
import com.a494studios.koreanconjugator.CustomApplication;
import com.a494studios.koreanconjugator.R;
import com.a494studios.koreanconjugator.parsing.Server;
import com.a494studios.koreanconjugator.type.ExampleInput;
import com.a494studios.koreanconjugator.utils.BaseActivity;
import com.a494studios.koreanconjugator.utils.Utils;
import com.apollographql.apollo.api.Response;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import io.reactivex.observers.DisposableObserver;

public class SuggestionActivity extends BaseActivity implements View.OnClickListener {

    public final static String EXTRA_ENTRY_ID = "EXTRA_ENTRY_ID";

    private String entryID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example_suggestion);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setTitle("Add to Entry");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        entryID = getIntent().getStringExtra(EXTRA_ENTRY_ID);
    }

    @Override
    @SuppressLint("CheckResult")
    public void onClick(View view) {
        TextInputEditText antonymInput = findViewById(R.id.suggestion_antonym);
        TextInputEditText synonymInput = findViewById(R.id.suggestion_synonym);
        TextInputEditText sentenceInput = findViewById(R.id.suggestion_sentence);
        TextInputEditText translationInput = findViewById(R.id.suggestion_translation);
        TextInputLayout sentenceLayout = findViewById(R.id.suggestion_sentenceLayout);
        TextInputLayout translationLayout = findViewById(R.id.suggestion_translationLayout);

        String antonym = antonymInput.getText().toString().trim();
        String synonym = synonymInput.getText().toString().trim();
        String sentence = sentenceInput.getText().toString().trim();
        String translation = translationInput.getText().toString().trim();

        // Clear error messages
        sentenceLayout.setError(null);
        translationLayout.setError(null);

        if(antonym.length() == 0 && synonym.length() == 0 && sentence.length() == 0 && translation.length() == 0){
            Toast.makeText(this, "At least one addition is required", Toast.LENGTH_SHORT).show();
            return;
        } else if (sentence.length() == 0 && translation.length() > 0) {
            sentenceLayout.setError("Sentence is required for example");
            return;
        } else if (sentence.length() > 0 && translation.length() == 0) {
            translationLayout.setError("Translation is required for example");
            return;
        }

        ExampleInput example = null;
        if(sentence.length() > 0 && translation.length() > 0) {
            example = ExampleInput.builder()
                    .sentence(sentence)
                    .translation(translation)
                    .build();
        }

        Server.createSuggestion(entryID, antonym, synonym, example, (CustomApplication)getApplication())
                .subscribeWith(new DisposableObserver<Response<CreateSuggestionMutation.Data>>() {
            @Override
            public void onNext(Response<CreateSuggestionMutation.Data> dataResponse) {
                if(dataResponse.getData().createEntrySuggestion().success()) {
                    Toast.makeText(getBaseContext(), "Succeeded!", Toast.LENGTH_SHORT).show();
                    SuggestionActivity.this.onBackPressed();
                } else {
                    Toast.makeText(getBaseContext(), "Failed!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                AppCompatActivity activity = SuggestionActivity.this;
                Utils.handleError(e, activity,12, (dialogInterface, i) -> activity.finish());
            }

            @Override
            public void onComplete() {

            }
        });
    }
}