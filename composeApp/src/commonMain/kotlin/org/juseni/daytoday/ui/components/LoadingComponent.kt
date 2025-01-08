package org.juseni.daytoday.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import org.juseni.daytoday.resources.Res
import org.juseni.daytoday.resources.exit
import org.juseni.daytoday.resources.logging_in
import org.juseni.daytoday.resources.saved_successfully
import org.juseni.daytoday.resources.saving

@Composable
fun FullProgressIndicator(
    modifier: Modifier = Modifier.fillMaxSize()
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.outline)
    }
}

@Composable
fun LoggingProgressIndicator() {
    AlertDialog(
        onDismissRequest = {},
        confirmButton = {},
        dismissButton = {},
        title = {
            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = stringResource(Res.string.logging_in)
                )
            }
        },
        text = {
            Box(modifier = Modifier.fillMaxWidth()) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    )
}

@Composable
fun SavingProgressIndicator(
    showActions: Boolean,
    onNewText: String,
    secondActionText: String? = null,
    onNewClicked: () -> Unit,
    onSecondActionClicked: () -> Unit = {},
    onExitClicked: () -> Unit
) {
    AlertDialog(
        onDismissRequest = {},
        confirmButton = {},
        dismissButton = {},
        title = {
            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = if (showActions) {
                        stringResource(Res.string.saved_successfully)
                    } else {
                        stringResource(Res.string.saving)
                    }
                )
            }
        },
        text = {
            Box(modifier = Modifier.fillMaxWidth()) {
                if (showActions) {
                    Column(
                        modifier = Modifier.align(Alignment.BottomCenter)
                            .padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        TextButton(onClick = onNewClicked) {
                            Text(
                                text = onNewText,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                        secondActionText?.let {
                            TextButton(onClick = onSecondActionClicked) {
                                Text(
                                    text = it,
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        }
                        TextButton(onClick = onExitClicked) {
                            Text(
                                text = stringResource(Res.string.exit),
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                } else {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        }
    )
}