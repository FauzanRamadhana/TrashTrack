package com.greentech.monitorsampah

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.greentech.monitorsampah.screen.HomeScreen
import com.greentech.monitorsampah.screen.MapsScreen
import com.greentech.monitorsampah.screen.SplashScreen
import com.greentech.monitorsampah.ui.theme.MonitorSampahTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MonitorSampahTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "splash") {
                    composable("splash") {
                        SplashScreen(navController = navController)
                    }
                    composable("home") {
                        HomeScreen(navController = navController)
                    }
                    composable("maps") {
                        MapsScreen(navController = navController)
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MonitorSampahTheme {
    }
}