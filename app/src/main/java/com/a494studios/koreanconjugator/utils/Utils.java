package com.a494studios.koreanconjugator.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.a494studios.koreanconjugator.BuildConfig;
import com.a494studios.koreanconjugator.CustomApplication;
import com.a494studios.koreanconjugator.R;
import com.a494studios.koreanconjugator.parsing.Favorite;
import com.a494studios.koreanconjugator.parsing.FavoriteSerializer;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.apollographql.apollo.api.Error;
import com.apollographql.apollo.exception.ApolloNetworkException;
import com.eggheadgames.aboutbox.AboutConfig;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.mikepenz.aboutlibraries.LibsBuilder;

import org.rm3l.maoni.Maoni;

import java.util.ArrayList;
import java.util.Locale;

import javax.annotation.Nullable;

/**
 * Created by akash on 1/9/2018.
 */

public class Utils {
    public static final String PREF_FAV_COUNT = "pref_fav_count";
    private static final String PREF_FAV_VALUES = "FAVORITES_VALUES";
    private static final String PREF_FIRST_BOOT = "FIRST_BOOT";
    private static final String PREF_FIRST_TWO = "FIRST_TWO";
    private static final String PREF_AD_FREE = "AD_FREE";
    public static final String SKU_AD_FREE = "ad_free_upgrade";

    public static boolean isFirstBoot(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(PREF_FIRST_BOOT,true);
    }

    public static void setFirstBoot(Context context, boolean firstBoot){
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(PREF_FIRST_BOOT,firstBoot).apply();
    }

    public static boolean isFirstTwo(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(PREF_FIRST_TWO, true);
    }

    public static void setFirstTwo(Context context, boolean firstTwo) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(PREF_FIRST_TWO, firstTwo).apply();
    }

    public static int getFavCount(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(PREF_FAV_COUNT, 3);
    }

    public static Boolean isAdFree(Context context) {
        if (PreferenceManager.getDefaultSharedPreferences(context).contains(PREF_AD_FREE)) {
            return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(PREF_AD_FREE, false);
        } else {
            return null;
        }
    }

    public static void setAdFree(Context context, boolean adFree) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(PREF_AD_FREE, adFree).apply();
    }

    public static void setFavorites(ArrayList<Favorite> data, Context context){
        Gson gson = new Gson();
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putString(PREF_FAV_VALUES,gson.toJson(data));
        editor.putInt(PREF_FAV_COUNT,data.size());
        editor.apply();
    }

    public static ArrayList<Favorite> getFavorites(Context context) {
        String jsonString = PreferenceManager.getDefaultSharedPreferences(context).getString(PREF_FAV_VALUES,"");
        if(jsonString.isEmpty()){
            return new ArrayList<>();
        }

        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeHierarchyAdapter(Favorite.class, new FavoriteSerializer());
        java.lang.reflect.Type type = new TypeToken<ArrayList<Favorite>>(){}.getType();

        ArrayList<Favorite> favorites;
        try {
            favorites = builder.create().fromJson(jsonString,type);
        } catch (Exception e) {
            FirebaseCrashlytics crashlytics = FirebaseCrashlytics.getInstance();
            crashlytics.log("Exception caught while parsing favorites");
            crashlytics.recordException(e);

            crashlytics.log("Resetting favorites");
            favorites = new ArrayList<>();
            favorites.add(new Favorite("Past","declarative past informal high",false));
            favorites.add(new Favorite("Present","declarative present informal high",false));
            favorites.add(new Favorite("Future","declarative future informal high",false));
            setFavorites(favorites, context);
            crashlytics.log("Reset favorites");
        }

        return favorites;
    }

    public static String toTitleCase(String string) {
        StringBuilder titleCase = new StringBuilder();
        String[] words = string.split("\\s+");
        for(String word: words) {
            String newWord = Character.toTitleCase(word.charAt(0)) + word.substring(1).toLowerCase();
            titleCase.append(newWord);
            titleCase.append(" ");
        }

        return titleCase.toString().trim();
    }

    public static void makeAboutBox(final Activity activity){
        final AboutConfig aboutConfig = AboutConfig.getInstance();
        aboutConfig.appName = activity.getString(R.string.app_name);
        aboutConfig.appIcon = R.mipmap.ic_launcher;
        aboutConfig.version = BuildConfig.VERSION_NAME;
        aboutConfig.author = "494 Studios";
        aboutConfig.aboutLabelTitle = "About App";
        aboutConfig.packageName = activity.getPackageName();
        aboutConfig.buildType = AboutConfig.BuildType.GOOGLE;
        aboutConfig.appPublisher = "494 Studios"; // app publisher for "Try Other Apps" item
        aboutConfig.privacyHtmlPath = "https://hanji.vercel.app/privacy";
        aboutConfig.acknowledgmentHtmlPath = "www.google.com";
        // Custom handler for Acknowledgements and Privacy Policy options
        aboutConfig.dialog = (appCompatActivity, url, tag) -> {
            if(tag.equals(activity.getString(R.string.egab_privacy_policy))) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://hanji.vercel.app/privacy"));
                activity.startActivity(intent);
            }else if(tag.equals(activity.getString(R.string.egab_acknowledgements))){
                new LibsBuilder()
                        .withExcludedLibraries("support_cardview","support_v4","support_annotations","AppCompat","appcompat_v7","recyclerview_v7","GooglePlayServices","design","volleyplus")
                        .withLicenseDialog(true)
                        .withLicenseShown(true)
                        .withAboutIconShown(false)
                        .withAboutVersionShownName(false)
                        .withAboutVersionShown(false)
                        .withActivityTitle("Libraries Used")
                        .start(activity);
            }
        };
    }

    @Nullable
    public static Maoni makeMaoniActivity(AppCompatActivity context){
        SlackHandler listener = new SlackHandler(context);
        if(!listener.auth()){
            displayErrorDialog(context,"Can't Connect to Server","Check your network settings and try again",null);
            return null;
        }
        return new Maoni.Builder(context, "com.a494studios.koreanconjugator.fileprovider")
                .enableScreenCapturingFeature()
                .withLogsCapturingFeature(false)
                .withHandler(listener)
                .withExtraLayout(R.layout.activity_maoni_extra)
                .withHeader(R.color.colorPrimary)
                .withTheme(R.style.AppTheme_NoActionBar)
                .build();
    }

    public static void showAdFreeUpgrade(Activity activity){
        if(!CustomApplication.isBillingConnected()) {
            Toast.makeText(activity, "Couldn't connect to Google Play, please try again later.", Toast.LENGTH_SHORT).show();
            return;
        }

        BillingClient billingClient = CustomApplication.getBillingClient();
        ArrayList<String> skuList = new ArrayList<>();
        skuList.add(SKU_AD_FREE);
        SkuDetailsParams params = SkuDetailsParams.newBuilder()
                .setSkusList(skuList)
                .setType(BillingClient.SkuType.INAPP)
                .build();
        billingClient.querySkuDetailsAsync(params, (billingResult, skuDetailsList) -> {
            if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                for (SkuDetails skuDetails : skuDetailsList) {
                    if (skuDetails.getSku().equals(SKU_AD_FREE)) {
                        BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                                .setSkuDetails(skuDetails)
                                .build();

                        activity.runOnUiThread(() -> {
                            BillingResult result = billingClient.launchBillingFlow(activity, flowParams);
                            if(result.getResponseCode() == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
                                Toast.makeText(activity,"You've already purchased an ad free upgrade", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            } else {
                System.out.println("Error getting SKU Details: " + billingResult.getResponseCode());
            }
        });
    }

    public static void handleError(Error error, AppCompatActivity context, DialogInterface.OnClickListener listener){
        ErrorDialogFragment fragment = ErrorDialogFragment.newInstance("Something went wrong", error.getMessage());

        if(listener != null){
            fragment.setListener(listener);
        }
        context.getSupportFragmentManager()
                .beginTransaction()
                .add(fragment,"frag_alert")
                .commitAllowingStateLoss();
    }

    public static void handleError(Throwable error, AppCompatActivity context, int errorCode, DialogInterface.OnClickListener listener){
        String message = "Error code: " + String.format(Locale.getDefault(), "%03d", errorCode);
        ErrorDialogFragment fragment;
        if(error instanceof ApolloNetworkException){
            message += ". Check your network settings and try again";
            fragment = ErrorDialogFragment.newInstance("Can't connect to server", message);
        } else {
            FirebaseCrashlytics.getInstance().recordException(error);
            message += ". Try again later or contact support";
            fragment = ErrorDialogFragment.newInstance("Something went wrong", message);
        }

        if(listener != null){
            fragment.setListener(listener);
        }
        context.getSupportFragmentManager()
                .beginTransaction()
                .add(fragment,"frag_alert")
                .commitAllowingStateLoss();
    }

    public static void handleError(Throwable error, AppCompatActivity context, int errorCode) {
        handleError(error,context,errorCode,null);
    }

    public static void displayErrorDialog(AppCompatActivity context, String title, String msg,DialogInterface.OnClickListener listener){
        ErrorDialogFragment fragment = ErrorDialogFragment.newInstance(title, msg);
        if(listener != null){
            fragment.setListener(listener);
        }

        context.getSupportFragmentManager()
                .beginTransaction()
                .add(fragment,"frag_alert")
                .commitAllowingStateLoss();
    }

    public static boolean isHangul(String korean){
        if(korean.isEmpty()){
            return false;
        }

        korean = korean.replace(" ","");
        for(int i=0;i<korean.length();i++){
            char c = korean.charAt(i);
            if(!((int)c >= '가' && (int)c <= '힣')){
                return false;
            }
        }
        return true;
    }
}
