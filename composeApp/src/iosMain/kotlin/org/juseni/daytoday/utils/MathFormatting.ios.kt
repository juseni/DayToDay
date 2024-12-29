package org.juseni.daytoday.utils

import platform.Foundation.NSLocale
import platform.Foundation.NSNumber
import platform.Foundation.NSNumberFormatter
import platform.Foundation.NSNumberFormatterDecimalStyle
import platform.Foundation.currentLocale

actual class NonScientificFormatter {
    actual fun format(value: Double, decimalPlaces: Int): String {
        val formatter = NSNumberFormatter()
        formatter.numberStyle = NSNumberFormatterDecimalStyle
        formatter.minimumFractionDigits = decimalPlaces.toULong()
        formatter.maximumFractionDigits = decimalPlaces.toULong()
        formatter.locale = NSLocale.currentLocale // Use current locale settings

        return formatter.stringFromNumber(NSNumber(value)) ?: "0.00"
    }
}