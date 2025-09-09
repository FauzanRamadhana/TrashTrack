package com.greentech.monitorsampah.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.greentech.monitorsampah.MainActivity
import com.greentech.monitorsampah.R
import com.greentech.monitorsampah.model.SensorData


class SensorDataService : Service() {

    private val channelId = "SensorDataChannel"
    private val tag = "SensorDataService"
    private val notificationId = 1001
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate() {
        super.onCreate()
        Log.d(tag, "Service started")

        createNotificationChannel()
        startForegroundService()
        startFetchingData()
    }

    private fun createNotificationChannel() {
        val notificationChannel = NotificationChannel(
            channelId,
            "Sensor Data Service",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Channel for TrashTrack sensor monitoring"
        }

        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(notificationChannel)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun startForegroundService() {
        val notification: Notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("TrashTrack")
            .setContentText("Service Sedang Berjalan")
            .setSmallIcon(R.drawable.logo)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        startForeground(notificationId, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC)
    }

    private fun startFetchingData() {
        val database = FirebaseDatabase.getInstance()
        val ref = database.getReference("sensor_data")

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    if (!snapshot.exists()) {
                        Log.d(tag, "No data available")
                        return
                    }

                    // Iterasi setiap bin
                    snapshot.children.forEach { binSnapshot ->
                        val binId = binSnapshot.key ?: return@forEach

                        // Mengambil data untuk setiap bin
                        val distance = binSnapshot.child("distance").getValue(Double::class.java)
                        val imageUrl = binSnapshot.child("imageUrl").getValue(String::class.java) ?: ""
                        val latitude = binSnapshot.child("latitude").getValue(Double::class.java) ?: 0.0
                        val longitude = binSnapshot.child("longitude").getValue(Double::class.java) ?: 0.0
                        val height = binSnapshot.child("height").getValue(Double::class.java) ?: 0.0
                        val maxVolume = binSnapshot.child("maxVolume").getValue(Double::class.java) ?: 0.0


                        if (distance != null) {
                            val binData = SensorData(
                                binId = binId,
                                distance = distance,
                                imageUrl = imageUrl,
                                latitude = latitude,
                                longitude = longitude,
                                height = height,
                                maxVolume = maxVolume
                            )

                            checkBinStatus(binData)
                        }
                    }
                } catch (e: Exception) {
                    Log.e(tag, "Error processing sensor data", e)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(tag, "Database error: ${error.message}")
            }
        })
    }

    private fun checkBinStatus(binData: SensorData) {
        val tinggiSampah = binData.height - (binData.distance)  // Pastikan menjadi Double
        val persentaseTinggiSampah = (tinggiSampah / binData.height) * 100

        Log.d(tag, "Bin ${binData.binId} - Persentase tinggi sampah: $persentaseTinggiSampah%")

        if (persentaseTinggiSampah > 75) {
            sendNotification(binData, persentaseTinggiSampah)
        }
    }


    @SuppressLint("DefaultLocale")
    private fun sendNotification(binData: SensorData, percentage: Double) {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        // Intent untuk membuka MainActivity dengan informasi bin spesifik
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("BIN_ID", binData.binId)
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            binData.binId.hashCode(), // Menggunakan hashCode sebagai unique request code
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val soundUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        // Notifikasi dengan informasi spesifik bin
        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Peringatan! Tempat Sampah ${binData.binId}")
            .setContentText("Tempat Sampah penuh (${String.format("%.1f", percentage)}%)")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("Tempat Sampah di lokasi (${binData.latitude}, ${binData.longitude}) " +
                        "sudah mencapai ${String.format("%.1f", percentage)}% dari kapasitas maksimal"))
            .setSmallIcon(R.drawable.logo)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setSound(soundUri)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(binData.binId.hashCode(), notification)
        Log.d(tag, "Notification sent for bin ${binData.binId}")
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        Log.d(tag, "Service destroyed")
    }
}