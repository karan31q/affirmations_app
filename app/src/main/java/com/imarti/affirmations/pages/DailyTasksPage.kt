package com.imarti.affirmations.pages

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DailyTasksPage(context: Context) {
    val sharedPrefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

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

    fun saveAnswers(
        index: Int,
        actualIndex: Int,
        answer: String,
        time: String,
        sharedPrefs: SharedPreferences
    ) {
        sharedPrefs.edit().putBoolean("answer_${index}", true).apply()
        sharedPrefs.edit().putString("daily_answer_${index}", answer.trim())
            .apply() // remove whitespace if needed
        sharedPrefs.edit().putString("daily_answer_${index}_time", time).apply()
        sharedPrefs.edit().putBoolean("daily_task_completed", true).apply()
        sharedPrefs.edit().putInt("previous_question", index).apply()
    }

    fun getAnswers(index: Int, sharedPrefs: SharedPreferences): String? {
        return sharedPrefs.getString("daily_answer_${index}", "Error")
    }

    // i know these function names are kinda confusing, ill change them when i can
    fun getAnswersFilledTime(index: Int, sharedPrefs: SharedPreferences): String? {
        return sharedPrefs.getString("daily_answer_${index}_time", "Null")
    }

    fun getAnswersFilled(index: Int, sharedPrefs: SharedPreferences): Boolean {
        return sharedPrefs.getBoolean("answer_${index}", false)
    }

    fun getDailyAnswersFilled(sharedPrefs: SharedPreferences): Boolean {
        return sharedPrefs.getBoolean("daily_task_completed", false)
    }

    fun getCurrentQuestionFill(sharedPrefs: SharedPreferences): Int {
        return sharedPrefs.getInt("prev_ques_completed", 0) + 1
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceAround,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(questions.size) { index ->
            val indexPlus1 = index + 1
            val dailyQuestionsDone = getAnswersFilled(indexPlus1, sharedPrefs)

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
                    Row(
                        modifier = Modifier.padding(40.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Day $indexPlus1",
                            style = MaterialTheme.typography.bodyLarge,
                            fontFamily = HarmonyOS_Sans
                        )
                        if (dailyQuestionsDone) {
                            Icon(
                                Icons.Outlined.Check,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
    if (questionsDialog) {
        val currentQuestion = getCurrentQuestionFill(sharedPrefs)
        AlertDialog(
            onDismissRequest = { questionsDialog = false },
            confirmButton = {
                // if answers aren't filled and daily target isn't completed
                if (!getAnswersFilled(questionsIndex + 1, sharedPrefs)
                    && !getDailyAnswersFilled(sharedPrefs) && currentQuestion == questionsIndex + 1
                ) {
                    TextButton(onClick = {
                        questionsDialog = false
                        val time = SimpleDateFormat("dd/MM/yy", Locale.getDefault()).format(Date())
                        saveAnswers(
                            questionsIndex + 1,
                            questionsIndex,
                            questionsAnswer,
                            time,
                            sharedPrefs
                        )
                        questionsAnswer = ""
                    }
                    ) {
                        Text(
                            stringResource(R.string.confirm_text),
                            fontFamily = HarmonyOS_Sans
                        )
                    }
                }
            },
            dismissButton = {
                // if answers are filled or daily target is completed
                if (getAnswersFilled(questionsIndex + 1, sharedPrefs)
                    || getDailyAnswersFilled(sharedPrefs) || currentQuestion != questionsIndex + 1
                ) {
                    TextButton(onClick = {
                        questionsDialog = false
                    }
                    ) {
                        Text(
                            stringResource(R.string.dismiss_text),
                            fontFamily = HarmonyOS_Sans
                        )
                    }
                }
            },
            title = {
                Text(
                    questions[questionsIndex],
                    fontFamily = HarmonyOS_Sans
                )
            },
            text = {
                // check if answer was already filled
                if (getAnswersFilled(questionsIndex + 1, sharedPrefs)) {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            getAnswers(questionsIndex + 1, sharedPrefs) ?: "Error",
                            fontFamily = HarmonyOS_Sans,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(
                                getAnswersFilledTime(questionsIndex + 1, sharedPrefs) ?: "Null",
                                fontFamily = HarmonyOS_Sans,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(top = 5.dp, bottom = 5.dp)
                            )
                        }
                    }
                } else {
                    OutlinedTextField(
                        enabled = !getDailyAnswersFilled(sharedPrefs) && currentQuestion == questionsIndex + 1, // questionsIndex starts at 0
                        value = questionsAnswer,
                        onValueChange = {
                            questionsAnswer = it
                        },
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
