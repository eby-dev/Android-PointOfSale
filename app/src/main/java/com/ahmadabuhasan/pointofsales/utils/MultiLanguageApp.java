package com.ahmadabuhasan.pointofsales.utils;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;

/*
 * Created by Ahmad Abu Hasan (C) 2022
 */

public class MultiLanguageApp extends Application {

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(LocaleManager.setLocale(context));
    }

    @Override
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        LocaleManager.setLocale(this);
    }
}