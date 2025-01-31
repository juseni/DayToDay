package org.juseni.daytoday.data.network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.http.URLProtocol
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import org.juseni.daytoday.BuildConfig
import org.juseni.daytoday.data.network.models.CurrencyConverterRemote
import org.juseni.daytoday.utils.isSuccessful

private const val CURRENCY_CONVERTER_URL = "/v1/currencies/usd.json"

class CurrencyApiServer {
    private val clientNew by lazy {
        HttpClient {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
            install(DefaultRequest) {
                url {
                    protocol = URLProtocol.HTTPS
                    host = BuildConfig.URL_HOST_CURRENCY_EXCHANGE
                }

            }
        }
    }

    suspend fun getCurrencyConverter(): Double = withContext(Dispatchers.IO) {
        try {

            val httResponse = clientNew.get(CURRENCY_CONVERTER_URL)
            if (httResponse.status.isSuccessful()) {
                httResponse.body<CurrencyConverterRemote>().usd.rateExchange
            } else {
                0.0
            }
        } catch (ex: Exception) {
            println("Error: ${ex.message}")
            0.0
        }
    }
}