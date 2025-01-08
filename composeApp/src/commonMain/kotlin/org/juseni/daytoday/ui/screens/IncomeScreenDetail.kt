package org.juseni.daytoday.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import org.jetbrains.compose.resources.stringResource
import org.juseni.daytoday.domain.models.Apartment
import org.juseni.daytoday.domain.models.Income
import org.juseni.daytoday.domain.models.RentApartmentType
import org.juseni.daytoday.resources.Res
import org.juseni.daytoday.resources.general_error
import org.juseni.daytoday.resources.income_detail_screen_amount
import org.juseni.daytoday.resources.income_detail_screen_apartment
import org.juseni.daytoday.resources.income_detail_screen_date
import org.juseni.daytoday.resources.income_detail_screen_description
import org.juseni.daytoday.resources.income_detail_screen_rent_apartment_type
import org.juseni.daytoday.resources.income_detail_screen_title
import org.juseni.daytoday.ui.components.DayToDayTopAppBar
import org.juseni.daytoday.ui.components.EmptyHolderComponent
import org.juseni.daytoday.ui.components.ErrorScreenComponent
import org.juseni.daytoday.ui.components.FullProgressIndicator
import org.juseni.daytoday.ui.components.Screen
import org.juseni.daytoday.ui.viewmodels.IncomeDetailScreenUiState
import org.juseni.daytoday.ui.viewmodels.IncomeDetailScreenViewModel
import org.juseni.daytoday.utils.RentType
import org.juseni.daytoday.utils.formatDouble
import org.juseni.daytoday.utils.toFormatString
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun IncomeScreenDetail(
    monthSelected: Int,
    yearSelected: Int,
    navController: NavHostController,
    viewModel: IncomeDetailScreenViewModel = koinViewModel<IncomeDetailScreenViewModel>()
) {
    LaunchedEffect(Unit) {
        viewModel.fetchIncomeDetail(year = yearSelected, month = monthSelected)
    }

    val incomeDetailScreenUiState by viewModel.incomeDetailScreenUiState.collectAsState()
    val apartments by viewModel.apartments.collectAsState()
    val rentApartmentTypes by viewModel.rentApartmentTypes.collectAsState()


    Screen {
        when (incomeDetailScreenUiState) {
            is IncomeDetailScreenUiState.Loading -> FullProgressIndicator()
            is IncomeDetailScreenUiState.Error -> ErrorScreenComponent(
                errorMessage = stringResource(Res.string.general_error),
                navController = navController
            )

            is IncomeDetailScreenUiState.Success ->
                IncomeScreenDetailContent(
                    incomes = (incomeDetailScreenUiState as IncomeDetailScreenUiState.Success).incomes,
                    apartments = apartments,
                    rentApartmentTypes = rentApartmentTypes,
                    onBackClick = { navController.popBackStack() }
                )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncomeScreenDetailContent(
    incomes: List<Income>,
    apartments: List<Apartment>,
    rentApartmentTypes: List<RentApartmentType>,
    onBackClick: () -> Unit
) {
    val incomesSorted = incomes.groupBy { it.rentType }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        topBar = {
            DayToDayTopAppBar(
                scrollBehavior = scrollBehavior,
                title = stringResource(Res.string.income_detail_screen_title),
                hasEndSessionButton = false,
                onBackClick = onBackClick
            )
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { innerPadding ->
        if (incomesSorted.isEmpty()) {
            EmptyHolderComponent()
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                incomesSorted.forEach { (rentTypeId, items) ->
                    val rentType = RentType.entries.firstOrNull { it.type == rentTypeId }
                    item {
                        if (rentType?.textRes != null) {
                            Text(
                                text = stringResource(rentType.textRes),
                                style = MaterialTheme.typography.titleLarge
                            )

                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 4.dp),
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                    items(items) { income ->
                        IncomeDetailItem(
                            income = income,
                            apartment = apartments.firstOrNull { it.id == income.apartmentId },
                            rentApartmentType = rentApartmentTypes.firstOrNull { it.id == income.rentApartmentTypeId }
                        )
                    }
                }
            }
        }

    }
}

@Composable
fun IncomeDetailItem(
    income: Income,
    apartment: Apartment?,
    rentApartmentType: RentApartmentType?
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp)),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CommonItemText(
                title = stringResource(Res.string.income_detail_screen_date),
                value = income.date.toFormatString()
            )
            if (apartment != null) {
                CommonItemText(
                    title = stringResource(Res.string.income_detail_screen_apartment),
                    value = apartment.name.plus(" - ").plus(apartment.number)
                )
            }
            if (rentApartmentType != null) {
                CommonItemText(
                    title = stringResource(Res.string.income_detail_screen_rent_apartment_type),
                    value = rentApartmentType.name
                )
            }
            CommonItemText(
                title = stringResource(Res.string.income_detail_screen_amount),
                value = income.amount.formatDouble()
            )
            if (!income.saleDescription.isNullOrBlank()) {
                CommonItemText(
                    title = stringResource(Res.string.income_detail_screen_description),
                    value = income.saleDescription
                )
            }
        }
    }
}

@Composable
fun CommonItemText(
    title: String,
    value: String
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium
        )
    }
}