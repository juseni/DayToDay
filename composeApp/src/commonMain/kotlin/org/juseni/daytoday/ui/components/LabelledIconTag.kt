package org.juseni.daytoday.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun LabelledIconTag(
    iconRes: DrawableResource,
    tagName: StringResource,
    modifier: Modifier = Modifier
) {
    Row (
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .padding(8.dp)
    ) {
        Icon(
            modifier = Modifier
                .size(25.dp),
//                .padding(10.dp),
            painter = painterResource(iconRes),
            tint = MaterialTheme.colorScheme.outline,
            contentDescription = null,
        )
        Spacer(modifier = Modifier.size(8.dp))
        Text(text = stringResource(tagName))
    }
}