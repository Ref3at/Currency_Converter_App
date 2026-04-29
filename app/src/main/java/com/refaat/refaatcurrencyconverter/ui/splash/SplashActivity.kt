package com.refaat.refaatcurrencyconverter.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.refaat.refaatcurrencyconverter.ui.currencyConversion.ActivityConversion
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            delay(1500)
            startActivity(Intent(this@SplashActivity, ActivityConversion::class.java))
            finish()
        }
    }
}
