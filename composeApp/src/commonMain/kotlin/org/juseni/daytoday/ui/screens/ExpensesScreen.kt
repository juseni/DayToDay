package org.juseni.daytoday.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavHostController
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.stringResource
import org.juseni.daytoday.domain.models.Apartment
import org.juseni.daytoday.domain.models.Bill
import org.juseni.daytoday.domain.models.Income
import org.juseni.daytoday.resources.Res
import org.juseni.daytoday.resources.expenses_screen_choose_income
import org.juseni.daytoday.resources.expenses_screen_commission
import org.juseni.daytoday.resources.expenses_screen_description
import org.juseni.daytoday.resources.expenses_screen_error
import org.juseni.daytoday.resources.expenses_screen_error_getting_income
import org.juseni.daytoday.resources.expenses_screen_info
import org.juseni.daytoday.resources.expenses_screen_percentage_commision
import org.juseni.daytoday.resources.expenses_screen_salary
import org.juseni.daytoday.resources.expenses_screen_title
import org.juseni.daytoday.resources.expenses_screen_type
import org.juseni.daytoday.resources.new_expense
import org.juseni.daytoday.resources.save
import org.juseni.daytoday.ui.components.CurrencySection
import org.juseni.daytoday.ui.components.DateSection
import org.juseni.daytoday.ui.components.DayToDayTopAppBar
import org.juseni.daytoday.ui.components.DropDownSelector
import org.juseni.daytoday.ui.components.EditTextComponent
import org.juseni.daytoday.ui.components.ErrorAlertDialog
import org.juseni.daytoday.ui.components.IncomeUiSelector
import org.juseni.daytoday.ui.components.SavingProgressIndicator
import org.juseni.daytoday.ui.components.Screen
import org.juseni.daytoday.ui.viewmodels.ExpensesScreenViewModel
import org.juseni.daytoday.ui.viewmodels.IncomesUiState
import org.juseni.daytoday.utils.ExpensesType
import org.juseni.daytoday.utils.RentType
import org.juseni.daytoday.utils.getTodayLocalDate
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun  ExpensesScreen(
    navHostController: NavHostController,
    viewModel: ExpensesScreenViewModel = koinViewModel<ExpensesScreenViewModel>()
) {
    val incomesUiState by viewModel.incomesUiState.collectAsState()
    val apartments by viewModel.apartments.collectAsState()
    val currencyExchangeRate by viewModel.currencyExchangeRate.collectAsState()
    var expensesUiState by remember { mutableStateOf(ExpensesUiState()) }
    var showIncomeSelectionDialog by remember { mutableStateOf(false) }

    val showErrorMessage by viewModel.showErrorMessage.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    val isExpenseSaved by viewModel.isExpenseSaved.collectAsState()

    Screen {
        ExpensesScreenContent(
            expensesUiState = expensesUiState,
            showSaveButton = when (expensesUiState.expensesType) {
                ExpensesType.SALARY -> expensesUiState.amount > 0.0 && expensesUiState.description.isNotEmpty()
                ExpensesType.COMMISSION -> expensesUiState.amount > 0.0
                        && expensesUiState.rentType != RentType.UNSELECTED && expensesUiState.commissionPercentage > 0.0
                ExpensesType.UNSELECTED -> false
            },
            currencyExchangeRate = currencyExchangeRate,
            callbacks = ExpenseCallbacks(
                onExpensesTypeSelected = {
                    expensesUiState = ExpensesUiState()
                    expensesUiState = expensesUiState.copy(expensesType = it)
                },
                onRentTypeSelected = {
                    showIncomeSelectionDialog = true
                    expensesUiState = expensesUiState.copy(rentType = it)
                    if (it != RentType.UNSELECTED) {
                        viewModel.getIncomesByRentType(expensesUiState.date, it)
                    }
                },
                onAmountChange = {
                    expensesUiState = expensesUiState.copy(amount = it)
                },
                onDateSelected = {
                    expensesUiState = expensesUiState.copy(date = it)
                },
                onDescriptionChange = {
                    expensesUiState = expensesUiState.copy(description = it)
                },
                onCommissionPercentageChange = {
                    expensesUiState = expensesUiState.copy(commissionPercentage = it)
                }
            ),
            onBackClick = { navHostController.navigateUp() },
            onSaveClicked = {
                showDialog = true
                viewModel.saveNewExpense(
                    Bill(
                        id = 0,
                        date = expensesUiState.date,
                        tag = expensesUiState.expensesType.tagId,
                        amount = if (expensesUiState.expensesType == ExpensesType.SALARY) {
                            expensesUiState.amount
                        } else {
                            expensesUiState.amount * (expensesUiState.commissionPercentage / 100)
                        },
                        description = expensesUiState.description
                    )
                )
            }
        )

        if (showIncomeSelectionDialog) {
            IncomeSelectorDialog(
                incomeUiState = incomesUiState,
                apartments = apartments,
                onIncomeSelected = {
                    expensesUiState = expensesUiState.copy(amount = it.amount)
                    showIncomeSelectionDialog = false
                },
                onDismiss = {
                    showIncomeSelectionDialog = false
                }
            )
        }
        if (showDialog) {
            SavingProgressIndicator(
                showActions = isExpenseSaved,
                onNewText = stringResource(Res.string.new_expense),
                onNewClicked = {
                    showDialog = false
                    expensesUiState = ExpensesUiState()
                },
                onExitClicked = {
                    showDialog = false
                    navHostController.popBackStack()
                },
            )
        }
        if (showErrorMessage) {
            showDialog = false
            ErrorAlertDialog(
                errorMessage = stringResource(Res.string.expenses_screen_error),
                onDismiss = { viewModel.resetExpensesAction() }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpensesScreenContent(
    expensesUiState: ExpensesUiState,
    showSaveButton: Boolean = false,
    currencyExchangeRate: Double,
    callbacks: ExpenseCallbacks,
    onBackClick: () -> Unit,
    onSaveClicked: () -> Unit
) {

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        topBar = {
            DayToDayTopAppBar(
                scrollBehavior = scrollBehavior,
                title = stringResource(Res.string.expenses_screen_title),
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
                text = stringResource(Res.string.expenses_screen_info),
                textAlign = TextAlign.Center
            )

            ExpenseTypeSection(
                expenseSelected = expensesUiState.expensesType,
                onExpenseSelected = callbacks.onExpensesTypeSelected
            )
            AnimatedVisibility(expensesUiState.expensesType != ExpensesType.UNSELECTED) {
                DateSection(
                    date = expensesUiState.date,
                    onDateSelected = callbacks.onDateSelected
                )
            }

            if (expensesUiState.expensesType == ExpensesType.COMMISSION) {
                RentTypeSection(
                    rentType = expensesUiState.rentType,
                    onRentTypeSelected = callbacks.onRentTypeSelected
                )
                EditTextComponent(
                    title = stringResource(Res.string.expenses_screen_percentage_commision),
                    isAmount = true,
                    amountToShow = expensesUiState.commissionPercentage,
                    onAmountChange = callbacks.onCommissionPercentageChange
                )
            }

            if (expensesUiState.expensesType != ExpensesType.UNSELECTED) {
                CurrencySection(
                    amountToShow = expensesUiState.amount,
                    currencyExchangeRate = currencyExchangeRate,
                    onAmountChange = callbacks.onAmountChange
                )
                // Description section
                EditTextComponent(
                    title = stringResource(Res.string.expenses_screen_description),
                    textToShowInEditBox = expensesUiState.description,
                    onValueChange = callbacks.onDescriptionChange
                )

            }
            AnimatedVisibility(showSaveButton) {
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
fun IncomeSelectorDialog(
    incomeUiState: IncomesUiState,
    apartments: List<Apartment>,
    onIncomeSelected: (Income) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        modifier = Modifier
            .fillMaxWidth(0.9f) // Set width to 90% of the screen width
            .height(300.dp), // Set a fixed height for the dialog
        properties = DialogProperties(usePlatformDefaultWidth = false), // Disable default width behavior
        onDismissRequest = onDismiss,
        confirmButton = {
            if (incomeUiState is IncomesUiState.Error) {
                onDismiss()
            }
        },
        dismissButton = {},
        title = {
            if (incomeUiState is IncomesUiState.Success) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        modifier = Modifier.align(Alignment.Center),
                        text = stringResource(Res.string.expenses_screen_choose_income)
                    )
                }
            }
        },
        text = {
            Box(modifier = Modifier.fillMaxWidth()) {
                when (incomeUiState) {
                    IncomesUiState.Loading ->
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center),
                            color = MaterialTheme.colorScheme.outline
                        )

                    is IncomesUiState.Error ->
                        Text(
                            modifier = Modifier.align(Alignment.Center),
                            text = stringResource(Res.string.expenses_screen_error_getting_income),
                            style = MaterialTheme.typography.bodyLarge
                        )

                    is IncomesUiState.Success ->
                        IncomeUiSelector(
                            incomes = incomeUiState.incomes,
                            apartments = apartments,
                            onIncomeSelected = onIncomeSelected
                        )

                }
            }
        }
    )
}

@Composable
fun ExpenseTypeSection(
    expenseSelected: ExpensesType,
    onExpenseSelected: (ExpensesType) -> Unit
) {
    val expensesTypeFiltered = ExpensesType.entries.filter { it != ExpensesType.UNSELECTED }
    val salaryText = stringResource(Res.string.expenses_screen_salary)
    val commissionText = stringResource(Res.string.expenses_screen_commission)
    DropDownSelector(
        options = expensesTypeFiltered.map { stringResource(it.textRes!!) },
        labelToShow = stringResource(Res.string.expenses_screen_type),
        selectedOption = expenseSelected.textRes?.let { stringResource(it) },
        onOptionSelected = { optionSelected ->
            onExpenseSelected(
                when (optionSelected) {
                    salaryText -> ExpensesType.SALARY
                    commissionText -> ExpensesType.COMMISSION
                    else -> ExpensesType.UNSELECTED
                }
            )
        }
    )
}

data class ExpensesUiState(
    val expensesType: ExpensesType = ExpensesType.UNSELECTED,
    val rentType: RentType = RentType.UNSELECTED,
    val amount: Double = 0.0,
    val date: LocalDate = getTodayLocalDate(),
    val description: String = "",
    val commissionPercentage: Double = 0.0
)

data class ExpenseCallbacks(
    val onExpensesTypeSelected: (ExpensesType) -> Unit,
    val onRentTypeSelected: (RentType) -> Unit,
    val onAmountChange: (Double) -> Unit,
    val onDateSelected: (LocalDate) -> Unit,
    val onDescriptionChange: (String) -> Unit,
    val onCommissionPercentageChange: (Double) -> Unit
)