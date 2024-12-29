package org.juseni.daytoday.utils

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

actual class NonScientificFormatter {
    actual fun format(value: Double, decimalPlaces: Int): String {
        val symbols = DecimalFormatSymbols().apply {
            decimalSeparator = '.' // Set your desired decimal separator
            groupingSeparator = ',' // Set your desired grouping separator
        }

        // Create a DecimalFormat instance with the desired pattern
        val pattern = "#,##0.${"#".repeat(decimalPlaces)}" // Pattern for thousands separator and decimal places
        val decimalFormat = DecimalFormat(pattern, symbols)

        return decimalFormat.format(value)
    }
}