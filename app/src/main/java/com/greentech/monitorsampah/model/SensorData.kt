package com.greentech.monitorsampah.model

data class SensorData(
    val binId: String = "",
    val distance: Double = 0.0,
    val height: Double = 0.0,
    val maxVolume: Double = 0.0,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val imageUrl: String? = null
)
