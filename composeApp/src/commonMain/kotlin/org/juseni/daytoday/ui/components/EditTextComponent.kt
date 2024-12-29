package org.juseni.daytoday.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import org.juseni.daytoday.utils.formatDoubleAmount
import org.juseni.daytoday.utils.formatToDouble

@Composable
fun EditTextComponent(
    title: String,
    isAmount: Boolean = false,
    amountToShow: Double = 0.0,
    textToShowInEditBox: String = "",
    onValueChange: (String) -> Unit = {},
    onAmountChange: (Double) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    var textState by remember { mutableStateOf(TextFieldValue("")) }

    if (isAmount) {
        if (amountToShow == 0.0) {
            textState = TextFieldValue("")
        }
    } else {
        if (textToShowInEditBox.isEmpty()) {
            textState = TextFieldValue("")
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = title
        )
        TextField(
            modifier = Modifier.weight(2f),
            value = textState,
            onValueChange = { input ->
                if (isAmount) {
                    val unformattedText =
                        input.text.filter { it.isDigit() || it == '.' } // Keep only digits
                    val formattedText = unformattedText.formatDoubleAmount()
                    textState = TextFieldValue(
                        text = formattedText,
                        selection = TextRange(formattedText.length) // Ensure cursor is at the end
                    )
                    onAmountChange.invoke(formattedText.formatToDouble())

                } else {
                    textState = input.copy(selection = TextRange(input.text.length))
                    onValueChange.invoke(input.text)
                }
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = if (isAmount) KeyboardType.Number else KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
            trailingIcon = {
                IconButton(
                    onClick = {
                        focusManager.clearFocus()
                    }) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "close circle",
                    )
                }
            }
        )
    }
}
