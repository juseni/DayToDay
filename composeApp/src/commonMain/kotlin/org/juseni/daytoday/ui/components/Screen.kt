package org.juseni.daytoday.ui.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.juseni.daytoday.theme.DayToDayTheme

@Composable
fun Screen(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    DayToDayTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
            content = content
        )
    }
}