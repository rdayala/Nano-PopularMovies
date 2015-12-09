package com.example.android.popularmovies.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Class used for storing shared preferences data in the App
 * Created by rdayala
 * https://guides.codepath.com/android/Storing-and-Accessing-SharedPreferences
 */

public class PreferenceUtil {

    public static void savePrefs(Context context, String key, boolean value) {
        SharedPreferences mSettings  = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static boolean getPrefs(Context context, String key, boolean defaultValue) {
        SharedPreferences mSettings = PreferenceManager.getDefaultSharedPreferences(context);
        return mSettings.getBoolean(key, defaultValue);
    }

    public static void savePrefs(Context context, String key, int value) {
        SharedPreferences mSettings = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public static int getPrefs(Context context, String key, int defaultValue) {
        SharedPreferences mSettings = PreferenceManager.getDefaultSharedPreferences(context);
        return mSettings.getInt(key, defaultValue);
    }

    public static void savePrefs(Context context, String key, long value) {
        SharedPreferences mSettings = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putLong(key, value);
        editor.commit();
    }

    public static long getPrefs(Context context, String key, long defaultValue) {
        SharedPreferences mSettings = PreferenceManager.getDefaultSharedPreferences(context);
        return mSettings.getLong(key, defaultValue);
    }

    // [String] - save and get preferences for String data

    public static void savePrefs(Context context, String key, String value) {
        SharedPreferences mSettings = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getPrefs(Context context, String key, String defaultValue) {
        SharedPreferences mSettings = PreferenceManager.getDefaultSharedPreferences(context);
        return mSettings.getString(key, defaultValue);
    }
}
