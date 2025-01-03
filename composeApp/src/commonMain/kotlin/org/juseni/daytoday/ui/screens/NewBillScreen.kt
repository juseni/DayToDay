package org.juseni.daytoday.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource
import org.juseni.daytoday.resources.Res
import org.juseni.daytoday.resources.cancel
import org.juseni.daytoday.resources.detail_date
import org.juseni.daytoday.resources.logging_in
import org.juseni.daytoday.resources.new_bill_add_date
import org.juseni.daytoday.resources.new_bill_add_tag
import org.juseni.daytoday.resources.new_bill_amount
import org.juseni.daytoday.resources.new_bill_amount_label
import org.juseni.daytoday.resources.new_bill_description
import org.juseni.daytoday.resources.new_bill_description_label
import org.juseni.daytoday.resources.new_bill_modify_date
import org.juseni.daytoday.resources.new_bill_modify_tag
import org.juseni.daytoday.resources.new_bill_screen_info
import org.juseni.daytoday.resources.new_bill_screen_title
import org.juseni.daytoday.resources.new_bill_tag
import org.juseni.daytoday.resources.ok
import org.juseni.daytoday.resources.save
import org.juseni.daytoday.ui.ScreenRoute
import org.juseni.daytoday.ui.components.DayToDayTopAppBar
import org.juseni.daytoday.ui.components.LabelledIconTag
import org.juseni.daytoday.ui.components.SavingProgressIndicator
import org.juseni.daytoday.ui.components.Screen
import org.juseni.daytoday.ui.viewmodels.NewBillScreenViewModel
import org.juseni.daytoday.utils.Tags
import org.juseni.daytoday.utils.formatAmount
import org.juseni.daytoday.utils.formatToLong
import org.juseni.daytoday.utils.toFormatString
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun NewBillScreen(
    navController: NavHostController,
    viewModel: NewBillScreenViewModel = koinViewModel<NewBillScreenViewModel>()
) {
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var selectedTag by remember { mutableStateOf<Tags?>(null) }
    var amount by remember { mutableStateOf(0L) }
    var description by remember { mutableStateOf("") }
    var showSaveButton by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }

    val isBillSaved by viewModel.isBillSaved.collectAsState()

    showSaveButton = selectedDate != null && selectedTag != null && amount > 0

    Screen {
        NewBillContent(
            showSaveButton = showSaveButton,
            billInformation = BillInformation(
                selectedDate = selectedDate,
                selectedTag = selectedTag,
                amount = amount,
                description = description
            ),
            newBillCallbacks = NewBillCallbacks(
                onDateSelected = { selectedDate = it },
                onTagSelected = { selectedTag = it },
                onAmountChange = { amount = it },
                onDescriptionChange = { description = it }
            ),
            onBackClick = { navController.popBackStack() },
            onSaveClicked = {
                showDialog = true
                viewModel.saveBill(
                    date = selectedDate!!,
                    tag = selectedTag!!,
                    amount = amount.toDouble(),
                    description = description
                )
            }
        )
        if (showDialog) {
            SavingProgressIndicator(
                showActions = isBillSaved,
                onNewBillClicked = {
                    showDialog = false
                    selectedDate = null
                    selectedTag = null
                    amount = 0
                    description = ""
                },
                onExitClicked = { navController.popBackStack() },
                onGoToMonthlyScreenClicked = {
                    navController.popBackStack()
                    navController.navigate(ScreenRoute.MONTHS_SCREEN)
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewBillContent(
    showSaveButton: Boolean,
    billInformation: BillInformation,
    newBillCallbacks: NewBillCallbacks,
    onBackClick: () -> Unit,
    onSaveClicked: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        topBar = {
            DayToDayTopAppBar(
                scrollBehavior = scrollBehavior,
                title = stringResource(Res.string.new_bill_screen_title),
                hasEndSessionButton = false,
                onBackClick = onBackClick
            )
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { innerPadding ->
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                modifier = Modifier.fillMaxWidth()
                    .padding(bottom = 24.dp),
                text = stringResource(Res.string.new_bill_screen_info),
                textAlign = TextAlign.Center
            )
            DateSection(
                date = billInformation.selectedDate,
                onDateSelected = newBillCallbacks.onDateSelected
            )
            TagSection(
                tag = billInformation.selectedTag,
                onTagSelected = newBillCallbacks.onTagSelected
            )
            DescriptionSection(
                description = billInformation.description,
                onDescriptionChange = newBillCallbacks.onDescriptionChange
            )
            AmountSection(
                amount = billInformation.amount,
                onAmountChange = newBillCallbacks.onAmountChange
            )
            if (showSaveButton) {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onSaveClicked
                ) {
                    Text(text = stringResource(Res.string.save))
                }
            }
        }
    }
}

@Composable
fun DescriptionSection(
    description: String,
    onDescriptionChange: (String) -> Unit
) {
    CommonComponent(
        text = stringResource(Res.string.new_bill_description),
        hasTextField = true,
        textToShowInEditBox = description,
        onValueChange = onDescriptionChange
    )
}

@Composable
fun AmountSection(
    amount: Long,
    onAmountChange: (Long) -> Unit
) {
    CommonComponent(
        text = stringResource(Res.string.new_bill_amount),
        hasTextField = true,
        isAmount = true,
        amountToShow = amount,
        onAmountChange = onAmountChange
    )
}

@Composable
fun CommonComponent(
    text: String,
    value: String? = "",
    buttonText: String = "",
    hasTextField: Boolean = false,
    isAmount: Boolean = false,
    amountToShow: Long = 0,
    textToShowInEditBox: String = "",
    onButtonClick: () -> Unit = {},
    onValueChange: (String) -> Unit = {},
    onAmountChange: (Long) -> Unit = {}
) {
    val focusManager = LocalFocusManager.current
    var textState by remember { mutableStateOf(TextFieldValue("")) }

    if (isAmount) {
        if (amountToShow == 0L) {
            textState = TextFieldValue("")
        }
    } else {
        if (textToShowInEditBox.isEmpty()) {
            textState = TextFieldValue("")
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = text)
        Text(
            text = value.orEmpty(),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.secondary
        )
        if (hasTextField) {
            OutlinedTextField(
                modifier = Modifier.padding(24.dp),
                value = textState,
                onValueChange = { input ->
                    if (isAmount) {
                        val unformattedText = input.text.filter { it.isDigit() } // Keep only digits
                        val formattedText = unformattedText.formatAmount()
                        textState = TextFieldValue(
                            text = formattedText,
                            selection = TextRange(formattedText.length) // Ensure cursor is at the end
                        )
                        onAmountChange.invoke(formattedText.formatToLong())

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
                label = {
                    Text(
                        text = stringResource(
                            if (isAmount) Res.string.new_bill_amount_label
                            else Res.string.new_bill_description_label
                        )
                    )
                },
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
        } else {
            Button(onClick = onButtonClick) {
                Text(text = buttonText)
            }
        }
    }
}

@Composable
fun TagSection(
    tag: Tags?,
    onTagSelected: (Tags) -> Unit
) {
    var openDialog by remember { mutableStateOf(false) }
    val selectedTag = tag?.id ?: 0

    CommonComponent(
        text = stringResource(Res.string.new_bill_tag),
        value = Tags.entries.firstOrNull { it.id == selectedTag }?.tagName?.let { stringResource(it) },
        buttonText = stringResource(
            if (selectedTag == 0) Res.string.new_bill_add_tag
            else Res.string.new_bill_modify_tag
        ),
        onButtonClick = { openDialog = true }
    )

    if (openDialog) {
        AlertDialog(
            onDismissRequest = { openDialog = false },
            confirmButton = { },
            dismissButton = { },
            containerColor = MaterialTheme.colorScheme.surface,
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
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(Tags.entries) { tag ->
                            LabelledIconTag(
                                iconRes = tag.icon,
                                tagName = tag.tagName,
                                modifier = Modifier.clickable {
                                    onTagSelected(tag)
                                    openDialog = false
                                }
                            )
                            HorizontalDivider()
                        }
                    }
                }
            }
        )
    }
}

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
                                    .toLocalDateTime(TimeZone.currentSystemDefault())
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

data class BillInformation(
    val selectedDate: LocalDate? = null,
    val selectedTag: Tags? = null,
    val amount: Long = 0,
    val description: String = ""
)

data class NewBillCallbacks(
    val onDateSelected: (LocalDate) -> Unit,
    val onTagSelected: (Tags) -> Unit,
    val onAmountChange: (Long) -> Unit,
    val onDescriptionChange: (String) -> Unit
)
