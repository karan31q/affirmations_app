package com.imarti.affirmations

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.imarti.affirmations.ui.theme.AffirmationsTheme
import com.imarti.affirmations.ui.theme.HarmonyOS_Sans
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun JournalPage() {

    val context = LocalContext.current
    var journalEntryText by remember { mutableStateOf("") }

    fun saveJournalEntry(text: String) {
        val sharedPrefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val entriesJson = sharedPrefs.getString("entries", "[]")
        val entriesArray = JSONArray(entriesJson)
        val entryObject = JSONObject().apply {
            put("text", text)
            put("dateTime", SimpleDateFormat("dd/mm/yy, HH:mm", Locale.getDefault()).format(Date()))
        }
        entriesArray.put(entryObject)
        sharedPrefs.edit().putString("entries", entriesArray.toString()).apply()
    }

    fun getJournalEntries(): List<JournalEntry> {
        val sharedPrefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val entriesJson = sharedPrefs.getString("entries", "[]")
        val entriesArray = JSONArray(entriesJson)
        val entriesList = mutableListOf<JournalEntry>()
        for (i in 0 until entriesArray.length()) {
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
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        OutlinedTextField(
            value = journalEntryText,
            onValueChange = { journalEntryText = it },
            modifier = Modifier.fillMaxWidth(),
            textStyle = TextStyle(fontFamily = HarmonyOS_Sans),
            placeholder = { Text("Enter your journal entry here") }
        )
        Button(
            onClick = {
                saveJournalEntry(journalEntryText)
                journalEntryText = "" // save the text and then clear the text field
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Save")
        }
        
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            val journalEntries = getJournalEntries().reversed() // show from the latest to the oldest
            journalEntries.forEach { entry ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            entry.text,
                            style = MaterialTheme.typography.headlineLarge
                        )
                        Text(
                            entry.dateTime,
                            style = MaterialTheme.typography.bodySmall
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


@Preview(showBackground = true)
@Composable
fun JournalPagePreview() {
    AffirmationsTheme {
        JournalPage()
    }
}