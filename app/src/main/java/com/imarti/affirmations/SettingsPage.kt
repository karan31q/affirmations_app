package com.imarti.affirmations

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.navigation.NavHostController
import com.imarti.affirmations.ui.theme.AffirmationsTheme
import com.maxkeppeker.sheets.core.models.base.rememberUseCaseState
import com.maxkeppeler.sheets.clock.ClockDialog
import com.maxkeppeler.sheets.clock.models.ClockConfig
import com.maxkeppeler.sheets.clock.models.ClockSelection
import kotlinx.coroutines.launch
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsPage(navController: NavHostController) {

    val context = LocalContext.current
    var alarmManager: AlarmManager
    var pendingIntent: PendingIntent

    val sharedPrefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    var userName by rememberSaveable {
        mutableStateOf(sharedPrefs.getString("user_name", "") ?: "User")
    }

    val showUserNameDialog = remember { mutableStateOf(false) }

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val clockState = rememberUseCaseState()

    // https://stackoverflow.com/questions/67401294/jetpack-compose-close-application-by-button
    // val activity = (LocalContext.current as? Activity)

    // so it can be used with coroutines
    val emptyUserNameWarning = stringResource(R.string.empty_username_warning)
    val okLabel = stringResource(R.string.got_it)
    val savedUserName = stringResource(R.string.username_updated)
    // val restartAppNotification = stringResource(R.string.restart_app_notification)
    // val restartText = stringResource(R.string.restart_action)
    val defaultUserName = stringResource(R.string.default_username)
    val alarmSetMessage = "Reminder set successfully"
    val alarmCancelledMessage = "Reminder cancelled"

    val calendar = Calendar.getInstance()

    fun setAlarm(calendar: Calendar) {
        alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        intent.action = "com.imarti.affirmations.ACTION_SET_ALARM"
        pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            alarmManager.setInexactRepeating(
                AlarmManager.RTC_WAKEUP, calendar.timeInMillis,
                AlarmManager.INTERVAL_DAY, pendingIntent
            )
        } else {
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP, calendar.timeInMillis,
                AlarmManager.INTERVAL_DAY, pendingIntent
            )
        }


        // show a snackbar
        scope.launch {
            snackbarHostState.showSnackbar(
                message = alarmSetMessage,
                actionLabel = okLabel,
                duration = SnackbarDuration.Short
            )
        }
    }

    fun cancelAlarm(showSnackBar: Boolean) {

        // cancel alarm
        alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        intent.action = "com.imarti.affirmations.ACTION_CANCEL_ALARM"
        pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        alarmManager.cancel(pendingIntent)

        // only show a snackbar when a user cancels manually TODO - add a button for same
        if (showSnackBar) {
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = alarmCancelledMessage,
                    actionLabel = okLabel,
                    duration = SnackbarDuration.Short
                )
            }
        }
    }

    Scaffold(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection)
            .statusBarsPadding(),
        topBar = {
             LargeTopAppBar(
                 colors = TopAppBarDefaults.topAppBarColors(
                     containerColor = MaterialTheme.colorScheme.surface,
                     titleContentColor = MaterialTheme.colorScheme.onSurface,
                 ),
                 title = { Text(
                     text = stringResource(R.string.settings_menu_heading),
                     maxLines = 1,
                     style = MaterialTheme.typography.headlineLarge
                 ) },
                 navigationIcon = {
                     IconButton(onClick = { navController.navigateUp() }) {
                         Icon(
                             imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                             contentDescription = stringResource(R.string.settings_menu_back_button)
                         )
                     }
                 },
                 scrollBehavior = scrollBehavior
             )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(start = 10.dp, end = 10.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp, bottom = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ){
                IconButton(
                    onClick = { showUserNameDialog.value = true },
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .size(100.dp)
                ) {
                    Icon(
                        Icons.Outlined.AccountCircle,
                        stringResource(R.string.topbar_settings_button),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(100.dp)
                    )
                }
                if (showUserNameDialog.value) {
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
                            TextField(
                                value = userName,
                                onValueChange = { userName = it },
                            )
                        },
                        onDismissRequest = {
                            showUserNameDialog.value = false
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
                                    showUserNameDialog.value = false
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
                                    showUserNameDialog.value = false
                                }
                            ) {
                                Text(
                                    text = stringResource(R.string.dismiss_text)
                                )
                            }
                        }
                    )

                }
                Text(
                    modifier = Modifier.padding(top = 10.dp),
                    text = userName,
                    style = MaterialTheme.typography.titleLarge
                )
            }

            ClockDialog(
                state = clockState,
                config = ClockConfig(
                    is24HourFormat = true
                ),
                selection = ClockSelection.HoursMinutes { hours, minutes ->
                    sharedPrefs.edit().putInt("hour_selected", hours).apply()
                    sharedPrefs.edit().putInt("minute_selected", minutes).apply()

                    // get user specified time
                    calendar[Calendar.HOUR_OF_DAY] = hours
                    calendar[Calendar.MINUTE] = minutes
                    calendar[Calendar.SECOND] = 0
                    calendar[Calendar.MILLISECOND] = 0

                    cancelAlarm(showSnackBar = false) // first cancel old alarm
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
                    setAlarm(calendar)
                }
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ){
                TextButton(
                    colors = ButtonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    contentPadding = PaddingValues(15.dp),
                    onClick = { clockState.show() }
                ) {
                    Icon(
                        Icons.Outlined.Notifications,
                        contentDescription = "Select time for daily notifications",
                    )
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 10.dp),
                        text = "Select Time for Daily Notifications",
                    )
                }
            }

            /*
            Button(
                onClick = {
                    sharedPrefs.edit().putString("user_name", "").apply()
                    sharedPrefs.edit().putBoolean("first_launch", true).apply()
                    sharedPrefs.edit().putInt("hour_selected", 12).apply()
                    scope.launch {
                        val result = snackbarHostState
                            .showSnackbar(
                                message = restartAppNotification,
                                actionLabel = restartText,
                                duration = SnackbarDuration.Short
                            )
                        when (result) {
                            SnackbarResult.ActionPerformed -> {
                                activity?.finish()
                            }
                            SnackbarResult.Dismissed -> TODO()
                        }
                    }

                }
            ) {
                Text(
                    text = stringResource(R.string.reset_app)
                )
            }
            */
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