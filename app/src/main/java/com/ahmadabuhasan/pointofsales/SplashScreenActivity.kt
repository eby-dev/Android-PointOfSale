package com.ahmadabuhasan.pointofsales

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.credentials.CredentialsApi

/*
 * Created by Ahmad Abu Hasan (C) 2022
 */

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {

    companion object {
        const val SPLASH_TIME_OUT: Int = CredentialsApi.CREDENTIAL_PICKER_REQUEST_CODE + 1000
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.requestFeature(Window.FEATURE_NO_TITLE)
        @Suppress("DEPRECATION")
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_splash_screen)
        supportActionBar?.hide()

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, DashboardActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
            finish()
        }, SPLASH_TIME_OUT.toLong())
    }
}