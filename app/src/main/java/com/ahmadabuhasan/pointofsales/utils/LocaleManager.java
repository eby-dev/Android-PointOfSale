package com.ahmadabuhasan.pointofsales.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.preference.PreferenceManager;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Locale;

/*
 * Created by Ahmad Abu Hasan (C) 2022
 */

public class LocaleManager {

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({ENGLISH, INDONESIAN})
    public @interface LocaleDef {
        String[] SUPPORTED_LOCALES = {
                ENGLISH,
                INDONESIAN
        };
    }

    public static final String ENGLISH = "en";
    public static final String INDONESIAN = "in";

    /**
     * SharedPreferences Key
     */
    static final String LANGUAGE_KEY = "language_key";

    /**
     * set current pref locale
     */
    public static Context setLocale(Context context) {
        return updateResources(context, getLanguagePref(context));
    }

    /**
     * Set new Locale with context
     */
    public static Context setNewLocale(Context context, @LocaleDef String language) {
        setLanguagePref(context, language);
        return updateResources(context, language);
    }

    /**
     * Get saved Locale from SharedPreferences
     *
     * @param context current Context
     * @return current locale key by default return english locale
     */
    public static String getLanguagePref(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(LANGUAGE_KEY, ENGLISH);
    }

    /**
     * set pref key
     */
    private static void setLanguagePref(Context context, String localeKey) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putString(LANGUAGE_KEY, localeKey).apply();
    }

    /**
     * update resource
     */
    private static Context updateResources(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Resources resources = context.getResources();
        Configuration configuration = new Configuration(resources.getConfiguration());
        if (Build.VERSION.SDK_INT >= 17) {
            configuration.setLocale(locale);
            context = context.createConfigurationContext(configuration);
        } else {
            configuration.locale = locale;
            resources.updateConfiguration(configuration, resources.getDisplayMetrics());
        }
        return context;
    }

    /**
     * get current locale
     */
    public static Locale getLocale(Resources resources) {
        Configuration configuration = resources.getConfiguration();
        return Build.VERSION.SDK_INT >= 24 ? configuration.getLocales().get(0) : configuration.locale;
    }
}