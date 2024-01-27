package com.imarti.affirmations

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.imarti.affirmations.ui.theme.AffirmationsTheme
import com.imarti.affirmations.ui.theme.HarmonyOS_Sans

@Composable
fun SetupScreen(navController: NavHostController) {
    val context = LocalContext.current
    val sharedPrefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    var userName by rememberSaveable {
        mutableStateOf(sharedPrefs.getString("user_name", "") ?: "User")
    }
    // Same as the bar in main screen (idk if i should add or not)
    /*
    Row (
        Modifier.padding(start = 14.dp, end = 14.dp, top = 14.dp)
    ){
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(22.dp)
                ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Daily Affirmations",
                modifier = Modifier.padding(14.dp),
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                style = MaterialTheme.typography.headlineSmall,
                fontFamily = HarmonyOS_Sans
            )
        }
    }
    */
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Welcome User!\nPlease update the following according to your preference",
            fontFamily = HarmonyOS_Sans,
            modifier = Modifier.padding(14.dp),
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )
        OutlinedTextField(
            modifier = Modifier.padding(14.dp),
            value = userName,
            onValueChange = {
                userName = it
            },
        )
        Button(
            onClick = {
                sharedPrefs.edit().putBoolean("first_launch", false).apply()
                if (userName.isEmpty()) {
                    userName = "User"
                    Toast.makeText(context, R.string.empty_username_warning, Toast.LENGTH_SHORT).show()
                }
                sharedPrefs.edit().putString("user_name", userName).apply()
                navController.navigate("main") {
                    popUpTo(navController.graph.startDestinationId) {
                        inclusive = true
                    }
                }
            },
            modifier = Modifier.padding(start = 14.dp)
        ) {
            Text(
                text = "Done",
                fontFamily = HarmonyOS_Sans

            )
        }

    }
}

@Preview(showBackground = true)
@Composable
fun SetupScreenPreview() {
    AffirmationsTheme {
        SetupScreen(navController = NavHostController(LocalContext.current))
    }
}