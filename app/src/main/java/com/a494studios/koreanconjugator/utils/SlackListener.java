package com.a494studios.koreanconjugator.utils;

import org.rm3l.maoni.common.contract.Listener;
import org.rm3l.maoni.common.model.Feedback;
import allbegray.slack.SlackClientFactory;
import allbegray.slack.exception.SlackException;
import allbegray.slack.webapi.SlackWebApiClient;

public class SlackListener implements Listener {

    SlackWebApiClient webApiClient;

    public SlackListener(){
        webApiClient = SlackClientFactory.createWebApiClient("***REMOVED***");
        new Thread() {
            @Override
            public void run() {
                System.out.println(webApiClient.auth().getTeam());
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
                    body.append(feedback.deviceInfo.toString());
                }
                body.append("\n\n");

                try {
                    webApiClient.uploadFile(feedback.screenshotFile, "screenshot", body.toString(), "bugs");
                }catch (SlackException e){
                    e.printStackTrace();
                }finally {
                    webApiClient.shutdown();
                }
            }
        }.start();
        return true;
    }
}
