package com.imarti.affirmations.pages

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.imarti.affirmations.R
import com.imarti.affirmations.ui.theme.AffirmationsTheme
import com.imarti.affirmations.ui.theme.HarmonyOS_Sans
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun JournalPage(context: Context, snackbarHostState: SnackbarHostState) {
    val sharedPrefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    val keyboardController = LocalSoftwareKeyboardController.current

    // so it can be used with coroutines
    val okLabel = stringResource(R.string.ok)
    val emptyJournalEntry = stringResource(R.string.empty_journal_entry)

    var journalEntries by remember {
        mutableStateOf(getJournalEntries(sharedPrefs))
    }
    var journalEntryText by remember {
        mutableStateOf("")
    }

    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        OutlinedTextField(
            value = journalEntryText,
            onValueChange = { journalEntryText = it },
            modifier = Modifier.fillMaxWidth(),
            textStyle = TextStyle(
                fontFamily = HarmonyOS_Sans
            ),
            placeholder = {
                Text(
                    stringResource(R.string.enter_journal_entry),
                    fontFamily = HarmonyOS_Sans
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface
            )
        )
        Button(
            onClick = {
                if (journalEntryText.isEmpty()) {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = emptyJournalEntry,
                            actionLabel = okLabel,
                            duration = SnackbarDuration.Short
                        )
                    }
                    journalEntryText = "" // just in case
                    keyboardController!!.hide()
                } else {
                    saveJournalEntry(journalEntryText, sharedPrefs)
                    journalEntryText = "" // save the text and then clear the text field
                    // update entries after saving
                    journalEntries = getJournalEntries(sharedPrefs)
                    keyboardController!!.hide()
                }
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text(
                text = stringResource(R.string.save),
                fontFamily = HarmonyOS_Sans
            )
        }

        Column(
            modifier = Modifier.verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            journalEntries.forEachIndexed { index, entry ->
                JournalCard(
                    entry.text,
                    entry.dateTime,
                    onDelete = {
                        deleteJournalEntry(index, sharedPrefs)
                        // update entries after deleting
                        journalEntries = getJournalEntries(sharedPrefs)
                    }
                )
            }
        }
    }
}

@Composable
fun JournalCard(
    entryText: String,
    entryDateTime: String,
    showDeleteButton: Boolean = true,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
    ) {
        Column(
            modifier = Modifier.padding(10.dp)
        ) {
            Text(
                entryText,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.fillMaxWidth(),
                fontFamily = HarmonyOS_Sans
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    entryDateTime,
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = HarmonyOS_Sans
                )
                if (showDeleteButton) {
                    IconButton(
                        onClick = {
                            onDelete()
                        }
                    ) {
                        Icon(
                            Icons.Outlined.Delete,
                            stringResource(R.string.delete_journal)
                        )
                    }
                }
            }
        }
    }
}

data class JournalEntry(
    val text: String,
    val dateTime: String
)

fun saveJournalEntry(text: String, sharedPrefs: SharedPreferences) {
    val entriesJson = sharedPrefs.getString("entries", "[]")
    val entriesArray = JSONArray(entriesJson)
    val entryObject = JSONObject().apply {
        put("text", text.trim()) // remove whitespace if any
        put("dateTime", SimpleDateFormat("dd/MM/yy, hh:mm a", Locale.getDefault()).format(Date()))
    }
    entriesArray.put(entryObject)
    sharedPrefs.edit().putString("entries", entriesArray.toString()).apply()
}

fun getLatestJournalEntry(sharedPrefs: SharedPreferences): JournalEntry? {
    val entriesList = getJournalEntries(sharedPrefs)
    return if (entriesList.isNotEmpty()) {
        entriesList[0]
    } else {
        null
    }
}
fun getJournalEntries(sharedPrefs: SharedPreferences): List<JournalEntry> {
    val entriesJson = sharedPrefs.getString("entries", "[]")
    val entriesArray = JSONArray(entriesJson)
    val entriesList = mutableListOf<JournalEntry>()
    for (i in entriesArray.length() - 1 downTo 0) {
        val entryObject = entriesArray.getJSONObject(i)
        entriesList.add(
            JournalEntry(
                text = entryObject.getString("text"),
                dateTime = entryObject.getString("dateTime")
            )
        )
    }
    return entriesList
}

fun deleteJournalEntry(index: Int, sharedPrefs: SharedPreferences) {
    val entriesJson = sharedPrefs.getString("entries", "[]")
    val entriesArray = JSONArray(entriesJson)

    val entriesList = getJournalEntries(sharedPrefs)

    if (index >= 0 && index < entriesList.size) {
        val entriesIndex = entriesArray.length() - index - 1
        entriesArray.remove(entriesIndex)
        sharedPrefs.edit().putString("entries", entriesArray.toString()).apply()
    }
}

@Preview(showBackground = true)
@Composable
fun JournalPagePreview() {
    AffirmationsTheme {
        JournalPage(LocalContext.current, SnackbarHostState())
    }
}