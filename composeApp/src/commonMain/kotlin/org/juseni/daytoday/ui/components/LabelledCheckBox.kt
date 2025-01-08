package org.juseni.daytoday.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun LabelledCheckBox(
    checked: Boolean,
    showCheckBoxAtTheEnd: Boolean = false,
    onCheckedChange: (Boolean) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    Row (
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clickable { onCheckedChange(!checked) }
            .padding(if (showCheckBoxAtTheEnd) 0.dp else 8.dp)
    ) {
        if (!showCheckBoxAtTheEnd) {
            Checkbox(
                checked = checked,
                onCheckedChange = { onCheckedChange(it) }
            )

            Spacer(modifier = Modifier.size(8.dp))
        }
        Text(
            modifier = if (showCheckBoxAtTheEnd) Modifier.weight(1f) else Modifier,
            text = label
        )
        if (showCheckBoxAtTheEnd) {
            Checkbox(
                modifier = Modifier.weight(1f),
                checked = checked,
                onCheckedChange = { onCheckedChange(it) }
            )
        }
    }
}