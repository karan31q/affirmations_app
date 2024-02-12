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
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.imarti.affirmations.fetch.AffirmationsApi
import com.imarti.affirmations.fetch.FetchAffirmationsService
import com.imarti.affirmations.ui.theme.AffirmationsTheme
import com.imarti.affirmations.ui.theme.HarmonyOS_Sans

@Composable
fun AffirmationsPage(navController: NavHostController, affirmationsApi: FetchAffirmationsService){
    // val context = LocalContext.current

    var affirmation by remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        affirmation = try {
            val response = affirmationsApi.getAffirmation()
            response
        } catch (e: Exception) {
            // Handle error
            "Error receiving affirmation,\nPlease check your internet connection"
        }
    }
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
                Text(
                    text = stringResource(R.string.topbar_heading),
                    modifier = Modifier.padding(14.dp),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    style = MaterialTheme.typography.headlineSmall,
                    fontFamily = HarmonyOS_Sans
                )
                FloatingActionButton(
                    onClick = {
                              navController.navigate("settings")
                    },
                    modifier = Modifier
                        .padding(5.dp),
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    shape = RoundedCornerShape(22.dp)
                ) {
                    Icon(Icons.Outlined.Settings, stringResource(R.string.topbar_settings_button))
                }
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
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = affirmation,
                modifier = Modifier
                    .padding(top = 10.dp),
                fontFamily = HarmonyOS_Sans,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
            )
            Text(
                text = affirmation,
                fontFamily = HarmonyOS_Sans,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
            )
            // share sheet (will add back after adding affirmations)
            /*
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, "Placeholder")
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, null)
            val context = LocalContext.current
            Button(
                onClick = { context.startActivity(shareIntent)},
                modifier = Modifier.padding(top = 5.dp),
            ) {
                Text(
                    text = stringResource(R.string.share_button),
                    fontFamily = HarmonyOS_Sans
                )
            }
             */
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AffirmationsPagePreview() {
    AffirmationsTheme {
        AffirmationsPage(navController = NavHostController(LocalContext.current), affirmationsApi = AffirmationsApi.retrofitService)
    }
}