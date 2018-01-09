package com.a494studios.koreanconjugator;

import android.content.Context;
import android.preference.PreferenceManager;

/**
 * Created by akash on 1/9/2018.
 */

public class Utils {
    public static final String PREF_LUCKY_KOR = "pref_luckyKorean";
    public static final String PREF_LUCKY_ENG = "pref_luckyEnglish";

    public static boolean getKoreanLuck(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(PREF_LUCKY_KOR, false);
    }

    public static boolean getEnglishLuck(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(PREF_LUCKY_ENG, false);
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
