package com.a494studios.koreanconjugator;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
                        public void onConjugationReceived(Conjugation conjugation) {
                                textView.setText(conjugation.getType()+":"+conjugation.getConjugated());
                                textView.setVisibility(View.VISIBLE);
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
