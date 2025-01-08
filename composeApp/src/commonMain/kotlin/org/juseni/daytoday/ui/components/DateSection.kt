package org.juseni.daytoday.ui.components

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource
import org.juseni.daytoday.resources.Res
import org.juseni.daytoday.resources.cancel
import org.juseni.daytoday.resources.detail_date
import org.juseni.daytoday.resources.new_bill_add_date
import org.juseni.daytoday.resources.new_bill_modify_date
import org.juseni.daytoday.resources.ok
import org.juseni.daytoday.ui.screens.CommonComponent
import org.juseni.daytoday.utils.toFormatString

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateSection(
    date: LocalDate?,
    onDateSelected: (LocalDate) -> Unit
) {
    val datePickerState = rememberDatePickerState()
    var openDialog by remember { mutableStateOf(false) }
    val selectedDate = date?.toFormatString().orEmpty()

    CommonComponent(
        text = stringResource(Res.string.detail_date),
        value = selectedDate,
        buttonText = stringResource(
            if (selectedDate.isEmpty()) Res.string.new_bill_add_date
            else Res.string.new_bill_modify_date
        ),
        onButtonClick = { openDialog = true }
    )

    if (openDialog) {
        val confirmEnabled = remember {
            derivedStateOf { datePickerState.selectedDateMillis != null }
        }
        DatePickerDialog(
            onDismissRequest = { openDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        openDialog = false
                        datePickerState.selectedDateMillis?.let { date ->
                            onDateSelected.invoke(
                                Instant
                                    .fromEpochMilliseconds(date)
                                    .toLocalDateTime(TimeZone.UTC)
                                    .date
                            )
                        }
                    },
                    enabled = confirmEnabled.value
                ) {
                    Text(stringResource(Res.string.ok))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { openDialog = false }
                ) {
                    Text(stringResource(Res.string.cancel))
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}