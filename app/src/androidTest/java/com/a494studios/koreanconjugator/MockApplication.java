package com.a494studios.koreanconjugator;

public class MockApplication extends CustomApplication {
    private String serverUrl;

    @Override
    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String url) {
        this.serverUrl = url;
    }
}
