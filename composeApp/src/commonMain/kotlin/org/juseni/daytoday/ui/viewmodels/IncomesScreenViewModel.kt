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
import org.juseni.daytoday.domain.models.Apartment
import org.juseni.daytoday.domain.models.Income
import org.juseni.daytoday.domain.models.RentApartmentType
import org.juseni.daytoday.domain.repositories.NetworkRepository

sealed interface ApartmentsUiState {
    data class Success(val apartments: List<Apartment>) : ApartmentsUiState
    data object Error : ApartmentsUiState
    data object Loading : ApartmentsUiState
}

class IncomesScreenViewModel(
    private val networkRepository: NetworkRepository
) : ViewModel()  {

    private val _isIncomeSaved = MutableStateFlow(false)
    val isIncomeSaved = _isIncomeSaved.asStateFlow()

    private val _showErrorMessage = MutableStateFlow(false)
    val showErrorMessage = _showErrorMessage.asStateFlow()

    private val _apartmentsUiState = MutableStateFlow<ApartmentsUiState>(ApartmentsUiState.Loading)
    val apartmentsUiState = _apartmentsUiState.asStateFlow()

    val rentApartmentTypes: StateFlow<List<RentApartmentType>> =
        networkRepository.getRentApartmentTypes()
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

    fun getApartments() {
        viewModelScope.launch {
            _apartmentsUiState.value = ApartmentsUiState.Loading
            networkRepository.getApartments().collect { apartments ->
                if (apartments.isEmpty()) {
                    _apartmentsUiState.value = ApartmentsUiState.Error
                } else {
                    _apartmentsUiState.value = ApartmentsUiState.Success(apartments)
                }
            }
        }
    }

    fun resetApartmentsUiState() {
        _apartmentsUiState.value = ApartmentsUiState.Loading
    }

    fun saveNewIncome(income: Income) {
        viewModelScope.launch {
            networkRepository.saveNewIncome(income).conflate().collectLatest { isSuccess ->
                if (isSuccess) {
                    _isIncomeSaved.value = true
                } else {
                    _showErrorMessage.value = true
                }
            }
        }
    }

    fun resetIncomeAction() {
        viewModelScope.launch {
            _isIncomeSaved.value = false
            _showErrorMessage.value = false
        }
    }

}