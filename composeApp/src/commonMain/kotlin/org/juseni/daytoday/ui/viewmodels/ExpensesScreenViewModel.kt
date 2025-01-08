package org.juseni.daytoday.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import org.juseni.daytoday.domain.models.Apartment
import org.juseni.daytoday.domain.models.Bill
import org.juseni.daytoday.domain.models.Income
import org.juseni.daytoday.domain.repositories.NetworkRepository
import org.juseni.daytoday.domain.repositories.UserRepository
import org.juseni.daytoday.utils.RentType

sealed interface IncomesUiState {
    data object Loading : IncomesUiState
    data class Success(val incomes: List<Income>) : IncomesUiState
    data object Error : IncomesUiState
}

class ExpensesScreenViewModel(
    private val networkRepository: NetworkRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _incomesUiState = MutableStateFlow<IncomesUiState>(IncomesUiState.Loading)
    val incomesUiState = _incomesUiState.asStateFlow()

    private val _showErrorMessage = MutableStateFlow(false)
    val showErrorMessage = _showErrorMessage.asStateFlow()

    private val _isExpenseSaved = MutableStateFlow(false)
    val isExpenseSaved = _isExpenseSaved.asStateFlow()

    val apartments: StateFlow<List<Apartment>> =
        networkRepository.getApartments().conflate()
            .catch { emit(emptyList()) }
            .stateIn(
                scope = viewModelScope,
                started = kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    val currencyExchangeRate: StateFlow<Double> =
        networkRepository.getCurrencyConverter()
            .catch { emit(0.0) }
            .stateIn(
                scope = viewModelScope,
                started = kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000),
                initialValue = 0.0
            )

    fun getIncomesByRentType(dateSelected: LocalDate, rentType: RentType) {
        viewModelScope.launch {
            _incomesUiState.value = IncomesUiState.Loading
            userRepository.getUser().conflate().collect { user ->
                if (user != null) {
                    networkRepository.getIncomes(
                        monthSelected = dateSelected.monthNumber,
                        yearSelected = dateSelected.year,
                        userId = user.id,
                        rentTypeId = rentType.type
                    ).conflate().collectLatest { incomes ->
                        _incomesUiState.value = IncomesUiState.Success(incomes)
                    }
                } else {
                    _incomesUiState.value = IncomesUiState.Error
                }
            }
        }

    }

    fun saveNewExpense(bill: Bill) {
        viewModelScope.launch {
            networkRepository.saveBill(bill).conflate().collectLatest { isSaved ->
                if (isSaved) {
                    _isExpenseSaved.value = true
                } else {
                    _showErrorMessage.value = true
                }
            }
        }
    }

    fun resetExpensesAction() {
        viewModelScope.launch {
            _isExpenseSaved.value = false
            _showErrorMessage.value = false
        }
    }
}