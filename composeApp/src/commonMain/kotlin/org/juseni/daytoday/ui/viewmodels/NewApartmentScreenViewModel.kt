package org.juseni.daytoday.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.launch
import org.juseni.daytoday.domain.models.Apartment
import org.juseni.daytoday.domain.repositories.NetworkRepository

class NewApartmentScreenViewModel(
    private val networkRepository: NetworkRepository
) : ViewModel() {

    private val _isApartmentSaved = MutableStateFlow(false)
    val isApartmentSaved = _isApartmentSaved.asStateFlow()

    private val _showErrorMessage = MutableStateFlow(false)
    val showErrorMessage = _showErrorMessage.asStateFlow()

    fun saveApartment(apartment: Apartment) {
        viewModelScope.launch {
            networkRepository.saveApartment(apartment).conflate().collectLatest { isSaved ->
                if (isSaved) {
                    _isApartmentSaved.value = true
                } else {
                    _showErrorMessage.value = true
                }
            }
        }
    }

    fun newApartmentAction() {
        viewModelScope.launch {
            _isApartmentSaved.value = false
            _showErrorMessage.value = false
        }
    }
}