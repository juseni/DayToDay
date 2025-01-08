package org.juseni.daytoday.utils

import org.jetbrains.compose.resources.StringResource
import org.juseni.daytoday.resources.Res
import org.juseni.daytoday.resources.incomes_screen_independent
import org.juseni.daytoday.resources.incomes_screen_rent
import org.juseni.daytoday.resources.incomes_screen_sale

enum class RentType(val type: Int, val textRes: StringResource?) {
    UNSELECTED(0, null),
    RENT(1, Res.string.incomes_screen_rent),
    SALE(2, Res.string.incomes_screen_sale),
    INDEPENDENT(3, Res.string.incomes_screen_independent)
}