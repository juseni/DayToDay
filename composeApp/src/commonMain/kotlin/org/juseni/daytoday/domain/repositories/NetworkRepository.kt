package org.juseni.daytoday.domain.repositories

import kotlinx.coroutines.flow.Flow
import org.juseni.daytoday.domain.models.Bill
import org.juseni.daytoday.domain.models.User

interface NetworkRepository {

    fun singUpUser(newUser: User): Flow<Boolean>

    fun login(userName: String, password: String): Flow<Boolean>

    fun getBillsByMonth(monthSelected: Int, userId: Int): Flow<List<Bill>>

    fun saveBill(bill: Bill): Flow<Boolean>
}