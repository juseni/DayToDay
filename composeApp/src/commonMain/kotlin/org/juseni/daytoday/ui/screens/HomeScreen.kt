package org.juseni.daytoday.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import org.jetbrains.compose.resources.stringResource
import org.juseni.daytoday.resources.Res
import org.juseni.daytoday.resources.app_name
import org.juseni.daytoday.resources.consolidated
import org.juseni.daytoday.resources.expenses
import org.juseni.daytoday.resources.general_error
import org.juseni.daytoday.resources.incomes
import org.juseni.daytoday.resources.new_apartment
import org.juseni.daytoday.resources.new_bill
import org.juseni.daytoday.ui.ScreenRoute
import org.juseni.daytoday.ui.components.DayToDayTopAppBar
import org.juseni.daytoday.ui.components.ErrorScreenComponent
import org.juseni.daytoday.ui.components.FullProgressIndicator
import org.juseni.daytoday.ui.components.Screen
import org.juseni.daytoday.ui.navigateTop
import org.juseni.daytoday.ui.viewmodels.HomeScreenUiState
import org.juseni.daytoday.ui.viewmodels.HomeScreenViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModel: HomeScreenViewModel = koinViewModel<HomeScreenViewModel>()
) {
    val homeScreenUiState by viewModel.homeScreenUiState.collectAsState()

    Screen {
        when (homeScreenUiState) {
            is HomeScreenUiState.Loading -> FullProgressIndicator()
            is HomeScreenUiState.Success -> {
                val user = (homeScreenUiState as HomeScreenUiState.Success).user
                HomeScreenContent(
                    user.hasBills,
                    user.hasIncomeExpenses,
                    HomeScreenCallbacks(
                        onConsolidatedClicked = { navController.navigate(ScreenRoute.MONTHS_SCREEN) },
                        onNewBillClicked = { navController.navigate(ScreenRoute.NEW_BILL_SCREEN) },
                        onIncomesClicked = { navController.navigate(ScreenRoute.INCOMES_SCREEN) },
                        onExpensesClicked = { navController.navigate(ScreenRoute.EXPENSES_SCREEN) },
                        onNewApartmentClicked = { navController.navigate(ScreenRoute.NEW_APARTMENT_SCREEN) }
                    ),
                    onEndSessionClick = {
                        viewModel.endSession()
                        navController.navigateTop(ScreenRoute.LOGIN_SCREEN)
                    }
                )
            }
            is HomeScreenUiState.Error -> ErrorScreenComponent(
                errorMessage = stringResource(Res.string.general_error),
                navController = navController
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenContent(
    hasBills: Boolean,
    hasIncomeExpenses: Boolean,
    homeScreenCallbacks: HomeScreenCallbacks,
    onEndSessionClick: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        topBar = {
            DayToDayTopAppBar(
                scrollBehavior = scrollBehavior,
                title = stringResource(Res.string.app_name),
                hasBackButton = false,
                onEndSessionClick = onEndSessionClick
            )
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(
                16.dp,
                alignment = Alignment.CenterVertically
            ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (hasBills) {
                ItemClick(
                    text = stringResource(Res.string.consolidated),
                    onClick = homeScreenCallbacks.onConsolidatedClicked
                )
                ItemClick(
                    text = stringResource(Res.string.new_bill),
                    onClick = homeScreenCallbacks.onNewBillClicked
                )
                ItemClick(
                    text = stringResource(Res.string.incomes),
                    onClick = homeScreenCallbacks.onIncomesClicked
                )
            }
            if (hasIncomeExpenses) {
                ItemClick(
                    text = stringResource(Res.string.expenses),
                    onClick = homeScreenCallbacks.onExpensesClicked
                )
                ItemClick(
                    text = stringResource(Res.string.new_apartment),
                    onClick = homeScreenCallbacks.onNewApartmentClicked
                )
            }
        }
    }
}

@Composable
private fun ItemClick(
    text: String,
    onClick: () -> Unit
) {
    ElevatedButton(onClick = onClick) {
        Text(
            text = text,
            modifier = Modifier.padding(8.dp),
            style = MaterialTheme.typography.titleMedium
        )
    }
}

data class HomeScreenCallbacks(
    val onConsolidatedClicked: () -> Unit,
    val onNewBillClicked: () -> Unit,
    val onIncomesClicked: () -> Unit,
    val onExpensesClicked: () -> Unit,
    val onNewApartmentClicked: () -> Unit
)