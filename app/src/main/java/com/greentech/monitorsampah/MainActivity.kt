package com.greentech.monitorsampah

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.greentech.monitorsampah.screen.BinDetailScreen
import com.greentech.monitorsampah.screen.HomeScreen
import com.greentech.monitorsampah.screen.MapsScreen
import com.greentech.monitorsampah.screen.SplashScreen
import com.greentech.monitorsampah.service.SensorDataService
import com.greentech.monitorsampah.ui.theme.MonitorSampahTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Memulai SensorDataService
        Intent(this, SensorDataService::class.java).also { intent ->
            startForegroundService(intent)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 101)
            }
        }


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
                    composable(
                        route = "maps?truck={truck}&capacity={capacity}",
                        arguments = listOf(
                            navArgument("truck") { nullable = true },
                            navArgument("capacity") { nullable = true }
                        )
                    ) { backStackEntry ->
                        val truck = backStackEntry.arguments?.getString("truck")
                        val capacity = backStackEntry.arguments?.getString("capacity")
                        MapsScreen(truck, capacity)
                    }
                    composable("bin_detail/{binId}") { backStackEntry ->
                        val binId = backStackEntry.arguments?.getString("binId")
                        if (binId != null) {
                            BinDetailScreen(binId = binId)
                        }
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