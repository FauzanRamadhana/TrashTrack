package com.greentech.monitorsampah.utils

import com.google.firebase.database.FirebaseDatabase
import com.greentech.monitorsampah.model.Car

object FirebaseUtils {
    fun addCarToRealtimeDatabase(car: Car, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val database = FirebaseDatabase.getInstance()
        val vehicleRef = database.getReference("vehicle")

        // Menambahkan mobil baru dengan ID unik
        val newCarRef = vehicleRef.push()
        newCarRef.setValue(car)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e) }
    }

}
