@file:Suppress("KotlinConstantConditions")

package com.greentech.monitorsampah.screen

import android.Manifest
import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.greentech.monitorsampah.R
import com.greentech.monitorsampah.model.Car
import com.greentech.monitorsampah.model.SensorData
import com.greentech.monitorsampah.ui.components.AddBinDialog
import com.greentech.monitorsampah.ui.components.AddCarDialog
import com.greentech.monitorsampah.ui.components.SelectCarDialog
import com.greentech.monitorsampah.ui.components.TrashTrackTopBar
import com.greentech.monitorsampah.ui.theme.Green


@Composable
fun HomeScreen(
    navController: NavHostController
) {
    var sensorDataList by remember { mutableStateOf<List<SensorData>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    var showAddCarDialog by remember { mutableStateOf(false) }
    var showAddBinDialog by remember { mutableStateOf(false) }

    var showCarDialog by remember { mutableStateOf(false) }
    var selectedCar by remember { mutableStateOf<Car?>(null) }
    
    // Mengambil data dari Firebase
    LaunchedEffect(Unit) {
        val database = FirebaseDatabase.getInstance().reference.child("sensor_data")
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val dataList = mutableListOf<SensorData>()
                snapshot.children.forEach { child ->
                    val data = child.getValue(SensorData::class.java)
                    if (data?.distance != null && data.imageUrl != null &&
                        data.latitude != null && data.longitude != null
                    ) {
                        dataList.add(data)
                    }
                }
                sensorDataList = dataList
                Log.d("ada", dataList.toString())
                isLoading = false
            }

            override fun onCancelled(error: DatabaseError) {
                isLoading = false
            }
        })
    }


    Scaffold(
        topBar = {
            TrashTrackTopBar()
        },
        floatingActionButton = {
            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier.padding(end = 16.dp, bottom = 16.dp)
            ) {
                FloatingActionButton(
                    onClick = { showAddBinDialog = true },
                    containerColor = Green,
                    contentColor = Color.White,
                    modifier = Modifier.padding(bottom = 12.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_delete_24),
                        contentDescription = "Tambah Tempat Sampah"
                    )
                }

                FloatingActionButton(
                    onClick = { showAddCarDialog = true },
                    containerColor = Green,
                    contentColor = Color.White,
                    modifier = Modifier.padding(bottom = 12.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_directions_car_filled_24),
                        contentDescription = "Tambah Mobil"
                    )
                }

                FloatingActionButton(
                    onClick = { showCarDialog = true},
                    containerColor = Green,
                    contentColor = Color.White
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_map_24),
                        contentDescription = "Peta"
                    )
                }
            }
        }

    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn {
                    items(sensorDataList) { data ->
                        ListSampah(data = data, navController = navController)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }

    // Dialog tambah mobil
    if (showAddCarDialog) {
        AddCarDialog(
            onDismiss = { showAddCarDialog = false },
            onSuccess = {
                showAddCarDialog = false
            }
        )
    }

    if (showAddBinDialog) {
        AddBinDialog(
            onDismiss = { showAddBinDialog = false },
            onSave = { newBin ->
                val databaseRef = FirebaseDatabase.getInstance().reference
                    .child("sensor_data").child(newBin.binId)

                val dataMap = mapOf(
                    "binId" to newBin.binId,
                    "latitude" to 0.0,
                    "longitude" to 0.0,
                    "distance" to 0.0,
                    "imageUrl" to "",
                    "height" to newBin.height,
                    "maxVolume" to newBin.maxVolume

                )
                databaseRef.setValue(dataMap)
            }
        )
    }
    if (showCarDialog) {
        SelectCarDialog(
            showDialog = showCarDialog,
            selectedCar = selectedCar,
            onCarSelected = { car ->
                selectedCar = car
            },
            onDismiss = { showCarDialog = false },
            onNavigate = { car ->
                navController.navigate("maps?truck=${car.plat}&capacity=${car.capacity}")
                showCarDialog = false // Close the dialog
            }
        )
    }

}

@SuppressLint("DefaultLocale")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ListSampah(
    modifier: Modifier = Modifier,
    data: SensorData,
    navController: NavHostController
) {
    val depth = data.height
    val jarak = data.distance
    val tinggiSampah = (depth - jarak).coerceAtLeast(0.0)
    val persentaseTinggiSampah = ((tinggiSampah / depth) * 100).coerceIn(0.0, 100.0)
    val formattedPercentage = String.format("%.2f", persentaseTinggiSampah)


    val color: Color = when {
        persentaseTinggiSampah <= 50 -> Color.Green
        persentaseTinggiSampah > 50 && persentaseTinggiSampah <= 75 -> Color.Yellow
        persentaseTinggiSampah > 75 -> Color.Red
        else -> Color.White
    }

    val status: String = when {
        persentaseTinggiSampah <= 50 -> "Kosong"
        persentaseTinggiSampah in 51.0..75.0 -> "Hampir Penuh"
        persentaseTinggiSampah > 75 -> "Penuh"
        else -> "Tidak Diketahui"
    }

    val permissionState = rememberMultiplePermissionsState(
        permissions = listOf(Manifest.permission.CAMERA)
    )

    LaunchedEffect(Unit) {
        permissionState.launchMultiplePermissionRequest()
    }


    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { navController.navigate("bin_detail/${data.binId}") }
            .shadow(4.dp, RoundedCornerShape(8.dp)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_delete_24),
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(40.dp)
                )
                Text(
                    text = "$formattedPercentage%",
                    color = color,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = data.binId,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Status: $status",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
