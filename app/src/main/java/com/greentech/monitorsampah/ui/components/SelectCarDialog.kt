package com.greentech.monitorsampah.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.greentech.monitorsampah.model.Car

@Composable
fun SelectCarDialog(
    showDialog: Boolean,
    selectedCar: Car?,
    onCarSelected: (Car) -> Unit,
    onDismiss: () -> Unit,
    onNavigate: (Car) -> Unit
) {
    if (!showDialog) return

    var expanded by remember { mutableStateOf(false) }
    var truckList by remember { mutableStateOf(listOf<Car>()) }
    var isLoading by remember { mutableStateOf(true) }

    // Ambil data mobil dari Firebase
    LaunchedEffect(Unit) {
        val database = FirebaseDatabase.getInstance().getReference("vehicle")
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val tempList = mutableListOf<Car>()
                for (child in snapshot.children) {
                    val car = child.getValue(Car::class.java)
                    car?.let { tempList.add(it) }
                }
                truckList = tempList
                isLoading = false
            }

            override fun onCancelled(error: DatabaseError) {
                // Contoh: log error atau tampilkan pesan ke user
                println("Gagal mengambil data mobil: ${error.message}")
                isLoading = false
            }
        })
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Pilih Mobil") },
        text = {
            Column {
                Text("Mobil yang dipilih:")
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(enabled = !isLoading) { expanded = true }
                        .padding(12.dp)
                        .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
                ) {
                    Text(
                        text = if (isLoading) "Memuat..." else selectedCar?.plat ?: "Pilih Mobil",
                        modifier = Modifier.padding(4.dp),
                        color = if (isLoading) Color.Gray else Color.Unspecified
                    )
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    truckList.forEach { car ->
                        DropdownMenuItem(
                            text = { Text("${car.plat} - ${car.capacity} liter") },
                            onClick = {
                                onCarSelected(car)
                                expanded = false
                            }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Text(
                text = "Cari",
                modifier = Modifier
                    .clickable(enabled = selectedCar != null) {
                        selectedCar?.let { onNavigate(it) }
                    }
                    .padding(13.dp),
                color = if (selectedCar != null) MaterialTheme.colorScheme.primary else Color.Gray
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}
