package com.imarti.affirmations

import android.Manifest
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat


class AlarmReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val tag = "DailyAffirmations"
        val action = intent.action
        if (action == "android.intent.action.BOOT_COMPLETED") {
            Log.i(tag, "Received boot intent, daily affirmations active!")
        } else if (action != null && (action == "com.imarti.affirmations.ACTION_SET_ALARM" ||
                    action == "com.imarti.affirmations.ACTION_CANCEL_ALARM")) {
            val i = Intent(context, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            val pendingIntent = PendingIntent.getActivity(context, 0, i, PendingIntent.FLAG_IMMUTABLE)

            val builder = NotificationCompat.Builder(
                context, context.getString(R.string.notification_channel_id))
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(context.getString(R.string.notification_channel_id))
                .setContentText(context.getString(R.string.notification_content))
                .setStyle(NotificationCompat.BigTextStyle()
                    .bigText(context.getString(R.string.notification_content_expanded)))
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
            Log.i(tag, "Alarm triggered, notifiying user")
            notificationManager.notify(1, builder.build())
        } else {
            Log.i(tag,"Received unexpected action $action")
        }
    }
}