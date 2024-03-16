package com.imarti.affirmations

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.imarti.affirmations.pages.SettingsPage
import com.imarti.affirmations.ui.theme.AffirmationsTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(
            navigationBarStyle = SystemBarStyle.light(
                Color.Transparent.toArgb(), Color.Transparent.toArgb()
            )
        )
        createNotificationChannel(this)
        super.onCreate(savedInstanceState)
        val sharedPrefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val isFirstLaunch = sharedPrefs.getBoolean("first_launch", true)
        setContent {
            AffirmationsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavHost(
                        navController,
                        startDestination = if (isFirstLaunch) "setup" else "main"
                    ) {
                        composable("setup") { SetupUI(navController) }
                        composable("main") { MainUI(navController) }
                        composable("settings") { SettingsPage(navController) }
                    }
                }
            }
        }
    }

    private fun createNotificationChannel(context: Context) {
        val name = context.getString(R.string.notification_channel_id)
        val descriptionText = context.getString(R.string.notification_channel_desc)
        val channel = NotificationChannel(
            name, name, NotificationManager.IMPORTANCE_DEFAULT // id , visible name, importance
        )

        channel.description = descriptionText

        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }
}