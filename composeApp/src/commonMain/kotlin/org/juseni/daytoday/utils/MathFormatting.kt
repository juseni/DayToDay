package org.juseni.daytoday.utils

expect class NonScientificFormatter() {
    fun format(value: Double, decimalPlaces: Int = 2): String
}