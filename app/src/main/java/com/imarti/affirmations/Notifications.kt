package com.imarti.affirmations

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import java.util.Calendar

const val tag = "DailyAffirmations"

fun setAlarm(calendar: Calendar, context: Context) {
    val alarmManager: AlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, AlarmReceiver::class.java)
    intent.action = "com.imarti.affirmations.ACTION_SET_ALARM"
    val pendingIntent: PendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP, calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY, pendingIntent
        )
    } else {
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP, calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY, pendingIntent
        )
    }

    Log.i(tag, "Notification scheduled for ${calendar[Calendar.HOUR_OF_DAY]}:${calendar[Calendar.MINUTE]} daily")
}

fun cancelAlarm(context: Context) {
    val alarmManager: AlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, AlarmReceiver::class.java)
    intent.action = "com.imarti.affirmations.ACTION_CANCEL_ALARM"
    val pendingIntent: PendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
    alarmManager.cancel(pendingIntent)

    Log.i(tag, "Notifications cancelled")
}

fun notificationBuilder(context: Context) {
    val intent = Intent(context, MainActivity::class.java)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
    val sharedPrefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    val userName = sharedPrefs.getString("user_name", "User")

    val notificationText = context.getString(R.string.notification_content, userName)
    val notificationTextExpanded = context.getString(R.string.notification_content_expanded, userName)
    val builder = NotificationCompat.Builder(
        context, context.getString(R.string.notification_channel_id))
        .setSmallIcon(R.drawable.notification_icon)
        .setContentTitle(context.getString(R.string.notification_channel_id))
        .setContentText(notificationText)
        .setStyle(
            NotificationCompat.BigTextStyle()
            .bigText(notificationTextExpanded))
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setContentIntent(pendingIntent)

    val notificationManager = NotificationManagerCompat.from(context)
    if (ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(context as MainActivity,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                1
            )
        }
    }
    Log.i(tag, "Alarm triggered, notifying user")
    notificationManager.notify(1, builder.build())
}