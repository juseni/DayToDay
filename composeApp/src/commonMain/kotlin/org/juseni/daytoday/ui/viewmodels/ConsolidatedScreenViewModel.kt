package org.juseni.daytoday.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.launch
import org.juseni.daytoday.domain.models.Bill
import org.juseni.daytoday.domain.models.Income
import org.juseni.daytoday.domain.repositories.NetworkRepository
import org.juseni.daytoday.domain.repositories.UserRepository

sealed interface ConsolidatedScreenUiState {
    data object Loading : ConsolidatedScreenUiState
    data class Success(val bills: List<Bill>) : ConsolidatedScreenUiState
    data class Error(val message: String) : ConsolidatedScreenUiState
}

class ConsolidatedScreenViewModel(
    private val networkRepository: NetworkRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ConsolidatedScreenUiState>(ConsolidatedScreenUiState.Loading)
    val uiState: StateFlow<ConsolidatedScreenUiState> = _uiState.asStateFlow()

    private val _incomes = MutableStateFlow<List<Income>>(emptyList())
    val incomes: StateFlow<List<Income>> = _incomes.asStateFlow()

    fun getBillsByMonth(monthSelected: Int, yearSelected: Int) {
        viewModelScope.launch {
            userRepository.getUser().conflate().collect { user ->
                if (user != null) {
                    if (user.hasIncomeExpenses) {
                        networkRepository.getIncomes(monthSelected, yearSelected).conflate().collectLatest { incomes ->
                            _incomes.value = incomes
                        }
                    }
                    networkRepository.getBillsByMonth(monthSelected, yearSelected, user.id)
                        .conflate()
                        .collect { bills ->
                            _uiState.value = ConsolidatedScreenUiState.Success(bills)
                        }
                } else {
                    _uiState.value = ConsolidatedScreenUiState.Error("User not found")
                }
            }
        }
    }
}