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
import com.a494studios.koreanconjugator.utils.ErrorDialogFragment;
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
            actionBar.setElevation(0);
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

        TextInputLayout antonymLayout = findViewById(R.id.suggestion_antonymLayout);
        TextInputLayout synonymLayout = findViewById(R.id.suggestion_synonymLayout);
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

        // Language check
        if(sentence.length() > 0 && !Utils.isHangul(sentence)) {
            sentenceLayout.setError("Sentence must be in Korean");
            return;
        }
        if(translation.length() > 0 && Utils.isHangul(translation)) {
            translationLayout.setError("Translation must be in English");
            return;
        }
        if(antonym.length() > 0 && !Utils.isHangul(antonym)) {
            antonymLayout.setError("Antonym must be in Korean");
            return;
        }
        if(synonym.length() > 0 && !Utils.isHangul(synonym)) {
            synonymLayout.setError("Synonym must be in Korean");
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
                AppCompatActivity activity = SuggestionActivity.this;
                ErrorDialogFragment fragment;

                CreateSuggestionMutation.CreateEntrySuggestion response = dataResponse.getData().createEntrySuggestion();
                if(response.success()) {
                    fragment = ErrorDialogFragment.newInstance("Sent for Review",
                            "Thanks! Your additions have been sent for review. If approved, they will be added to this entry.");
                    fragment.setListener((dialogInterface, i) -> activity.finish());
                } else {
                    fragment = ErrorDialogFragment.newInstance("Failed to send addition",
                            "Error: " + response.message() + ".Please try again later or contact support.");
                }

                activity.getSupportFragmentManager()
                        .beginTransaction()
                        .add(fragment,"frag_alert")
                        .commitAllowingStateLoss();
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                Utils.handleError(e, SuggestionActivity.this,12);
            }

            @Override
            public void onComplete() {

            }
        });
    }
}