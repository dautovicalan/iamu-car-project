package com.alan.alancars

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.alan.alancars.model.DailyReminder

class AlarmReceiver : BroadcastReceiver() {

    private var notificationManager: NotificationManagerCompat? = null

    override fun onReceive(context: Context, intent: Intent) {
        val dailyReminder = intent.getSerializableExtra("daily_reminder") as? DailyReminder
        val tapResultIntent = Intent(context, HomeMainActivity::class.java)
        tapResultIntent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context,
            0,
            tapResultIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = context?.let {
            NotificationCompat.Builder(it, CHANNEL_ID)
                .setContentTitle(dailyReminder?.title)
                .setContentText(dailyReminder?.content)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .build()
        }
        notificationManager = context?.let { NotificationManagerCompat.from(it) }
        notification?.let { dailyReminder?.let { it1 -> notificationManager?.notify(it1._id, it) } }
    }
}