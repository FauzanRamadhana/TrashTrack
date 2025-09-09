package com.greentech.monitorsampah.screen

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.storage.FirebaseStorage
import com.greentech.monitorsampah.model.SensorData
import com.greentech.monitorsampah.ui.components.TrashTrackTopBar
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@SuppressLint("DefaultLocale")
@Composable
fun BinDetailScreen(
    binId: String
) {
    val context = LocalContext.current
    val dbRef = Firebase.database.reference.child("sensor_data").child(binId)

    var binData by remember { mutableStateOf<SensorData?>(null) }
    var showDialog by remember { mutableStateOf(false) }

    // TEMPORARY URI UNTUK FOTO KAMERA
    val cameraImageUri = remember {
        FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            File.createTempFile("temp_image", ".jpg", context.cacheDir)
        )
    }

    // Launcher Kamera
    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            uploadImageToFirebase(context, cameraImageUri, binId) { imageUrl ->
                dbRef.child("imageUrl").setValue(imageUrl)
            }
        }
    }

    // Launcher Galeri
    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            uploadImageToFirebase(context, it, binId) { imageUrl ->
                dbRef.child("imageUrl").setValue(imageUrl)
            }
        }
    }

    // Ambil data dari Firebase
    LaunchedEffect(binId) {
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                binData = snapshot.getValue(SensorData::class.java)
            }

            override fun onCancelled(error: DatabaseError) {
                // handle error
            }
        })
    }


    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Ambil Gambar") },
            text = { Text("Pilih metode untuk mengambil gambar.") },
            confirmButton = {
                TextButton(onClick = {
                    showDialog = false
                    cameraLauncher.launch(cameraImageUri)
                }) {
                    Text("Kamera")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDialog = false
                    galleryLauncher.launch("image/*")
                }) {
                    Text("Galeri")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TrashTrackTopBar()
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    showDialog = true
                },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Ambil Foto")
            }
        }
    ) { padding ->
        binData?.let { data ->
            val depth = data.height
            val jarak = data.distance
            val tinggiSampah = (depth - jarak).coerceAtLeast(0.0)
            val persentase = ((tinggiSampah / depth) * 100).coerceIn(0.0, 100.0)
            val status = when {
                persentase <= 50 -> "Kosong"
                persentase <= 75 -> "Hampir Penuh"
                else -> "Penuh"
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Letak Tempat Sampah: ${data.binId}")
                Text("Tinggi Sampah: ${String.format("%.2f", tinggiSampah)} cm")
                Text("Tinggi Tempat Sampah: ${String.format("%.2f", data.height)} cm")
                Text("Tinggi Sampah (liter): ${String.format("%.2f", data.maxVolume)} liter")
                Text("Status: $status")
                Text("Persentase Kepenuhan: ${String.format("%.2f", persentase)}%")

                if (!data.imageUrl.isNullOrEmpty()) {
                    AsyncImage(
                        model = data.imageUrl,
                        contentDescription = "Foto Tempat Sampah",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )
                }
            }
        } ?: run {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}


fun uploadImageToFirebase(
    context: Context,
    imageUri: Uri,
    binId: String,
    onSuccess: (String) -> Unit
) {
    val storageRef = FirebaseStorage.getInstance().reference
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val imageRef = storageRef.child("images/bin_${binId}_$timeStamp.jpg")

    val uploadTask = imageRef.putFile(imageUri)
    uploadTask.addOnSuccessListener {
        imageRef.downloadUrl.addOnSuccessListener { uri ->
            onSuccess(uri.toString())
            Toast.makeText(context, "Berhasil upload gambar", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(context, "Gagal mendapatkan URL gambar", Toast.LENGTH_SHORT).show()
        }
    }.addOnFailureListener {
        Toast.makeText(context, "Gagal upload gambar", Toast.LENGTH_SHORT).show()
    }
}
