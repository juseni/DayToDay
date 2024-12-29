package org.juseni.daytoday.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.juseni.daytoday.domain.models.User
import org.juseni.daytoday.domain.repositories.UserRepository

sealed interface HomeScreenUiState {
    data class Success(val user: User) : HomeScreenUiState
    data object Error : HomeScreenUiState
    data object Loading : HomeScreenUiState
}

class HomeScreenViewModel(
    userRepository: UserRepository
) : ViewModel() {

    val homeScreenUiState: StateFlow<HomeScreenUiState> =
        userRepository.getUser()
            .map {
                if (it != null) {
                    HomeScreenUiState.Success(it)
                } else {
                    HomeScreenUiState.Error
                }
            }
            .catch { emit(HomeScreenUiState.Error) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = HomeScreenUiState.Loading
            )

}