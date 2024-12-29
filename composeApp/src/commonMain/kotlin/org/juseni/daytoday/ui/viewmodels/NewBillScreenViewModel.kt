package org.juseni.daytoday.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import org.juseni.daytoday.domain.models.Bill
import org.juseni.daytoday.domain.repositories.NetworkRepository
import org.juseni.daytoday.utils.Tags

class NewBillScreenViewModel(
    private val networkRepository: NetworkRepository,
) : ViewModel() {

    private val _isBillSaved = MutableStateFlow(false)
    val isBillSaved = _isBillSaved.asStateFlow()

    private val _showErrorMessage = MutableStateFlow(false)
    val showErrorMessage = _showErrorMessage.asStateFlow()

    fun saveBill(date: LocalDate, tag: Tags, amount: Double, description: String? = null) {
        viewModelScope.launch {
            networkRepository.saveBill(
                Bill(
                    id = 0,
                    date = date,
                    tag = tag.id,
                    amount = amount,
                    description = description.orEmpty()
                )
            ).conflate().collectLatest { isSaved ->
                if (isSaved) {
                    _isBillSaved.value = true
                } else {
                    _showErrorMessage.value = true
                }
            }
        }
    }

    fun newBillAction() {
        viewModelScope.launch {
            _isBillSaved.value = false
            _showErrorMessage.value = false
        }
    }
}