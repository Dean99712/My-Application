package com.example.myapplication.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.myapplication.R
import com.example.myapplication.data.Repository
import com.example.myapplication.model.person.Person
import com.example.myapplication.ui.MainActivity
import com.example.myapplication.ui.register.LoginActivity
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ForegroundService : Service() {

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = NotificationsManager.getServiceNotification(this)
        startForeground(1, notification)
        myServiceFunction()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel(applicationContext)
    }

    private fun createNotificationChannel(context: Context) {

        val name = NotificationsManager.CHANNEL_ID
        val descriptionText = "Notification's description"
        val importance = NotificationManager.IMPORTANCE_LOW
        val channel = NotificationChannel(NotificationsManager.CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun myServiceFunction() {
        GlobalScope.launch(Dispatchers.IO) {
            while (true) {
                Thread.sleep(50)
                val listOfPeople: List<Person> =
                    Repository.getInstance(applicationContext).getAllPeopleList()
                listOfPeople.forEach { (print(it.name)) }
            }
        }
    }
}