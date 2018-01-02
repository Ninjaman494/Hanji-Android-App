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

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final TextView textView = findViewById(R.id.textView);
        final EditText editText = findViewById(R.id.editText);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    Server.requestConjugation(editText.getText().toString().trim(), getApplicationContext(), new Server.ServerListener() {
                        @Override
                        public void onConjugationReceived(ArrayList<Conjugation> conjugations) {
                                /*textView.setText(conjugation.getType()+":"+conjugation.getConjugated());
                                textView.setVisibility(View.VISIBLE);*/
                                Intent intent = new Intent(getApplicationContext(),DisplayActivity.class);
                                intent.putExtra("conj",conjugations);
                                startActivity(intent);
                        }

                        @Override
                        public void onErrorOccurred(String errorMsg) {
                            System.out.println(errorMsg);
                        }
                    });
                }
                return false;
            }
        });
    }
}
