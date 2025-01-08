package org.juseni.daytoday.utils

import io.ktor.http.HttpStatusCode
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

fun HttpStatusCode.isSuccessful() = value in 200..299

fun LocalDate.toFormatString(): String = "${dayOfMonth}/${monthNumber}/${year}"

fun Double.formatAmount(): String  {
    val parts = this.toString().split(".")
    val integerPart = parts[0]
    val decimalPart = if (parts.size > 1) "." + parts[1] else ""

    // Add thousands separator
    val formattedIntegerPart = integerPart.reversed().chunked(3).joinToString(",").reversed()

    return formattedIntegerPart + decimalPart.take(2)
}

fun String.formatDoubleAmount(): String  {
    val parts = this.split(".")
    val integerPart = parts[0]
    val decimalPart = if (parts.size > 1) "." + parts[1] else ""

    // Add thousands separator
    val formattedIntegerPart = integerPart.reversed().chunked(3).joinToString(",").reversed()

    return formattedIntegerPart + decimalPart
}

fun String.formatToDouble(): Double = this.replace(",", "").toDoubleOrNull() ?: 0.0

fun Double.formatDouble(): String {
    val formatter = NonScientificFormatter()
    return formatter.format(this, 2)
}

fun getTodayLocalDate() = Clock.System.todayIn(TimeZone.currentSystemDefault())

