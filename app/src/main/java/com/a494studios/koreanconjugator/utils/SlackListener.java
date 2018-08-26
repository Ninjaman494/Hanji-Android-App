package com.a494studios.koreanconjugator.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.a494studios.koreanconjugator.Utils;
import com.crashlytics.android.Crashlytics;

import org.rm3l.maoni.common.contract.Listener;
import org.rm3l.maoni.common.model.Feedback;
import allbegray.slack.SlackClientFactory;
import allbegray.slack.exception.SlackException;
import allbegray.slack.exception.SlackResponseErrorException;
import allbegray.slack.webapi.SlackWebApiClient;

public class SlackListener implements Listener {

    private AppCompatActivity context;
    private SlackWebApiClient webApiClient;

    public SlackListener(AppCompatActivity context){
        this.context  = context;
        webApiClient = SlackClientFactory.createWebApiClient("***REMOVED***");
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
                    webApiClient.uploadFile(feedback.screenshotFile, "screenshot", body.toString(), "bugs");
                }catch (SlackException e){
                    e.printStackTrace();
                }finally {
                    webApiClient.shutdown();
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context,"Feedback sent",Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        }.start();

        Utils.displayErrorDialog(context,
                "Thanks for Your Feedback",
                "We're sending your feedback now. You can continue using Hanji and we'll let you know when it's done.",
                null);
        return true;
    }

    private boolean isConnected(){
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm != null ? cm.getActiveNetworkInfo() : null;
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
}
