package com.nbsp.translator.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.nbsp.translator.ThemeManager;

/**
 * Created by Dimorinny on 27.09.15.
 */
public class PrefUtils {

    public static final String PREF_CURRENT_THEME_KEY = "current_theme_key";

    public static void setCurrentTheme(Context context, int currentTheme) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        sp.edit().putInt(PREF_CURRENT_THEME_KEY, currentTheme)
                .apply();
    }

    public static int getCurrentTheme(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getInt(PREF_CURRENT_THEME_KEY, ThemeManager.ERR_THEME_NOT_FOUND);
    }
}
