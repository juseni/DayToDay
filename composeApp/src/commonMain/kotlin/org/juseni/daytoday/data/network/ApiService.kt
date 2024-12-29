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
import org.juseni.daytoday.data.network.models.BillRemote
import org.juseni.daytoday.data.network.models.UserRemote
import org.juseni.daytoday.utils.isSuccessful

private const val SING_UP_URL = "/rest/v1/users_app"
private const val LOGIN_URL = "/rest/v1/rpc/login?"
private const val GET_BILLS_BY_MONTH_URL = "/rest/v1/rpc/get_bills_by_month_and_user_id?"
private const val SAVE_BILL_URL = "/rest/v1/bills"

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

    suspend fun getBillsByMonth(monthSelected: Int, userId: Int): List<BillRemote> =
        withContext(Dispatchers.IO) {
            try {
                val httResponse = client.get(
                    GET_BILLS_BY_MONTH_URL.plus(
                        "year_input=2024&month_input=$monthSelected&user_id_input=$userId"
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
}