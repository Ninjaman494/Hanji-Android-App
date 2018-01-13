package com.a494studios.koreanconjugator;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import com.a494studios.koreanconjugator.parsing.Category;
import com.a494studios.koreanconjugator.parsing.Form;
import com.a494studios.koreanconjugator.parsing.Formality;
import com.a494studios.koreanconjugator.parsing.Tense;
import com.a494studios.koreanconjugator.settings.LegalDisplayActivity;
import com.eggheadgames.aboutbox.AboutConfig;
import com.eggheadgames.aboutbox.IDialog;
import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.LibsBuilder;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by akash on 1/9/2018.
 */

public class Utils {
    public static final String PREF_LUCKY_KOR = "pref_luckyKorean";
    public static final String PREF_LUCKY_ENG = "pref_luckyEnglish";
    public static final String PREF_FAV_COUNT = "pref_fav_count";
    private static final String PREF_FAV_KEYS = "FAVORITES_KEYS";
    private static final String PREF_FAV_VALUES = "FAVORITES_VALUES";
    private static final String PREF_FIRST_BOOT = "FIRST_BOOT";

    private static final String DEFAULT_FAV_KEYS = "Past,Present,Future,";
    private static final String DEFAULT_FAV_VALUES = "INFORMAL_HIGH:DECLARATIVE:PAST:,INFORMAL_HIGH:DECLARATIVE:PRESENT:,INFORMAL_HIGH:DECLARATIVE:FUTURE:,";

    public static boolean isFirstBoot(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(PREF_FIRST_BOOT,true);
    }

    public static void setFirstBoot(Context context, boolean firstBoot){
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(PREF_FIRST_BOOT,firstBoot).apply();
    }

    public static boolean getKoreanLuck(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(PREF_LUCKY_KOR, false);
    }

    public static boolean getEnglishLuck(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(PREF_LUCKY_ENG, false);
    }

    public static int getFavCount(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(PREF_FAV_COUNT, 3);
    }

    public static void setFavCount(int count, Context context){
        PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(PREF_FAV_COUNT, count).apply();
    }

    private static void setFavorites(ArrayList<String> keys, ArrayList<Category[]> values, Context context) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        StringBuilder keyBuilder = new StringBuilder();
        for (String key : keys) {
            keyBuilder.append(key).append(",");
        }
        editor.putString(PREF_FAV_KEYS, keyBuilder.toString());
        StringBuilder valueBuilder = new StringBuilder();
        for (Category[] value : values) {
            for(Category c: value) {
                if(c == null){
                    valueBuilder.append("null").append(":");
                }else {
                    valueBuilder.append(c.getType()).append(":");
                }
            }
            valueBuilder.append(",");
        }
        editor.putString(PREF_FAV_VALUES, valueBuilder.toString());
        editor.apply();
    }

    public static void setFavorites(ArrayList<Map.Entry<String,Category[]>> data, Context context){
        ArrayList<String> keys = new ArrayList<>();
        ArrayList<Category[]> values = new ArrayList<>();
        for(Map.Entry<String,Category[]> entry : data){
            keys.add(entry.getKey());
            values.add(entry.getValue());
        }
        setFavorites(keys,values,context);
        setFavCount(data.size(),context);
    }

    public static ArrayList<Map.Entry<String,Category[]>> getFavorites(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        ArrayList<Map.Entry<String, Category[]>> outputMap = new ArrayList<>();

        String keyString = pref.getString(PREF_FAV_KEYS,DEFAULT_FAV_KEYS);
        String valString = pref.getString(PREF_FAV_VALUES,DEFAULT_FAV_VALUES);
        if(keyString.equals("") || valString.equals("")){
            return outputMap;
        }

        String[] keys = keyString.split(",");
        String[] values = valString.split(",");
        for(int i=0;i<values.length;i++){
            String s = values[i];
            String[] catStrings = s.split(":");
            Category[] categories = new Category[3];
            for(int j=0;j<catStrings.length;j++){
                categories[j] = Category.Categories.valueOf(catStrings[j]);
            }
            outputMap.add(new AbstractMap.SimpleEntry<>(keys[i],categories));
        }
        return outputMap;
    }

    public static boolean isHangul(String korean){
        korean = korean.replace(" ","");
        for(int i=0;i<korean.length();i++){
            char c = korean.charAt(i);
            if(!((int)c >= '가' && (int)c <= '힣')){
                return false;
            }
        }
        return true;
    }

    public static Formality generateFormality(String type) {
        if (type.contains(Formality.INFORMAL_LOW.toString())) {
            return Formality.INFORMAL_LOW;
        } else if (type.contains(Formality.INFORMAL_HIGH.toString())) {
            return Formality.INFORMAL_HIGH;
        } else if (type.contains(Formality.FORMAL_LOW.toString())) {
            return Formality.FORMAL_LOW;
        } else if (type.contains(Formality.FORMAL_HIGH.toString())) {
            return Formality.FORMAL_HIGH;
        } else {
            return Formality.NONE;
        }
    }

    public static Tense generateTense(String type){
        if(type.contains(Tense.FUT_COND.toString())){
            return Tense.FUT_COND;
        }else if(type.contains(Tense.FUTURE.toString())){
            return Tense.FUTURE;
        }else if(type.contains(Tense.PRESENT.toString())){
            return Tense.PRESENT;
        }else if(type.contains(Tense.PAST.toString())){
            return Tense.PAST;
        }else{
            return Tense.NONE;
        }
    }

    public static Form generateForm(String type) {
        if (type.contains(Form.DECLARATIVE.toString())) {
            return Form.DECLARATIVE;
        } else if (type.contains(Form.INQUISITIVE.toString())) {
            return Form.INQUISITIVE;
        } else if (type.contains(Form.IMPERATIVE.toString())) {
            return Form.IMPERATIVE;
        } else if (type.contains(Form.PROPOSITIVE.toString())) {
            return Form.PROPOSITIVE;
        } else if (type.contains(Form.CON_IF.toString())) {
            return Form.CON_IF;
        } else if (type.contains(Form.CON_AND.toString())) {
            return Form.CON_AND;
        } else if (type.contains(Form.NOMINAL.toString())) {
            return Form.NOMINAL;
        } else if (type.contains(Form.PAST_BASE.toString())) {
            return Form.PAST_BASE;
        } else if (type.contains(Form.FUTURE_BASE.toString())) {
            return Form.FUTURE_BASE;
        } else {
            return null;
        }
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
        // Contact Support email details
        aboutConfig.emailAddress = "494studios@gmail.com";
        aboutConfig.emailSubject = "Hanji - Contact Us";
        aboutConfig.privacyHtmlPath = "file:///android_asset/PrivacyPolicy.html";
        aboutConfig.acknowledgmentHtmlPath = "www.google.com";
        // Custom handler for Acknowledgements and Privacy Policy options
        aboutConfig.dialog = new IDialog() {
            @Override
            public void open(AppCompatActivity appCompatActivity, String url, String tag) {
                if(tag.equals(activity.getString(R.string.egab_privacy_policy))) {
                    Intent intent = new Intent(activity,LegalDisplayActivity.class);
                    intent.putExtra("type",LegalDisplayActivity.TYPE_PRIV_POLICY);
                    activity.startActivity(intent);
                }else if(tag.equals(activity.getString(R.string.egab_acknowledgements))){
                    new LibsBuilder()
                            .withActivityStyle(Libs.ActivityStyle.LIGHT_DARK_TOOLBAR)
                            .withExcludedLibraries("support_cardview","support_v4","support_annotations","AppCompat","appcompat_v7","recyclerview_v7","GooglePlayServices","design","volleyplus")
                            .withLicenseDialog(true)
                            .withLicenseShown(true)
                            .withActivityTitle("Libraries Used")
                            .withLibraries("aboutBox","linear_list","transitions")
                            .start(activity);
                }
            }
        };
    }
}
