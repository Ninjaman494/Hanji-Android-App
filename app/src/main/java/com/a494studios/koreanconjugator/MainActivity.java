package com.a494studios.koreanconjugator;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    String url = "http://192.168.1.9:3000/search=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final TextView textView = findViewById(R.id.textView);
        final EditText editText = findViewById(R.id.editText);
        final Response.Listener<JSONArray> listener = new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            JSONObject entry = (JSONObject)response.get(1);
                            textView.setText(entry.getString("conjugated"));
                            textView.setVisibility(View.VISIBLE);
                        } catch (Exception e){
                            e.printStackTrace();
                        }
                    }
        };
        final Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error.getMessage());
            }
        };

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    try {
                        String word = URLEncoder.encode(editText.getText().toString(),"UTF-8");
                        Log.d("MAIN","request url:" + url + word);
                        JsonArrayRequest jsRequest = new JsonArrayRequest(Request.Method.GET, url+word, null, listener, errorListener);
                        Volley.newRequestQueue(getApplicationContext()).add(jsRequest);
                    } catch (UnsupportedEncodingException e){
                        e.printStackTrace();
                    }
                }
                return false;
            }
        });
    }
}
