package com.imarti.affirmations

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.imarti.affirmations.ui.theme.AffirmationsTheme

@Composable
fun SettingsPage() {
    Column {
        Text(text = "Placeholder")
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsPagePreview() {
    AffirmationsTheme {
        SettingsPage()
    }
}