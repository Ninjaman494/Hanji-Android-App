package com.a494studios.koreanconjugator.settings;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.webkit.WebView;

import com.a494studios.koreanconjugator.R;


public class LegalDisplayActivity extends AppCompatActivity {

    public static final String TYPE_PRIV_POLICY = "PRIVACY_POLICY";
    private static final String TYPE_TERMS_COND = "TERMS_CONDITIONS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_legal_display);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        WebView wv = findViewById(R.id.webview);
        switch(getIntent().getStringExtra("type")){
            case TYPE_PRIV_POLICY:
                getSupportActionBar().setTitle("Privacy Policy");
                wv.loadUrl("file:///android_asset/PrivacyPolicy.html");
                break;
            case TYPE_TERMS_COND:
                getSupportActionBar().setTitle("Terms and Conditions of Use");
                wv.loadUrl("file:///android_asset/TCU.html");
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.onBackPressed();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}
