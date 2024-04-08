package com.imarti.affirmations

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Create
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.imarti.affirmations.fetch.AffirmationsApi
import com.imarti.affirmations.pages.AffirmationsPage
import com.imarti.affirmations.pages.DailyTasksPage
import com.imarti.affirmations.pages.JournalPage
import com.imarti.affirmations.ui.theme.AffirmationsTheme
import com.imarti.affirmations.ui.theme.HarmonyOS_Sans

@Composable
fun MainUI(navController: NavHostController) {
    val context = LocalContext.current

    val items = listOf(
        NavigationItems(
            "Affirmations",
            Icons.Filled.Favorite,
            Icons.Outlined.FavoriteBorder
        ),
        NavigationItems(
            "Journal",
            Icons.Filled.Create,
            Icons.Outlined.Create

        ),
        NavigationItems(
            "Daily Tasks",
            Icons.Filled.CheckCircle,
            Icons.Outlined.CheckCircle
        )
    )

    var selectedItem by remember {
        mutableIntStateOf(0)
    }
    var journalPage by remember {
        mutableStateOf(false)
    }
    var dailyTasksPage by remember {
        mutableStateOf(false)
    }
    val snackbarHostState = remember {
        SnackbarHostState()
    }

    Scaffold(
        modifier = Modifier
            .statusBarsPadding(),
        topBar = {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 5.dp, start = 10.dp, end = 10.dp),
                colors = CardDefaults.cardColors(
                    // same as NavigationBar
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp),
                ),
                shape = CircleShape
            ) {
                Row(
                    modifier = Modifier
                        .padding(5.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { navController.navigate("settings") },
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surface)
                    ) {
                        Icon(
                            Icons.Outlined.AccountCircle,
                            stringResource(R.string.topbar_settings_button),
                            tint = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                    Text(
                        text = stringResource(R.string.topbar_heading),
                        modifier = Modifier
                            .padding(10.dp),
                        style = MaterialTheme.typography.headlineMedium,
                        fontFamily = HarmonyOS_Sans
                    )
                }
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
            ) {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedItem == index,
                        onClick = {
                            selectedItem = index
                            if (selectedItem == 1) {
                                journalPage = true
                            } else if (selectedItem == 2) {
                                dailyTasksPage = true
                                journalPage = false
                            } else {
                                journalPage = false
                                dailyTasksPage = false
                            }
                        },
                        icon = {
                            Icon(
                                if (selectedItem == index) item.selectedIcon else item.unSelectedIcon,
                                item.title
                            )
                        },
                        label = {
                            Text(
                                item.title,
                                fontFamily = HarmonyOS_Sans
                            )
                        }
                    )
                }
            }
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .paint(
                    painterResource(id = R.drawable.app_bg),
                    contentScale = ContentScale.FillBounds,
                    colorFilter = ColorFilter.tint(
                        color = MaterialTheme.colorScheme.primary
                    ),
                    alpha = 0.5f
                )
                .padding(innerPadding)
                .fillMaxSize()
                .padding(start = 10.dp, end = 10.dp, top = 20.dp, bottom = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // if it works it works
            if (journalPage) {
                JournalPage(context, snackbarHostState)
            } else if (dailyTasksPage) {
                DailyTasksPage(context, snackbarHostState)
            } else {
                AffirmationsPage(AffirmationsApi.retrofitService, context)
            }
        }
    }
}

data class NavigationItems(
    val title: String,
    val selectedIcon: ImageVector,
    val unSelectedIcon: ImageVector
)

@Preview(showBackground = true)
@Composable
fun MainUIPreview() {
    AffirmationsTheme {
        MainUI(navController = NavHostController(LocalContext.current))
    }
}