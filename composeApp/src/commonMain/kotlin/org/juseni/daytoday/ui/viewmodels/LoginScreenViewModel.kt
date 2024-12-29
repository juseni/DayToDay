package org.juseni.daytoday.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import org.juseni.daytoday.domain.models.User
import org.juseni.daytoday.domain.repositories.DataStoreRepository
import org.juseni.daytoday.domain.repositories.NetworkRepository
import org.juseni.daytoday.domain.repositories.UserRepository

sealed interface LoginUiState {
    data object Loading : LoginUiState
    data class Login(val user: String, val password: String) : LoginUiState
    data class Success(val user: User?) : LoginUiState
    data class Error(val message: String) : LoginUiState
}

enum class LoginState {
    UNLOGGED,
    LOGGED_IN,
    LOGIN_IN,
    ERROR
}

class LoginScreenViewModel(
    private val dataStoreRepository: DataStoreRepository,
    private val userRepository: UserRepository,
    private val networkRepository: NetworkRepository
) : ViewModel() {

    private val _isLogged = MutableStateFlow(LoginState.UNLOGGED)
    val isLogged: StateFlow<LoginState> = _isLogged.asStateFlow()

    val loginUiState: StateFlow<LoginUiState> =
        combine(
            dataStoreRepository.getRememberMe().conflate(),
            dataStoreRepository.getAutoLogging().conflate(),
            userRepository.getUser().conflate(),
        ) { rememberMe, autoLogging, user ->
            if (user != null) {
                when {
                    autoLogging -> LoginUiState.Login(user.user, user.password)
                    rememberMe -> LoginUiState.Success(user)
                    else -> LoginUiState.Success(null)
                }
            } else {
                LoginUiState.Success(null)
            }
        }.take(1)
            .catch { LoginUiState.Error("Something went wrong") }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = LoginUiState.Loading
            )

    val rememberMe: StateFlow<Boolean> =
        dataStoreRepository.getRememberMe()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = false

            )

    val autoLogging: StateFlow<Boolean> =
        dataStoreRepository.getAutoLogging()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = false
            )

    fun singUp(newUser: User) {
        viewModelScope.launch {
            _isLogged.value = LoginState.LOGIN_IN
            networkRepository.singUpUser(newUser)
                .collect { isLogged ->
                    if (isLogged) {
                        _isLogged.value = LoginState.LOGGED_IN
                    } else {
                        _isLogged.value = LoginState.ERROR
                    }
                }
        }
    }

    fun login(userName: String, password: String) {
        viewModelScope.launch {
            if (_isLogged.value == LoginState.UNLOGGED) {
                _isLogged.value = LoginState.LOGIN_IN
                networkRepository.login(userName, password)
                    .collect { isLogged ->
                        if (isLogged) {
                            _isLogged.value = LoginState.LOGGED_IN
                        } else {
                            _isLogged.value = LoginState.ERROR
                        }
                    }
            }
        }
    }

    fun setRememberMe(rememberMe: Boolean) {
        viewModelScope.launch {
            dataStoreRepository.setRememberMe(rememberMe)
        }
    }

    fun setAutoLogging(autoLogging: Boolean) {
        viewModelScope.launch {
            dataStoreRepository.setAutoLogging(autoLogging)
        }
    }

    fun clearError() {
        _isLogged.value = LoginState.UNLOGGED
    }
}