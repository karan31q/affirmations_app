package com.imarti.affirmations.pages

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.imarti.affirmations.R
import com.imarti.affirmations.fetch.AffirmationsApi
import com.imarti.affirmations.fetch.FetchAffirmationsService
import com.imarti.affirmations.ui.theme.AffirmationsTheme
import com.imarti.affirmations.ui.theme.HarmonyOS_Sans
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject

@Composable
fun AffirmationsPage(affirmationsApi: FetchAffirmationsService, context: Context) {
    // so it can be used with coroutines
    val errorAffirmationString = stringResource(R.string.error_affirmation)
    val affirmationUnknownAuthor = stringResource(R.string.unknown)

    var affirmation by remember {
        mutableStateOf("")
    }
    var affirmationSource by remember {
        mutableStateOf("")
    }
    var canFetchAffirmation by remember {
        mutableStateOf(false)
    }

    val coroutineScope = rememberCoroutineScope()

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

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        LaunchedEffect(Unit) {
            coroutineScope.launch {
                fetchAffirmation(affirmationsApi)
            }
        }
        Text(
            text = affirmation,
            fontFamily = HarmonyOS_Sans,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Justify,
            maxLines = 4
        )
        Text(
            text = affirmationSource,
            fontFamily = HarmonyOS_Sans,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface,
        )
        if (!canFetchAffirmation) {
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


@Preview(showBackground = true)
@Composable
fun AffirmationsPagePreview() {
    AffirmationsTheme {
        AffirmationsPage(AffirmationsApi.retrofitService, LocalContext.current)
    }
}