package org.juseni.daytoday.data.network.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CurrencyConverterRemote(
    @SerialName("usd") val usd: USD
)

@Serializable
data class USD(
    @SerialName("cop") val rateExchange: Double
)