package org.juseni.daytoday.data.network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import org.juseni.daytoday.data.network.models.UserRemote

private const val SING_UP_URL = "/rest/v1/users_app?"
private const val LOGIN_URL = "/rest/v1/rpc/login?"

class ApiService(private val client: HttpClient) {

    suspend fun singUp(
        user: String,
        password: String,
        hasBills: Boolean,
        hasIncomesExpenses: Boolean
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            val httResponse = client.post(SING_UP_URL.plus("user_name=$user&password=$password&has_bills=$hasBills&has_income_expenses=$hasIncomesExpenses"))
//            val httResponse = client.post("/rest/v1/users?user=$user&password=$password&hasBills=$hasBills&hasIncomesExpenses=$hasIncomesExpenses")
            httResponse.status.value in 200..299
        } catch (ex: Exception) {
            false
        }
    }

    suspend fun login(user: String, password: String): UserRemote? = withContext(Dispatchers.IO) {
        try {
            val httResponse = client.get(LOGIN_URL.plus("input_user=$user&input_password=$password"))
            if (httResponse.status.value in 200..299) {
                httResponse.body<UserRemote>()
            } else {
                null
            }
        } catch (ex: Exception) {
            null
        }
    }
}