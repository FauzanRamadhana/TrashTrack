package com.greentech.monitorsampah.screen

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.location.Location
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.core.app.ActivityCompat
import coil.imageLoader
import coil.request.ImageRequest
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MarkerInfoWindowContent
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.greentech.monitorsampah.R
import com.greentech.monitorsampah.model.SensorData
import com.greentech.monitorsampah.ui.components.TrashTrackTopBar
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapsScreen(
    truck: String? = null,
    capacity: String? = null,
) {
    var sensorDataList by remember { mutableStateOf(emptyList<SensorData>()) }
    var isLoading by remember { mutableStateOf(true) }
    var myLocation by remember { mutableStateOf<LatLng?>(null) }
    var sortedBins by remember { mutableStateOf(emptyList<SensorData>()) }

    var totalVolume by remember { mutableDoubleStateOf(0.0) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val cameraPositionState = rememberCameraPositionState()

    // Fetch sensor data
    LaunchedEffect(Unit) {
        FirebaseDatabase.getInstance().reference.child("sensor_data")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    sensorDataList =
                        snapshot.children.mapNotNull { it.getValue(SensorData::class.java) }
                            .filter {
                                it.distance != null && it.imageUrl != null &&
                                        it.latitude != null && it.longitude != null &&
                                        it.binId != null && it.height != null && it.maxVolume != null
                            }
                    isLoading = false
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("FIREBASE", "Error: ${error.message}")
                    isLoading = false
                }
            })
    }

    // Calculate route
    LaunchedEffect(sensorDataList, myLocation) {
        if (myLocation != null && sensorDataList.isNotEmpty()) {
            val bins = sensorDataList.map {
                SensorData(
                    binId = it.binId,
                    latitude = it.latitude,
                    longitude = it.longitude,
                    distance = it.distance,
                    height = it.height,
                    maxVolume = it.maxVolume
                )
            }
            val parsedCapacity = capacity?.toDoubleOrNull() ?: Double.MAX_VALUE
            sortedBins = findNearestRoute(
                bins,
                myLocation!!.latitude,
                myLocation!!.longitude,
                parsedCapacity
            )
            totalVolume = sortedBins.sumOf { bin ->
                val filledHeight = bin.height - bin.distance
                val percentageFilled = filledHeight / bin.height
                (percentageFilled * bin.maxVolume).coerceAtLeast(0.0)
            }
        }
    }

    Scaffold(topBar = { TrashTrackTopBar() }) { innerPadding ->
        val locationPermissionState = rememberMultiplePermissionsState(
            listOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )

        if (locationPermissionState.allPermissionsGranted) {
            val fusedLocationClient = remember {
                LocationServices.getFusedLocationProviderClient(context)
            }

            val isDarkTheme = isSystemInDarkTheme()
            val mapStyleOptions = MapStyleOptions.loadRawResourceStyle(
                context,
                if (isDarkTheme) R.raw.dark_map_style else R.raw.light_map_style
            )

            LaunchedEffect(Unit) {
                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    fusedLocationClient.lastLocation.addOnSuccessListener {
                        myLocation = LatLng(it.latitude, it.longitude)
                        cameraPositionState.position =
                            CameraPosition.fromLatLngZoom(myLocation!!, 15f)
                    }
                }
            }

            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    properties = MapProperties(
                        isMyLocationEnabled = true,
                        mapStyleOptions = mapStyleOptions
                    ),
                ) {
                    sensorDataList.forEach { data ->
                        val location = LatLng(data.latitude, data.longitude)
                        var bitmap by remember { mutableStateOf<Bitmap?>(null) }

                        val persentase = ((data.height - data.distance) / data.height) * 100.0
                        val status = when {
                            persentase <= 50 -> "Kosong"
                            persentase <= 75 -> "Hampir Penuh"
                            else -> "Penuh"
                        }
                        val color = when (status) {
                            "Kosong" -> Color.Green
                            "Hampir Penuh" -> Color.Yellow
                            else -> Color.Red
                        }

                        scope.launch {
                            bitmap = loadBitmapFromUrl(data.imageUrl ?: "", context)
                        }

                        MarkerInfoWindowContent(state = MarkerState(position = location)) {
                            Column(
                                modifier = Modifier
                                    .padding(4.dp)
                                    .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp))
                                    .background(Color.White, RoundedCornerShape(12.dp))
                                    .padding(8.dp)
                                    .sizeIn(minWidth = 200.dp)
                            ) {
                                Text(
                                    text = data.binId,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = Color.Black
                                )
                                Text(
                                    text = status,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp,
                                    color = color
                                )
                                bitmap?.let {
                                    Image(
                                        bitmap = it.asImageBitmap(),
                                        contentDescription = null,
                                        modifier = Modifier.size(180.dp)
                                    )
                                } ?: Text("Gambar tidak tersedia", fontSize = 12.sp)
                            }
                        }
                    }

                    if (myLocation != null && sortedBins.isNotEmpty()) {
                        val routePoints =
                            listOf(myLocation!!) + sortedBins.map {
                                LatLng(
                                    it.latitude,
                                    it.longitude
                                )
                            }
                        Polyline(points = routePoints, color = Color.Blue, width = 6f)
                    }
                }

                // Then place the information overlay with zIndex to ensure it's on top
                Column(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(16.dp)
                        .background(
                            color = if (isSystemInDarkTheme()) Color.DarkGray.copy(alpha = 0.85f)
                            else Color.White.copy(alpha = 0.85f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(12.dp)
                        .sizeIn(minWidth = 220.dp)
                        .zIndex(1f)  // Make sure this is above the map
                ) {
                    val progress =
                        (totalVolume / (capacity?.toDoubleOrNull() ?: 1.0)).coerceIn(0.0, 1.0)

                    Text("📦 Rute Pengambilan", fontWeight = FontWeight.Bold)
                    Text("Mobil: $truck", fontSize = 14.sp)
                    Text("Total volume: %.2f liter".format(totalVolume), fontSize = 14.sp)
                    Text(
                        "Kapasitas mobil: %.2f liter".format(capacity?.toDoubleOrNull() ?: 0.0),
                        fontSize = 14.sp
                    )
                    Text("Jumlah tempat sampah: ${sortedBins.size}", fontSize = 14.sp)

                    LinearProgressIndicator(
                        progress = { progress.toFloat() },
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .fillMaxWidth()
                            .height(8.dp),
                        color = Color.Blue,
                    )

                    Text(
                        text = "%.0f%% kapasitas digunakan".format(progress * 100),
                        fontSize = 12.sp,
                        modifier = Modifier.align(Alignment.End),
                        color = Color.Gray
                    )
                }
            }

        } else {
            LaunchedEffect(Unit) { locationPermissionState.launchMultiplePermissionRequest() }
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Izin lokasi diperlukan untuk menggunakan fitur peta")
            }
        }
    }
}

suspend fun loadBitmapFromUrl(url: String, context: Context): Bitmap? {
    return try {
        val request = ImageRequest.Builder(context).data(url).allowHardware(false).build()
        val result = context.imageLoader.execute(request).drawable
        (result as? BitmapDrawable)?.bitmap?.copy(Bitmap.Config.ARGB_8888, true)
    } catch (e: Exception) {
        Log.e("LoadBitmap", "Error loading bitmap", e)
        null
    }
}

fun distanceBetween(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val results = FloatArray(1)
    Location.distanceBetween(lat1, lon1, lat2, lon2, results)
    return results[0].toDouble()
}

fun findNearestRoute(
    bins: List<SensorData>,
    startLat: Double,
    startLng: Double,
    truckCapacity: Double
): List<SensorData> {
    val fullBins = bins.filter {
        val percent = ((it.height - it.distance) / it.height) * 100
        percent > 75
    }.toMutableList()

    val route = mutableListOf<SensorData>()
    var currentLat = startLat
    var currentLng = startLng
    var remainingCapacity = truckCapacity

    while (fullBins.isNotEmpty()) {
        val nearest = fullBins.minByOrNull {
            distanceBetween(currentLat, currentLng, it.latitude, it.longitude)
        } ?: break

        val volume = nearest.maxVolume
        if (volume <= remainingCapacity) {
            route.add(nearest)
            remainingCapacity -= volume
            currentLat = nearest.latitude
            currentLng = nearest.longitude
        }
        fullBins.remove(nearest)
    }
    return route
}