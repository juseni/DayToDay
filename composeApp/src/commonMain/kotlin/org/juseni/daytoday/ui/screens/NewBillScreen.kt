package org.juseni.daytoday.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.stringResource
import org.juseni.daytoday.resources.Res
import org.juseni.daytoday.resources.new_bill_add_tag
import org.juseni.daytoday.resources.new_bill_amount
import org.juseni.daytoday.resources.new_bill_description
import org.juseni.daytoday.resources.new_bill_error
import org.juseni.daytoday.resources.new_bill_go_to_montly_screen
import org.juseni.daytoday.resources.new_bill_insert_new_bill
import org.juseni.daytoday.resources.new_bill_modify_tag
import org.juseni.daytoday.resources.new_bill_screen_info
import org.juseni.daytoday.resources.new_bill_screen_title
import org.juseni.daytoday.resources.new_bill_tag
import org.juseni.daytoday.resources.save
import org.juseni.daytoday.ui.ScreenRoute
import org.juseni.daytoday.ui.components.DateSection
import org.juseni.daytoday.ui.components.DayToDayTopAppBar
import org.juseni.daytoday.ui.components.EditTextComponent
import org.juseni.daytoday.ui.components.ErrorAlertDialog
import org.juseni.daytoday.ui.components.LabelledIconTag
import org.juseni.daytoday.ui.components.SavingProgressIndicator
import org.juseni.daytoday.ui.components.Screen
import org.juseni.daytoday.ui.viewmodels.NewBillScreenViewModel
import org.juseni.daytoday.utils.Tags
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun NewBillScreen(
    navController: NavHostController,
    viewModel: NewBillScreenViewModel = koinViewModel<NewBillScreenViewModel>()
) {
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var selectedTag by remember { mutableStateOf<Tags?>(null) }
    var amount by remember { mutableStateOf(0.0) }
    var description by remember { mutableStateOf("") }
    var showSaveButton by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }

    val isBillSaved by viewModel.isBillSaved.collectAsState()
    val showErrorMessage by viewModel.showErrorMessage.collectAsState()

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
                    amount = amount,
                    description = description
                )
            }
        )
        if (showDialog) {
            SavingProgressIndicator(
                showActions = isBillSaved,
                onNewText = stringResource(Res.string.new_bill_insert_new_bill),
                secondActionText = stringResource(Res.string.new_bill_go_to_montly_screen),
                onNewClicked = {
                    showDialog = false
                    selectedDate = null
                    selectedTag = null
                    amount = 0.0
                    description = ""
                    viewModel.newBillAction()
                },
                onExitClicked = { navController.popBackStack() },
                onSecondActionClicked = {
                    showDialog = false
                    navController.popBackStack()
                    navController.navigate(ScreenRoute.MONTHS_SCREEN)
                }
            )
        }
        if (showErrorMessage) {
            showDialog = false
            ErrorAlertDialog(
                errorMessage = stringResource(Res.string.new_bill_error),
                onDismiss = { viewModel.newBillAction() }
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
    amount: Double,
    onAmountChange: (Double) -> Unit
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
    amountToShow: Double = 0.0,
    textToShowInEditBox: String = "",
    onButtonClick: () -> Unit = {},
    onValueChange: (String) -> Unit = {},
    onAmountChange: (Double) -> Unit = {}
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (hasTextField) {
            EditTextComponent(
                title = text,
                isAmount = isAmount,
                amountToShow = amountToShow,
                textToShowInEditBox = textToShowInEditBox,
                onValueChange = onValueChange,
                onAmountChange = onAmountChange
            )
        } else {
            Text(text = text)
            Text(
                text = value.orEmpty(),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.secondary
            )
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
                        text = stringResource(Res.string.new_bill_tag)
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

data class BillInformation(
    val selectedDate: LocalDate? = null,
    val selectedTag: Tags? = null,
    val amount: Double = 0.0,
    val description: String = ""
)

data class NewBillCallbacks(
    val onDateSelected: (LocalDate) -> Unit,
    val onTagSelected: (Tags) -> Unit,
    val onAmountChange: (Double) -> Unit,
    val onDescriptionChange: (String) -> Unit
)
