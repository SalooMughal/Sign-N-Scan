package com.pixelz360.docsign.imagetopdf.creator

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d("Permission111", "Message received from: ${remoteMessage.from}")

        // Check if message contains a notification payload
        remoteMessage.notification?.let {
            Log.d("Permission111", "Message Notification Body: ${it.body}")
            sendNotification(it.body)
        }

        // Check if message contains a data payload
        if (remoteMessage.data.isNotEmpty()) {
            Log.d("Permission111", "Message Data Payload: ${remoteMessage.data}")
        }
    }

    override fun onNewToken(token: String) {
        Log.d("Permission111", "Refreshed token: $token")
        // Send token to your backend if required
    }

    private fun sendNotification(messageBody: String?) {
//        val intent = Intent(this, MainActivity::class.java)
        val intent = Intent(this, HomeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)

        val channelId = "update_channel"
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.app_icon) // Ensure you have an icon here
            .setContentTitle("App Update Notification")
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "App Update Channel",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0, notificationBuilder.build())
    }
}

