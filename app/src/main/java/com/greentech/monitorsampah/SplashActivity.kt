package com.greentech.monitorsampah

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import com.greentech.monitorsampah.screen.SplashScreen
import com.greentech.monitorsampah.ui.theme.MonitorSampahTheme
import kotlinx.coroutines.delay

@SuppressLint("CustomSplashScreen")
class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MonitorSampahTheme {
                SplashScreen(effect = {
                    LaunchedEffect(key1 = true) {
                        delay(2000)
                        startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                    }
                })
            }
        }
    }
}