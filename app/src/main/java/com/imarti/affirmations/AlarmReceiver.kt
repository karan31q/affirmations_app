package com.imarti.affirmations

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import java.time.LocalDateTime
import java.util.Calendar


class AlarmReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val tag = "DailyAffirmations"
        val action = intent.action
        val sharedPrefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val alarmSet = sharedPrefs.getBoolean("alarm_set", false) // false is default value

        if (action != null && action == "android.intent.action.BOOT_COMPLETED") {
            Log.i(tag, "Received boot intent")
            if (alarmSet) {
                val selectedHour = sharedPrefs.getInt("hour_selected", 8)
                val selectedMinute = sharedPrefs.getInt("minute_selected", 30)

                // get user specified
                val now = Calendar.getInstance()
                now[Calendar.HOUR_OF_DAY] = LocalDateTime.now().hour
                now[Calendar.MINUTE] = LocalDateTime.now().minute
                now[Calendar.SECOND] = 0
                now[Calendar.MILLISECOND] = 0

                val calendar = Calendar.getInstance()
                calendar[Calendar.HOUR_OF_DAY] = selectedHour
                calendar[Calendar.MINUTE] = selectedMinute
                calendar[Calendar.SECOND] = 0
                calendar[Calendar.MILLISECOND] = 0

                /*
                check if the time has already passed today and add a day if it that's the case
                */
                if (now.after(calendar)) {
                    Log.i(tag,"Added a day.")
                    calendar.add(Calendar.DATE, 1)
                }
                setAlarm(calendar, context)
            } else {
                Log.i(tag, "Alarms not set")
            }
        } else if (action != null && (action == "com.imarti.affirmations.ACTION_SET_ALARM" ||
                    action == "com.imarti.affirmations.ACTION_CANCEL_ALARM")) {
            notificationBuilder(context)
        } else {
            Log.i(tag,"Received unexpected action $action")
        }
    }
}