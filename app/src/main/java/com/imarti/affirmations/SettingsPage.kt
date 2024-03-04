package com.imarti.affirmations

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBox
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Create
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.imarti.affirmations.ui.theme.AffirmationsTheme
import com.imarti.affirmations.ui.theme.HarmonyOS_Sans
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsPage(navController: NavHostController) {
    val context = LocalContext.current
    val sharedPrefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    var userName by rememberSaveable {
        mutableStateOf(sharedPrefs.getString("user_name", "") ?: "User")
    }
    val showUserNameDialog = remember { mutableStateOf(false) }
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

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
                     text = "Settings",
                     maxLines = 1,
                     style = MaterialTheme.typography.headlineLarge
                 ) },
                 navigationIcon = {
                     IconButton(onClick = { navController.navigateUp() }) {
                         Icon(
                             imageVector = Icons.Outlined.ArrowBack,
                             contentDescription = "Go back"
                         )
                     }
                 },
                 scrollBehavior = scrollBehavior
             )
        },
        /*
        bottomBar = {
            var selectedItem by remember { mutableIntStateOf(0) }
            val items = listOf("Affirmations", "Journal")
            val itemsIcon = listOf(Icons.Outlined.Favorite, Icons.Outlined.Create)
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
            ) {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedItem == index,
                        onClick = { selectedItem = index },
                        icon = { Icon(itemsIcon[index], item) },
                        label = { Text(
                            item,
                            fontFamily = HarmonyOS_Sans
                        ) }
                    )
                }

            }
        }
         */
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(start = 10.dp, end = 10.dp, top = 20.dp, bottom = 20.dp),
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
                            contentDescription = "User icon",
                            modifier = Modifier.size(50.dp)
                        )
                    },
                    title = {
                        Text(text = "Change username")
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
                                    userName = "User"
                                    Toast.makeText(context, R.string.empty_username_warning, Toast.LENGTH_SHORT).show()
                                }
                                sharedPrefs.edit().putString("user_name", userName).apply()
                                showUserNameDialog.value = false
                            }
                        ) {
                            Text("Confirm")
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                showUserNameDialog.value = false
                            }
                        ) {
                            Text("Dismiss")
                        }
                    }
                )

            }
            Text(
                modifier = Modifier.padding(top = 10.dp),
                text = userName,
                style = MaterialTheme.typography.titleLarge
            )
            /*
            Row {
                Text(
                    modifier = Modifier.padding(top = 15.dp),
                    text = stringResource(id = R.string.settings_menu_change_username),
                    fontFamily = HarmonyOS_Sans,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
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
            ) {
                Text(
                    text = stringResource(id = R.string.save_changes_button),
                    fontFamily = HarmonyOS_Sans

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