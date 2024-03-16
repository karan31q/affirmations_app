package com.imarti.affirmations.pages

import android.app.Activity
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Refresh
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
import androidx.compose.material3.SnackbarResult
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
import androidx.navigation.NavHostController
import com.imarti.affirmations.R
import com.imarti.affirmations.alarm.cancelAlarm
import com.imarti.affirmations.clock.ClockDialogImarti
import com.imarti.affirmations.ui.theme.AffirmationsTheme
import com.maxkeppeker.sheets.core.models.base.rememberUseCaseState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsPage(navController: NavHostController) {
    val context = LocalContext.current
    val sharedPrefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    // https://stackoverflow.com/questions/67401294/jetpack-compose-close-application-by-button
    val activity = (LocalContext.current as? Activity)

    // so it can be used with coroutines
    val emptyUserNameWarning = stringResource(R.string.empty_username_warning)
    val okLabel = stringResource(R.string.ok)
    val savedUserName = stringResource(R.string.username_updated)
    val restartAppNotification = stringResource(R.string.restart_app_notification)
    val restartText = stringResource(R.string.restart_action)
    val defaultUserName = stringResource(R.string.default_username)
    val alarmSetMessage = stringResource(R.string.reminder_set)
    val alarmCancelledMessage = stringResource(R.string.reminder_cancelled)

    var userName by rememberSaveable {
        mutableStateOf(sharedPrefs.getString("user_name", "") ?: "User")
    }
    var showUserNameDialog by remember {
        mutableStateOf(false)
    }
    val snackbarHostState = remember {
        SnackbarHostState()
    }

    val scope = rememberCoroutineScope()
    val clockState = rememberUseCaseState(
        onCloseRequest = {
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = alarmSetMessage,
                    actionLabel = okLabel,
                    duration = SnackbarDuration.Short
                )
            }
        }
    )
    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

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
                title = {
                    Text(
                        text = stringResource(R.string.settings_menu_heading),
                        maxLines = 1,
                        style = MaterialTheme.typography.headlineLarge
                    )
                },
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
        ClockDialogImarti(clockState = clockState, context)
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
            ) {
                IconButton(
                    onClick = { showUserNameDialog = true },
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
                            TextField(
                                value = userName,
                                onValueChange = { userName = it },
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
                Text(
                    modifier = Modifier.padding(top = 10.dp),
                    text = userName,
                    style = MaterialTheme.typography.titleLarge
                )
            }
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
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
                        text = stringResource(R.string.select_time_notifications)
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
                        cancelAlarm(context)
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                message = alarmCancelledMessage,
                                actionLabel = okLabel,
                                duration = SnackbarDuration.Short
                            )
                        }
                    },
                    modifier = Modifier.padding(bottom = 5.dp)
                ) {
                    Icon(
                        Icons.Outlined.Close,
                        contentDescription = stringResource(R.string.cancel_notifications),
                    )
                    Text(
                        text = stringResource(R.string.cancel_notifications),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 10.dp),
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
                        // reset every shared prefs to default
                        sharedPrefs.edit().putString("user_name", "").apply()
                        sharedPrefs.edit().putBoolean("first_launch", true).apply()
                        sharedPrefs.edit().putInt("hour_selected", 8).apply()
                        sharedPrefs.edit().putInt("minute_selected", 30).apply()
                        sharedPrefs.edit().putBoolean("alarm_set", false).apply()
                        sharedPrefs.edit().putString("entries", "[]").apply()
                        cancelAlarm(context)

                        // show a snackbar for same
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
                    },
                    modifier = Modifier.padding(bottom = 5.dp)
                ) {
                    Icon(
                        Icons.Outlined.Refresh,
                        contentDescription = stringResource(R.string.reset_app)
                    )
                    Text(
                        text = stringResource(R.string.reset_app),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 10.dp),
                    )
                }
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