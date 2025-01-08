package org.juseni.daytoday.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
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
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.juseni.daytoday.domain.models.Apartment
import org.juseni.daytoday.domain.models.Income
import org.juseni.daytoday.domain.models.RentApartmentType
import org.juseni.daytoday.resources.Res
import org.juseni.daytoday.resources.error_getting_apartments
import org.juseni.daytoday.resources.incomes_screen_apartment
import org.juseni.daytoday.resources.incomes_screen_choose_apartment
import org.juseni.daytoday.resources.incomes_screen_error
import org.juseni.daytoday.resources.incomes_screen_independent
import org.juseni.daytoday.resources.incomes_screen_info
import org.juseni.daytoday.resources.incomes_screen_rent
import org.juseni.daytoday.resources.incomes_screen_rent_type
import org.juseni.daytoday.resources.incomes_screen_sale
import org.juseni.daytoday.resources.incomes_screen_sale_description
import org.juseni.daytoday.resources.incomes_screen_title
import org.juseni.daytoday.resources.incomes_screen_type
import org.juseni.daytoday.resources.new_income
import org.juseni.daytoday.resources.save
import org.juseni.daytoday.ui.components.ApartmentsUiSelector
import org.juseni.daytoday.ui.components.CurrencySection
import org.juseni.daytoday.ui.components.DateSection
import org.juseni.daytoday.ui.components.DayToDayTopAppBar
import org.juseni.daytoday.ui.components.DropDownSelector
import org.juseni.daytoday.ui.components.EditTextComponent
import org.juseni.daytoday.ui.components.ErrorAlertDialog
import org.juseni.daytoday.ui.components.SavingProgressIndicator
import org.juseni.daytoday.ui.components.Screen
import org.juseni.daytoday.ui.viewmodels.ApartmentsUiState
import org.juseni.daytoday.ui.viewmodels.IncomesScreenViewModel
import org.juseni.daytoday.utils.RentType
import org.juseni.daytoday.utils.getTodayLocalDate
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun IncomesScreen(
    navController: NavHostController,
    viewModel: IncomesScreenViewModel = koinViewModel<IncomesScreenViewModel>()
) {

    val apartmentsUiState by viewModel.apartmentsUiState.collectAsState()
    val rentApartmentTypes by viewModel.rentApartmentTypes.collectAsState()
    val currencyExchangeRate by viewModel.currencyExchangeRate.collectAsState()
    val isIncomeSaved by viewModel.isIncomeSaved.collectAsState()
    val showErrorMessage by viewModel.showErrorMessage.collectAsState()

    var showDialog by remember { mutableStateOf(false) }

    var showApartmentSelector by remember { mutableStateOf(false) }
    var incomeUiState by remember { mutableStateOf(IncomeUiState()) }

    Screen {

        IncomesScreenContent(
            uiState = incomeUiState,
            rentApartmentTypes = rentApartmentTypes,
            currencyExchangeRate = currencyExchangeRate,
            showSaveButton = when (incomeUiState.rentType) {
                RentType.RENT -> incomeUiState.apartment != null && incomeUiState.rentApartmentType != null
                        && incomeUiState.amount > 0.0
                RentType.SALE, RentType.INDEPENDENT ->
                    incomeUiState.saleDescription != null && incomeUiState.amount > 0.0
                RentType.UNSELECTED -> false
            },
            callbacks = IncomeCallbacks(
                onRentTypeSelected = {
                    incomeUiState = IncomeUiState(rentType = it)
                },
                onDateSelected = {
                    incomeUiState = incomeUiState.copy(date = it)
                },
                onApartmentSelected = {
                    incomeUiState = incomeUiState.copy(apartment = it)
                },
                onAmountChange = {
                    incomeUiState = incomeUiState.copy(amount = it)
                },
                onRentApartmentTypeChange = {
                    incomeUiState = incomeUiState.copy(rentApartmentType = it)
                },
                onSaleDescriptionChange = {
                    incomeUiState = incomeUiState.copy(saleDescription = it)
                }
            ),
            onBackClick = { navController.navigateUp() },
            showApartmentSelector = {
                viewModel.getApartments()
                showApartmentSelector = true
            },
            onSaveClicked = {
                showDialog = true
                viewModel.saveNewIncome(
                    Income(
                        date = incomeUiState.date,
                        rentType = incomeUiState.rentType.type,
                        apartmentId = incomeUiState.apartment?.id,
                        rentApartmentTypeId = incomeUiState.rentApartmentType?.id,
                        amount = if (incomeUiState.rentType == RentType.RENT) {
                            (incomeUiState.amount * incomeUiState.apartment!!.percentage) / 100
                        } else incomeUiState.amount,
                        saleDescription = incomeUiState.saleDescription
                    )
                )
            },
        )

        if (showApartmentSelector) {
            ApartmentSelector(
                uiState = apartmentsUiState,
                onApartmentSelected = {
                    incomeUiState = incomeUiState.copy(apartment = it)
                    showApartmentSelector = false
                },
                onDismiss = {
                    showApartmentSelector = false
                    viewModel.resetApartmentsUiState()
                }
            )
        }
        if (showDialog) {
            SavingProgressIndicator(
                showActions = isIncomeSaved,
                onNewText = stringResource(Res.string.new_income),
                onNewClicked = {
                    showDialog = false
                    incomeUiState = IncomeUiState()
                    viewModel.rentApartmentTypes
                },
                onExitClicked = {
                    showDialog = false
                    navController.popBackStack()
                },
            )
        }
        if (showErrorMessage) {
            showDialog = false
            ErrorAlertDialog(
                errorMessage = stringResource(Res.string.incomes_screen_error),
                onDismiss = { viewModel.resetIncomeAction() }
            )
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncomesScreenContent(
    uiState: IncomeUiState,
    rentApartmentTypes: List<RentApartmentType>,
    currencyExchangeRate: Double,
    showSaveButton: Boolean,
    callbacks: IncomeCallbacks,
    onBackClick: () -> Unit,
    showApartmentSelector: () -> Unit,
    onSaveClicked: () -> Unit
) {
    var amountToShow by remember { mutableStateOf(0.0) }

    if (uiState.amount == 0.0) {
        amountToShow = 0.0
    }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        topBar = {
            DayToDayTopAppBar(
                scrollBehavior = scrollBehavior,
                title = stringResource(Res.string.incomes_screen_title),
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
                text = stringResource(Res.string.incomes_screen_info),
                textAlign = TextAlign.Center
            )

            RentTypeSection(
                rentType = uiState.rentType,
                onRentTypeSelected = callbacks.onRentTypeSelected
            )

            if (uiState.rentType == RentType.RENT) {
                // Apartment section
                CommonComponent(
                    text = stringResource(Res.string.incomes_screen_apartment),
                    value = uiState.apartment?.let { it.name.plus("-").plus(it.number) },
                    buttonText = stringResource(Res.string.incomes_screen_choose_apartment),
                    onButtonClick = showApartmentSelector
                )

                // Rent Apartment type section
                DropDownSelector(
                    options = rentApartmentTypes.map { it.name },
                    labelToShow = stringResource(Res.string.incomes_screen_rent_type),
                    selectedOption = uiState.rentApartmentType?.name,
                    onOptionSelected = { optionSelected ->
                        callbacks.onRentApartmentTypeChange(
                            rentApartmentTypes.first { it.name == optionSelected }
                        )
                    }
                )
            }
            if (uiState.rentType != RentType.UNSELECTED || uiState.rentType == RentType.INDEPENDENT) {
                // Date Section
                DateSection(
                    date = uiState.date,
                    onDateSelected = callbacks.onDateSelected
                )

                // Currency section
                CurrencySection(
                    amountToShow = uiState.amount,
                    currencyExchangeRate = currencyExchangeRate,
                    onAmountChange = callbacks.onAmountChange
                )
            }
            AnimatedVisibility(uiState.rentType == RentType.SALE || uiState.rentType == RentType.INDEPENDENT) {
                // Sale description section
                EditTextComponent(
                    title = stringResource(Res.string.incomes_screen_sale_description),
                    textToShowInEditBox = uiState.saleDescription.orEmpty(),
                    onValueChange = callbacks.onSaleDescriptionChange
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
fun RentTypeSection(
    rentType: RentType,
    onRentTypeSelected: (RentType) -> Unit
) {
    val rentTypeFiltered = RentType.entries.filter { it != RentType.UNSELECTED }
    val rentText = stringResource(Res.string.incomes_screen_rent)
    val saleText = stringResource(Res.string.incomes_screen_sale)
    val independentText = stringResource(Res.string.incomes_screen_independent)

    DropDownSelector(
        options = rentTypeFiltered.map { stringResource(it.textRes!!) },
        labelToShow = stringResource(Res.string.incomes_screen_type),
        selectedOption = rentType.textRes?.let { stringResource(it) },
        onOptionSelected = { optionSelected ->
            onRentTypeSelected(
                when (optionSelected) {
                    rentText -> RentType.RENT
                    saleText -> RentType.SALE
                    independentText -> RentType.INDEPENDENT
                    else -> RentType.UNSELECTED
                }
            )
        }
    )
}

@Composable
fun CurrencyIcon(
    painterResource: DrawableResource,
    onCurrencyClicked: () -> Unit
) {
    IconButton(
        onClick = onCurrencyClicked
    ) {
        Image(
            modifier = Modifier.size(40.dp),
            painter = painterResource(painterResource),
            contentDescription = "Back",
        )
    }

}

@Composable
fun CommonComponent(
    text: String,
    value: String? = "",
    buttonText: String = "",
    onButtonClick: () -> Unit = {}
) {
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
        Button(onClick = onButtonClick) {
            Text(text = buttonText)
        }
    }
}

@Composable
fun ApartmentSelector(
    uiState: ApartmentsUiState,
    onApartmentSelected: (Apartment) -> Unit,
    onDismiss: () -> Unit = {}
) {
    AlertDialog(
        modifier = Modifier
            .fillMaxWidth(0.9f) // Set width to 90% of the screen width
            .height(300.dp), // Set a fixed height for the dialog
        properties = DialogProperties(usePlatformDefaultWidth = false), // Disable default width behavior
        onDismissRequest = onDismiss,
        confirmButton = {
            if (uiState is ApartmentsUiState.Error) {
                onDismiss()
            }
        },
        dismissButton = {},
        title = {
            if (uiState is ApartmentsUiState.Success) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        modifier = Modifier.align(Alignment.Center),
                        text = stringResource(Res.string.incomes_screen_choose_apartment)
                    )
                }
            }
        },
        text = {
            Box(modifier = Modifier.fillMaxWidth()) {
                when (uiState) {
                    ApartmentsUiState.Loading ->
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center),
                            color = MaterialTheme.colorScheme.outline
                        )

                    is ApartmentsUiState.Error ->
                        Text(
                            modifier = Modifier.align(Alignment.Center),
                            text = stringResource(Res.string.error_getting_apartments),
                            style = MaterialTheme.typography.bodyLarge
                        )

                    is ApartmentsUiState.Success ->
                        ApartmentsUiSelector(
                            apartments = uiState.apartments,
                            onApartmentSelected = onApartmentSelected
                        )

                }
            }
        }
    )
}

data class IncomeUiState(
    val rentType: RentType = RentType.UNSELECTED,
    val date: LocalDate = getTodayLocalDate(),
    val apartment: Apartment? = null,
    val amount: Double = 0.0,
    val rentApartmentType: RentApartmentType? = null,
    val saleDescription: String? = null
)

data class IncomeCallbacks(
    val onRentTypeSelected: (RentType) -> Unit,
    val onDateSelected: (LocalDate) -> Unit,
    val onApartmentSelected: (Apartment) -> Unit,
    val onAmountChange: (Double) -> Unit,
    val onRentApartmentTypeChange: (RentApartmentType) -> Unit,
    val onSaleDescriptionChange: (String) -> Unit
)