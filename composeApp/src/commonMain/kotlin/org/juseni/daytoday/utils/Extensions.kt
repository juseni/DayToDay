package org.juseni.daytoday.utils

import io.ktor.http.HttpStatusCode
import kotlinx.datetime.LocalDate

fun HttpStatusCode.isSuccessful() = value in 200..299

fun LocalDate.toFormatString(): String = "${dayOfMonth}/${monthNumber}/${year}"

fun Double.formatAmount(): String  {
    val parts = this.toString().split(".")
    val integerPart = parts[0]
    val decimalPart = if (parts.size > 1) "." + parts[1] else ""

    // Add thousands separator
    val formattedIntegerPart = integerPart.reversed().chunked(3).joinToString(",").reversed()

    return formattedIntegerPart + decimalPart
}

fun String.formatAmount(): String =
    this.toLongOrNull()?.toString()?.reversed()?.chunked(3)?.joinToString(",")?.reversed() ?: ""

fun String.formatToLong(): Long = this.replace(",", "").toLongOrNull() ?: 0L

