package com.a494studios.koreanconjugator;

import android.view.View;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class MockApplication extends CustomApplication {
    private String serverUrl;
    private boolean isAdFree;

    @Override
    public String getServerUrl() {
        return serverUrl;
    }

    @Override
    public void handleAdCard(AdView adView) {
        if (isAdFree) {
            adView.setVisibility(View.GONE);
        } else {
            adView.loadAd(new AdRequest.Builder().build());
        }
    }

    @Override
    public boolean isAdFree() {
        return isAdFree;
    }

    public void setServerUrl(String url) {
        this.serverUrl = url;
    }

    public void setAdFree(boolean isAdFree) {
        this.isAdFree = isAdFree;
    }
}
