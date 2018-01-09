package com.a494studios.koreanconjugator;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.a494studios.koreanconjugator.parsing.Category;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by akash on 1/9/2018.
 */

public class Utils {
    public static final String PREF_LUCKY_KOR = "pref_luckyKorean";
    public static final String PREF_LUCKY_ENG = "pref_luckyEnglish";
    public static final String PREF_FAV_KEYS = "FAVORITES_KEYS";
    public static final String PREF_FAV_VALUES = "FAVORITES_VALUES";

    private static final String DEFAULT_FAV_KEYS = "Past,Present,Future,";
    private static final String DEFAULT_FAV_VALUES = "INFORMAL_HIGH:DECLARATIVE:PAST:,INFORMAL_HIGH:DECLARATIVE:PRESENT:,INFORMAL_HIGH:DECLARATIVE:FUTURE:,";

    public static boolean getKoreanLuck(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(PREF_LUCKY_KOR, false);
    }

    public static boolean getEnglishLuck(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(PREF_LUCKY_ENG, false);
    }

    public static void setFavorites(ArrayList<String> keys, ArrayList<Category[]> values, Context context) {
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

    public static ArrayList<Map.Entry<String,Category[]>> getFavorites(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        ArrayList<Map.Entry<String, Category[]>> outputMap = new ArrayList<>();

        String[] keys = pref.getString(PREF_FAV_KEYS,DEFAULT_FAV_KEYS).split(",");
        String[] values = pref.getString(PREF_FAV_VALUES,DEFAULT_FAV_VALUES).split(",");
        for(int i=0;i<values.length;i++){
            String s = values[i];
            String[] catStrings = s.split(":");
            Category[] categories = new Category[catStrings.length];
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
}
