package com.a494studios.koreanconjugator.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.widget.Toast;

import org.rm3l.maoni.common.contract.Listener;
import org.rm3l.maoni.common.model.Feedback;
import allbegray.slack.SlackClientFactory;
import allbegray.slack.exception.SlackException;
import allbegray.slack.webapi.SlackWebApiClient;

public class SlackListener implements Listener {

    Activity context;
    SlackWebApiClient webApiClient;

    public SlackListener(Activity context){
        this.context  = context;
        webApiClient = SlackClientFactory.createWebApiClient("xoxb-423273386706-423066381252-fFNL8ZlJ0YQsKZg7Kd7cdyJx");
        new Thread() {
            @Override
            public void run() {
                System.out.println(webApiClient.auth());
            }
        }.start();
    }

    @Override
    public void onDismiss() {

    }

    @Override
    public boolean onSendButtonClicked(final Feedback feedback) {
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

        new AlertDialog.Builder(context)
                .setTitle("Thanks for Your Feedback")
                .setMessage("We're sending your feedback now. You can continue using Hanji and we'll let you know when it's done.")
                .setPositiveButton(android.R.string.ok,null)
                .create()
                .show();
        return true;
    }
}
