package org.juseni.daytoday.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
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
import org.juseni.daytoday.resources._50228_flag_colombia_icon
import org.juseni.daytoday.resources._50305_flag_usa_icon
import org.juseni.daytoday.resources.error_getting_apartments
import org.juseni.daytoday.resources.incomes_screen_amount
import org.juseni.daytoday.resources.incomes_screen_apartment
import org.juseni.daytoday.resources.incomes_screen_choose_apartment
import org.juseni.daytoday.resources.incomes_screen_currency
import org.juseni.daytoday.resources.incomes_screen_info
import org.juseni.daytoday.resources.incomes_screen_rent
import org.juseni.daytoday.resources.incomes_screen_rent_type
import org.juseni.daytoday.resources.incomes_screen_sale
import org.juseni.daytoday.resources.incomes_screen_sale_description
import org.juseni.daytoday.resources.incomes_screen_title
import org.juseni.daytoday.resources.incomes_screen_type
import org.juseni.daytoday.resources.new_apartment
import org.juseni.daytoday.resources.new_apartment_error
import org.juseni.daytoday.resources.save
import org.juseni.daytoday.ui.components.ApartmentsUiSelector
import org.juseni.daytoday.ui.components.DateSection
import org.juseni.daytoday.ui.components.DayToDayTopAppBar
import org.juseni.daytoday.ui.components.EditTextComponent
import org.juseni.daytoday.ui.components.ErrorAlertDialog
import org.juseni.daytoday.ui.components.SavingProgressIndicator
import org.juseni.daytoday.ui.components.Screen
import org.juseni.daytoday.ui.viewmodels.ApartmentsUiState
import org.juseni.daytoday.ui.viewmodels.IncomesScreenViewModel
import org.juseni.daytoday.utils.CurrencyType
import org.juseni.daytoday.utils.RentType
import org.juseni.daytoday.utils.formatDouble
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
                RentType.SALE -> incomeUiState.saleDescription != null && incomeUiState.amount > 0.0
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
                        isRent = incomeUiState.rentType == RentType.RENT,
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
                onNewText = stringResource(Res.string.new_apartment),
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
                errorMessage = stringResource(Res.string.new_apartment_error),
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
    var currencyType by remember { mutableStateOf(CurrencyType.UNSELECTED) }
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

            IncomeTypeSelector(
                selectedType = uiState.rentType,
                onTypeSelected = callbacks.onRentTypeSelected
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
                RentApartmentTypeSelector(
                    options = rentApartmentTypes,
                    selectedType = uiState.rentApartmentType,
                    onTypeSelected = callbacks.onRentApartmentTypeChange
                )
            }
            if (uiState.rentType != RentType.UNSELECTED) {
                // Date Section
                DateSection(
                    date = uiState.date,
                    onDateSelected = callbacks.onDateSelected
                )
                // Currency section
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp)),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 6.dp
                    ),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = stringResource(Res.string.incomes_screen_currency))
                        CurrencyIcon(
                            painterResource = Res.drawable._50228_flag_colombia_icon,
                            onCurrencyClicked = { currencyType = CurrencyType.COP }
                        )
                        CurrencyIcon(
                            painterResource = Res.drawable._50305_flag_usa_icon,
                            onCurrencyClicked = { currencyType = CurrencyType.USD }
                        )
                    }
                }
                if (currencyType != CurrencyType.UNSELECTED) {
                    // Amount section
                    EditTextComponent(
                        title = stringResource(Res.string.incomes_screen_amount),
                        isAmount = true,
                        amountToShow = amountToShow,
                        onAmountChange = {
                            amountToShow = it
                            if (currencyType == CurrencyType.COP) {
                                callbacks.onAmountChange(it)
                            } else {
                                callbacks.onAmountChange(it * currencyExchangeRate)
                            }
                        }
                    )
                    if (currencyType == CurrencyType.USD) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = (amountToShow * currencyExchangeRate).formatDouble(),
                            textAlign = TextAlign.End
                        )
                    }
                }
            }
            if (uiState.rentType == RentType.SALE) {
                // Sale description section
                EditTextComponent(
                    title = stringResource(Res.string.incomes_screen_sale_description),
                    textToShowInEditBox = uiState.saleDescription.orEmpty(),
                    onValueChange = callbacks.onSaleDescriptionChange
                )
            }
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

@Composable
fun IncomeTypeSelector(
    selectedType: RentType,
    onTypeSelected: (RentType) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val options = RentType.entries.filter { it != RentType.UNSELECTED }
    val rentText = stringResource(Res.string.incomes_screen_rent)
    val saleText = stringResource(Res.string.incomes_screen_sale)

    Column {
        OutlinedTextField(
            value = when (selectedType) {
                RentType.RENT -> rentText
                RentType.SALE -> saleText
                RentType.UNSELECTED -> ""
            },
            enabled = false,
            colors = TextFieldDefaults.colors(
                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                disabledIndicatorColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                disabledPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            ),
            onValueChange = { /* No-op */ },
            readOnly = true,
            label = { Text(stringResource(Res.string.incomes_screen_type)) },
            modifier = Modifier.fillMaxWidth().clickable { expanded = true },
            trailingIcon = {
                Icon(
                    imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                    contentDescription = null
                )
            }
        )

        // Dropdown menu items
        DropdownMenu(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = if (option == RentType.RENT) rentText else saleText,
                            textAlign = TextAlign.Center
                        )
                    },
                    onClick = {
                        onTypeSelected(option)
                        expanded = false
                    })
            }
        }
    }
}

@Composable
fun RentApartmentTypeSelector(
    options: List<RentApartmentType>,
    selectedType: RentApartmentType?,
    onTypeSelected: (RentApartmentType) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        OutlinedTextField(
            value = selectedType?.name.orEmpty(),
            enabled = false,
            colors = TextFieldDefaults.colors(
                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                disabledIndicatorColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                disabledPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            ),
            onValueChange = { /* No-op */ },
            readOnly = true,
            label = { Text(stringResource(Res.string.incomes_screen_rent_type)) },
            modifier = Modifier.fillMaxWidth().clickable { expanded = true },
            trailingIcon = {
                Icon(
                    imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                    contentDescription = null
                )
            }
        )

        // Dropdown menu items
        DropdownMenu(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = option.name,
                            textAlign = TextAlign.Center
                        )
                    },
                    onClick = {
                        onTypeSelected(option)
                        expanded = false
                    })
            }
        }
    }
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