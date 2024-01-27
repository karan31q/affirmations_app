package com.imarti.affirmations

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.imarti.affirmations.ui.theme.AffirmationsTheme
import com.imarti.affirmations.ui.theme.HarmonyOS_Sans

@Composable
fun SettingsPage(navController: NavHostController) {
    Column (
        modifier = Modifier.padding(start = 14.dp, end = 14.dp, top = 14.dp)
    ) {
        Row(
            modifier = Modifier.padding(bottom = 14.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(22.dp)
                    ),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // val sharedPrefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                // val testVar = sharedPrefs.getString("user_name", "User") ?: "User"
                FloatingActionButton(
                    onClick = {
                        navController.navigateUp()
                    },
                    modifier = Modifier
                        .padding(5.dp),
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    shape = RoundedCornerShape(22.dp)
                ) {
                    Icon(Icons.Outlined.ArrowBack, "Settings Icon")
                }
                /*
                IconButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.Filled.AccountCircle,
                        contentDescription = "Localized description",
                        modifier = Modifier
                            .size(36.dp),

                    )
                }
                 */
            }
        }
        Column(
            modifier = Modifier
                .padding(bottom = 2.dp)
                .fillMaxSize()
                .background(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(22.dp)
                ),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val dailyAffirmation = "settings placeholder"
            Text(
                text = dailyAffirmation,
                modifier = Modifier
                    .padding(start = 14.dp),
                fontFamily = HarmonyOS_Sans,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
            )
            Text(
                text = "",
                modifier = Modifier
                    .padding(start = 14.dp),
                fontFamily = HarmonyOS_Sans,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsPagePreview() {
    AffirmationsTheme {
        SettingsPage(navController = NavHostController(LocalContext.current))
    }
}