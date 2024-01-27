package com.imarti.affirmations

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.imarti.affirmations.ui.theme.AffirmationsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
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
                        composable("setup") { SetupScreen(navController) }
                        composable("main") { AffirmationsPage(navController) }
                        composable("settings") { SettingsPage(navController) }
                    }
                }
            }
        }
    }
}
