package org.juseni.daytoday.domain.repositories

import kotlinx.coroutines.flow.Flow
import org.juseni.daytoday.domain.models.Apartment
import org.juseni.daytoday.domain.models.Bill
import org.juseni.daytoday.domain.models.Income
import org.juseni.daytoday.domain.models.RentApartmentType
import org.juseni.daytoday.domain.models.User

interface NetworkRepository {

    fun singUpUser(newUser: User): Flow<Boolean>

    fun login(userName: String, password: String): Flow<Boolean>

    fun getBillsByMonth(monthSelected: Int, yearSelected: Int, userId: Int): Flow<List<Bill>>

    fun saveBill(bill: Bill): Flow<Boolean>

    fun saveApartment(apartment: Apartment): Flow<Boolean>

    fun getApartments(): Flow<List<Apartment>>

    fun getRentApartmentTypes(): Flow<List<RentApartmentType>>

    fun getCurrencyConverter(): Flow<Double>

    fun saveNewIncome(income: Income): Flow<Boolean>

    fun getIncomes(
        monthSelected: Int,
        yearSelected: Int,
        userId: Int,
        rentTypeId: Int = 0
    ): Flow<List<Income>>

}