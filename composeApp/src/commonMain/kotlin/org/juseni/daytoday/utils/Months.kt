package org.juseni.daytoday.utils

import org.jetbrains.compose.resources.StringResource
import org.juseni.daytoday.resources.Res
import org.juseni.daytoday.resources.month_april
import org.juseni.daytoday.resources.month_august
import org.juseni.daytoday.resources.month_december
import org.juseni.daytoday.resources.month_february
import org.juseni.daytoday.resources.month_january
import org.juseni.daytoday.resources.month_july
import org.juseni.daytoday.resources.month_june
import org.juseni.daytoday.resources.month_march
import org.juseni.daytoday.resources.month_may
import org.juseni.daytoday.resources.month_november
import org.juseni.daytoday.resources.month_october
import org.juseni.daytoday.resources.month_september

enum class Months(val value: Int, val monthName: StringResource) {
    JANUARY(1, Res.string.month_january),
    FEBRUARY(2, Res.string.month_february),
    MARCH(3, Res.string.month_march),
    APRIL(4, Res.string.month_april),
    MAY(5, Res.string.month_may),
    JUNE(6, Res.string.month_june),
    JULY(7, Res.string.month_july),
    AUGUST(8, Res.string.month_august),
    SEPTEMBER(9, Res.string.month_september),
    OCTOBER(10, Res.string.month_october),
    NOVEMBER(11, Res.string.month_november),
    DECEMBER(12, Res.string.month_december)
}