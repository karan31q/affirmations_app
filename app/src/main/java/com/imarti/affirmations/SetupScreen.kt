package com.imarti.affirmations

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.navigation.NavHostController
import com.imarti.affirmations.ui.theme.AffirmationsTheme
import com.imarti.affirmations.ui.theme.HarmonyOS_Sans
import com.maxkeppeker.sheets.core.models.base.rememberUseCaseState
import com.maxkeppeler.sheets.clock.ClockDialog
import com.maxkeppeler.sheets.clock.models.ClockConfig
import com.maxkeppeler.sheets.clock.models.ClockSelection
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SetupScreen(navController: NavHostController) {
    val context = LocalContext.current
    val sharedPrefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    var userName by rememberSaveable {
        mutableStateOf(sharedPrefs.getString("user_name", "") ?: "User")
    }
    var showUserNameDialog by remember {
        mutableStateOf(false)
    }
    var showSkipDialog by remember {
        mutableStateOf(false)
    }
    var userNameWritten by remember {
        mutableStateOf(false)
    }
    var notificationTimeSelected by remember {
        mutableStateOf(false)
    }
    val snackbarHostState = remember {
        SnackbarHostState()
    }
    val scope = rememberCoroutineScope()
    val clockState = rememberUseCaseState()

    data class StepDone(
        val title: String,
        val stepDone: ImageVector,
        val stepNotDone: ImageVector
    )

    // so it can be used with coroutines
    val emptyUserNameWarning = stringResource(R.string.empty_username_warning)
    val okLabel = stringResource(R.string.got_it)
    val savedUserName = stringResource(R.string.username_updated)
    val defaultUserName = stringResource(R.string.default_username)
    val alarmSetMessage = stringResource(R.string.reminder_set)

    // username dialog
    if (showUserNameDialog) {
        AlertDialog(
            icon = {
                Icon(
                    Icons.Outlined.AccountCircle,
                    contentDescription = stringResource(R.string.user_icon_desc),
                    modifier = Modifier.size(50.dp)
                )
            },
            title = {
                Text(
                    text = stringResource(R.string.change_username)
                )
            },
            text = {
                OutlinedTextField(
                    value = userName,
                    onValueChange = {
                        userName = it
                    }
                )
            },
            onDismissRequest = {
                showUserNameDialog = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (userName.isEmpty()) {
                            userName = defaultUserName
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
                        sharedPrefs.edit().putString("user_name", userName).apply()
                        showUserNameDialog = false
                        userNameWritten = true
                    }
                ) {
                    Text(
                        text = stringResource(R.string.confirm_text)
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showUserNameDialog = false
                    }
                ) {
                    Text(
                        text = stringResource(R.string.dismiss_text)
                    )
                }
            }
        )

    }

    if (showSkipDialog) {
        AlertDialog(
            text = {
               Text(
                   "Using default values, please change them in settings later!",
                   fontFamily = HarmonyOS_Sans,
                   textAlign = TextAlign.Center
               )
            },
            onDismissRequest = {
                showSkipDialog = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        sharedPrefs.edit().putString("user_name", defaultUserName).apply()
                        sharedPrefs.edit().putBoolean("alarm_set", false).apply() // don't schedule notfications by default
                        showSkipDialog = false
                        sharedPrefs.edit().putBoolean("first_launch", false).apply()
                        navController.navigate("main") {
                            popUpTo(navController.graph.startDestinationId) {
                                inclusive = true
                            }
                        }
                    }
                ) {
                    Text(
                        text = stringResource(R.string.confirm_text),
                        modifier = Modifier
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            }
        )
    }

    ClockDialog(
        state = clockState,
        config = ClockConfig(
            is24HourFormat = false
        ),
        selection = ClockSelection.HoursMinutes { hours, minutes ->
            sharedPrefs.edit().putBoolean("alarm_set", true).apply()
            sharedPrefs.edit().putInt("hour_selected", hours).apply()
            sharedPrefs.edit().putInt("minute_selected", minutes).apply()
            cancelAlarm(context) // first cancel old alarm
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    ActivityCompat.requestPermissions(context as MainActivity,
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
                Log.i(tag,"Added a day")
                calendar.add(Calendar.DATE, 1)
            }
            setAlarm(calendar, context)
            // show a snackbar
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = alarmSetMessage,
                    actionLabel = okLabel,
                    duration = SnackbarDuration.Short
                )
            }
            notificationTimeSelected = true
        }
    )
    Scaffold(
        modifier = Modifier
            .statusBarsPadding(),
        topBar = {
             LargeTopAppBar(
                 title = {
                     Column(
                         verticalArrangement = Arrangement.spacedBy(5.dp)
                     ) {
                         Text(
                             "Welcome User!",
                             fontFamily = HarmonyOS_Sans,
                             style = MaterialTheme.typography.headlineLarge
                         )
                         Text(
                             "Please update the following according to your preference.",
                             fontFamily = HarmonyOS_Sans,
                             style = MaterialTheme.typography.bodySmall,
                         )
                     }
                 }
             )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(10.dp)
        ) {
            TextButton(
                colors = ButtonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                contentPadding = PaddingValues(15.dp),
                onClick = { showUserNameDialog = true},
                modifier = Modifier.padding(bottom = 5.dp)
            ) {
                Icon(
                    Icons.Outlined.AccountCircle,
                    contentDescription = "Enter your username",
                )
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp),
                    text = "Enter your username",
                    fontFamily = HarmonyOS_Sans
                )
            }
            TextButton(
                colors = ButtonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                contentPadding = PaddingValues(15.dp),
                onClick = {
                    clockState.show()
                },
                modifier = Modifier.padding(bottom = 5.dp)
            ) {
                Icon(
                    Icons.Outlined.Notifications,
                    contentDescription = stringResource(R.string.select_time_notifications),
                )
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp),
                    text = stringResource(R.string.select_time_notifications),
                    fontFamily = HarmonyOS_Sans
                )
            }
            Column(
                modifier = Modifier
                    .padding(top = 20.dp, start = 10.dp)
                    .fillMaxWidth()
            ) {
                Row {
                    val icon1 = StepDone(
                        title = "Username",
                        stepDone = Icons.Outlined.Check,
                        stepNotDone = Icons.Outlined.Close
                    )

                    Icon(
                        if (userNameWritten) icon1.stepDone else icon1.stepNotDone,
                        tint = if (userNameWritten) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.error,
                        contentDescription = null
                    )
                    Text(
                        icon1.title,
                        fontFamily = HarmonyOS_Sans
                    )
                }
                Row {
                    val icon2 = StepDone(
                        title = "Notifications",
                        stepDone = Icons.Outlined.Check,
                        stepNotDone = Icons.Outlined.Close
                    )
                    Icon(
                        if (notificationTimeSelected) icon2.stepDone else icon2.stepNotDone,
                        tint = if (notificationTimeSelected) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.error,
                        contentDescription = null
                    )
                    Text(
                        icon2.title,
                        fontFamily = HarmonyOS_Sans
                    )
                }
            }
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                FloatingActionButton(
                    modifier = Modifier.size(60.dp),
                    onClick = {
                          if (userNameWritten && notificationTimeSelected) {
                              sharedPrefs.edit().putBoolean("first_launch", false).apply()
                              navController.navigate("main") {
                                  popUpTo(navController.graph.startDestinationId) {
                                      inclusive = true
                                  }
                              }
                          } else {
                              scope.launch {
                                  snackbarHostState.showSnackbar(
                                      message = "Please fill in both of the requirements!",
                                      actionLabel = "Dismiss",
                                      duration = SnackbarDuration.Short
                                  )
                              }
                          }
                    },
                    shape = CircleShape
                ) {
                    Icon(
                        Icons.AutoMirrored.Outlined.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(30.dp)
                    )
                }
                TextButton(
                    onClick = {
                        showSkipDialog = true
                    }
                ) {
                    Text(
                        "Skip"
                    )
                }
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