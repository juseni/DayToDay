package org.juseni.daytoday.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import org.juseni.daytoday.resources.Res
import org.juseni.daytoday.resources.total_amount
import org.juseni.daytoday.resources.total_expenses
import org.juseni.daytoday.utils.formatDouble

@Composable
fun DayToDayBottomBar(
    onIconClick: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainer,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        shadowElevation = 8.dp
    ) {
        Box(
            modifier =
            Modifier.fillMaxWidth()
                .clip(MaterialTheme.shapes.extraSmall)
        ) {
            IconButton(
                modifier = Modifier.align(Alignment.Center),
                onClick = onIconClick
            ) {
                Icon(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .padding(bottom = 16.dp)
                        .align(Alignment.Center)
                        .size(32.dp),
                    imageVector = Icons.Default.Home,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}

@Composable
fun DayToDayTotalBottomBar(
    totalIncomes: Double,
    totalExpenses: Double
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainer,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        shadowElevation = 8.dp
    ) {
        Column(
            modifier =
            Modifier.fillMaxWidth()
                .clip(MaterialTheme.shapes.extraSmall)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier =
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(
                    text = stringResource(Res.string.total_amount),
                    style = MaterialTheme.typography.titleMedium
                )
                Text(text = (totalIncomes - totalExpenses).formatDouble())
            }
            Row(
                modifier =
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(
                    text = stringResource(Res.string.total_expenses),
                    style = MaterialTheme.typography.titleMedium
                )
                Text(text = totalExpenses.formatDouble())
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}