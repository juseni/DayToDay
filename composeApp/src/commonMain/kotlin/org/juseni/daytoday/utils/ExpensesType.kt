package org.juseni.daytoday.utils

import org.jetbrains.compose.resources.StringResource
import org.juseni.daytoday.resources.Res
import org.juseni.daytoday.resources.tag_commission
import org.juseni.daytoday.resources.tag_salary

enum class ExpensesType(val tagId: Int, val textRes: StringResource?) {
    UNSELECTED(0, null),
    SALARY(13, Res.string.tag_salary),
    COMMISSION(14, Res.string.tag_commission)
}