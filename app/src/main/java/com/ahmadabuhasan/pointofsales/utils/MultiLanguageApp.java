package com.ahmadabuhasan.pointofsales.utils;

import static com.ahmadabuhasan.pointofsales.utils.AppConfig.initializeCustomValue;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.MobileAds;

/*
 * Created by Ahmad Abu Hasan (C) 2022
 */

public class MultiLanguageApp extends Application {

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(LocaleManager.setLocale(context));
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration configuration) {
        super.onConfigurationChanged(configuration);
        LocaleManager.setLocale(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        initializeCustomValue();
        MobileAds.initialize(this);
    }
}