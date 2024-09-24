package com.greentech.monitorsampah.screen

import android.Manifest
import android.content.Intent
import android.provider.MediaStore
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.greentech.monitorsampah.DataDummy
import com.greentech.monitorsampah.R
import com.greentech.monitorsampah.dataDummy
import com.greentech.monitorsampah.ui.theme.Green
import com.greentech.monitorsampah.ui.theme.Grey
import com.greentech.monitorsampah.ui.theme.GreyShadow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(modifier: Modifier = Modifier, navController: NavHostController) {
    Scaffold(
        topBar = {
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
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("maps") }, containerColor = Green,
                contentColor = Color.White
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_map_24),
                    contentDescription = null
                )
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            items(dataDummy.size) { index ->
                ListSampah(data = dataDummy[index], item = index + 1)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ListSampah(modifier: Modifier = Modifier, data: DataDummy, item: Int) {
    val context = LocalContext.current
    val permissionState = rememberMultiplePermissionsState(
        permissions = listOf(Manifest.permission.CAMERA)
    )
    LaunchedEffect(Unit) {
        permissionState.launchMultiplePermissionRequest()
    }
    var showDialog by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { showDialog = true }
            .shadow(4.dp, RoundedCornerShape(8.dp)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_delete_24),
                contentDescription = null,
                tint = when (data.status) {
                    "Penuh" -> Color.Red
                    else -> Green
                },
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = "Tong Sampah $item",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Status: ${data.status}",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Koordinat: ${data.coordinate.first}, ${data.coordinate.second}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Ambil Foto") },
            text = { Text("Ambil foto menggunakan kamera") },
            confirmButton = {
                Button(
                    onClick = {
                        showDialog = false
                        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        context.startActivity(intent)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Green)
                ) {
                    Text("Ambil", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showDialog = false }
                ) {
                    Text("Batal")
                }
            }
        )
    }
}