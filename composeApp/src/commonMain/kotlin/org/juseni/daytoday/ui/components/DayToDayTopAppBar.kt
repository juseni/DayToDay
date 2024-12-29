package org.juseni.daytoday.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.juseni.daytoday.resources.Res
import org.juseni.daytoday.resources.end_session
import org.juseni.daytoday.resources.ic_filter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayToDayTopAppBar(
    scrollBehavior: TopAppBarScrollBehavior,
    title: String,
    year: String = "",
    hasBackButton: Boolean = true,
    hasEndSessionButton: Boolean = true,
    onBackClick: () -> Unit = {},
    onEndSessionClick: () -> Unit = {},
    onYearFilterClick: () -> Unit = {}
) {
    TopAppBar(
        title = {
            Text(
//                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                text = title,
                maxLines = 1,
                style = MaterialTheme.typography.titleMedium
            )
        },
        scrollBehavior = scrollBehavior,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        navigationIcon = {
            if (hasBackButton) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.outline
                    )
                }
            }
        },
        actions = {
            if (hasEndSessionButton) {
                TextButton(
                    onClick = onEndSessionClick,
                    modifier = Modifier.alignByBaseline(),
                    content = {
                        Text(
                            text = stringResource(Res.string.end_session),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                )
            }
            if (year.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .clickable { onYearFilterClick() }
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = year,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Icon(
                        modifier = Modifier.size(20.dp),
                        painter = painterResource(Res.drawable.ic_filter),
                        contentDescription = "Filter",
                        tint = MaterialTheme.colorScheme.outline
                    )
                }
            }
        }
    )
}