package org.juseni.daytoday.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import org.jetbrains.compose.resources.stringResource
import org.juseni.daytoday.domain.models.Apartment
import org.juseni.daytoday.resources.Res
import org.juseni.daytoday.resources.new_apartment
import org.juseni.daytoday.resources.new_apartment_error
import org.juseni.daytoday.resources.new_apartment_is_direct
import org.juseni.daytoday.resources.new_apartment_name
import org.juseni.daytoday.resources.new_apartment_number
import org.juseni.daytoday.resources.new_apartment_percentage
import org.juseni.daytoday.resources.new_apartment_screen_info
import org.juseni.daytoday.resources.new_apartment_screen_title
import org.juseni.daytoday.resources.save
import org.juseni.daytoday.ui.components.DayToDayTopAppBar
import org.juseni.daytoday.ui.components.EditTextComponent
import org.juseni.daytoday.ui.components.ErrorAlertDialog
import org.juseni.daytoday.ui.components.LabelledCheckBox
import org.juseni.daytoday.ui.components.SavingProgressIndicator
import org.juseni.daytoday.ui.components.Screen
import org.juseni.daytoday.ui.viewmodels.NewApartmentScreenViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun NewApartmentScreen(
    navHostController: NavHostController,
    viewModel: NewApartmentScreenViewModel = koinViewModel<NewApartmentScreenViewModel>()
) {

    var apartmentUiState by remember { mutableStateOf(ApartmentUiState()) }
    var showDialog by remember { mutableStateOf(false) }

    val isApartmentSaved by viewModel.isApartmentSaved.collectAsState()
    val showErrorMessage by viewModel.showErrorMessage.collectAsState()

    Screen {
        NewApartmentContent(
            apartmentUiState = apartmentUiState,
            newApartmentCallbacks = NewApartmentCallbacks(
                onNameChanged = { apartmentUiState = apartmentUiState.copy(name = it) },
                onNumberChanged = { apartmentUiState = apartmentUiState.copy(number = it) },
                onPercentageChanged = { apartmentUiState = apartmentUiState.copy(percentage = it) },
                onIsDirectChanged = { apartmentUiState = apartmentUiState.copy(isDirect = it) }
            ),
            showSaveButton = apartmentUiState.name.isNotEmpty() && apartmentUiState.number.isNotEmpty()
                    && apartmentUiState.percentage > 0.0,
            onBackClick = { navHostController.popBackStack() },
            onSaveClicked = {
                showDialog = true
                viewModel.saveApartment(
                    Apartment(
                        name = apartmentUiState.name,
                        number = apartmentUiState.number,
                        percentage = apartmentUiState.percentage,
                        isDirect = apartmentUiState.isDirect
                    )
                )
            }
        )
        if (showDialog) {
            SavingProgressIndicator(
                showActions = isApartmentSaved,
                onNewText = stringResource(Res.string.new_apartment),
                onNewClicked = {
                    showDialog = false
                    apartmentUiState = ApartmentUiState()
                    viewModel.newApartmentAction()
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
                errorMessage = stringResource(Res.string.new_apartment_error),
                onDismiss = { viewModel.newApartmentAction() }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewApartmentContent(
    apartmentUiState: ApartmentUiState,
    newApartmentCallbacks: NewApartmentCallbacks,
    showSaveButton: Boolean = false,
    onBackClick: () -> Unit = {},
    onSaveClicked: () -> Unit = {}
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        topBar = {
            DayToDayTopAppBar(
                scrollBehavior = scrollBehavior,
                title = stringResource(Res.string.new_apartment_screen_title),
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
                text = stringResource(Res.string.new_apartment_screen_info),
                textAlign = TextAlign.Center
            )
            // Name Section
            EditTextComponent(
                title = stringResource(Res.string.new_apartment_name),
                textToShowInEditBox = apartmentUiState.name,
                onValueChange = newApartmentCallbacks.onNameChanged
            )

            // Number Section
            EditTextComponent(
                title = stringResource(Res.string.new_apartment_number),
                textToShowInEditBox = apartmentUiState.number,
                onValueChange = newApartmentCallbacks.onNumberChanged
            )

            // Percentage Section
            EditTextComponent(
                title = stringResource(Res.string.new_apartment_percentage),
                amountToShow = apartmentUiState.percentage,
                isAmount = true,
                onAmountChange = newApartmentCallbacks.onPercentageChanged
            )

            // Is Direct Section
            LabelledCheckBox(
                checked = apartmentUiState.isDirect,
                showCheckBoxAtTheEnd = true,
                onCheckedChange = newApartmentCallbacks.onIsDirectChanged,
                label = stringResource(Res.string.new_apartment_is_direct),
                modifier = Modifier.fillMaxWidth()
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

data class ApartmentUiState(
    val name: String = "",
    val number: String = "",
    val percentage: Double = 0.0,
    val isDirect: Boolean = false
)

data class NewApartmentCallbacks(
    val onNameChanged: (String) -> Unit,
    val onNumberChanged: (String) -> Unit,
    val onPercentageChanged: (Double) -> Unit,
    val onIsDirectChanged: (Boolean) -> Unit
)