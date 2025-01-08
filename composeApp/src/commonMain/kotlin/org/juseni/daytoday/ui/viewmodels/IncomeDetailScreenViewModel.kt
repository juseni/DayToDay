package org.juseni.daytoday.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
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
import org.juseni.daytoday.domain.repositories.UserRepository

sealed interface IncomeDetailScreenUiState {
    data class Success(val incomes: List<Income>) : IncomeDetailScreenUiState
    data object Error : IncomeDetailScreenUiState
    data object Loading : IncomeDetailScreenUiState
}

class IncomeDetailScreenViewModel(
    private val networkRepository: NetworkRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _incomeDetailScreenUiState =
        MutableStateFlow<IncomeDetailScreenUiState>(IncomeDetailScreenUiState.Loading)
    val incomeDetailScreenUiState = _incomeDetailScreenUiState.asStateFlow()

    val apartments: StateFlow<List<Apartment>> =
        networkRepository.getApartments()
            .catch { emit(emptyList()) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    val rentApartmentTypes: StateFlow<List<RentApartmentType>> =
        networkRepository.getRentApartmentTypes()
            .catch { emit(emptyList()) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    fun fetchIncomeDetail(year: Int, month: Int) {
        viewModelScope.launch {
            userRepository.getUser().collect { user ->
                if (user != null) {
                    networkRepository.getIncomes(
                        monthSelected = month,
                        yearSelected = year,
                        userId = user.id
                    ).conflate().collectLatest { incomes ->
                        _incomeDetailScreenUiState.value =
                            IncomeDetailScreenUiState.Success(incomes)
                    }
                } else {
                    _incomeDetailScreenUiState.value = IncomeDetailScreenUiState.Error
                }

            }
        }
    }

}