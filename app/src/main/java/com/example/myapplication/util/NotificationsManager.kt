package com.example.myapplication.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.myapplication.R
import com.example.myapplication.model.person.Person
import com.example.myapplication.ui.MainActivity
import com.example.myapplication.ui.register.LoginActivity

object NotificationsManager {

    const val CHANNEL_ID = "Notification"
    const val notificationId = 1

     private fun createNotificationChannel(context : Context) {

        val name = CHANNEL_ID
        val descriptionText = "Notification's description"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    fun displayNotification(context: Context, person: Person) {

        createNotificationChannel(context)
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_baseline_home_24)
            .setContentTitle("My notification")
            .setContentText("You've been added ${person.name}")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            notify(notificationId, builder.build())
        }
    }

    fun getServiceNotification(context: Context): Notification {
        createNotificationChannel(context)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            Intent(context, LoginActivity::class.java),
            0
        )
        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("My service notification")
            .setSmallIcon(R.drawable.ic_baseline_home_24)
            .setContentIntent(pendingIntent)
            .setContentText("Now the user can see that im working in background").build()
    }
}