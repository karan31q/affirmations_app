package com.imarti.affirmations

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.core.app.ActivityCompat
import com.maxkeppeker.sheets.core.models.base.UseCaseState
import com.maxkeppeler.sheets.clock.ClockDialog
import com.maxkeppeler.sheets.clock.models.ClockConfig
import com.maxkeppeler.sheets.clock.models.ClockSelection
import java.time.LocalDateTime
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClockDialogImarti(clockState: UseCaseState, context: Context) {
    val sharedPrefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    ClockDialog(
        state = clockState,
        config = ClockConfig(
            is24HourFormat = false
        ),
        selection = ClockSelection.HoursMinutes { hours, minutes ->
            sharedPrefs.edit().putInt("hour_selected", hours).apply()
            sharedPrefs.edit().putInt("minute_selected", minutes).apply()
            cancelAlarm(context) // first cancel old alarm
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    ActivityCompat.requestPermissions(
                        context as MainActivity,
                        arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                        1
                    )
                }
            }

            // get user specified time
            val now = Calendar.getInstance()
            now[Calendar.HOUR_OF_DAY] = LocalDateTime.now().hour
            now[Calendar.MINUTE] = LocalDateTime.now().minute
            now[Calendar.SECOND] = 0
            now[Calendar.MILLISECOND] = 0

            val calendar = Calendar.getInstance()
            calendar[Calendar.HOUR_OF_DAY] = hours
            calendar[Calendar.MINUTE] = minutes
            calendar[Calendar.SECOND] = 0
            calendar[Calendar.MILLISECOND] = 0

            /*
            check if the time has already passed today and add a day if it that's the case
            */
            if (now.after(calendar)) {
                Log.i(tag, "Added a day")
                calendar.add(Calendar.DATE, 1)
            }
            setAlarm(calendar, context)
        }
    )
}