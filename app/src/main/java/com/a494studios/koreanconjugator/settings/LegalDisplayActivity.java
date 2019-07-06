package com.a494studios.koreanconjugator.settings;

import android.content.DialogInterface;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MenuItem;
import android.webkit.WebView;

import com.a494studios.koreanconjugator.R;
import com.a494studios.koreanconjugator.utils.ErrorDialogFragment;
import com.crashlytics.android.Crashlytics;


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
        String type = getIntent().getStringExtra("type");
        if(type == null){ // Null check for extra
            ErrorDialogFragment.newInstance().setListener(new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    onBackPressed();
                }
            }).show(getSupportFragmentManager(),"error_dialog");
            Crashlytics.log("Type was null in LegalDisplayActivity");
        }else {
            switch (type) {
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
