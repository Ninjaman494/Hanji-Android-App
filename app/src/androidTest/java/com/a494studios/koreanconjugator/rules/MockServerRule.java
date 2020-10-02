package com.a494studios.koreanconjugator.rules;

import com.a494studios.koreanconjugator.MockApplication;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import okhttp3.mockwebserver.MockWebServer;

public class MockServerRule implements TestRule {
    public final MockWebServer server = new MockWebServer();
    private MockApplication app;

    public MockServerRule(MockApplication app) {
        this.app = app;
    }

    @Override
    public Statement apply(Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                server.start();
                app.setServerUrl(server.url("/").toString());
                base.evaluate();
                server.shutdown();
            }
        };
    }
}
