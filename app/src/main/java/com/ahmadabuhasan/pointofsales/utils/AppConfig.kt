package com.ahmadabuhasan.pointofsales.utils

import com.ahmadabuhasan.pointofsales.BuildConfig

class AppConfig {

    companion object {
        init {
            System.loadLibrary("native-lib")
        }

        @JvmStatic
        external fun setMode(customValue: String)

        @JvmStatic
        fun initializeCustomValue() {
            setMode(BuildConfig.SERVER_KEY)
        }

        @JvmStatic
        external fun baseUrl(): String

        @JvmStatic
        external fun apiKey(): String
    }

}