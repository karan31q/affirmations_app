package com.imarti.affirmations.pages

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.imarti.affirmations.R
import com.imarti.affirmations.ui.theme.AffirmationsTheme
import com.imarti.affirmations.ui.theme.HarmonyOS_Sans

@Composable
fun DailyTasksPage(context: Context) {
    val questions: Array<String> = stringArrayResource(id = R.array.questions)

    var questionsDialog by remember {
        mutableStateOf(false)
    }
    var questionsIndex by remember {
        mutableIntStateOf(0)
    }
    var questionsAnswer by remember {
        mutableStateOf("")
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceAround,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(questions.size) { index ->
            val indexPlus1 = index + 1
            Column(
                verticalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier
                    .padding(20.dp)
                    .padding(
                        start = if (index % 2 == 0) 100.dp else 0.dp,
                        end = if (index % 2 != 0) 100.dp else 0.dp
                    )
            ) {
                TextButton(
                    colors = ButtonDefaults.textButtonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    onClick = {
                        questionsIndex = index
                        questionsDialog = true
                    }
                ) {
                    Text(
                        text = "Day $indexPlus1",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier
                            .padding(40.dp),
                        fontFamily = HarmonyOS_Sans
                    )
                }
            }
        }
    }
    if (questionsDialog) {
        AlertDialog(
            onDismissRequest = { questionsDialog = false },
            confirmButton = {
                TextButton(onClick = { questionsDialog = false }
                ) {
                    Text(
                        stringResource(R.string.confirm_text),
                        fontFamily = HarmonyOS_Sans
                    )
                }
            },
            title = {
                Text(
                    questions[questionsIndex],
                    fontFamily = HarmonyOS_Sans
                )
            },
            text = {
                OutlinedTextField(
                    value = questionsAnswer,
                    onValueChange = { questionsAnswer = it },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(
                        fontFamily = HarmonyOS_Sans
                    ),
                    placeholder = {
                        Text(
                            stringResource(R.string.enter_answer),
                            fontFamily = HarmonyOS_Sans
                        )
                    },
                )
            }
        )
    }
}

@Preview
@Composable
fun DailyTasksPagePreview() {
    AffirmationsTheme {
        DailyTasksPage(LocalContext.current)
    }
}
