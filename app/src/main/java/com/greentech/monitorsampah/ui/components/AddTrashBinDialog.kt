package com.greentech.monitorsampah.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.greentech.monitorsampah.model.SensorData

@Composable
fun AddBinDialog(
    onDismiss: () -> Unit,
    onSave: (SensorData) -> Unit
) {
    var binId by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var volume by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }


    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tambah Tempat Sampah") },
        text = {
            Column {
                OutlinedTextField(
                    value = binId,
                    onValueChange = { binId = it },
                    label = { Text("Nama Tempat Sampah") }
                )
                OutlinedTextField(
                    value = height,
                    onValueChange = { height = it },
                    label = { Text("Tinggi Tempat Sampah (cm)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = volume,
                    onValueChange = { volume = it },
                    label = { Text("Volume Tempat Sampah (liter)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                errorMessage?.let {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(it, color = Color.Red)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val heightDouble = height.toDoubleOrNull()
                    val volumeDouble = volume.toDoubleOrNull()

                    if (binId.isNotEmpty() && heightDouble != null && volumeDouble != null) {
                        val newBin = SensorData(
                            binId = binId,
                            latitude = 0.0,
                            longitude = 0.0,
                            distance = 0.0,
                            imageUrl = "",
                            height = heightDouble,
                            maxVolume = volumeDouble
                        )
                        onSave(newBin)
                        onDismiss()
                    }
                    else {
                        errorMessage = "Data Kosong!"
                    }
                }
            ) {
                Text("Simpan")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}
