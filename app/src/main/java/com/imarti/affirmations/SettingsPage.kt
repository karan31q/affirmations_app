package com.imarti.affirmations

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.imarti.affirmations.ui.theme.AffirmationsTheme
import com.imarti.affirmations.ui.theme.HarmonyOS_Sans

@Composable
fun SettingsPage(navController: NavHostController) {
    val context = LocalContext.current
    val sharedPrefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    var userName by rememberSaveable {
        mutableStateOf(sharedPrefs.getString("user_name", "") ?: "User")
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
                verticalAlignment = Alignment.CenterVertically
            ) {
                FloatingActionButton(
                    onClick = {
                        navController.navigateUp()
                    },
                    modifier = Modifier
                        .padding(start = 5.dp, top = 5.dp, bottom = 5.dp),
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    shape = RoundedCornerShape(22.dp)
                ) {
                    Icon(
                        Icons.Outlined.ArrowBack,
                        stringResource(R.string.settings_menu_back_button)
                    )
                }
                Text(
                    text = stringResource(R.string.settings_menu_heading),
                    modifier = Modifier.padding(14.dp),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    style = MaterialTheme.typography.headlineSmall,
                    fontFamily = HarmonyOS_Sans
                )
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
        ) {
            Row(
                modifier = Modifier
                    .padding(start = 15.dp, top = 10.dp, bottom = 10.dp, end = 15.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.padding(top = 8.dp), // seems to be in line with the text field
                    text = stringResource(id = R.string.settings_menu_change_username),
                    fontFamily = HarmonyOS_Sans,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                )
                OutlinedTextField(
                    modifier = Modifier.padding(start = 15.dp),
                    value = userName,
                    onValueChange = { userName = it },
                )
            }
            Button(
                onClick = {
                    if (userName.isEmpty()) {
                        userName = "User"
                        Toast.makeText(context, R.string.empty_username_warning, Toast.LENGTH_SHORT).show()
                    }
                    sharedPrefs.edit().putString("user_name", userName).apply()
                },
                modifier = Modifier.padding(start = 15.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.save_changes_button),
                    fontFamily = HarmonyOS_Sans

                )
            }
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