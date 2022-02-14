package com.refaat.refaatcurrencyconverter.ui.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.refaat.refaatcurrencyconverter.ui.currencyConversion.ActivityConversion

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Handler(Looper.getMainLooper()).postDelayed({
            // Your Code
            val intent = Intent(this, ActivityConversion::class.java)
            startActivity(intent)
            finish()
        }, 2000)
    }
}