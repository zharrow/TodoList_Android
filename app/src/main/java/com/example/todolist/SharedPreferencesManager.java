package com.example.todolist;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesManager {
    private static final String PREF_NAME = "task_manager_prefs";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_THEME = "theme";
    private static final String KEY_LANGUAGE = "language";
    private static final String KEY_NOTIFICATIONS = "notifications";
    private static final String KEY_FIRST_LAUNCH = "first_launch";

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static void saveUserId(Context context, long userId) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putLong(KEY_USER_ID, userId);
        editor.apply();
    }

    public static long getUserId(Context context) {
        return getSharedPreferences(context).getLong(KEY_USER_ID, -1);
    }

    public static void clearUserId(Context context) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.remove(KEY_USER_ID);
        editor.apply();
    }

    public static boolean isLoggedIn(Context context) {
        return getUserId(context) != -1;
    }

    public static void saveTheme(Context context, String theme) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(KEY_THEME, theme);
        editor.apply();
    }

    public static String getTheme(Context context) {
        return getSharedPreferences(context).getString(KEY_THEME, "system");
    }

    public static void saveNotificationsEnabled(Context context, boolean enabled) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putBoolean(KEY_NOTIFICATIONS, enabled);
        editor.apply();
    }

    public static boolean areNotificationsEnabled(Context context) {
        return getSharedPreferences(context).getBoolean(KEY_NOTIFICATIONS, true);
    }



    public static void setFirstLaunch(Context context, boolean isFirstLaunch) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putBoolean(KEY_FIRST_LAUNCH, isFirstLaunch);
        editor.apply();
    }

    public static boolean isFirstLaunch(Context context) {
        return getSharedPreferences(context).getBoolean(KEY_FIRST_LAUNCH, true);
    }

    public static void clearAllPreferences(Context context) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.clear();
        editor.apply();
    }
}
