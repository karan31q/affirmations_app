package com.imarti.affirmations

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.imarti.affirmations.ui.theme.AffirmationsTheme
import com.imarti.affirmations.ui.theme.HarmonyOS_Sans

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SetupScreen(navController: NavHostController) {
    val context = LocalContext.current
    val sharedPrefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    var userName by rememberSaveable {
        mutableStateOf(sharedPrefs.getString("user_name", "") ?: "User")
    }

    /*
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
     */

    Scaffold(
        modifier = Modifier
            .statusBarsPadding(),

        /*
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
         */
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.setup_screen_text),
                fontFamily = HarmonyOS_Sans,
                modifier = Modifier.padding(14.dp),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
            OutlinedTextField(
                modifier = Modifier.padding(14.dp),
                value = userName,
                onValueChange = {
                    userName = it
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primaryContainer,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                onClick = {
                    sharedPrefs.edit().putBoolean("first_launch", false).apply()
                    // will add later
                    /*
                    if (userName.isEmpty()) {
                        userName = "User"
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                message = emptyUserNameWarning,
                                actionLabel = okLabel,
                                duration = SnackbarDuration.Short
                            )
                        }
                    } else {
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                message = savedUserName,
                                actionLabel = okLabel,
                                duration = SnackbarDuration.Short
                            )
                        }
                    }
                     */
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
            ) {
                Text(
                    text = "Done",
                    fontFamily = HarmonyOS_Sans

                )
            }

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