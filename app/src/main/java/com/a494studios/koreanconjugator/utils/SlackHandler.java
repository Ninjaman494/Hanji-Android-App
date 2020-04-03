package com.a494studios.koreanconjugator.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.a494studios.koreanconjugator.BuildConfig;
import com.a494studios.koreanconjugator.R;
import com.crashlytics.android.Crashlytics;

import org.rm3l.maoni.common.contract.Handler;
import org.rm3l.maoni.common.model.Feedback;
import allbegray.slack.SlackClientFactory;
import allbegray.slack.exception.SlackException;
import allbegray.slack.exception.SlackResponseErrorException;
import allbegray.slack.webapi.SlackWebApiClient;

public class SlackHandler implements Handler {

    private static final String SLACK_DIALOG_FEEDBACK = "reviews";
    private static final String SLACK_REPORT_BUG = "bugs";
    private static final String SLACK_FEATURE_IDEA = "feature-ideas";
    private static final String SLACK_OTHER_FEEDBACK = "other-feedback";

    private AppCompatActivity context;
    private SlackWebApiClient webApiClient;
    private RadioGroup radioGroup;

    public SlackHandler(AppCompatActivity context){
        this.context  = context;
        webApiClient = SlackClientFactory.createWebApiClient(BuildConfig.SLACK_KEY);
    }

    @Override
    public void onCreate(View view, Bundle bundle) {
        radioGroup = view.findViewById(R.id.extra_radiogroup);
    }

    @Override
    public boolean validateForm(View view) {
        return radioGroup.getCheckedRadioButtonId() != -1; // An option needs to be selected
    }

    public boolean auth(){
        final boolean success[] = new boolean[1];
        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    System.out.println(webApiClient.auth());
                    success[0] = true;
                }catch (SlackResponseErrorException e) {
                    Crashlytics.log("Wrong token code for Slack app?");
                    Crashlytics.logException(e);
                    success[0] = false;
                }catch (SlackException e){
                    e.printStackTrace();
                    success[0] = false;
                }
            }
        };
        t.start();
        try {
            t.join();
        }catch (InterruptedException e){
            success[0] = false;
        }
        return success[0];
    }

    @Override
    public void onDismiss() {

    }

    // For Maoni Activity
    @Override
    public boolean onSendButtonClicked(final Feedback feedback) {
        if(!isConnected()){
            Utils.displayErrorDialog(context,
                    "Internet Connection Required",
                    "We need an internet connection to send feedback. Please try again when you have an active connection.",
                    null);
            return true;
        }

        new Thread() {
            @Override
            public void run() {
                final StringBuilder body = new StringBuilder();

                body.append(feedback.userComment).append("\n\n");

                body.append("\n------ Application ------\n");
                if (feedback.appInfo != null) {
                    if (feedback.appInfo.caller != null) {
                        body.append("- Activity: ").append(feedback.appInfo.caller).append("\n");
                    }
                    if (feedback.appInfo.buildType != null) {
                        body.append("- Build Type: ").append(feedback.appInfo.buildType).append("\n");
                    }
                    if (feedback.appInfo.versionName != null) {
                        body.append("- Version Name: ").append(feedback.appInfo.versionName).append("\n");
                    }
                }

                body.append("\n------ Device ------\n");
                if (feedback.deviceInfo != null) {
                    body.append("- brand: ").append(feedback.deviceInfo.brand).append("\n");
                    body.append("- isTablet: ").append(feedback.deviceInfo.isTablet).append("\n");
                    body.append("- manufacturer: ").append(feedback.deviceInfo.manufacturer).append("\n");
                    body.append("- model: ").append(feedback.deviceInfo.model).append("\n");
                    body.append("- resolution: ").append(feedback.deviceInfo.resolution).append("\n");
                    body.append("- sdkVersion: ").append(feedback.deviceInfo.sdkVersion).append("\n");
                }
                body.append("\n\n");

                try {
                    int radioBtn = radioGroup.getCheckedRadioButtonId();
                    String channel;
                    switch (radioBtn){
                        case R.id.extra_bug_btn:
                            channel = SLACK_REPORT_BUG;
                            break;
                        case R.id.extra_feature_btn:
                            channel = SLACK_FEATURE_IDEA;
                            break;
                        default: // saying hi
                            channel = SLACK_OTHER_FEEDBACK;
                            break;
                    }

                    if(feedback.screenshotFile != null) {
                        webApiClient.uploadFile(feedback.screenshotFile, "screenshot", body.toString(), channel);
                    }else { // User opted-out of sharing a screenshot
                        webApiClient.postMessage(channel,body.toString());
                    }
                }catch (SlackException e){
                    e.printStackTrace();
                }finally {
                    webApiClient.shutdown();
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, R.string.feedback_sent,Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        }.start();
        Toast.makeText(context, R.string.feedback_sending,Toast.LENGTH_SHORT).show();
        return true;
    }

    // For Feedback Dialog
    public void sendFeedback(final String feedback){
        new Thread() {
            @Override
            public void run() {
                try {
                    webApiClient.postMessage(SLACK_DIALOG_FEEDBACK, feedback);
                    webApiClient.shutdown();
                } catch (SlackException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private boolean isConnected(){
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm != null ? cm.getActiveNetworkInfo() : null;
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
}
