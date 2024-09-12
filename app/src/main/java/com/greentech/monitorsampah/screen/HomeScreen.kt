package com.greentech.monitorsampah.screen

import android.Manifest
import android.content.Intent
import android.provider.MediaStore
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.greentech.monitorsampah.DataDummy
import com.greentech.monitorsampah.R
import com.greentech.monitorsampah.dataDummy
import com.greentech.monitorsampah.ui.theme.Green
import com.greentech.monitorsampah.ui.theme.MonitorSampahTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(modifier: Modifier = Modifier, navController: NavHostController) {
    Scaffold(topBar = {
        TopAppBar(
            title = { Text(text = "TrashTrack") },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Green))
    }, floatingActionButton = { FloatingActionButton(onClick = { navController.navigate("maps") }) {
        Icon(painter = painterResource(id = R.drawable.baseline_map_24), contentDescription = null)
    } }) { innerPadding ->
        LazyColumn (modifier = Modifier.padding(innerPadding)) {
            items(dataDummy.size) { index ->
                ListSampah(data = dataDummy[index], item = index + 1)
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ListSampah(modifier: Modifier = Modifier, data: DataDummy, item: Int) {
    val context = LocalContext.current
    val permissionState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.CAMERA
        )
    )
    LaunchedEffect(Unit) {
        permissionState.launchMultiplePermissionRequest()
    }
    var showDialog by remember { mutableStateOf(false) }
    Card(modifier = modifier
        .fillMaxWidth()
        .padding(vertical = 7.dp, horizontal = 10.dp)
        .clickable {
            showDialog = true
        }) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(text = "Tong Sampah $item", fontWeight = FontWeight.ExtraBold)
            Text(text = data.status)
            Text(text = "Koordinat : ${data.coordinate.first}, ${data.coordinate.second}")
        }
    }
    if (showDialog) {
        AlertDialog(onDismissRequest = {
            showDialog = false
        },
            title = {
                Text(text = "Ambil Foto")
            },
            text = {
                Text(text = "Ambil foto menggunakan kamera")
            },
            confirmButton = {
                Button(onClick = {
                    showDialog = false
                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    context.startActivity(intent)
                }) {
                    Text(text = "Ambil")
                }
            },
            dismissButton = {
                Button(onClick = {
                    showDialog = false
                }) {
                    Text(text = "Batal")
                }
            })
    }
}

@Preview
@Composable
private fun Homeprev() {
    MonitorSampahTheme {
//        HomeScreen(navController = navController)
    }
}