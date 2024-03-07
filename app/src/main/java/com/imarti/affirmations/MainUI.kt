package com.imarti.affirmations

import android.content.Intent
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
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Create
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.imarti.affirmations.fetch.AffirmationsApi
import com.imarti.affirmations.fetch.FetchAffirmationsService
import com.imarti.affirmations.ui.theme.AffirmationsTheme
import com.imarti.affirmations.ui.theme.HarmonyOS_Sans
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject

@Composable
fun AffirmationsPage(navController: NavHostController, affirmationsApi: FetchAffirmationsService) {

    val context = LocalContext.current
    var affirmation by remember { mutableStateOf("") }
    var affirmationSource by remember { mutableStateOf("") }
    var canFetchAffirmation by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableIntStateOf(0) }
    var journalPage by remember { mutableStateOf(false) }

    val errorAffirmationString = stringResource(R.string.error_affirmation)
    val affirmationUnknownAuthor = stringResource(R.string.unknown)
    
    data class NavigationItems(
        val title: String,
        val selectedIcon: ImageVector,
        val unSelectedIcon: ImageVector
    )

    suspend fun fetchAffirmation(affirmationsApi: FetchAffirmationsService) {
        try {
            val response = affirmationsApi.getAffirmation()
            val affirmationJson = JSONObject(response)
            affirmation = affirmationJson.getString("affirmation")
            affirmationSource = affirmationJson.getString("author")
            if (affirmationSource == affirmationUnknownAuthor) {
                affirmationSource = ""
            }
            canFetchAffirmation = true
        } catch (e: Exception) {
            affirmation = errorAffirmationString
            affirmationSource = ""
            canFetchAffirmation = false

        }
    }

    LaunchedEffect(Unit) {
        fetchAffirmation(affirmationsApi)
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
                     containerColor = MaterialTheme.colorScheme.surfaceVariant
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

                )
            )
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
                                } else {
                                    journalPage = false
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
        floatingActionButton = {
            if (journalPage) {
                FloatingActionButton(
                    onClick = { /*TODO*/ }
                ) {
                    Icon(
                        Icons.Outlined.Add, stringResource(R.string.journal_entry_add),
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(start = 10.dp, end = 10.dp, top = 20.dp, bottom = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ){
            if (journalPage) {
                JournalPage()
            } else {
                Text(
                    text = affirmation,
                    fontFamily = HarmonyOS_Sans,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    textAlign = TextAlign.Justify,
                    maxLines = 4
                )
                Text(
                    text = affirmationSource,
                    fontFamily = HarmonyOS_Sans,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                )
                Button(
                    onClick = {
                        CoroutineScope(Dispatchers.IO).launch {
                            fetchAffirmation(affirmationsApi)
                        }
                    },
                    modifier = Modifier.padding(top = 5.dp),
                ) {
                    Text(
                        text = stringResource(R.string.refresh_affirmation),
                        fontFamily = HarmonyOS_Sans
                    )
                }
                if (canFetchAffirmation) {
                    val sendIntent: Intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, "$affirmation\nby: $affirmationSource")
                        type = "text/plain"
                    }
                    val shareIntent = Intent.createChooser(sendIntent, null)
                    Button(
                        onClick = { context.startActivity(shareIntent) },
                        modifier = Modifier.padding(top = 5.dp),
                    ) {
                        Text(
                            text = stringResource(R.string.share_button),
                            fontFamily = HarmonyOS_Sans
                        )
                    }
                }
            }
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