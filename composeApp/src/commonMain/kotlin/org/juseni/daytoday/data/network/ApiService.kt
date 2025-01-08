package org.juseni.daytoday.data.network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import org.juseni.daytoday.data.network.models.ApartmentRemote
import org.juseni.daytoday.data.network.models.BillRemote
import org.juseni.daytoday.data.network.models.IncomeRemote
import org.juseni.daytoday.data.network.models.RentApartmentTypeRemote
import org.juseni.daytoday.data.network.models.UserRemote
import org.juseni.daytoday.utils.isSuccessful

private const val SING_UP_URL = "/rest/v1/users_app"
private const val LOGIN_URL = "/rest/v1/rpc/login?"
private const val GET_BILLS_BY_MONTH_URL = "/rest/v1/rpc/get_bills_by_month_and_user_id?"
private const val SAVE_BILL_URL = "/rest/v1/bills"
private const val APARTMENTS_URL = "/rest/v1/apartments"
private const val RENT_APARTMENT_TYPES_URL = "/rest/v1/rent_type"
private const val INCOME_URL = "/rest/v1/incomes"
private const val GET_INCOME_URL = "/rest/v1/rpc/get_incomes_by_month?"

class ApiService(private val client: HttpClient) {

    suspend fun singUp(
        user: String,
        password: String,
        hasBills: Boolean,
        hasIncomesExpenses: Boolean
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            val newUser = UserRemote(
                user = user,
                password = password,
                hasBills = hasBills,
                hasIncomeExpenses = hasIncomesExpenses
            )
            val httResponse = client.post(SING_UP_URL) {
                contentType(ContentType.Application.Json)
                setBody(newUser)
            }
            httResponse.status.isSuccessful()
        } catch (ex: Exception) {
            false
        }
    }

    suspend fun login(user: String, password: String): UserRemote? = withContext(Dispatchers.IO) {
        try {
            val httResponse =
                client.get(LOGIN_URL.plus("input_user=$user&input_password=$password"))
            if (httResponse.status.isSuccessful()) {
                val bodyResponse = httResponse.body<List<UserRemote>>()
                bodyResponse.firstOrNull()
            } else {
                null
            }
        } catch (ex: Exception) {
            null
        }
    }

    suspend fun getBillsByMonth(
        monthSelected: Int,
        yearSelected: Int,
        userId: Int
    ): List<BillRemote> =
        withContext(Dispatchers.IO) {
            try {
                val httResponse = client.get(
                    GET_BILLS_BY_MONTH_URL.plus(
                        "year_input=$yearSelected&month_input=$monthSelected&user_id_input=$userId"
                    )
                )
                if (httResponse.status.isSuccessful()) {
                    httResponse.body<List<BillRemote>>()
                } else {
                    emptyList()
                }
            } catch (ex: Exception) {
                emptyList()
            }
        }

    suspend fun saveNewBill(bill: BillRemote): Boolean = withContext(Dispatchers.IO) {
        try {
            val httResponse = client.post(SAVE_BILL_URL) {
                contentType(ContentType.Application.Json)
                setBody(bill)
            }
            httResponse.status.isSuccessful()
        } catch (ex: Exception) {
            false
        }
    }

    suspend fun saveNewApartment(apartment: ApartmentRemote): Boolean =
        withContext(Dispatchers.IO) {
            try {
                val httResponse = client.post(APARTMENTS_URL) {
                    contentType(ContentType.Application.Json)
                    setBody(apartment)
                }
                httResponse.status.isSuccessful()
            } catch (ex: Exception) {
                false
            }
        }

    suspend fun getApartments(): List<ApartmentRemote> = withContext(Dispatchers.IO) {
        try {
            val httResponse = client.get(APARTMENTS_URL)
            if (httResponse.status.isSuccessful()) {
                httResponse.body<List<ApartmentRemote>>()
            } else {
                emptyList()
            }
        } catch (ex: Exception) {
            emptyList()
        }
    }

    suspend fun getRentApartmentTypes(): List<RentApartmentTypeRemote> =
        withContext(Dispatchers.IO) {
            try {
                val httResponse = client.get(RENT_APARTMENT_TYPES_URL)
                if (httResponse.status.isSuccessful()) {
                    httResponse.body<List<RentApartmentTypeRemote>>()
                } else {
                    emptyList()
                }
            } catch (ex: Exception) {
                emptyList()
            }
        }

    suspend fun saveNewIncome(income: IncomeRemote): Boolean = withContext(Dispatchers.IO) {
        try {
            val httResponse = client.post(INCOME_URL) {
                contentType(ContentType.Application.Json)
                setBody(income)
            }
            httResponse.status.isSuccessful()
        } catch (ex: Exception) {
            false
        }
    }

    suspend fun getIncomes(
        monthSelected: Int,
        yearSelected: Int,
        userId: Int,
        rentTypeId: Int
    ): List<IncomeRemote> = withContext(Dispatchers.IO) {
        try {
            val httResponse = client.get(
                GET_INCOME_URL.plus(
                    "year_input=$yearSelected&month_input=$monthSelected&user_id_input=$userId&rent_type_id_input=$rentTypeId"
                )
            )
            if (httResponse.status.isSuccessful()) {
                httResponse.body<List<IncomeRemote>>()
            } else {
                emptyList()
            }
        } catch (ex: Exception) {
            emptyList()
        }
    }
}