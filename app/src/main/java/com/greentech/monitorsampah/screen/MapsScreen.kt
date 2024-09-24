package com.greentech.monitorsampah.screen

import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.navigation.NavHostController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MarkerInfoWindowContent
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.greentech.monitorsampah.R
import com.greentech.monitorsampah.dataDummy
import com.greentech.monitorsampah.ui.theme.Green
import com.greentech.monitorsampah.ui.theme.Grey
import com.greentech.monitorsampah.ui.theme.GreyShadow

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun MapsScreen(modifier: Modifier = Modifier, navController: NavHostController) {
    Scaffold(topBar = {
        TopAppBar(
            modifier = modifier
                .border(
                    (2).dp,
                    brush = Brush.verticalGradient(listOf(Color.Transparent, GreyShadow)),
                    shape = RoundedCornerShape(10.dp)
                ),
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = null,
                        modifier = Modifier
                            .size(40.dp)
                            .padding(end = 5.dp)
                    )
                    Row {
                        Text(
                            text = "Trash",
                            fontSize = 30.sp,
                            fontFamily = cinzelFont,
                            color = Green,
                            fontWeight = FontWeight.ExtraBold,
                        )
                        Text(
                            text = "Track",
                            fontSize = 30.sp,
                            fontFamily = cinzelFont,
                            color = Grey,
                            fontWeight = FontWeight.Black,
                        )
                    }
                }
            }
        )
    }) { innerPadding ->
        val locationPermissionState = rememberMultiplePermissionsState(
            permissions = listOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )

        if (locationPermissionState.allPermissionsGranted) {

            val context = LocalContext.current
            val fusedLocationClient = remember {
                LocationServices.getFusedLocationProviderClient(context)
            }

            val cameraPositionState = rememberCameraPositionState()
            var myLocation by remember { mutableStateOf<LatLng?>(null) }

            // Detect system theme
            val isDarkTheme = isSystemInDarkTheme()
            val mapStyleOptions = MapStyleOptions.loadRawResourceStyle(
                context,
                if (isDarkTheme) R.raw.dark_map_style else R.raw.light_map_style
            )

            LaunchedEffect(Unit) {
                if (locationPermissionState.allPermissionsGranted) {
                    if (ActivityCompat.checkSelfPermission(
                            context,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                            context,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                    }
                    fusedLocationClient.lastLocation.addOnSuccessListener { loc ->
                        loc?.let {
                            myLocation = LatLng(it.latitude, it.longitude)
                            cameraPositionState.position = CameraPosition.fromLatLngZoom(myLocation!!, 15f)
                        }
                    }
                } else {
                    locationPermissionState.launchMultiplePermissionRequest()
                }
            }

            GoogleMap(
                modifier = modifier.padding(innerPadding),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(
                    isMyLocationEnabled = locationPermissionState.allPermissionsGranted,
                    mapStyleOptions = mapStyleOptions // Apply the map style
                ),
            ) {
                dataDummy.forEach { data ->
                    val location = LatLng(data.coordinate.first, data.coordinate.second)
                    MarkerInfoWindowContent(
                        state = MarkerState(position = location),
                    ) {
                        Column {
                            Text(text = "Tong Sampah ${dataDummy.indexOf(data) + 1}", fontWeight = FontWeight.ExtraBold, color = Color.Black)
                            Image(painter = painterResource(id = R.drawable.logo), contentDescription = null, modifier = modifier.size(150.dp))
                        }
                    }
                }
            }
        } else {
            LaunchedEffect(Unit) {
                locationPermissionState.launchMultiplePermissionRequest()
            }
        }
    }
}
