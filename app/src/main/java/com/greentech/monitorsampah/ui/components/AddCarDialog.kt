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
import com.greentech.monitorsampah.model.Car
import com.greentech.monitorsampah.utils.FirebaseUtils.addCarToRealtimeDatabase

@Composable
fun AddCarDialog(
    onDismiss: () -> Unit,
    onSuccess: () -> Unit
) {
    var plat by remember { mutableStateOf("") }
    var capacity by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tambah Mobil") },
        text = {
            Column {
                OutlinedTextField(
                    value = plat,
                    onValueChange = { plat = it },
                    label = { Text("Plat Nomor") },
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = capacity,
                    onValueChange = { capacity = it },
                    label = { Text("Kapasitas") },
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
                    val capInt = capacity.toIntOrNull()
                    if (plat.isNotBlank() && capInt != null) {
                        isLoading = true
                        addCarToRealtimeDatabase(
                            car = Car(plat = plat, capacity = capInt),
                            onSuccess = {
                                isLoading = false
                                onSuccess()
                            },
                            onFailure = {
                                isLoading = false
                                errorMessage = "Gagal menambahkan: ${it.message}"
                            }
                        )
                    } else {
                        errorMessage = "Data Kosong"
                    }
                },
                enabled = !isLoading
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
