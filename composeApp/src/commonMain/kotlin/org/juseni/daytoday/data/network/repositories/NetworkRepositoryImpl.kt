package org.juseni.daytoday.data.network.repositories

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.juseni.daytoday.data.db.DayToDayDatabase
import org.juseni.daytoday.data.network.ApiService
import org.juseni.daytoday.data.network.CurrencyApiServer
import org.juseni.daytoday.data.network.models.ApartmentRemote
import org.juseni.daytoday.data.network.models.BillRemote
import org.juseni.daytoday.data.network.models.IncomeRemote
import org.juseni.daytoday.data.network.models.RentApartmentTypeRemote
import org.juseni.daytoday.data.network.models.toApartment
import org.juseni.daytoday.data.network.models.toBill
import org.juseni.daytoday.data.network.models.toIncome
import org.juseni.daytoday.data.network.models.toRentApartmentType
import org.juseni.daytoday.data.network.models.toUserEntity
import org.juseni.daytoday.domain.models.Apartment
import org.juseni.daytoday.domain.models.Bill
import org.juseni.daytoday.domain.models.Income
import org.juseni.daytoday.domain.models.RentApartmentType
import org.juseni.daytoday.domain.models.toApartmentRemote
import org.juseni.daytoday.domain.models.User
import org.juseni.daytoday.domain.models.toBillRemote
import org.juseni.daytoday.domain.models.toIncomeRemote
import org.juseni.daytoday.domain.repositories.NetworkRepository

class NetworkRepositoryImpl(
    private val apiService: ApiService,
    private val database: DayToDayDatabase,
    private val currencyApiServer: CurrencyApiServer
) : NetworkRepository {

    private val userDao by lazy { database.userDao() }

    private val mutex = Mutex()

    override fun singUpUser(newUser: User): Flow<Boolean> =
        flow {
            val responseSuccess = apiService.singUp(
                user = newUser.user,
                password = newUser.password,
                hasBills = newUser.hasBills,
                hasIncomesExpenses = newUser.hasIncomeExpenses
            )

            mutex.withLock {
                if (responseSuccess) {
                    userDao.deleteAll()
                    val userRemote = apiService.login(newUser.user, newUser.password)
                    userRemote?.let {
                        userDao.insertUser(userRemote.toUserEntity())
                    }
                }
            }
            emit(responseSuccess && userDao.getUsersCount() > 0)
        }


    override fun login(userName: String, password: String): Flow<Boolean> =
        flow {
            val userRemote = apiService.login(userName, password)
            mutex.withLock {
                if (userRemote != null) {
                    userDao.deleteAll()
                    userDao.insertUser(userRemote.toUserEntity())
                }
            }
            emit(userRemote != null && userDao.getUsersCount() > 0)
        }

    override fun getBillsByMonth(
        monthSelected: Int,
        yearSelected: Int,
        userId: Int
    ): Flow<List<Bill>> =
        flow {
            val bills = apiService.getBillsByMonth(monthSelected, yearSelected, userId)
            emit(bills.map(BillRemote::toBill))
        }

    override fun saveBill(bill: Bill): Flow<Boolean> =
        flow {
            userDao.getUser().collect { user ->
                if (user != null) {
                    emit(apiService.saveNewBill(bill.toBillRemote(user.id)))
                } else {
                    emit(false)
                }
            }
        }

    override fun saveApartment(apartment: Apartment): Flow<Boolean> =
        flow {
            emit(apiService.saveNewApartment(apartment.toApartmentRemote()))
        }

    override fun getApartments(): Flow<List<Apartment>> =
        flow {
            val apartments = apiService.getApartments()
            emit(apartments.map(ApartmentRemote::toApartment))
        }

    override fun getRentApartmentTypes(): Flow<List<RentApartmentType>> =
        flow {
            val rentApartmentTypes = apiService.getRentApartmentTypes()
            emit(rentApartmentTypes.map(RentApartmentTypeRemote::toRentApartmentType))
        }

    override fun getCurrencyConverter(): Flow<Double> =
        flow {
            emit(currencyApiServer.getCurrencyConverter())
        }

    override fun saveNewIncome(income: Income): Flow<Boolean> =
        flow {
            userDao.getUser().collect { user ->
                if (user != null) {
                    emit(apiService.saveNewIncome(income.toIncomeRemote(user.id)))
                } else {
                    emit(false)
                }
            }
        }

    override fun getIncomes(
        monthSelected: Int,
        yearSelected: Int,
        userId: Int,
        rentTypeId: Int
    ): Flow<List<Income>> =
        flow {
            val incomes = apiService.getIncomes(
                yearSelected = yearSelected,
                monthSelected = monthSelected,
                userId = userId,
                rentTypeId = rentTypeId
            )
            emit(incomes.map(IncomeRemote::toIncome))
        }

}